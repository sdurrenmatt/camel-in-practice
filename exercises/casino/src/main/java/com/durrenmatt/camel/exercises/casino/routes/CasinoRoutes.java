package com.durrenmatt.camel.exercises.casino.routes;

import com.durrenmatt.camel.exercises.casino.model.SpinEvent;
import com.durrenmatt.camel.exercises.casino.model.SpinEventType;
import com.durrenmatt.camel.exercises.casino.model.SpinOutcome;
import com.durrenmatt.camel.exercises.casino.model.SpinResponse;
import com.durrenmatt.camel.exercises.casino.services.CasinoRuleEngine;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CasinoRoutes extends RouteBuilder {

    @Override
    public void configure() {
        rest("/casino")
            .post("/spin")
            .to("direct:spin");

        from("direct:spin")
            .setProperty("spinId", simple("${uuid(random)}"))

            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
            .setBody(exchange -> new SpinResponse(exchange.getProperty("spinId", String.class)))

            .marshal().json()

            .to(ExchangePattern.InOnly, "seda:spinQueue");

        from("seda:spinQueue")
            .process("casinoRngProcessor")

            .setProperty("spinOutcome", method(CasinoRuleEngine.class, "evaluate(${exchangeProperty.spinValue})"))

            .setBody(exchange -> new SpinEvent(
                    SpinEventType.SPIN_RESULT,
                    exchange.getProperty("spinId", String.class),
                    exchange.getProperty("spinValue", Integer.class),
                    exchange.getProperty("spinOutcome", SpinOutcome.class)
            ))

            .marshal().json()

            .to("{{casino.ws.endpoint}}");
    }
}