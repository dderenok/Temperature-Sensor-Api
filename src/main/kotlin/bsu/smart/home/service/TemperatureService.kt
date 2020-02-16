package bsu.smart.home.service

import bsu.smart.home.config.exception.TemperatureNameException
import bsu.smart.home.config.exception.TemperatureNotFoundException
import bsu.smart.home.config.exception.TemperatureValueException
import bsu.smart.home.model.Temperature
import bsu.smart.home.model.response.DeleteResponse
import bsu.smart.home.repository.TemperatureRepository
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.UUID.randomUUID
import javax.transaction.Transactional

@Service
class TemperatureService(
    private var temperatureRepository: TemperatureRepository
) {
    fun findAllTemperatures() =
            temperatureRepository.findAll()

    fun findTemperature(guid: UUID) =
            temperatureRepository.findByGuid(guid) ?:
            throw TemperatureNotFoundException(temperatureNotFoundMessage("guid", guid.toString()))

    fun findTemperatureByName(name: String) =
            temperatureRepository.findByName(name) ?:
                    throw TemperatureNameException(temperatureNotFoundMessage("name", name))

    @Transactional
    fun createTemperature(temperature: Temperature): Temperature {
        temperatureValueIsCorrect(temperature.temperatureValue)
        temperature.name?.let {
            if (!checkNameUnique(it) && it.isNotEmpty())
                throw TemperatureNameException(temperatureNameUniqueMessage(it))
        } ?: throw TemperatureNameException(temperatureNullNameMessage())

        temperature
                .apply { guid = randomUUID() }
                .saveTemperature()
        return temperature
    }

    @Transactional
    fun updateStatus(guid: UUID) = temperatureRepository.findByGuid(guid)?.let {
        temperatureRepository.save(it.apply {
            status = !status
        })
    } ?: throw TemperatureNotFoundException(temperatureNotFoundMessage("guid", guid.toString()))

    @Transactional
    fun updateTemperature(guid: UUID, temperature: Temperature) = temperatureRepository.findByGuid(guid)?.let {
        temperature.name?.let { name ->
            if (!checkNameUnique(name))
                throw TemperatureNameException(temperatureNameUniqueMessage((name)))
        }
        temperatureValueIsCorrect(temperature.temperatureValue)

        it.apply {
            temperature.name?.let { tempName -> name = tempName }
            status = temperature.status
            temperatureValue = temperature.temperatureValue
        }.saveTemperature()
    } ?: throw TemperatureNotFoundException(temperatureNotFoundMessage("guid", guid.toString()))

    @Transactional
    fun deleteTemperature(guid: UUID) = temperatureRepository.findByGuid(guid)?.let {
        temperatureRepository.deleteByGuid(guid).run {
            ResponseEntity(DeleteResponse(temperatureDeleteMessage(guid.toString())), OK)
        }
    } ?: throw TemperatureNotFoundException(temperatureNotFoundMessage("guid", guid.toString()))

    fun checkNameUnique(temperatureName: String) = !temperatureRepository.existsByName(temperatureName)

    fun temperatureValueIsCorrect(value: Int) {
        if (value !in RANGE_CORRECT_TEMPERATURE_VALUE) throw TemperatureValueException()
    }

    fun Temperature.saveTemperature() = temperatureRepository.save(this)

    companion object {
        private fun temperatureNotFoundMessage(element: String, value: String) =
                "Temperature with $element '$value' not found"
        private fun temperatureNameUniqueMessage(name: String) =
                "Temperature with such name $name already exist"
        private fun temperatureDeleteMessage(guid: String) =
                "Temperature with guid $guid successfully deleted"
        private fun temperatureNullNameMessage() =
                "Temperature name cannot be null"
        private val RANGE_CORRECT_TEMPERATURE_VALUE = 16..25
    }
}