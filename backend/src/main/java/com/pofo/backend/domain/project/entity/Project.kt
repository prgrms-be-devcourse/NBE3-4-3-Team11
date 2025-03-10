package com.pofo.backend.domain.project.entity

import com.fasterxml.jackson.annotation.JsonProperty
import com.pofo.backend.common.jpa.entity.BaseTime
import com.pofo.backend.domain.skill.entity.ProjectSkill
import com.pofo.backend.domain.tool.entity.ProjectTool
import com.pofo.backend.domain.user.join.entity.User
import jakarta.persistence.*
import java.time.LocalDate


@Entity
@Table(name = "projects")
class Project(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User?,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var startDate: LocalDate,

    @Column(nullable = false)
    var endDate: LocalDate,

    @Column(nullable = false)
    var memberCount: Int,

    @Column(nullable = false)
    var position: String,

    var repositoryLink: String? =null,

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    var description: String,

    @Column(nullable = false)
    var imageUrl: String,

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var thumbnailPath: String? =null,

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
    val projectTools: MutableList<ProjectTool> = mutableListOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
    val projectSkills: MutableList<ProjectSkill> =mutableListOf(),

    @Column(nullable = false)
    var isDeleted: Boolean = false // 기본값 false: 활성화, true: 휴지통 상태
) : BaseTime() {

    // ✅ JPA 기본 생성자 추가
    constructor() : this(
        user = null,
        name = "",
        startDate = LocalDate.now(),
        endDate = LocalDate.now(),
        memberCount = 0,
        position = "",
        repositoryLink = null,
        description = "",
        imageUrl = "",
        thumbnailPath = null,
        projectTools = mutableListOf(),
        projectSkills = mutableListOf(),
        isDeleted = false
    )

    fun updateBasicInfo(
        name: String?, startDate: LocalDate?, endDate: LocalDate?, memberCount: Int?,
        position: String?, repositoryLink: String?, description: String?, imageUrl: String?
    ) {
        this.name = name ?: this.name
        this.startDate = startDate ?: this.startDate
        this.endDate = endDate ?: this.endDate
        this.memberCount = memberCount ?: this.memberCount
        this.position = position ?: this.position
        this.repositoryLink = repositoryLink ?:this.repositoryLink
        this.description = description ?: this.description
        this.imageUrl = imageUrl ?: this.imageUrl
    }

}
