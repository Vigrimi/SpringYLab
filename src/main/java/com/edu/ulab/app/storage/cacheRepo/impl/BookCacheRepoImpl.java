package com.edu.ulab.app.storage.cacheRepo.impl;

import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.storage.cacheRepo.IBookCacheRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BookCacheRepoImpl implements IBookCacheRepo {
    private final long EMPTY_BOOK_CACHE_DB = 0L;
    private List<BookEntity> booksEntity = new ArrayList<>();

    @Override
    public void initBookEntity(BookEntity bookEntity) {
        this.booksEntity.add(bookEntity);
        booksEntity.sort(Comparator.comparing(BookEntity::getId));
        log.info("\n-------20 class BookCacheRepoImpl after adding book in DB:" + booksEntity);
    }

    @Override
    public long getLastBookId() {
        return (booksEntity.isEmpty()) ? EMPTY_BOOK_CACHE_DB : booksEntity.get(booksEntity.size()-1).getId();
    }

    @Override
    public List<Long> getAllBooksIdByUserId(Long id) {
        List<Long> allBooksIdByUserId = booksEntity.stream().filter( (b) -> Objects.equals(b.getUserId(), id))
                .map(BookEntity::getId)
                .collect(Collectors.toList());
        log.info("\n-------class BookCacheRepoImpl List allBooksIdByUserId:" + allBooksIdByUserId);
        return allBooksIdByUserId;
    }

    @Override
    public void deleteAllBooksByUserId(Long userId) {
        log.info("\n------- class BookCacheRepoImpl before deleteAllBooksByUserId:" + userId + "*****" + booksEntity);
        booksEntity.removeIf(nextBook -> Objects.equals(nextBook.getUserId(), userId));
        log.info("\n------- class BookCacheRepoImpl after deleteAllBooksByUserId:" + userId + "*****" + booksEntity);
    }

    @Override
    public boolean bookIdIsInDB(Long id) {
        return (booksEntity.stream().anyMatch((b) -> Objects.equals(b.getId(), id)));
    }

    @Override
    public BookEntity getBookEntityById(Long id) {
        BookEntity bookEntity = bookIdIsOutOfRange();
        if (bookIdIsInDB(id)){
            for (BookEntity entity : booksEntity) {
                if (Objects.equals(entity.getId(), id)){
                    bookEntity = entity;
                    break;
                }
            }
        }
        return bookEntity;
    }

    @Override
    public BookEntity bookIdIsOutOfRange() {
        return BookEntity.builder()
                .id(0L)
                .userId(0L)
                .title("Введённый Вами Id пользователя отсутствует в базе.")
                .author("Нет данных.")
                .pageCount(1)
                .build();
    }

    @Override
    public void deleteBookByBookId(Long id){
        BookEntity foundBook = getBookEntityById(id);
        booksEntity.remove(foundBook);
    }

}
