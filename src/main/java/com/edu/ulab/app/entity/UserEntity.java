package com.edu.ulab.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @NotNull
    private Long id;

    @NotBlank
    private String fullName;

    @NotNull
    private String title; // role

    @NotNull
    private int age;

    @NotNull
    private List<Long> userHasBooksIdList;
}
