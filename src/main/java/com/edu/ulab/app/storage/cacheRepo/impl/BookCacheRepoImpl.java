package com.edu.ulab.app.storage.cacheRepo.impl;

import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.storage.cacheRepo.IBookCacheRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BookCacheRepoImpl implements IBookCacheRepo {
    private final long EMPTY_BOOK_CACHE_DB = 0L;
    private ConcurrentSkipListMap<Long, BookEntity> booksEntity = new ConcurrentSkipListMap<>(); // LinkedHashMap

    @Override
    public void initBookEntity(BookEntity bookEntity) {
        log.info("\n-------20 before adding book in DB:" + booksEntity);
        this.booksEntity.put(bookEntity.getId(), bookEntity);
        log.info("\n-------22 after adding book in DB:" + booksEntity);
    }

    @Override
    public long getLastBookId() {
        boolean booksEntityIsEmpty = booksEntity.isEmpty();
        log.info("\n------- getLastBookId - booksEntityIsEmpty:" + booksEntityIsEmpty);
        return (booksEntityIsEmpty) ? EMPTY_BOOK_CACHE_DB : booksEntity.lastKey();
    }

    @Override
    public List<Long> getAllBooksIdByUserId(Long id) {
        List<Long> allBooksIdByUserId = booksEntity.values().stream().filter((b) -> Objects.equals(b.getUserId(), id))
                .map(BookEntity::getId)
                .collect(Collectors.toList());
        log.info("\n-------List allBooksIdByUserId:" + allBooksIdByUserId);
        return allBooksIdByUserId;
    }

    @Override
    public void deleteAllBooksByUserId(Long userId) {
        log.info("\n------- before deleteAllBooksByUserId:" + userId + "*****" + booksEntity);
        booksEntity.entrySet().removeIf(entry -> Objects.equals(entry.getValue().getUserId(), userId));
        log.info("\n------- after deleteAllBooksByUserId:" + userId + "*****" + booksEntity);
    }

    @Override
    public boolean bookIdIsInDB(Long id) {
        log.info("\n-------bookIdIsInDB - id:" + id);
        return (booksEntity.containsKey(id));
    }

    @Override
    public BookEntity getBookEntityById(Long id) {
        log.info("\n-------getBookEntityById - id:" + id);
        return (bookIdIsInDB(id)) ? booksEntity.get(id) : bookIdIsOutOfRange();
    }

    @Override
    public BookEntity bookIdIsOutOfRange() {
        log.info("\n-------bookIdIsOutOfRange");
        return BookEntity.builder()
                .id(0L)
                .userId(0L)
                .title("Введённый Вами Id книги отсутствует в базе.")
                .author("Нет данных.")
                .pageCount(1)
                .build();
    }

    @Override
    public void deleteBookByBookId(Long id){
        BookEntity foundBook = getBookEntityById(id);
        booksEntity.remove(foundBook.getId());
        log.info("\n-------deleteBookByBookId - id:" + id);
        log.info("\n-------deleteBookByBookId - foundBook:" + foundBook);
    }

}
