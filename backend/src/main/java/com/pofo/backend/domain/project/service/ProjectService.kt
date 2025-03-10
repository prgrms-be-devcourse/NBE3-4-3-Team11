package com.pofo.backend.domain.project.service;

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.pofo.backend.domain.mapper.ProjectMapper
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest
import com.pofo.backend.domain.project.dto.request.ProjectUpdateRequest
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse
import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse
import com.pofo.backend.domain.project.dto.response.ProjectUpdateResponse
import com.pofo.backend.domain.project.entity.Project
import com.pofo.backend.domain.project.exception.ProjectCreationException
import com.pofo.backend.domain.project.repository.ProjectRepository
import com.pofo.backend.domain.skill.repository.ProjectSkillRepository
import com.pofo.backend.domain.skill.service.SkillService
import com.pofo.backend.domain.tool.repository.ProjectToolRepository
import com.pofo.backend.domain.tool.service.ToolService
import com.pofo.backend.domain.user.join.entity.User
import jakarta.validation.ConstraintViolationException
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class ProjectService(

    private val projectRepository: ProjectRepository,
    private val projectMapper: ProjectMapper,
    private val skillService: SkillService,
    private val toolService: ToolService,
    private val projectSkillRepository: ProjectSkillRepository,
    private val projectToolRepository: ProjectToolRepository,
    private val fileService: FileService
) {

    private val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())  // ✅ LocalDate 변환 지원
        registerKotlinModule()  // ✅ Kotlin 기본값 처리 지원
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // 예상치 못한 필드 무시
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)  // ✅ LocalDate를 "yyyy-MM-dd" 형식으로 변환
        setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY))  // ✅ null이 들어오면 빈 값으로 처리
    }

    fun createProject(user: User, projectRequestJson: String?, thumbnail: MultipartFile?): ProjectCreateResponse{
        if (projectRequestJson.isNullOrBlank()) {
            throw ProjectCreationException.badRequest("projectRequest가 필요합니다.")
        }

        return try {
            // ✅ JSON 파싱
            log.info("📢 [createProject] JSON 파싱 시작: $projectRequestJson")
            val request = objectMapper.readValue(projectRequestJson, ProjectCreateRequest::class.java)
            log.info("✅ [createProject] JSON 파싱 완료: $request")

            // ✅ 썸네일 저장
            val thumbnailPath: String? = thumbnail?.takeIf { !it.isEmpty }?.let {
                fileService.uploadThumbnail(it).also { path ->
                    log.info("📢 [createProject] 썸네일 저장 완료: $path")
                }
            }

            // ✅ 프로젝트 엔티티 생성 및 저장
            val project = Project(
                user = user,
                name = request.name,
                startDate = request.startDate,
                endDate = request.endDate,
                memberCount = request.memberCount,
                position = request.position,
                repositoryLink = request.repositoryLink,
                description = request.description,
                imageUrl = request.imageUrl,
                thumbnailPath = thumbnailPath ?: request.thumbnailPath,  // 기존 값 유지
                isDeleted = false
            )

            val savedProject = projectRepository.save(project)

            // ✅ 기술 스택 & 사용 도구 저장
            skillService.addProjectSkills(savedProject.id, request.skills)
            toolService.addProjectTools(savedProject.id, request.tools)

            log.info("✅ [createProject] 프로젝트 등록 완료: ID=${savedProject.id}")
            ProjectCreateResponse(savedProject.id!!)

        } catch (e: JsonProcessingException) {
            log.error("❌ JSON 파싱 실패: ${e.message}")
            throw ProjectCreationException.badRequest("잘못된 JSON 형식입니다. ${e.message}")

        } catch (e: ConstraintViolationException) {
            log.error("❌ 유효성 검증 실패: ${e.message}")
            throw ProjectCreationException.badRequest("유효성 검증 실패: ${e.message}")

        } catch (e: Exception) {
            log.error("❌ 프로젝트 등록 중 오류 발생: ${e.message}", e)
            throw ProjectCreationException.serverError("프로젝트 등록 중 오류가 발생했습니다.")
        }
    }


    fun detailAllProject(user: User): List<ProjectDetailResponse> {

        return try {
           val projects = projectRepository.findByIsDeletedFalseOrderByIdDesc()

            // 프로젝트가 없으면 예외 처리
            if (projects.isEmpty()) {
                throw ProjectCreationException.notFound("프로젝트가 존재하지 않습니다.")
            }

            // 사용자가 접근할 수 있는 프로젝트만 필터링 (본인 소유 또는 관리자)
           val accessibleProjects = projects.filter{ it.user?.id == user.id }

            // 사용자가 접근할 수 있는 프로젝트가 없으면 예외 발생
            if (accessibleProjects.isEmpty()) {
                throw ProjectCreationException.forbidden("프로젝트 전체 조회 할 권한이 없습니다.")
            }

            // thumbnailPath가 없으면 imageUrl을 사용하여 매핑
            accessibleProjects.map { project ->
                projectMapper.projectToProjectDetailResponse(
                    project.apply { thumbnailPath = thumbnailPath ?: imageUrl }
                )
            }

        } catch (ex: DataAccessException) {
            throw ProjectCreationException.serverError("프로젝트 전체 조회 중 데이터베이스 오류가 발생했습니다.")
        } catch (ex: ProjectCreationException) {
            throw ex  // 이미 정의된 예외는 다시 던진다.
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ProjectCreationException.badRequest("프로젝트 전체 조회 중 오류가 발생했습니다.")
        }
    }

    fun detailProject(projectId: Long, user: User): ProjectDetailResponse{

        return try {
            val project = projectRepository.findById(projectId)
                    .orElseThrow{ ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다.") }

            if (project.user?.id != user.id) {
                throw ProjectCreationException.forbidden("프로젝트 단건 조회 할 권한이 없습니다.")
            }

            // thumbnailPath가 없으면 imageUrl을 대신 사용
            val thumbnail = project.thumbnailPath ?: project.imageUrl
            projectMapper.projectToProjectDetailResponse(project.apply { thumbnailPath = thumbnail })

        } catch (ex: DataAccessException) {
            throw ProjectCreationException.serverError("프로젝트 단건 조회 중 데이터베이스 오류가 발생했습니다.")
        } catch (ex: ProjectCreationException) {
            throw ex  // 이미 정의된 예외는 다시 던진다.
        } catch (ex: Exception) {
            throw ProjectCreationException.badRequest("프로젝트 단건 조회 중 오류가 발생했습니다.")
        }
    }


    fun  searchProjectsByKeyword(user: User, keyword: String): List<ProjectDetailResponse> {
         return try {
            // 이름이나 설명에 키워드가 포함된 프로젝트 검색
            val projects = projectRepository.searchByKeyword(keyword)

            // 접근 권한 필터링 (자신의 프로젝트만 조회)
           val accessibleProjects = projects.filter { it.user?.id == user.id }

            if (accessibleProjects.isEmpty()) {
                throw ProjectCreationException.notFound("검색된 프로젝트가 없습니다.")
            }

             accessibleProjects.map { projectMapper.projectToProjectDetailResponse(it) }.toList()

        } catch (ex: DataAccessException) {
            throw ProjectCreationException.serverError("프로젝트 검색 중 데이터베이스 오류가 발생했습니다.")
        } catch (ex: ProjectCreationException) {
            throw ex   // 정의된 예외 재전달
        } catch (ex: Exception) {
            throw ProjectCreationException.badRequest("프로젝트 검색 중 오류가 발생했습니다.")
        }
    }



    @Transactional
    fun updateProject(
            projectId: Long,
            projectRequestJson: String?,
            user: User,
            thumbnail: MultipartFile?,
            deleteThumbnail: Boolean?
    ): ProjectUpdateResponse {

        return try {
            // JSON -> ProjectUpdateRequest 변환
            val request: ProjectUpdateRequest? = projectRequestJson?.takeIf { it.isNotBlank() }?.let {
                objectMapper.readValue(it, ProjectUpdateRequest::class.java) // ✅ 기존 objectMapper 사용
            }

            // 프로젝트 조회
            val project = projectRepository.findById(projectId)
                .orElseThrow { ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다.") }

            // 권한 확인
            if (project.user?.id != user.id) {
                throw ProjectCreationException.forbidden("프로젝트 수정할 권한이 없습니다.")
            }

            //프로젝트 데이터 업데이트 (썸네일 처리 포함)
            updateProjectData(project, request, thumbnail, deleteThumbnail)
        } catch (e: JsonProcessingException) {
            throw ProjectCreationException.badRequest("잘못된 JSON 형식입니다.")
        }
    }

    private fun updateProjectData(
            project: Project,
            request: ProjectUpdateRequest?,
            thumbnail : MultipartFile?,
            deleteThumbnail: Boolean?
    ): ProjectUpdateResponse {
        return try {

            var thumbnailPath: String? = project.thumbnailPath


            // 썸네일 삭제 처리 (null 체크 추가)
            if (deleteThumbnail == true && !thumbnailPath.isNullOrBlank()) {
                fileService.deleteFile(thumbnailPath)
                thumbnailPath = null
            }



            // 새로운 썸네일 업로드
            try {
                if (thumbnail?.isEmpty == false) {
                    thumbnailPath?.let { fileService.deleteFile(it) }
                    thumbnailPath = fileService.uploadThumbnail(thumbnail)
                }
            } catch (e: Exception) {
                throw ProjectCreationException.serverError("썸네일 업로드 중 오류가 발생했습니다.")
            }



            // 기본 정보 업데이트
             request?.let {
                project.updateBasicInfo(
                    it.name,
                    it.startDate,
                    it.endDate,
                    it.memberCount,
                    it.position,
                    it.repositoryLink,
                    it.description,
                    it.imageUrl
                );

                it.skills.let { skills -> skillService.updateProjectSkills(project.id!!, skills) }
                it.tools.let { tools -> toolService.updateProjectTools(project.id!!, tools) }

            }

            project.thumbnailPath = thumbnailPath ?: project.thumbnailPath

            projectRepository.save(project);

            ProjectUpdateResponse(
                    project.id!!,
                    project.name,
                    project.startDate,
                    project.endDate,
                    project.memberCount,
                    project.position,
                    project.repositoryLink,
                    project.description,
                    project.imageUrl,
                    project.thumbnailPath,
                    skillService.getProjectSkillNames(project.id),
                    toolService.getProjectToolNames(project.id),
                    project.isDeleted
            );

        } catch (ex: ProjectCreationException) {
            throw ex
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ProjectCreationException.serverError("프로젝트 수정 중 오류가 발생했습니다.")
        }
    }

    fun deleteProject(projectId: Long, user: User) {

        try{
            val project = projectRepository.findById(projectId)
                    .orElseThrow { ProjectCreationException.notFound("해당 프로젝트를 찾을 수 없습니다.") }

            if (project.user?.id != user.id) {
                throw ProjectCreationException.forbidden("프로젝트 삭제 할 권한이 없습니다.")
            }

            //중간 테이블 데이터 먼저 삭제
            project.isDeleted = true
            projectRepository.save(project)

        } catch (ex: DataAccessException) {
            throw ProjectCreationException.serverError("프로젝트 삭제 중 데이터베이스 오류가 발생했습니다.")
        } catch (ex: ProjectCreationException) {
            throw ex  // 이미 정의된 예외는 다시 던진다.
        } catch (ex: Exception) {
            throw ProjectCreationException.badRequest("프로젝트 삭제 중 오류가 발생했습니다.")
        }

    }

    fun moveToTrash(projectIds: List<Long>, user: User) {
        try {
            val projects = projectRepository.findAllById(projectIds)

            for (project in projects) {
                if (project.user?.id != user.id) {
                    throw ProjectCreationException.forbidden("프로젝트 삭제 할 권한이 없습니다.")
                }
                project.isDeleted = true // 휴지통 이동
            }

            projectRepository.saveAll(projects)  // 저장 시도 (이 부분에서 예외 발생 가능)

        } catch (ex: DataAccessException) {
            throw ProjectCreationException.serverError("프로젝트 삭제 중 데이터베이스 오류가 발생했습니다.")
        } catch (ex: ProjectCreationException) {
            throw ex
        } catch (ex: Exception) {
            throw ProjectCreationException.badRequest("프로젝트 삭제 중 오류가 발생했습니다.")
        }
    }

    fun getDeletedProjects(user: User): List<ProjectDetailResponse> {
        return try {
            val deletedProjects = projectRepository.findByUserAndIsDeletedTrue(user)

            deletedProjects.map { projectMapper.projectToProjectDetailResponse(it) }
        } catch (ex: DataAccessException) {
            ex.printStackTrace()
            throw ProjectCreationException.serverError("휴지통 목록 조회 중 데이터베이스 오류가 발생했습니다.")
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ProjectCreationException.badRequest("휴지통 목록 조회 중 예기치 않은 오류가 발생했습니다.")
        }
    }


    //요청된 프로젝트 ID 중에서, 휴지통에 있는 프로젝트만 조회하고 검증하는 메서드
    fun validateTrashProjects(projectIds: List<Long>): List<Project> {
        val trashProjects = projectRepository.findByIdInAndIsDeletedTrue(ArrayList(projectIds))

        val validTrashIds = trashProjects.map { it.id }.toSet()

        val invalidIds = projectIds.filter { it !in validTrashIds }

        if (invalidIds.isNotEmpty()) {
            throw ProjectCreationException.badRequest(
                "휴지통에 없는 프로젝트가 포함되어 있습니다: $invalidIds"
            )
        }

        return trashProjects
    }



    fun restoreProjects(projectIds: List<Long> , user: User) {
        val trashProjects = validateTrashProjects(projectIds)

        trashProjects.forEach {it.isDeleted = false }
        projectRepository.saveAll(trashProjects)
    }

    fun permanentlyDeleteProjects(projectIds: List<Long>, user: User) {
        val trashProjects = validateTrashProjects(projectIds)

        val userProjectIds = trashProjects.map {it.id}

        // 다중 삭제 한 번에 처리
        skillService.deleteProjectSkills(userProjectIds)
        toolService.deleteProjectTools(userProjectIds)
        projectRepository.deleteAll(trashProjects)
    }

}