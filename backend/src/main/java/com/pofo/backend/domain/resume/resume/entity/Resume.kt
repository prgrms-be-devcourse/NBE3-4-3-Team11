package com.pofo.backend.domain.resume.resume.entity

import com.pofo.backend.common.jpa.entity.BaseTime
import com.pofo.backend.domain.resume.activity.activity.entity.Activity
import com.pofo.backend.domain.resume.course.entity.Course
import com.pofo.backend.domain.resume.education.entity.Education
import com.pofo.backend.domain.resume.experience.entity.Experience
import com.pofo.backend.domain.resume.language.entity.Language
import com.pofo.backend.domain.resume.license.entity.License
import com.pofo.backend.domain.skill.entity.ResumeSkill
import com.pofo.backend.domain.tool.entity.ResumeTool
import com.pofo.backend.domain.user.join.entity.User
import jakarta.persistence.*
import lombok.*
import java.time.LocalDate

@Getter
@Entity
@Table(name = "resumes")
class Resume(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) var user: User,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var birth: LocalDate,

    @Column(nullable = false)
    var number: String,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false)
    var addressDetail: String,
    var gitAddress: String?,
    var blogAddress: String?,

    @OneToMany(mappedBy = "resume", cascade = [CascadeType.ALL], orphanRemoval = true)
    val activities: Set<Activity> = HashSet(),

    @OneToMany(mappedBy = "resume", cascade = [CascadeType.ALL], orphanRemoval = true)
    val courses: Set<Course> = HashSet(),

    @OneToMany(mappedBy = "resume", cascade = [CascadeType.ALL], orphanRemoval = true)
    val experiences: Set<Experience> = HashSet(),

    @OneToMany(mappedBy = "resume", cascade = [CascadeType.ALL], orphanRemoval = true)
    val educations: Set<Education> = HashSet(),

    @OneToMany(mappedBy = "resume", cascade = [CascadeType.ALL], orphanRemoval = true)
    val licenses: Set<License> = HashSet(),

    @OneToMany(mappedBy = "resume", cascade = [CascadeType.ALL], orphanRemoval = true)
    val languages: Set<Language> = HashSet(),

    @OneToMany(mappedBy = "resume", cascade = [CascadeType.ALL], orphanRemoval = true)
    val resumeTools: Set<ResumeTool> = HashSet(),

    @OneToMany(mappedBy = "resume", cascade = [CascadeType.ALL], orphanRemoval = true)
    val resumeSkills: Set<ResumeSkill> = HashSet(),
) : BaseTime()
