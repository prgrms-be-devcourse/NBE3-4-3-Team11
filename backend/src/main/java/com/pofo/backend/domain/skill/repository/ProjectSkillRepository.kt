package com.pofo.backend.domain.skill.repository;

import com.pofo.backend.domain.skill.entity.ProjectSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
interface ProjectSkillRepository : JpaRepository<ProjectSkill, Long> {
    fun deleteByProjectId(projectId: Long)
    fun findByProjectId(projectId: Long) : List<ProjectSkill>
    fun deleteByProjectIdIn(projectIds: List<Long>)
}
