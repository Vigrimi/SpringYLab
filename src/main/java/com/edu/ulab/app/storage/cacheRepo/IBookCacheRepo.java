package com.edu.ulab.app.storage.cacheRepo;

import com.edu.ulab.app.entity.BookEntity;
import java.util.List;

public interface IBookCacheRepo {
    void initBookEntity(BookEntity bookEntity);

    long getLastBookId();

    List<Long> getAllBooksIdByUserId(Long id);

    void deleteAllBooksByUserId(Long userId);

    boolean bookIdIsInDB(Long id);

    BookEntity getBookEntityById(Long id);

    BookEntity bookIdIsOutOfRange();

    void deleteBookByBookId(Long id);
}
