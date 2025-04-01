package br.com.gabxdev.commons;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class ClockProvider {
    public OffsetDateTime now() {
        return OffsetDateTime.now();
    }
}
