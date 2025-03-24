package com.example.app.components;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LogDirInitializer {

    @PostConstruct
    public void ensureLogDirExists() {
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }
}
