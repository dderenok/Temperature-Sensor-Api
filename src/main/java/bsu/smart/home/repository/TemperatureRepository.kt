package bsu.smart.home.repository

import bsu.smart.home.model.Temperature
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface TemperatureRepository : CrudRepository<Temperature, Long> {
    override fun findAll(): List<Temperature>

    fun findByGuid(guid: UUID): Temperature?

    fun findByName(name: String): Temperature?

    fun existsByName(name: String): Boolean

    fun deleteByGuid(guid: UUID)
}