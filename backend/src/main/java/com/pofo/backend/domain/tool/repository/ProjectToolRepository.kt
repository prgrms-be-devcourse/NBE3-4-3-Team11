package com.pofo.backend.domain.tool.repository;

import com.pofo.backend.domain.tool.entity.ProjectTool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ProjectToolRepository : JpaRepository<ProjectTool, Long> {
    fun deleteByProjectId(projectId: Long)
    fun findByProjectId(projectId: Long):  List<ProjectTool>
    fun deleteByProjectIdIn(projectIds: List<Long> )
}

