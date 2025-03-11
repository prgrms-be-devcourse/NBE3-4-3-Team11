package com.pofo.backend.domain.resume.language.service

import com.pofo.backend.domain.resume.language.dto.LanguageRequest
import com.pofo.backend.domain.resume.language.entity.Language
import com.pofo.backend.domain.resume.language.repository.LanguageRepository
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository
import org.springframework.stereotype.Service

@Service
class LanguageService(
    private val languageRepository: LanguageRepository,
    private val resumeRepository: ResumeRepository
) {

    fun addLanguages(resumeId: Long, languages: List<LanguageRequest>) {
        val resume = resumeRepository.findById(resumeId)
            .orElseThrow { ResumeCreationException("이력서를 찾을 수 없습니다.") }

        val languageEntities = languages.map { languageRequest ->
            Language(
                language = languageRequest.language,
                result = languageRequest.result,
                certifiedDate = languageRequest.certifiedDate,
                name = languageRequest.name,
                resume = resume
            )
        }

        languageRepository.saveAll(languageEntities)
    }

    fun updateLanguages(resumeId: Long, languages: List<LanguageRequest>) {
        languageRepository.deleteByResumeId(resumeId)
        addLanguages(resumeId, languages)
    }
}
