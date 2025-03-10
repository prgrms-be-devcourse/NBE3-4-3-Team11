package com.pofo.backend.domain.admin.login.service

import com.pofo.backend.domain.admin.login.entitiy.Admin
import com.pofo.backend.domain.admin.login.entitiy.AdminLoginHistory
import com.pofo.backend.domain.admin.login.repository.AdminRepository
import com.pofo.backend.domain.admin.login.repository.AdminLoginHistoryRepository
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class AdminService(
    private val adminRepository: AdminRepository,
    private val adminLoginHistoryRepository: AdminLoginHistoryRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    @Value("\${ADMIN_USERNAME:admin}")
    private lateinit var adminUsername: String

    @Value("\${ADMIN_PASSWORD:admin_password}")
    private lateinit var adminPassword: String

    @PostConstruct
    fun initializeAdminUser() {
        if (adminRepository.findByUsername(adminUsername).isEmpty) {
            // 기본 생성자를 사용하고, 프로퍼티 접근 구문으로 값 할당
            val admin = Admin()
            admin.username = adminUsername
            admin.password = passwordEncoder.encode(adminPassword)
            admin.status = Admin.Status.ACTIVE
            admin.failureCount = 0
            adminRepository.save(admin)

            val loginHistory = AdminLoginHistory()
            loginHistory.admin = admin
            loginHistory.loginStatus = AdminLoginHistory.SUCCESS
            loginHistory.failureCount = 0
            adminLoginHistoryRepository.save(loginHistory)
        }
    }

    @Transactional
    fun recordLoginFailure(username: String) {
        val optionalAdmin: Optional<Admin> = adminRepository.findByUsername(username)
        if (optionalAdmin.isPresent) {
            val admin = optionalAdmin.get()
            if (admin.failureCount >= 5) return

            val newFailureCount = admin.failureCount + 1
            admin.failureCount = newFailureCount

            if (newFailureCount >= 5) {
                admin.status = Admin.Status.INACTIVE
            }
            adminRepository.save(admin)

            val loginHistory = AdminLoginHistory()
            loginHistory.admin = admin
            loginHistory.loginStatus = AdminLoginHistory.FAILED
            loginHistory.failureCount = newFailureCount
            adminLoginHistoryRepository.save(loginHistory)
        }
    }

    @Transactional
    fun recordLoginSuccess(username: String) {
        val optionalAdmin: Optional<Admin> = adminRepository.findByUsername(username)
        if (optionalAdmin.isPresent) {
            val admin = optionalAdmin.get()
            admin.failureCount = 0
            adminRepository.save(admin)

            val loginHistory = AdminLoginHistory()
            loginHistory.admin = admin
            loginHistory.loginStatus = AdminLoginHistory.SUCCESS
            loginHistory.failureCount = 0
            adminLoginHistoryRepository.save(loginHistory)
        }
    }

    fun findByUsername(username: String): Optional<Admin> {
        return adminRepository.findByUsername(username)
    }
}
