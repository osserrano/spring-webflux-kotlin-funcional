package com.osserco.springwebfluxkotlinfuncional.handler

import com.osserco.springwebfluxkotlinfuncional.model.CatDto
import com.osserco.springwebfluxkotlinfuncional.model.toDto
import com.osserco.springwebfluxkotlinfuncional.model.toEntity
import com.osserco.springwebfluxkotlinfuncional.repository.CatRepository
import kotlinx.coroutines.flow.map
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class CatHandler(private val catRepository: CatRepository) {

    suspend fun getAll(req: ServerRequest): ServerResponse{
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyAndAwait(
                catRepository.findAll().map{it.toDto()}
            )
    }

    suspend fun getById(req: ServerRequest): ServerResponse{
        val id=Integer.parseInt(req.pathVariable("id"))
        val existingCat=catRepository.findById(id.toLong())

        return existingCat?.let {
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(it)
        }?: ServerResponse.notFound().buildAndAwait()
    }

    suspend fun add(req: ServerRequest) :ServerResponse{
        val receivedCat = req.awaitBodyOrNull(CatDto::class)
        return receivedCat?.let {
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(
                    catRepository.save(it.toEntity()).toDto()
                )
        }?: ServerResponse.badRequest().buildAndAwait()
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val id = req.pathVariable("id")
        val receivedCat = req.awaitBodyOrNull(CatDto::class)
            ?: return ServerResponse.badRequest().buildAndAwait()
        val existingCat = catRepository.findById(id.toLong())
            ?: return ServerResponse.notFound().buildAndAwait()
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                catRepository.save(
                    receivedCat.toEntity().copy(id = existingCat.id)
                ).toDto()
            )
    }

    suspend fun delete(req: ServerRequest) :ServerResponse{
        val id = req.pathVariable("id")
        return if(catRepository.existsById(id.toLong())){
            catRepository.deleteById(id.toLong())
            ServerResponse.noContent().buildAndAwait()
        }else{
            ServerResponse.notFound().buildAndAwait()
        }
    }
}