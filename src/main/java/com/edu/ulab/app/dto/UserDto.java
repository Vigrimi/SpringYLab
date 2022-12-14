package com.edu.ulab.app.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Validated
public class UserDto {
    private Long id;

    @NotNull
    private String fullName;

    @NotNull
    private String title; // role

    @NotNull
    private int age;
}
