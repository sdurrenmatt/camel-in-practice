package com.durrenmatt.camel.labs.casino.model;

public record SpinEvent(
        SpinEventType eventType,
        String spinId,
        int spinValue,
        SpinOutcome spinOutcome
) {}