package bsu.smart.home.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
@Table
data class Temperature (
    @Id
    @JsonIgnore
    @GeneratedValue
    val id: Long? = null,

    @Column(columnDefinition = "BINARY(16)")
    var guid: UUID? = null,

    var name: String? = null,

    var temperatureValue: Int? = DEFAULT_TEMPERATURE_VALUE,

    var status: Boolean = false
) {
    companion object {
        private const val DEFAULT_TEMPERATURE_VALUE = 20
    }
}