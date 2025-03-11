package com.pofo.backend.domain.resume.license.service

import com.pofo.backend.domain.resume.license.dto.LicenseRequest
import com.pofo.backend.domain.resume.license.entity.License
import com.pofo.backend.domain.resume.license.repository.LicenseRepository
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository
import org.springframework.stereotype.Service

@Service
class LicenseService(
    private val licenseRepository: LicenseRepository,
    private val resumeRepository: ResumeRepository
) {

    fun addLicenses(resumeId: Long, licenses: List<LicenseRequest>) {
        val resume = resumeRepository.findById(resumeId)
            .orElseThrow { ResumeCreationException("이력서를 찾을 수 없습니다.") }

        val licenseEntities = licenses.map { licenseRequest ->
            License(
                name = licenseRequest.name,
                institution = licenseRequest.institution,
                certifiedDate = licenseRequest.certifiedDate,
                resume = resume
            )
        }

        licenseRepository.saveAll(licenseEntities)
    }

    fun updateLicenses(resumeId: Long, licenses: List<LicenseRequest>) {
        licenseRepository.deleteByResumeId(resumeId)
        addLicenses(resumeId, licenses)
    }
}
