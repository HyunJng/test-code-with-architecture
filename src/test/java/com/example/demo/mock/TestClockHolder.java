package com.example.demo.mock;

import com.example.demo.common.service.port.ClockHolder;

public class TestClockHolder implements ClockHolder {

    private final long clock;

    public TestClockHolder(long clock) {
        this.clock = clock;
    }

    @Override
    public long millis() {
        return clock;
    }
}