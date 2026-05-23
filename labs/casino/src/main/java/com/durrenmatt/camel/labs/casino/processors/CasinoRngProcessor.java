package com.durrenmatt.camel.labs.casino.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CasinoRngProcessor implements Processor {

    private final SecureRandom random = new SecureRandom();

    @Override
    public void process(Exchange exchange) {
        int spinValue = random.nextInt(1000);
        exchange.setProperty("spinValue", spinValue);
    }
}
