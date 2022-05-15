package com.gradle.hw.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class WeatherUrl {
    @Value("${weather.url}")
    private String url;

    @Value("${weather.apikey}")
    private String apiKey;
}
