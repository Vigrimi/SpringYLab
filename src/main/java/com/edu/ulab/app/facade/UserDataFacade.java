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
        UserEntity userEntityForCheck = userService.checkIfNewUserEntityFromUserDtoAlreadyIsInDB(userDto);
        if (userEntityForCheck.getId() > USER_ID_NOT_FOUND){
            log.error("Exception - can't create new user - already exist: {}", userDto);
            throw new NotFoundException("Упс, такой user уже есть в базе под идентификационным номером id=" +
                    userEntityForCheck.getId() + ". Воспользуйтесь кнопкой <Update> или <Get via id>.");
        }
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
        UserEntity foundUserEntityByUserDto = userService.findUserEntityByFullNameAndTitleFromUserDto(userDto);
        if (Objects.equals(foundUserEntityByUserDto.getId(), USER_ID_NOT_FOUND)){
            log.error("Exception - can't updateUserWithBooks - USER_ID_NOT_FOUND: {}", userDto);
            throw new NotFoundException("Упс, введённые данные пользователя (FullName, Title) не найдены в базе. " +
                    "Обновлять нечего. Попробуйте снова, уточнив актуальные данные.");
        }
        bookService.deleteAllBooksByUserId(foundUserEntityByUserDto.getId());

        List<Long> inputedBookIdList = getListBooksIdsForUserEntAndAddBooksInRepo(userBookRequest,foundUserEntityByUserDto);
        log.info("update User With Books - found UserEntity By UserDto: {}", foundUserEntityByUserDto);
        log.info("update User With Books - inputed Book Id List: {}", inputedBookIdList);
        userService.changeUserDataAndUpdateRepo(userDto, foundUserEntityByUserDto, inputedBookIdList);
        return UserBookResponse.builder()
                .userId(foundUserEntityByUserDto.getId())
                .booksIdList(inputedBookIdList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("getUserWithBooks userId: {}", userId);
        Long actualUserId = userService.getUserById(userId).getId();
        log.info("getUserWithBooks actualUserId: {}", actualUserId);
        if (Objects.equals(actualUserId, USER_ID_NOT_FOUND)){
            log.error("Exception - can't getUserWithBooks - USER_ID_NOT_FOUND: {}", actualUserId);
            throw new NotFoundException("Упс, введённый идентификатор пользователя id=" + userId + " не найден в " +
                    "базе. Отобразить нечего. Попробуйте снова, уточнив актуальные данные.");
        }
        return UserBookResponse.builder()
                .userId(actualUserId)
                .booksIdList(bookCacheRepo.getAllBooksIdByUserId(actualUserId))
                .build();
    }

    public void deleteUserWithBooks(Long userId) {
        Long actualUserId = userService.getUserById(userId).getId();
        log.info("deleteUserWithBooks actualUserId: {}", actualUserId);
        if (actualUserId == USER_ID_NOT_FOUND){
            log.error("Exception - can't deleteUserWithBooks - USER_ID_NOT_FOUND: {}", actualUserId);
            throw new NotFoundException("USER_ID_NOT_FOUND");
        } else {
            bookService.deleteAllBooksByUserId(actualUserId);
            userService.deleteUserById(actualUserId);
        }
    }
}
