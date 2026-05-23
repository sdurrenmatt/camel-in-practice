package com.durrenmatt.camel.examples.rest;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

import static org.apache.camel.builder.Builder.constant;

/**
 * Unit tests for the RESTful routes defined in the {@link RestRoutes} class.
 * <p>
 * This test class uses Apache Camel and Spring Boot testing tools to simulate HTTP requests
 * and verify the responses returned by the routes. The tests ensure that random pet image requests
 * (for both dogs and cats) are processed correctly, with the appropriate transformation applied to the API responses.
 * Mock endpoints are used to intercept HTTP requests and validate the expected behavior.
 * </p>
 *
 * @author Steven Dürrenmatt
 * @see <a href="https://camel.apache.org/manual/testing.html">Apache Camel Testing</a>
 * @see <a href="https://camel.apache.org/components/mock-component.html">Mock Component</a>
 */
@CamelSpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockEndpointsAndSkip("http:*")
class RestRoutesTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @EndpointInject("mock:http:dogApi")
    private MockEndpoint mockDogApi;

    @EndpointInject("mock:http:catApi")
    private MockEndpoint mockCatApi;

    @Value("classpath:dog-api-response.json")
    private Resource dogApiResponse;

    @Value("classpath:expected-pet-dog.json")
    private Resource expectedPetDog;

    @Value("classpath:cat-api-response.json")
    private Resource catApiResponse;

    @Value("classpath:expected-pet-cat.json")
    private Resource expectedPetCat;

    @Test
    void testGetPetDog() throws Exception {
        String mockResponse = StreamUtils.copyToString(dogApiResponse.getInputStream(), StandardCharsets.UTF_8);
        String expectedResponse = StreamUtils.copyToString(expectedPetDog.getInputStream(), StandardCharsets.UTF_8);

        mockDogApi.expectedMessageCount(1);
        mockDogApi.returnReplyBody(constant(mockResponse));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/pets/surprise?type=dog", String.class);

        mockDogApi.assertIsSatisfied();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedResponse, response.getBody(), true);
    }

    @Test
    void testGetPetCat() throws Exception {
        String mockResponse = StreamUtils.copyToString(catApiResponse.getInputStream(), StandardCharsets.UTF_8);
        String expectedResponse = StreamUtils.copyToString(expectedPetCat.getInputStream(), StandardCharsets.UTF_8);

        mockCatApi.expectedMessageCount(1);
        mockCatApi.returnReplyBody(constant(mockResponse));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/pets/surprise?type=cat", String.class);

        mockCatApi.assertIsSatisfied();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedResponse, response.getBody(), true);
    }
}