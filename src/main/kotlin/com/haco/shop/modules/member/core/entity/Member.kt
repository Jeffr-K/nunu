package com.haco.shop.modules.member.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column
    val email: String = "",

    @Column
    val nickname: String = "",

    @Column
    val profileImage: String? = null,

    @Column
    @Enumerated(EnumType.STRING)
    val provider: SocialProvider = SocialProvider.KAKAO,

    @Column
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    val updatedAt: LocalDateTime = LocalDateTime.now()
)