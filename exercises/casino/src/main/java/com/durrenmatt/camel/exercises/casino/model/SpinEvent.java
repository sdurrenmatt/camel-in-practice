package com.durrenmatt.camel.exercises.casino.model;

public record SpinEvent(
        SpinEventType eventType,
        String spinId,
        int spinValue,
        SpinOutcome spinOutcome
) {}