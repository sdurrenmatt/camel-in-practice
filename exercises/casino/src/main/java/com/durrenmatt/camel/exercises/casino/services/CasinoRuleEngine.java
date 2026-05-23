package com.durrenmatt.camel.exercises.casino.services;

import com.durrenmatt.camel.exercises.casino.model.SpinOutcome;
import org.springframework.stereotype.Service;

@Service
public class CasinoRuleEngine {

    public SpinOutcome evaluate(int spinValue) {
        if (spinValue < 700) return SpinOutcome.LOSE;

        if (spinValue < 900) return SpinOutcome.WIN;

        if (spinValue < 960) return SpinOutcome.BIG_WIN;

        if (spinValue < 985) return SpinOutcome.MEGA_WIN;

        if (spinValue < 999) return SpinOutcome.EPIC_WIN;

        return SpinOutcome.MAX_WIN;
    }
}