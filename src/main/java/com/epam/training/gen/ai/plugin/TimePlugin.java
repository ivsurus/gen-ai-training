package com.epam.training.gen.ai.plugin;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;


@Slf4j
public class TimePlugin {

    @DefineKernelFunction(name = "getCurrentTime", description = "Returns the current UTC time.")
    public Instant getCurrentTime() {
        Instant now = Instant.now();
        log.info("Providing current time: {}", now);
        return now;
    }
}
