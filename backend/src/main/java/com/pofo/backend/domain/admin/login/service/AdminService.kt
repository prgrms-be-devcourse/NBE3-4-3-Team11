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
            // Lombok 빌더를 이용하여 Admin 객체 생성
            val admin = Admin.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .status(Admin.Status.ACTIVE)
                .failureCount(0)
                .build()
            adminRepository.save(admin)

            // AdminLoginHistory 객체도 빌더로 생성
            val loginHistory = AdminLoginHistory.builder()
                .admin(admin)
                .loginStatus(AdminLoginHistory.SUCCESS)
                .failureCount(0)
                .build()
            adminLoginHistoryRepository.save(loginHistory)
        }
    }

    @Transactional
    fun recordLoginFailure(username: String) {
        val optionalAdmin = adminRepository.findByUsername(username)
        if (optionalAdmin.isPresent) {
            val admin = optionalAdmin.get()
            // 이미 실패횟수가 5 이상이면 추가 실패 기록 없이 종료
            if (admin.failureCount >= 5) return

            val newFailureCount = admin.failureCount + 1
            admin.failureCount = newFailureCount

            if (newFailureCount >= 5) {
                admin.status = Admin.Status.INACTIVE
            }
            adminRepository.save(admin)

            val loginHistory = AdminLoginHistory.builder()
                .admin(admin)
                .loginStatus(AdminLoginHistory.FAILED)
                .failureCount(newFailureCount)
                .build()
            adminLoginHistoryRepository.save(loginHistory)
        }
    }

    @Transactional
    fun recordLoginSuccess(username: String) {
        val optionalAdmin = adminRepository.findByUsername(username)
        if (optionalAdmin.isPresent) {
            val admin = optionalAdmin.get()
            admin.failureCount = 0
            adminRepository.save(admin)

            val loginHistory = AdminLoginHistory.builder()
                .admin(admin)
                .loginStatus(AdminLoginHistory.SUCCESS)
                .failureCount(0)
                .build()
            adminLoginHistoryRepository.save(loginHistory)
        }
    }

    fun findByUsername(username: String): Optional<Admin> {
        return adminRepository.findByUsername(username)
    }
}
