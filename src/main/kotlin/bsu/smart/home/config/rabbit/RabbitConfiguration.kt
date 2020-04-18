package bsu.smart.home.config.rabbit

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfiguration(
    @Value("\${spring.rabbitmq.host}") private val rabbitmqHost: String,
    @Value("\${temperature.create.exchange}") private val temperatureCreateExchange: String,
    @Value("\${temperature.create.queue}") private val temperatureCreateQueue: String,
    @Value("\${temperature.delete.exchange}") private val temperatureDeleteExchange: String,
    @Value("\${temperature.delete.queue}") private val temperatureDeleteQueue: String
) {
    @Bean
    fun connectionFactory() = CachingConnectionFactory(rabbitmqHost)

    @Bean
    fun jsonMessageConvertor(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun ampqAdmin() = RabbitAdmin(connectionFactory())

    @Bean
    fun rabbitTemplate() = RabbitTemplate(connectionFactory()).apply {
        messageConverter = jsonMessageConvertor()
    }

    @Bean
    fun temperatureCreateExchange() = FanoutExchange(temperatureCreateExchange)

    @Bean
    fun temperatureCreateQueue() = Queue(temperatureCreateQueue)

    @Bean
    fun temperatureDeleteExchange() = FanoutExchange(temperatureDeleteExchange)

    @Bean
    fun temperatureDeleteQueue() = Queue(temperatureDeleteQueue)

    @Bean
    fun bindCreatingQueues() = BindingBuilder.bind(temperatureCreateQueue()).to(temperatureCreateExchange())

    @Bean
    fun bindDeletionQueues() = BindingBuilder.bind(temperatureDeleteQueue()).to(temperatureDeleteExchange())
}