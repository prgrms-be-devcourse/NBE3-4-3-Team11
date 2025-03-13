package com.pofo.backend.domain.resume.service

import com.pofo.backend.domain.resume.activity.activity.service.ActivityService
import com.pofo.backend.domain.resume.course.service.CourseService
import com.pofo.backend.domain.resume.education.entity.Education
import com.pofo.backend.domain.resume.education.service.EducationService
import com.pofo.backend.domain.resume.experience.entity.Experience
import com.pofo.backend.domain.resume.experience.service.ExperienceService
import com.pofo.backend.domain.resume.language.entity.Language
import com.pofo.backend.domain.resume.language.service.LanguageService
import com.pofo.backend.domain.resume.license.service.LicenseService
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest
import com.pofo.backend.domain.resume.resume.entity.Resume
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository
import com.pofo.backend.domain.resume.resume.service.ResumeService
import com.pofo.backend.domain.user.join.entity.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate
import java.util.Optional

@SpringBootTest
@AutoConfigureWebMvc
class ResumeServiceTest {

    @Autowired
    private lateinit var resumeService: ResumeService

    @MockitoBean
    private lateinit var resumeRepository: ResumeRepository

    @Mock
    private lateinit var activityService: ActivityService

    @Mock
    private lateinit var courseService: CourseService

    @Mock
    private lateinit var experienceService: ExperienceService

    @Mock
    private lateinit var educationService: EducationService

    @Mock
    private lateinit var licenseService: LicenseService

    @Mock
    private lateinit var languageService: LanguageService

    @Mock
    private lateinit var mockUser: User

    @Mock
    private lateinit var mockResume: Resume

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(mockUser.id).thenReturn(1L)
        whenever(mockResume.id).thenReturn(1L)
        whenever(mockResume.name).thenReturn("김상진")
        whenever(mockResume.email).thenReturn("prgrms@naver.com")
        whenever(mockResume.birth).thenReturn(LocalDate.of(2000, 11, 18))
        whenever(mockResume.number).thenReturn("010-1234-5678")
        whenever(mockResume.address).thenReturn("서울시 강남구")
        whenever(mockResume.addressDetail).thenReturn("역삼동")

        whenever(mockResume.user).thenReturn(mockUser)
    }

    private fun createResumeRequest(): ResumeCreateRequest {
        return ResumeCreateRequest(
            name = "김상진",
            birth = LocalDate.of(2000, 11, 18),
            number = "010-1234-5678",
            email = "prgrms@naver.com",
            address = "서울시 강남구",
            addressDetail = "역삼동",
            gitAddress = "https://github.com/kim",
            blogAddress = "https://kim.blog",
            activities = null,
            courses = null,
            experiences = null,
            educations = null,
            licenses = null,
            languages = null,
            skills = null,
            tools = null
        )
    }

    @Test
    @DisplayName("이력서 조회 성공")
    fun getResumeByUser_success() {
        whenever(resumeRepository.findByUser(mockUser)).thenReturn(Optional.of(mockResume))

        val result = resumeService.getResumeResponse(mockUser)

        assertEquals("김상진", result.name)
        assertEquals("prgrms@naver.com", result.email)
        verify(resumeRepository).findByUser(mockUser)
    }

    @Test
    @DisplayName("이력서 조회 실패 - 이력서 없음")
    fun getResumeByUser_notFound() {
        whenever(resumeRepository.findByUser(mockUser)).thenReturn(Optional.empty())

        assertThrows(ResumeCreationException::class.java) {
            resumeService.getResumeResponse(mockUser)
        }

        verify(resumeRepository).findByUser(mockUser)
    }

    @Test
    @DisplayName("이력서 생성 성공")
    fun createResume_success() {
        val request = createResumeRequest()
        whenever(resumeRepository.save(any(Resume::class.java))).thenReturn(mockResume)

        val result = resumeService.createResume(request, mockUser)

        assertNotNull(result)
        verify(resumeRepository).save(any(Resume::class.java))
        verifyNoInteractions(activityService, courseService, experienceService, educationService, licenseService, languageService)
    }

    @Test
    @DisplayName("이력서 수정 성공")
    fun updateResume_success() {
        val request = createResumeRequest()

        whenever(resumeRepository.findByUser(mockUser)).thenReturn(Optional.of(mockResume))
        whenever(resumeRepository.save(any(Resume::class.java))).thenReturn(mockResume)

        val result = resumeService.updateResume(request, mockUser)

        assertNotNull(result)
        verify(resumeRepository).findByUser(mockUser)
        verify(resumeRepository).save(any(Resume::class.java))
        verifyNoInteractions(activityService, courseService, experienceService, educationService, licenseService, languageService)
    }

    @Test
    @DisplayName("이력서 수정 실패")
    fun updateResume_noPermission() {
        val request = createResumeRequest()
        val differentUser = mock(User::class.java)
        whenever(resumeRepository.findByUser(differentUser)).thenReturn(Optional.of(mockResume))

        assertThrows(ResumeCreationException::class.java) {
            resumeService.updateResume(request, mockUser)
        }
    }

    @Test
    @DisplayName("이력서 삭제 성공")
    fun deleteResume_success() {
        whenever(resumeRepository.findByUser(mockUser)).thenReturn(Optional.of(mockResume))

        resumeService.deleteResume(mockUser)

        verify(resumeRepository).delete(mockResume)
    }

    @Test
    @DisplayName("이력서 삭제 실패")
    fun deleteResume_noPermission() {
        val differentUser = mock(User::class.java)
        whenever(resumeRepository.findByUser(differentUser)).thenReturn(Optional.of(mockResume))

        assertThrows(ResumeCreationException::class.java) {
            resumeService.deleteResume(mockUser)
        }
    }

    @Test
    @DisplayName("이력서 조회 성공 - 언어, 경력 정보 포함")
    fun getResumeByUser_withLanguages_and_experiences() {

        val language = Language(
            language = "영어",
            result = "900점",
            certifiedDate = LocalDate.of(2020, 5, 20),
            name = "토익",
            resume = mockResume
        )

        val experience = Experience(
            name = "Google",
            department = "Engineering",
            position = "Software Engineer",
            responsibility = "Developed backend services",
            startDate = LocalDate.of(2020, 5, 1),
            endDate = LocalDate.of(2022, 5, 1),
            resume = mockResume
        )

        val languages = setOf(language)
        val experiences = setOf(experience)

        whenever(mockResume.languages).thenReturn(languages)
        whenever(mockResume.experiences).thenReturn(experiences)
        whenever(resumeRepository.findByUser(mockUser)).thenReturn(Optional.of(mockResume))

        val result = resumeService.getResumeResponse(mockUser)

        assertNotNull(result)
        assertEquals("영어", result.languages.iterator().next().language)
        assertEquals("Google", result.experiences.iterator().next().name)
    }

}
