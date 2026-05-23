package com.durrenmatt.camel.exercises.casino.routes;

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
            // TODO 1: Generate a unique spinId (UUID v4) and store it as an exchange property
            // Hint: Use a Simple expression (https://camel.apache.org/components/languages/simple-language.html)

            // TODO 2: Build a REST response containing the spinId (HTTP 202, SpinResponse)

            // TODO 3: Serialize the response to JSON

            .to(ExchangePattern.InOnly, "seda:spinQueue");

        from("seda:spinQueue")
            // TODO 4: Invoke the RNG processor to generate a random spinValue
            // Hint: Check how to use a processor in a route (https://camel.apache.org/manual/processor.html)

            // TODO 5: Use the Rule Engine to compute the spinOutcome
            // Hint: Use a Bean method expression (https://camel.apache.org/components/languages/bean-language.html)

            // TODO 6: Build the WebSocket event payload (SpinEvent)

            // TODO 7: Serialize the event to JSON

            .to("{{casino.ws.endpoint}}");
    }
}