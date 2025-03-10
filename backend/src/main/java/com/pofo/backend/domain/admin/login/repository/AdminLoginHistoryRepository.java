package com.pofo.backend.domain.admin.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLoginHistoryRepository extends JpaRepository<AdminLoginHistory, Long> {
    AdminLoginHistory findTopByAdminOrderByCreatedAtDesc(Admin admin);
}
