package com.gradle.hw.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class EventInfo {

    private String title;
    private LocalDate start;
    private LocalDate end;
    private String description;
    private String weather;
}
