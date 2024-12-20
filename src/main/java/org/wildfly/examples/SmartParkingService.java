package org.wildfly.examples;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SmartParkingService {

    public String hello(String name) {
        return String.format("Hello '%s'.", name);
    }
}