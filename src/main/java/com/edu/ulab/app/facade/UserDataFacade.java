package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.cacheRepo.IBookCacheRepo;
import com.edu.ulab.app.storage.cacheRepo.IUserCacheRepo;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final Long USER_ID_NOT_FOUND = 0L;
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    // TODO как останется время обработку репозит-я перенести в соответствующие сервисы
    private final IUserCacheRepo userCacheRepo;
    private final IBookCacheRepo bookCacheRepo;

    public UserDataFacade(UserService userService, BookService bookService,
                          UserMapper userMapper, BookMapper bookMapper,
                          IUserCacheRepo userCacheRepo, IBookCacheRepo bookCacheRepo) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
        this.userCacheRepo = userCacheRepo;
        this.bookCacheRepo = bookCacheRepo;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserEntity createdUserEntity = userService.createUser(userDto);
        log.info("\n--------Created user before adding books ids: {}", createdUserEntity);
        userCacheRepo.initUserEntity(createdUserEntity);

        List<Long> bookIdList = getListBooksIdsForUserEntAndAddBooksInRepo(userBookRequest,createdUserEntity);
        log.info("\n------------Collected book ids: {}", bookIdList);
        createdUserEntity.setUserHasBooksIdList(bookIdList);
        log.info("\n--------Created user after adding books ids: {}", createdUserEntity);

        return UserBookResponse.builder()
                .userId(createdUserEntity.getId())
                .booksIdList(createdUserEntity.getUserHasBooksIdList())
                .build();
    }

    private List<Long> getListBooksIdsForUserEntAndAddBooksInRepo(UserBookRequest userBookRequest,
                                                                  UserEntity userEntity){
        return userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(userEntity.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(bookCacheRepo::initBookEntity)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookEntity::getId)
                .toList();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest) {
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        UserEntity foundUserEntityByUserDto = userService.findUserEntityByUserDto(userDto);
        bookService.deleteAllBooksByUserId(foundUserEntityByUserDto.getId());

        List<Long> inputedBookIdList = getListBooksIdsForUserEntAndAddBooksInRepo(userBookRequest,foundUserEntityByUserDto);

        userService.changeUserDataAndUpdateRepo(userDto, foundUserEntityByUserDto, inputedBookIdList);
        return UserBookResponse.builder()
                .userId(foundUserEntityByUserDto.getId())
                .booksIdList(inputedBookIdList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("user data facade getUserWithBooks: {}", userId);
        Long actualUserId = userService.getUserById(userId).getId();
        return UserBookResponse.builder()
                .userId(actualUserId)
                .booksIdList(bookCacheRepo.getAllBooksIdByUserId(actualUserId))
                .build();
    }

    public void deleteUserWithBooks(Long userId) {
        Long actualUserId = userService.getUserById(userId).getId();
        if (actualUserId == USER_ID_NOT_FOUND){
            throw new NotFoundException("USER_ID_NOT_FOUND");
        } else {
            bookService.deleteAllBooksByUserId(actualUserId);
            userService.deleteUserById(actualUserId);
        }
    }
}
