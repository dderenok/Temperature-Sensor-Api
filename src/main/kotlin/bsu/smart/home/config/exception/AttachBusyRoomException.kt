package bsu.smart.home.config.exception

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(code = BAD_REQUEST)
class AttachBusyRoomException(exceptionMessage: String = REASON) : RuntimeException(exceptionMessage) {

    companion object {
        const val REASON = "Temperature cannot be attach to room with attached another temperature sensor."
        private const val serialVersionUID = -8457953252981687666L
    }
}