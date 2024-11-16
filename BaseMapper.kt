package com.mayad.instagram.core.domain.interactors

interface BaseMapper<DTO, DOMAIN> {
    fun mapToDomain(dto: DTO): DOMAIN
}