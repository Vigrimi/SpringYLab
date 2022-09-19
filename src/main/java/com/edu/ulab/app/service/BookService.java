package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.BookEntity;

public interface BookService {
    BookEntity createBook(BookDto userDto);

    void updateBook(BookEntity changedBookEntity);

    BookDto getBookById(Long id);

    void deleteAllBooksByUserId(Long userId);

    void deleteBookById(Long id);
}
