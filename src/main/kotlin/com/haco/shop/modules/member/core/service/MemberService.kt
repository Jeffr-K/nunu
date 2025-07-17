package com.haco.shop.modules.member.core.service

import com.haco.shop.modules.member.domain.Member
import com.haco.shop.modules.member.domain.SocialProvider
import com.haco.shop.modules.member.infrastructure.adapter.oauth2.google.GoogleUserInfoResponse
import com.haco.shop.modules.member.infrastructure.adapter.oauth2.kakao.KakaoUserInfoResponse
import com.haco.shop.modules.member.infrastructure.adapter.oauth2.naver.NaverUserInfoResponse
import com.haco.shop.modules.member.infrastructure.repositories.MemberRepository
import org.springframework.stereotype.Service


@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun register(userInfo: KakaoUserInfoResponse) {
        if (userInfo.kakaoAccount?.email != null) {
            val existingMember = memberRepository.findMemberByEmail(userInfo.kakaoAccount.email)
            if (existingMember == null) {
                val newMember = Member(
                    email = userInfo.kakaoAccount.email,
                    nickname = userInfo.kakaoAccount.profile?.nickname ?: "Unknown",
                    profileImage = userInfo.kakaoAccount.profile?.profileImageUrl,
                    provider = SocialProvider.KAKAO
                )
                memberRepository.save(newMember)
            }
        }
    }

    fun registerWithNaver(userInfo: NaverUserInfoResponse): Member {
        val naverUser = userInfo.response

        if (naverUser.email != null) {
            val existingMember = memberRepository.findMemberByEmail(naverUser.email)
            if (existingMember == null) {
                val newMember = Member(
                    email = naverUser.email,
                    nickname = naverUser.nickname ?: naverUser.name ?: "Unknown",
                    profileImage = naverUser.profileImage,
                    provider = SocialProvider.NAVER
                )
                return memberRepository.save(newMember)
            }
            return existingMember
        }
        throw IllegalArgumentException("네이버 사용자 정보가 유효하지 않습니다.")
    }

    fun registerWithGoogle(userInfo: GoogleUserInfoResponse): Member {
        if (userInfo.email != null) {
            val existingMember = memberRepository.findMemberByEmail(userInfo.email)
            if (existingMember == null) {
                val newMember = Member(
                    email = userInfo.email,
                    nickname = userInfo.name ?: userInfo.givenName ?: "Unknown",
                    profileImage = userInfo.picture,
                    provider = SocialProvider.GOOGLE
                )
                return memberRepository.save(newMember)
            }
            return existingMember
        }
        throw IllegalArgumentException("구글 사용자 정보가 유효하지 않습니다.")
    }
}