package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.cacheRepo.IBookCacheRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private final String BOOK_TITLE_NULL = "UnknownBookTitle";
    private final String BOOK_AUTHOR_NULL = "UnknownBookAuthor";
    private final IBookCacheRepo bookCacheRepo;

    public BookServiceImpl(IBookCacheRepo bookCacheRepo) {
        this.bookCacheRepo = bookCacheRepo;
    }

    @Override
    public BookEntity createBook(BookDto bookDto) {
        BookEntity bookEntity = BookEntity.builder()
                .id(bookCacheRepo.getLastBookId()+1)
                .userId(bookDto.getUserId())
                .title(Optional.ofNullable(bookDto.getTitle()).orElse(BOOK_TITLE_NULL))
                .author(Optional.ofNullable(bookDto.getAuthor()).orElse(BOOK_AUTHOR_NULL))
                .pageCount(bookDto.getPageCount())
                .build();
        log.info("\n --------- create book bookEntity:" + bookEntity);
        return bookEntity;
    }

    @Override
    public void updateBook(BookEntity changedBookEntity) {
        bookCacheRepo.initBookEntity(changedBookEntity);
        log.info("\n --------- updateBook changedBookEntity:" + changedBookEntity);
    }

    @Override
    public BookDto getBookById(Long id) {
        BookEntity bookEntity = bookCacheRepo.getBookEntityById(id);
        log.info("--2 getUserById: {}", bookEntity);
        return BookDto.builder()
                .id(bookEntity.getId())
                .userId(bookEntity.getUserId())
                .title(bookEntity.getTitle())
                .author(bookEntity.getAuthor())
                .pageCount(bookEntity.getPageCount())
                .build();
    }

    @Override
    public void deleteAllBooksByUserId(Long userId) {
        bookCacheRepo.deleteAllBooksByUserId(userId);
        log.info("---- deleteAllBooksByUserId userId: {}", userId);
    }

    @Override
    public void deleteBookById(Long id) {
        bookCacheRepo.deleteBookByBookId(id);
        log.info("---- deleteBookById id: {}", id);
    }
}
