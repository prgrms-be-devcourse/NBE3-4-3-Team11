package com.pofo.backend.domain.project.repository;

import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.user.join.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ProjectRepository : JpaRepository<Project, Long> {

   fun findByIsDeletedFalseOrderByIdDesc():  List<Project>

   @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(CAST(p.description AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
   fun searchByKeyword(@Param("keyword") keyword: String): List<Project>

   fun findByUserAndIsDeletedTrue(user: User): List<Project>


   fun findByIdInAndIsDeletedTrue(projectIds: List<Long> ) : List<Project>
}
