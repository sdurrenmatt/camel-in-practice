package com.durrenmatt.camel.exercises.casino.routes;

import com.durrenmatt.camel.exercises.casino.model.SpinEvent;
import com.durrenmatt.camel.exercises.casino.model.SpinEventType;
import com.durrenmatt.camel.exercises.casino.model.SpinResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

@CamelSpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("{{casino.ws.endpoint}}")
class CasinoRoutesTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @EndpointInject("mock:{{casino.ws.endpoint}}")
    private MockEndpoint mockWs;

    @Test
    void testSpinRest() {
        ResponseEntity<String> response = restTemplate.postForEntity("/casino/spin", null, String.class);

        Assertions.assertEquals(
                HttpStatus.ACCEPTED,
                response.getStatusCode(),
                "REST spin endpoint must return HTTP 202 ACCEPTED"
        );

        SpinResponse spinResponse = Assertions.assertDoesNotThrow(
                () -> new ObjectMapper().readValue(response.getBody(), SpinResponse.class),
                "Response must be valid SpinResponse"
        );

        Assertions.assertNotNull(
                spinResponse.spinId(),
                "Response must contain spinId"
        );

        Assertions.assertDoesNotThrow(
                () -> UUID.fromString(spinResponse.spinId()),
                "spinId must be a valid UUID v4"
        );
    }

    @Test
    void testSpinWs() throws Exception {
        mockWs.expectedMessageCount(1);
        mockWs.setResultWaitTime(5000);

        ResponseEntity<String> response = restTemplate.postForEntity("/casino/spin", null, String.class);

        ObjectMapper mapper = new ObjectMapper();

        String spinId = Assertions.assertDoesNotThrow(
                () -> mapper.readValue(response.getBody(), SpinResponse.class).spinId(),
                "Failed to retrieve spinId from REST response"
        );

        mockWs.assertIsSatisfied();

        String body = mockWs.getExchanges()
                .getFirst()
                .getMessage()
                .getBody(String.class);

        SpinEvent event = Assertions.assertDoesNotThrow(
                () -> mapper.readValue(body, SpinEvent.class),
                "WebSocket payload must be valid SpinEvent"
        );

        Assertions.assertEquals(
                SpinEventType.SPIN_RESULT,
                event.eventType(),
                "eventType must be SPIN_RESULT"
        );

        Assertions.assertNotNull(
                event.spinId(),
                "event must contain spinId"
        );

        Assertions.assertTrue(
                event.spinValue() >= 0,
                "spinValue must be non-negative"
        );

        Assertions.assertNotNull(
                event.spinOutcome(),
                "event must contain spinOutcome"
        );

        Assertions.assertEquals(
                spinId,
                event.spinId(),
                "WebSocket spinId must match REST spinId"
        );
    }

}