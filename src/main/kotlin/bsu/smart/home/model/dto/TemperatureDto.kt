package bsu.smart.home.model.dto

import bsu.smart.home.model.Temperature
import java.io.Serializable
import java.util.UUID

data class TemperatureDto(
    var guid: UUID? = null,
    var name: String? = null,
    var temperatureValue: Int? = null,
    var status: Boolean = false,
    var roomGuid: UUID? = null
) : Serializable {

    companion object {
        fun toTemperature(temperatureDto: TemperatureDto) = Temperature().apply {
            guid = temperatureDto.guid
            name = temperatureDto.name
            temperatureValue = temperatureDto.temperatureValue ?: DEFAULT_TEMPERATURE_VALUE
            status = temperatureDto.status
            roomGuid = temperatureDto.roomGuid
        }

        private const val DEFAULT_TEMPERATURE_VALUE = 20
        private const val serialVersionUID = 18050923851891936L
    }
}