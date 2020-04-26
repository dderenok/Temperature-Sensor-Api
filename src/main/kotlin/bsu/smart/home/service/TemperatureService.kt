package bsu.smart.home.service

import bsu.smart.home.config.exception.TemperatureNameException
import bsu.smart.home.config.exception.TemperatureNotFoundException
import bsu.smart.home.config.exception.TemperatureValueException
import bsu.smart.home.model.Temperature
import bsu.smart.home.model.Temperature.Companion.DEFAULT_TEMPERATURE_VALUE
import bsu.smart.home.model.dto.TemperatureDto
import bsu.smart.home.model.dto.TemperatureDto.Companion.toTemperature
import bsu.smart.home.model.response.DeleteResponse
import bsu.smart.home.repository.TemperatureRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.UUID.randomUUID
import javax.transaction.Transactional

@Service
class TemperatureService(
    private var temperatureRepository: TemperatureRepository,
    @Value("\${temperature.create.exchange}") private val createTemperatureExchange: String,
    @Value("\${temperature.delete.exchange}") private val deleteTemperatureExchange: String
) {
    @Autowired
    private lateinit var rabbitTemplate: RabbitTemplate

    fun TemperatureDto.createNotification() = rabbitTemplate.apply {
        setExchange(createTemperatureExchange)
    }.convertAndSend(this)

    fun deleteNotification(guid: UUID) = rabbitTemplate.apply {
        setExchange(deleteTemperatureExchange)
    }.convertAndSend(guid.toString())

    private fun sendRabbitMqNotification(exchange: String) = rabbitTemplate.apply {
        setExchange(exchange)
    }.convertAndSend(this)

    fun findAllTemperatures() =
            temperatureRepository.findAll()

    fun findTemperature(guid: UUID) =
            temperatureRepository.findByGuid(guid) ?:
            throw TemperatureNotFoundException(temperatureNotFoundMessage("guid", guid.toString()))

    fun findTemperatureByName(name: String) =
            temperatureRepository.findByName(name) ?:
                    throw TemperatureNameException(temperatureNotFoundMessage("name", name))

    @Transactional
    fun createTemperature(temperatureDto: TemperatureDto): Temperature {
        temperatureValueIsCorrect(temperatureDto.temperatureValue)
        temperatureDto.name?.let {
            if (!checkNameUnique(it) && it.isNotEmpty())
                throw TemperatureNameException(temperatureNameUniqueMessage(it))
        } ?: throw TemperatureNameException(temperatureNullNameMessage())
        temperatureDto.apply {
            guid = randomUUID()
        }.createNotification()

        return toTemperature(temperatureDto)
                .saveTemperature()
    }

    @Transactional
    fun updateStatus(guid: UUID) = temperatureRepository.findByGuid(guid)?.let {
        temperatureRepository.save(it.apply {
            status = !status
        })
    } ?: throw TemperatureNotFoundException(temperatureNotFoundMessage("guid", guid.toString()))

    @Transactional
    fun updateTemperature(guid: UUID, temperatureDto: TemperatureDto) = temperatureRepository.findByGuid(guid)?.let {
        temperatureDto.name?.let { name ->
            if (!checkNameUnique(name))
                throw TemperatureNameException(temperatureNameUniqueMessage((name)))
        }
        temperatureValueIsCorrect(temperatureDto.temperatureValue)

        it.apply {
            temperatureDto.name?.let { tempName -> name = tempName }
            status = temperatureDto.status
            temperatureValue = temperatureDto.temperatureValue ?: DEFAULT_TEMPERATURE_VALUE
        }.saveTemperature()
    } ?: throw TemperatureNotFoundException(temperatureNotFoundMessage("guid", guid.toString()))

    @Transactional
    fun deleteTemperature(guid: UUID) = temperatureRepository.findByGuid(guid)?.let {
        temperatureRepository.deleteByGuid(guid).run {
            ResponseEntity(DeleteResponse(temperatureDeleteMessage(guid.toString())), OK)
        }
        deleteNotification(guid)
    } ?: throw TemperatureNotFoundException(temperatureNotFoundMessage("guid", guid.toString()))

    fun checkNameUnique(temperatureName: String) = !temperatureRepository.existsByName(temperatureName)

    fun temperatureValueIsCorrect(value: Int?) {
        if (value == null || value !in RANGE_CORRECT_TEMPERATURE_VALUE) throw TemperatureValueException()
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