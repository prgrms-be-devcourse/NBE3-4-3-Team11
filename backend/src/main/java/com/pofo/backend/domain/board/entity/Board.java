
package com.pofo.backend.domain.board.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;


@Entity
@Getter
@Setter // ✅ Lombok의 Setter 추가 (필드 값을 변경할 수 있도록 설정)
@NoArgsConstructor
@Table(name = "boards")
public class Board extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // ✅ private → protected 변경

    @Column(nullable = false, length = 100)
    private String title; // ✅ private → protected 변경

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // ✅ private → protected 변경

    public Board(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    // ✅ 명시적 getter 추가 (Kotlin에서 접근 가능하도록)
    public User getUser() {
        return this.user;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    // ✅ 명시적 setter 추가 (Kotlin에서 값 변경 가능하도록 설정)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }


}