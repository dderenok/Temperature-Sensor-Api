package bsu.smart.home.controller

import bsu.smart.home.model.Temperature
import bsu.smart.home.model.dto.TemperatureDto
import bsu.smart.home.service.TemperatureService
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestMethod.PUT
import org.springframework.web.bind.annotation.RequestMethod.DELETE
import java.util.*

@RestController
@RequestMapping("/temperature")
@CrossOrigin(origins = ["http://localhost:3000"], allowedHeaders = ["*"], methods = [GET, POST, PUT, DELETE])
class TemperatureController(
    private val temperatureService: TemperatureService
) {
    @GetMapping
    fun findAll(): List<Temperature> = temperatureService.findAllTemperatures()

    @GetMapping("/{guid}")
    fun find(@PathVariable guid: UUID) = temperatureService.findTemperature(guid)

    @GetMapping("/list")
    fun findAllByGuids(@RequestParam("guids") guids: List<UUID>) = temperatureService.findAllByGuids(guids)

    @GetMapping("/attach-available")
    fun findAttachAvailable() = temperatureService.findAvailableToAttach()

    @GetMapping("/filter")
    fun findTemperatureByName(@RequestParam("name") name: String) = temperatureService.findTemperatureByName(name)

    @PutMapping("/status/{guid}")
    fun updateTemperatureStatus(@PathVariable guid: UUID) = temperatureService.updateStatus(guid)

    @PutMapping("/{guid}")
    fun updateTemperature(@PathVariable guid: UUID, @RequestBody temperatureDto: TemperatureDto) =
            temperatureService.updateTemperature(guid, temperatureDto)

    @PostMapping
    fun createTemperature(@RequestBody temperature: TemperatureDto) = temperatureService.createTemperature(temperature)

    @DeleteMapping("/{guid}")
    fun deleteTemperature(@PathVariable guid: UUID) = temperatureService.deleteTemperature(guid)
}