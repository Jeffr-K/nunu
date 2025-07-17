package com.haco.shop.modules.member.infrastructure.repositories

import com.haco.shop.modules.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findMemberByEmail(email: String): Member?
}