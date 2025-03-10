package com.pofo.backend.domain.project.service

/*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pofo.backend.common.rsData.RsData
import com.pofo.backend.domain.mapper.ProjectMapper
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest
import com.pofo.backend.domain.project.dto.request.ProjectUpdateRequest
import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse
import com.pofo.backend.domain.project.entity.Project
import com.pofo.backend.domain.project.exception.ProjectCreationException
import com.pofo.backend.domain.project.repository.ProjectRepository
import com.pofo.backend.domain.skill.entity.ProjectSkill
import com.pofo.backend.domain.skill.entity.Skill
import com.pofo.backend.domain.skill.repository.ProjectSkillRepository
import com.pofo.backend.domain.skill.service.SkillService
import com.pofo.backend.domain.tool.entity.ProjectTool
import com.pofo.backend.domain.tool.entity.Tool
import com.pofo.backend.domain.tool.repository.ProjectToolRepository
import com.pofo.backend.domain.tool.service.ToolService
import com.pofo.backend.domain.user.join.entity.User
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.DataAccessException
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.*
import kotlin.test.assertNotNull


@ExtendWith(MockKExtension::class)
open class ProjectServiceTest {

        private lateinit var projectService: ProjectService

        @MockK
        private lateinit var projectSkillRepository: ProjectSkillRepository

        @MockK
        private lateinit var projectToolRepository: ProjectToolRepository

        @MockK
        private lateinit var projectRepository: ProjectRepository

        @MockK
        private lateinit var projectMapper: ProjectMapper

        @MockK
        private lateinit var skillService: SkillService

        @MockK
        private lateinit var toolService: ToolService


        @MockK
        private lateinit var fileService: FileService

        @MockK
        private lateinit var mockUser: User

        @MockK
        private lateinit var mockProject: Project

        @MockK
        private lateinit var mockProjectResponse: ProjectDetailResponse

        private val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())  // ✅ LocalDate 변환 지원
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // 예상치 못한 필드 무시
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)  // ✅ LocalDate를 "yyyy-MM-dd"로 변환
        }


        private val startDate = LocalDate.of(2025, 1, 22)
        private val endDate = LocalDate.of(2025, 2, 14)


        @BeforeEach
        fun setUp() {
        // MockK 초기화
        MockKAnnotations.init(this)

        // ✅ `ProjectService`를 직접 초기화 (자동 주입 문제 해결)
        projectService = ProjectService(
        projectRepository = projectRepository,
        projectMapper = projectMapper,
        skillService = skillService,
        toolService = toolService,
        projectSkillRepository = projectSkillRepository,
        projectToolRepository = projectToolRepository,
        fileService = fileService
        )


        // ✅ mockUser 설정
        every { mockUser.id } returns 1L

        // ✅ mockProject 설정
        every { mockProject.id } returns 1L
        every { mockProject.name } returns "원두 주문 웹페이지"
        every { mockProject.startDate } returns startDate
        every { mockProject.endDate } returns endDate
        every { mockProject.memberCount } returns 6
        every { mockProject.position } returns "백엔드"
        every { mockProject.repositoryLink } returns "programmers@github.com"
        every { mockProject.description } returns "커피 원두를 주문할 수 있는 웹페이지"
        every { mockProject.imageUrl } returns "test.img"
        every { mockProject.user } returns mockUser
        every { mockProject.isDeleted } returns false

        // ✅ Mock Skill & Tool 객체 생성
        every { skillService.getSkillByName("Java") } returns Skill("Java")
        every { skillService.getSkillByName("Spring Boot") } returns Skill("Spring Boot")
        every { toolService.getToolByName("IntelliJ IDEA") } returns Tool("IntelliJ IDEA")
        every { toolService.getToolByName("Docker") } returns Tool("Docker")


        }

        private fun projectCreateRequest(): ProjectCreateRequest {
        return ProjectCreateRequest(
        name = "PoFo 프로젝트",
        startDate = startDate,
        endDate = endDate,
        memberCount = 5,
        position = "백엔드",
        repositoryLink = "testRepositoryLink",
        description = "프로젝트 설명",
        imageUrl = "sample.img",
        thumbnailPath = "",
        skills = listOf("Java", "Spring Boot"),
        tools = listOf("IntelliJ IDEA", "Docker")
        )
        }

        @Test
        @DisplayName("✅ 프로젝트 등록 성공")
        fun t1() {
        // Given
        val request = projectCreateRequest()
        val projectRequestJson = objectMapper.writeValueAsString(request)

        // ✅ Mock MultipartFile 생성 (썸네일 파일 업로드 테스트를 위해 필요)
        val mockFile = mockk<MultipartFile> {
        every { isEmpty } returns false
        every { originalFilename } returns "mock_thumbnail.jpg"
        }

        // ✅ 파일 업로드 Mock 설정 (실제 업로드 대신 경로 반환)
        val mockThumbnailPath = "mocked/path/to/thumbnail.jpg"
        every { fileService.uploadThumbnail(any()) } returns mockThumbnailPath

        // ✅ 실제 저장될 프로젝트 객체 (spy로 감싸기)
        val realProject = spyk(
        Project(
        user = mockUser,
        name = request.name,
        startDate = request.startDate,
        endDate = request.endDate,
        memberCount = request.memberCount,
        position = request.position,
        repositoryLink = request.repositoryLink,
        description = request.description,
        imageUrl = request.imageUrl,
        thumbnailPath = mockThumbnailPath,
        isDeleted = false
        )
        )

        // ✅ 프로젝트 저장 Mocking
        every { projectRepository.save(any()) } returns realProject

        // ✅ Service 메서드 호출
        every { skillService.addProjectSkills(any(), any()) } just Runs
        every { toolService.addProjectTools(any(), any()) } just Runs


        val response = projectService.createProject(mockUser, projectRequestJson, mockFile)
        // Then
        assertNotNull(response) // ✅ 응답이 null이 아닌지 확인
        assertEquals(realProject.id, response.projectId) // ✅ 반환된 ID 검증
        verify { projectRepository.save(any()) } // ✅ 프로젝트 저장이 호출되었는지 검증
        verify { fileService.uploadThumbnail(any()) } // ✅ 썸네일 업로드가 호출되었는지 검증
        verify { skillService.addProjectSkills(realProject.id!!, request.skills) }
        verify { toolService.addProjectTools(realProject.id!!, request.tools) }
        }


        @Test
        @DisplayName("❌ 프로젝트 등록 실패 - 예외 발생")
        fun t2() {
        // Given
        val request = projectCreateRequest()
        val projectRequestJson = objectMapper.writeValueAsString(request)

        // ✅ Mock MultipartFile 생성 (실패 시에도 필요)
        val mockFile = mockk<MultipartFile> {
        every { isEmpty } returns false
        every { originalFilename } returns "mock_thumbnail.jpg"
        }

        // ✅ 프로젝트 저장 시 강제 예외 발생하도록 설정
        every { projectRepository.save(any()) } throws ProjectCreationException("400", "프로젝트 등록 중 오류가 발생했습니다.")

        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.createProject(mockUser, projectRequestJson, mockFile)
        }

        val rsData = exception.rsData
        assertEquals("400", rsData.resultCode)
        assertEquals("프로젝트 등록 중 오류가 발생했습니다.", rsData.message)
        }


        @Test
        @DisplayName("프로젝트 전체 조회 성공")
        fun t3() {
                every { mockProjectResponse.name } returns "원두 주문 웹페이지"
                every { mockProjectResponse.startDate } returns startDate
                every { mockProjectResponse.endDate } returns endDate
                every { mockProjectResponse.memberCount } returns 6
                every { mockProjectResponse.position } returns "백엔드"
                every { mockProjectResponse.repositoryLink } returns "programmers@github.com"
                every { mockProjectResponse.description } returns "커피 원두를 주문할 수 있는 웹페이지"
                every { mockProjectResponse.imageUrl } returns "test.img"

                every { mockProjectResponse.skills } returns listOf("Java", "Spring Boot")
                every { mockProjectResponse.tools } returns listOf("IntelliJ IDEA", "Docker")

                every { projectMapper.projectToProjectDetailResponse(mockProject) } returns mockProjectResponse

                val mockProjectList = listOf(mockProject)
                every { projectRepository.findByIsDeletedFalseOrderByIdDesc() } returns mockProjectList

                // When
                val response = projectService.detailAllProject(mockUser)

                // Then
                assertEquals(1, response.size)
                assertEquals("원두 주문 웹페이지", response[0].name)
                assertEquals(startDate, response[0].startDate)
                assertEquals(endDate, response[0].endDate)
                assertEquals(6, response[0].memberCount)
                assertEquals("백엔드", response[0].position)
                assertEquals("programmers@github.com", response[0].repositoryLink)
                assertEquals("커피 원두를 주문할 수 있는 웹페이지", response[0].description)
                assertEquals("test.img", response[0].imageUrl)

                assertEquals(2, response[0].skills.size)
                assertEquals(2, response[0].tools.size)
                assertTrue(response[0].skills.containsAll(listOf("Java", "Spring Boot")))
                assertTrue(response[0].tools.containsAll(listOf("IntelliJ IDEA", "Docker")))

                // Verify
                verify { projectRepository.findByIsDeletedFalseOrderByIdDesc() }
                verify { projectMapper.projectToProjectDetailResponse(mockProject) }
        }


        @Test
        @DisplayName("프로젝트 전체 조회 실패 - 프로젝트 없는 경우")
        fun t4() {
        // given
        val mockUser: User = mockk()
        every { projectRepository.findByIsDeletedFalseOrderByIdDesc() } returns emptyList()

        // when & then
        val exception = assertThrows<ProjectCreationException> {
        projectService.detailAllProject(mockUser)
        }

        // 예외 메시지 확인
        val rsData: RsData<Void> = exception.rsData
        assertEquals("404", rsData.resultCode)
        assertEquals("프로젝트가 존재하지 않습니다.", rsData.message)
        }


        @Test
        @DisplayName("프로젝트 전체 조회 실패 - 예기치 않은 오류 발생")
        fun t5() {
        // given
        every { projectRepository.findByIsDeletedFalseOrderByIdDesc() } throws RuntimeException("Unexpected error")

        // when & then
        val exception = assertThrows<ProjectCreationException> {
        projectService.detailAllProject(mockUser)
        }

        // 예외 메시지 확인
        val rsData: RsData<Void> = exception.rsData
        assertEquals("400", rsData.resultCode)
        assertEquals("프로젝트 전체 조회 중 오류가 발생했습니다.", rsData.message)
        }


        @Test
        @DisplayName("프로젝트 검색 성공 - 이름 또는 설명에 키워드 포함")
        fun t6() {
        // Given
        val keyword = "커피"

        // when & Then
        every { mockProjectResponse.name } returns "커피 원두 주문 시스템"
        every { mockProjectResponse.description } returns "원두를 주문할 수 있는 편리한 웹 서비스"
        every { projectRepository.searchByKeyword(keyword) } returns listOf(mockProject)
        every { projectMapper.projectToProjectDetailResponse(mockProject) } returns mockProjectResponse
        every { mockProject.user } returns mockUser

        // When
        val response = projectService.searchProjectsByKeyword(mockUser, keyword)

        // Then
        assertEquals(1, response.size)
        assertEquals("커피 원두 주문 시스템", response[0].name)
        assertEquals("원두를 주문할 수 있는 편리한 웹 서비스", response[0].description)

        // Verify
        verify(exactly = 1) { projectRepository.searchByKeyword(keyword) }
        verify(exactly = 1) { projectMapper.projectToProjectDetailResponse(mockProject) }
        }


        @Test
        @DisplayName("프로젝트 검색 실패 - 검색 결과 없음")
        fun t7() {
        // Given
        val keyword = "없는키워드"
        every { projectRepository.searchByKeyword(any()) } returns emptyList()

        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.searchProjectsByKeyword(mockUser, keyword)
        }

        val rsData: RsData<Void> = exception.rsData
        assertEquals("404", rsData.resultCode)
        assertEquals("검색된 프로젝트가 없습니다.", rsData.message)
        }

        @Test
        @DisplayName("프로젝트 검색 실패 - 데이터베이스 오류 발생")
        fun t8() {
        // Given
        val keyword = "커피"
        every { projectRepository.searchByKeyword(keyword) } throws object : DataAccessException("Database error") {}

        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.searchProjectsByKeyword(mockUser, keyword)
        }

        val rsData: RsData<Void> = exception.rsData
        assertEquals("500", rsData.resultCode)
        assertEquals("프로젝트 검색 중 데이터베이스 오류가 발생했습니다.", rsData.message)
        }

        @Test
        @DisplayName("프로젝트 검색 실패 - 예기치 않은 오류 발생")
        fun t9() {
        // Given
        val keyword = "커피"
        every { projectRepository.searchByKeyword(keyword) } throws RuntimeException("Unexpected error")

        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.searchProjectsByKeyword(mockUser, keyword)
        }

        val rsData: RsData<Void> = exception.rsData
        assertEquals("400", rsData.resultCode)
        assertEquals("프로젝트 검색 중 오류가 발생했습니다.", rsData.message)
        }


        @Test
        @DisplayName("프로젝트 단건 조회 성공")
        fun t10() {
        val projectId = 1L
        val ownUser: User = mockk()
        every { ownUser.id } returns 3L

        val project = Project(
        user = ownUser,
        name = "국내 여행 추천 웹페이지",
        startDate = startDate,
        endDate = endDate,
        memberCount = 4,
        position = "백엔드",
        repositoryLink = "koreaTravel@github.com",
        description = "국내 여행지 추천해주는 웹페이지입니다.",
        imageUrl = "travel.img"
        )

        project.projectSkills = mutableListOf(
        ProjectSkill(project, Skill("Java")),
        ProjectSkill(project, Skill("Spring Boot"))
        )

        project.projectTools = mutableListOf(
        ProjectTool(project, Tool("IntelliJ IDEA")),
        ProjectTool(project, Tool("Docker"))
        )

        val mockResponse: ProjectDetailResponse = mockk()

        // mockResponse의 필드 값 설정
        every { mockResponse.name } returns "국내 여행 추천 웹페이지"
        every { mockResponse.startDate } returns startDate
        every { mockResponse.endDate } returns endDate
        every { mockResponse.memberCount } returns 4
        every { mockResponse.position } returns "백엔드"
        every { mockResponse.repositoryLink } returns "koreaTravel@github.com"
        every { mockResponse.description } returns "국내 여행지 추천해주는 웹페이지입니다."
        every { mockResponse.imageUrl } returns "travel.img"
        every { mockResponse.skills } returns listOf("Java", "Spring Boot")
        every { mockResponse.tools } returns listOf("IntelliJ IDEA", "Docker")

        every { projectRepository.findById(projectId) } returns Optional.of(project)
        every { projectMapper.projectToProjectDetailResponse(project) } returns mockResponse

        // When
        val response = projectService.detailProject(projectId, ownUser)

        // Then
        assertNotNull(response)
        assertEquals("국내 여행 추천 웹페이지", response.name)
        assertEquals(startDate, response.startDate)
        assertEquals(endDate, response.endDate)
        assertEquals(4, response.memberCount)
        assertEquals("백엔드", response.position)
        assertEquals("koreaTravel@github.com", response.repositoryLink)
        assertEquals("국내 여행지 추천해주는 웹페이지입니다.", response.description)
        assertEquals("travel.img", response.imageUrl)

        // Skill 및 Tool 검증
        assertEquals(2, response.skills.size)
        assertEquals(2, response.tools.size)
        assertTrue(response.skills.containsAll(listOf("Java", "Spring Boot")))
        assertTrue(response.tools.containsAll(listOf("IntelliJ IDEA", "Docker")))

        // Verify
        verify(exactly = 1) { projectRepository.findById(projectId) }
        verify(exactly = 1) { projectMapper.projectToProjectDetailResponse(project) }
        }

        @Test
        @DisplayName("프로젝트 단건 조회 실패 - 프로젝트 없음")
        fun t11() {
        // Given
        val projectId = 1L
        val mockUser: User = mockk()
        every { projectRepository.findById(projectId) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.detailProject(projectId, mockUser)
        }

        val rsData: RsData<Void> = exception.rsData
        assertEquals("404", rsData.resultCode)
        assertEquals("해당 프로젝트를 찾을 수 없습니다.", rsData.message)
        }

        @Test
        @DisplayName("프로젝트 단건 조회 실패 - 예기치 못한 오류")
        fun t12() {
        val projectId = 1L
        val mockUser: User = mockk()
        every { projectRepository.findById(projectId) } throws RuntimeException("Unexpected Error")

        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.detailProject(projectId, mockUser)
        }

        val rsData: RsData<Void> = exception.rsData
        assertEquals("400", rsData.resultCode)
        assertEquals("프로젝트 단건 조회 중 오류가 발생했습니다.", rsData.message)
        }


        private fun projectUpdateRequest(): ProjectUpdateRequest {
        return ProjectUpdateRequest(
        name = "업데이트된 프로젝트",
        startDate = LocalDate.of(2025, 1, 25),
        endDate = LocalDate.of(2025, 2, 20),
        memberCount = 8,
        position = "프론트엔드",
        repositoryLink = "newRepoLink",
        description = "업데이트된 프로젝트 설명",
        imageUrl = "newImage.img",
        thumbnailPath = "mocked/path/to/thumbnail.jpg",
        skills = listOf("React", "TypeScript"),
        tools = listOf("vs Code", "Figma")
        )
        }

        @Test
        @Transactional
        @DisplayName("프로젝트 수정 성공")
        open fun t13() {
        // Given
        val projectId = 1L
        val updateRequest = projectUpdateRequest()
        val projectRequestJson = objectMapper.writeValueAsString(updateRequest)

        println("✅ 변환된 JSON: $projectRequestJson") // JSON 확인

        // ✅ Mock MultipartFile (비어 있지 않도록 설정)
        val mockFile: MultipartFile = mockk {
        every { isEmpty } returns false // ⚡ 썸네일 파일이 존재한다고 설정
        every { originalFilename } returns "mock_thumbnail.jpg"
        }
        val deleteThumbnail = true

        // realProject를 실제 객체로 사용
        val realProject = Project(
        user = mockUser,
        name = "기존 프로젝트",
        startDate = LocalDate.of(2025, 1, 1),
        endDate = LocalDate.of(2025, 2, 10),
        memberCount = 4,
        position = "백엔드",
        repositoryLink = "oldRepoLink",
        description = "기존 프로젝트 설명",
        imageUrl = "oldImage.img",
        thumbnailPath = "old/path/to/thumbnail.jpg"
        )

        every { projectRepository.findById(projectId) } returns Optional.of(realProject)
        every { projectRepository.save(any()) } returns realProject
        every { skillService.getSkillByName(any()) } returns Skill("React")
        every { toolService.getToolByName(any()) } returns Tool("VS Code")

        // ✅ 썸네일 업로드가 반드시 호출되도록 설정
        every { fileService.uploadThumbnail(any()) } returns "mocked/path/to/thumbnail.jpg"
        every { fileService.deleteFile(any()) } just Runs // ⚡ 파일 삭제 동작 추가

        every { skillService.updateProjectSkills(any(), any()) } just Runs
        every { toolService.updateProjectTools(any(), any()) } just Runs

        every { skillService.getProjectSkillNames(any()) } returns listOf("React", "TypeScript")
        every { toolService.getProjectToolNames(any()) } returns listOf("VS Code", "Figma")

        // When
        val response = try {
        projectService.updateProject(projectId, projectRequestJson, mockUser, mockFile, deleteThumbnail)
        } catch (e: ProjectCreationException) {
        println("❌ 예외 발생: ${e.message}")
        throw e
        }

        // Then
        assertNotNull(response)
        assertEquals(updateRequest.name, response.name)
        verify(exactly = 1) { projectRepository.save(any()) }
        verify(exactly = 1) { fileService.uploadThumbnail(any()) } // ✅ 썸네일 업로드가 실행되었는지 검증
        }


        @Test
        @DisplayName("프로젝트 수정 실패 - 프로젝트 없음")
        fun t14() {
        // Given
        val updateRequest = projectUpdateRequest()
        val projectRequestJson = objectMapper.writeValueAsString(updateRequest)
        val mockFile: MultipartFile = mockk()
        val deleteThumbnail = false

        every { projectRepository.findById(1L) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.updateProject(1L, projectRequestJson, mockUser, mockFile, deleteThumbnail)
        }

        val rsData = exception.rsData
        assertEquals("404", rsData.resultCode)
        assertEquals("해당 프로젝트를 찾을 수 없습니다.", rsData.message)
        }

        @Test
        @DisplayName("프로젝트 수정 실패 - 권한 없음")
        fun t15() {
                // Given
                val updateRequest = projectUpdateRequest()
                val projectRequestJson = objectMapper.writeValueAsString(updateRequest)
                val mockFile: MultipartFile = mockk()
                val deleteThumbnail = false

                // ✅ mockUser와 다른 사용자 설정
                val differentUser: User = mockk {
                        every { id } returns 999L // ✅ ID를 명확히 설정하여 비교 가능하게 만듦
                }
                val mockProject: Project = mockk {
                        every { user } returns differentUser // ✅ project.user를 differentUser로 설정
                }

                every { projectRepository.findById(1L) } returns Optional.of(mockProject)

                // When & Then
                val exception = assertThrows<ProjectCreationException> {
                        projectService.updateProject(1L, projectRequestJson, mockUser, mockFile, deleteThumbnail)
                }

                val rsData = exception.rsData
                assertEquals("403", rsData.resultCode) // ✅ 403이 정상적으로 발생하는지 확인
                assertEquals("프로젝트 수정할 권한이 없습니다.", rsData.message) // ✅ 메시지가 일치하는지 확인
        }



        @Test
        @DisplayName("프로젝트 삭제 성공 - 휴지통으로 이동")
        fun t16() {
        // Given
        val projectId = 1L
        val mockProject: Project = mockk(relaxed = true)

        every { projectRepository.findById(projectId) } returns Optional.of(mockProject)
        every { mockProject.user } returns mockUser
        every { projectRepository.save(mockProject) } returns mockProject

        // When
        assertDoesNotThrow { projectService.deleteProject(projectId, mockUser) }

        // Then
        verify { mockProject.isDeleted = true }
        verify { projectRepository.save(mockProject) }
        }

        @Test
        @DisplayName("프로젝트 삭제 실패 - 삭제 권한 없음")
        fun t17() {
        // Given
        val projectId = 1L
        val mockProject: Project = mockk(relaxed = true)
        val differentUser: User = mockk(relaxed = true)
        val mockUser: User = mockk(relaxed = true)

        every { projectRepository.findById(projectId) } returns Optional.of(mockProject)

        // ✅ User 객체에 명확하게 ID 설정
        every { differentUser.id } returns 999L
        every { mockUser.id } returns 100L
        every { mockProject.user } returns differentUser

        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.deleteProject(projectId, mockUser)
        }

        val rsData = exception.rsData
        assertEquals("403", rsData.resultCode)
        assertEquals("프로젝트 삭제 할 권한이 없습니다.", rsData.message)
        }


        @Test
        @DisplayName("프로젝트 삭제 실패 - 데이터베이스 오류 발생(휴지통 이동 실패)")
        fun t18() {
        // Given
        val projectId = 1L
        val mockProject: Project = mockk(relaxed = true)

        every { projectRepository.findById(projectId) } returns Optional.of(mockProject)
        every { mockProject.user } returns mockUser
        every { projectRepository.save(mockProject) } throws object : DataAccessException("DB 오류") {}


        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.deleteProject(projectId, mockUser)
        }

        val rsData = exception.rsData
        assertEquals("500", rsData.resultCode)
        assertEquals("프로젝트 삭제 중 데이터베이스 오류가 발생했습니다.", rsData.message)
        }

        @Test
        @DisplayName("다중 프로젝트 삭제 성공 - 휴지통으로 이동")
        fun t19() {
        // Given
        val projectIds = listOf(1L, 2L, 3L)

        val mockProject1: Project = mockk(relaxed = true)
        val mockProject2: Project = mockk(relaxed = true)
        val mockProject3: Project = mockk(relaxed = true)

        // ✅ projectRepository.findByIdInAndIsDeletedTrue()의 결과를 전체 프로젝트로 변경
        every { projectRepository.findAllById(projectIds) } returns listOf(mockProject1, mockProject2, mockProject3)

        every { mockProject1.user } returns mockUser
        every { mockProject2.user } returns mockUser
        every { mockProject3.user } returns mockUser

        every { projectRepository.saveAll(any<List<Project>>()) } returns listOf(
        mockProject1,
        mockProject2,
        mockProject3
        )

        // When
        assertDoesNotThrow { projectService.moveToTrash(projectIds, mockUser) }

        // Then
        verify { mockProject1.isDeleted = true }
        verify { mockProject2.isDeleted = true }
        verify { mockProject3.isDeleted = true }
        verify { projectRepository.saveAll(listOf(mockProject1, mockProject2, mockProject3)) }
        }


        @Test
        @DisplayName("휴지통 복원 성공")
        fun t20() {
        // Given
        val projectIds = listOf(1L, 2L)

        val mockProject1: Project = mockk(relaxed = true)
        val mockProject2: Project = mockk(relaxed = true)

        every { mockProject1.id } returns 1L
        every { mockProject2.id } returns 2L

        every { mockProject1.user } returns mockUser
        every { mockProject2.user } returns mockUser

        every { mockProject1.isDeleted } returns true
        every { mockProject2.isDeleted } returns true

        every { projectRepository.findByIdInAndIsDeletedTrue(any<List<Long>>()) } returns listOf(
        mockProject1,
        mockProject2
        )

        every { projectRepository.saveAll(any<List<Project>>()) } returns listOf(mockProject1, mockProject2)


        // When
        assertDoesNotThrow { projectService.restoreProjects(projectIds, mockUser) }

        // Then
        verify { mockProject1.isDeleted = false }
        verify { mockProject2.isDeleted = false }
        verify { projectRepository.saveAll(listOf(mockProject1, mockProject2)) }
        }

        @Test
        @DisplayName("프로젝트 영구 삭제 성공")
        fun t21() {
        // Given
        val projectIds = listOf(1L, 2L)

        val mockProject1: Project = mockk(relaxed = true)
        val mockProject2: Project = mockk(relaxed = true)

        every { mockProject1.id } returns 1L
        every { mockProject2.id } returns 2L

        every { mockProject1.user } returns mockUser
        every { mockProject2.user } returns mockUser

        every { mockProject1.isDeleted } returns true
        every { mockProject2.isDeleted } returns true

        every { projectRepository.findByIdInAndIsDeletedTrue(projectIds) } returns listOf(mockProject1, mockProject2)

        // 스킬 및 툴 삭제 Mock 설정
        every { skillService.deleteProjectSkills(projectIds) } returns Unit
        every { toolService.deleteProjectTools(projectIds) } returns Unit
        every { projectRepository.deleteAll(any()) } returns Unit

        // When
        assertDoesNotThrow { projectService.permanentlyDeleteProjects(projectIds, mockUser) }

        // Then
        verify { skillService.deleteProjectSkills(projectIds) }
        verify { toolService.deleteProjectTools(projectIds) }
        verify { projectRepository.deleteAll(listOf(mockProject1, mockProject2)) }
        }

        @Test
        @DisplayName("다중 프로젝트 삭제 실패 - 삭제 권한 없음")
        fun t22() {
        // Given
        val projectIds = listOf(1L, 2L)

        val mockProject1: Project = mockk(relaxed = true)
        val mockProject2: Project = mockk(relaxed = true)
        val differentUser: User = mockk(relaxed = true)
        val mockUser: User = mockk(relaxed = true)

        every { mockProject1.id } returns 1L
        every { mockProject2.id } returns 2L

        // ✅ User 객체에 명확하게 ID 설정
        every { differentUser.id } returns 999L // ❌ mockUser와 다른 사용자
        every { mockUser.id } returns 100L // ✅ 테스트 대상 사용자

        every { mockProject1.user } returns differentUser
        every { mockProject2.user } returns mockUser

        every { projectRepository.findAllById(projectIds) } returns listOf(mockProject1, mockProject2)

        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.moveToTrash(projectIds, mockUser)
        }

        val rsData = exception.rsData
        assertEquals("403", rsData.resultCode)
        assertEquals("프로젝트 삭제 할 권한이 없습니다.", rsData.message)
        }


        @Test
        @DisplayName("다중 프로젝트 삭제 실패 - 데이터베이스 오류 발생")
        fun t23() {
        // Given
        val projectIds = listOf(1L, 2L)

        val mockProject1: Project = mockk(relaxed = true)
        val mockProject2: Project = mockk(relaxed = true)

        every { mockProject1.id } returns 1L
        every { mockProject2.id } returns 2L

        every { projectRepository.findAllById(projectIds) } returns listOf(mockProject1, mockProject2)
        every { mockProject1.user } returns mockUser
        every { mockProject2.user } returns mockUser

        // DB 삭제 실패 발생
        every { projectRepository.saveAll(any<List<Project>>()) } throws object : DataAccessException("DB 오류") {}

        // When & Then
        val exception = assertThrows<ProjectCreationException> {
        projectService.moveToTrash(projectIds, mockUser)
        }

        val rsData = exception.rsData
        assertEquals("500", rsData.resultCode)
        assertEquals("프로젝트 삭제 중 데이터베이스 오류가 발생했습니다.", rsData.message)
        }

        }
        */
