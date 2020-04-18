package bsu.smart.home.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.Column
import javax.validation.constraints.NotBlank

@Entity
@Table(
    name = "temperature",
    uniqueConstraints = [UniqueConstraint(name = "name", columnNames = ["name"])]
)
data class Temperature (
    @Id
    @JsonIgnore
    @GeneratedValue
    val id: Long? = null,

    @Column(columnDefinition = "BINARY(16)")
    var guid: UUID? = null,

    @get:NotBlank
    var name: String? = null,

    var temperatureValue: Int = DEFAULT_TEMPERATURE_VALUE,

    var status: Boolean = false
): Serializable {
    companion object {
        private const val DEFAULT_TEMPERATURE_VALUE = 20

        private const val serialVersionUID = 18050923851891936L
    }
}