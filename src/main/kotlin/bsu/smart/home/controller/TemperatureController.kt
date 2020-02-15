package bsu.smart.home.controller

import bsu.smart.home.model.Temperature
import bsu.smart.home.service.TemperatureService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/temperature")
class TemperatureController(
    private val temperatureService: TemperatureService
) {
    @GetMapping
    fun findAll(): List<Temperature> = temperatureService.findAllTemperatures()

    @GetMapping("/{guid}")
    fun find(@PathVariable guid: UUID) = temperatureService.findTemperature(guid)

    @GetMapping("/filter")
    fun findTemperatureByName(@RequestParam("name") name: String) = temperatureService.findTemperatureByName(name)

    @PutMapping("/status/{guid}")
    fun updateTemperatureStatus(@PathVariable guid: UUID) = temperatureService.updateStatus(guid)

    @PutMapping("/{guid}")
    fun updateTemperature(@PathVariable guid: UUID, @RequestBody temperature: Temperature) =
            temperatureService.updateTemperature(guid, temperature)

    @PostMapping
    fun createTemperature(@RequestBody temperature: Temperature) = temperatureService.createTemperature(temperature)

    @DeleteMapping("/{guid}")
    fun deleteTemperature(@PathVariable guid: UUID) = temperatureService.deleteLight(guid)
}