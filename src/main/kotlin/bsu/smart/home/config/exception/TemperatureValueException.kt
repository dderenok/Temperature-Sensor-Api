package bsu.smart.home.config.exception

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(code = BAD_REQUEST)
class TemperatureValueException(exceptionMessage: String = REASON) : RuntimeException(exceptionMessage) {

    companion object {
        const val REASON = "Temperature value is out of range"

        private const val serialVersionUID = -3252845795981687666L
    }
}