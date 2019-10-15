package io.axoniq.labs.chat

import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.messaging.interceptors.CorrelationDataInterceptor
import org.axonframework.spring.config.AxonConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


fun main(args: Array<String>) {
    SpringApplication.run(ChatScalingOutApplication::class.java, *args)
}

@SpringBootApplication
class ChatScalingOutApplication

@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
}

@Configuration
class AxonConfig{
//    @Bean
    fun commandBus(txManager: TransactionManager, axonConfiguration: AxonConfiguration): SimpleCommandBus {
        val commandBus = SimpleCommandBus.builder()
                .transactionManager(txManager)
                .messageMonitor(axonConfiguration.messageMonitor<CommandMessage<*>>(CommandBus::class.java, "commandBus"))
                .build()
        commandBus.registerHandlerInterceptor(
                CorrelationDataInterceptor<CommandMessage<*>>(axonConfiguration.correlationDataProviders())
        )
        return commandBus
    }
}
