package com.osserco.springwebfluxkotlinfuncional.repository

import com.osserco.springwebfluxkotlinfuncional.model.Cat
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CatRepository: CoroutineCrudRepository<Cat, Long> {
    override fun findAll(): Flow<Cat>
    override suspend fun findById(id: Long): Cat?
    override suspend fun existsById(id: Long): Boolean
    override suspend fun <S : Cat> save(entity: S): Cat
    override suspend fun deleteById(id: Long)
}