package com.haco.shop.infrastructure.jooq.repository

import com.haco.shop.infrastructure.jooq.generated.tables.Member.Companion.MEMBER
import com.haco.shop.infrastructure.jooq.generated.tables.pojos.Member
import com.haco.shop.infrastructure.utils.logger.logger
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class MemberJooqRepository(
    private val dslContext: DSLContext
) {

    fun findById(id: Long): Member? {
        return try {
            dslContext
                .selectFrom(MEMBER)
                .where(MEMBER.ID.eq(id))
                .fetchOneInto(Member::class.java)
        } catch (e: Exception) {
            logger.error("회원 조회 중 오류 발생 - ID: {}, 오류: {}", id, e.message, e)
            null
        }
    }

    fun findByEmail(email: String): Member? {
        return try {
            dslContext
                .selectFrom(MEMBER)
                .where(MEMBER.EMAIL.eq(email))
                .fetchOneInto(Member::class.java)
        } catch (e: Exception) {
            logger.error("이메일로 회원 조회 중 오류 발생 - Email: {}, 오류: {}", email, e.message, e)
            null
        }
    }

    fun findAll(): List<Member> {
        return try {
            dslContext
                .selectFrom(MEMBER)
                .fetchInto(Member::class.java)
        } catch (e: Exception) {
            logger.error("모든 회원 조회 중 오류 발생 - 오류: {}", e.message, e)
            emptyList()
        }
    }

    fun save(member: Member): Member? {
        return try {
            dslContext
                .insertInto(MEMBER)
                .set(MEMBER.EMAIL, member.email)
                .set(MEMBER.NICKNAME, member.nickname)
                .set(MEMBER.PROVIDER, member.provider)
                .execute()

            findByEmail(member.email!!)
        } catch (e: Exception) {
            logger.error("회원 저장 중 오류 발생 - Email: {}, 오류: {}", member.email, e.message, e)
            null
        }
    }

    fun update(member: Member): Member? {
        logger.debug("JOOQ를 사용하여 회원 수정 - ID: {}", member.id)
        
        return try {
            dslContext.update(MEMBER)
                .set(MEMBER.EMAIL, member.email)
                .set(MEMBER.NICKNAME, member.nickname)
                .set(MEMBER.PROVIDER, member.provider)
                .where(MEMBER.ID.eq(member.id))
                .execute()
            
            // 수정 후 조회하여 반환
            findById(member.id!!)
        } catch (e: Exception) {
            logger.error("회원 수정 중 오류 발생 - ID: {}, 오류: {}", member.id, e.message, e)
            null
        }
    }

    fun deleteById(id: Long): Boolean {
        return try {
            val deletedCount = dslContext
                .deleteFrom(MEMBER)
                .where(MEMBER.ID.eq(id))
                .execute()
            
            deletedCount > 0
        } catch (e: Exception) {
            logger.error("회원 삭제 중 오류 발생 - ID: {}, 오류: {}", id, e.message, e)
            false
        }
    }
}