package com.edu.ulab.app.entity;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class BookEntity {
    @NotNull
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String title;

    @NotNull
    private String author;

    @NotNull
    private long pageCount;
}
