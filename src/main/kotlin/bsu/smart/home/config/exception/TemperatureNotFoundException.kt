package bsu.smart.home.config.exception

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(code = NOT_FOUND)
class TemperatureNotFoundException(exceptionMessage: String = REASON) : RuntimeException(exceptionMessage) {

    companion object {
        const val REASON = "Temperature not found"

        private const val serialVersionUID = -3252845795981687666L
    }
}