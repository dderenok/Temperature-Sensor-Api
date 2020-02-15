package bsu.smart.home.service

import bsu.smart.home.config.exception.TemperatureValueException
import bsu.smart.home.model.Temperature
import bsu.smart.home.repository.TemperatureRepository
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.UUID.randomUUID
import javax.persistence.NonUniqueResultException
import javax.transaction.Transactional

@Service
class TemperatureService(
    private var temperatureRepository: TemperatureRepository
) {
    fun findAllTemperatures() =
            temperatureRepository.findAll()

    fun findTemperature(guid: UUID) = temperatureRepository.findByGuid(guid)

    fun findTemperatureByName(name: String) = temperatureRepository.findByName(name)

    @Transactional
    fun createTemperature(temperature: Temperature) {
        temperatureValueIsCorrect(temperature.temperatureValue)
        temperature.name?.let {
            if (checkNameUnique(it)) temperatureRepository.save(
                    temperature.apply { guid = randomUUID() }
            )
            else throw NonUniqueResultException()
        }
    }

    @Transactional
    fun updateStatus(guid: UUID) = temperatureRepository.findByGuid(guid)?.let {
        temperatureRepository.save(it.apply {
            status = !status
        })
    }

    @Transactional
    fun updateTemperature(guid: UUID, light: Temperature) = temperatureRepository.findByGuid(guid)?.let {
        temperatureRepository.save(it.apply {
            name = light.name
            status = light.status
        })
    }

    @Transactional
    fun deleteLight(guid: UUID) = temperatureRepository.deleteByGuid(guid)

    fun checkNameUnique(lightName: String) = !temperatureRepository.existsByName(lightName)

    fun temperatureValueIsCorrect(value: Int) {
        if (value !in RANGE_CORRECT_TEMPERATURE_VALUE) throw TemperatureValueException()
    }

    companion object {
        private val RANGE_CORRECT_TEMPERATURE_VALUE = 16..24
    }
}