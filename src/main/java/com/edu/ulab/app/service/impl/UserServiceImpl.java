package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.service.CheckDifferentData;
import com.edu.ulab.app.storage.cacheRepo.IUserCacheRepo;
import com.edu.ulab.app.validation.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import javax.validation.ConstraintViolation;
import java.util.*;

@Slf4j
@Service
@Validated
public class UserServiceImpl implements UserService, CheckDifferentData {
    private final String JOHN_DOE_NULL = "JohnDoeNull";
    private final String USER_TITLE_NULL = "UnknownUserRole(Title)";
    private final IUserCacheRepo userCacheRepo;
//    private final UserValidator userValidator;

    public UserServiceImpl(IUserCacheRepo userCacheRepo, UserValidator userValidator) {
        this.userCacheRepo = userCacheRepo;
//        this.userValidator = userValidator;
    }

    @Override
    public UserEntity createUser(UserDto userDto) {
        UserEntity userEntity = userCacheRepo.userIdIsOutOfRange();
        Set<ConstraintViolation<UserDto>> violationSet = new HashSet<>();
//                userValidator.isValidUserDto(userDto);
        log.info("\n------- createUser Set ConstraintViolation UserDto:" + violationSet);
        if (violationSet.isEmpty()){
            userEntity = UserEntity.builder()
                    .id(userCacheRepo.getLastUserId()+1)
                    .fullName(Optional.ofNullable(userDto.getFullName()).orElse(JOHN_DOE_NULL))
                    .title(Optional.ofNullable(userDto.getTitle()).orElse(USER_TITLE_NULL))
                    .age(checkValidHumanAge(userDto.getAge()))
                    .userHasBooksIdList(new ArrayList<>())
                    .build();
        } else {
            log.error("Exception - can't create new user - inputed data errors: {}", userDto);
            throw new NotFoundException("Новый пользователь и его книги не были сохранены из-за ошибок при вводе " +
                    "данных:" + violationSet.toString());
        }
        log.info("---- createUser userEntity: {}", userEntity);
        return userEntity;
    }

    @Override
    public void updateUser(UserEntity changedUserEntity) {
        userCacheRepo.initUserEntity(changedUserEntity);
        log.info("---- updateUser changedUserEntity: {}", changedUserEntity);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("--1 impl getUserById id: {}", id);
        UserEntity userEntity = userCacheRepo.getUserEntityById(id);
        log.info("--2 getUserById userEntity: {}", userEntity);
        return UserDto.builder()
                .id(userEntity.getId())
                .fullName(userEntity.getFullName())
                .title(userEntity.getTitle())
                .age(userEntity.getAge())
                .build();
    }

    @Override
    public void deleteUserById(Long id) {
        userCacheRepo.deleteUserByUserId(id);
        log.info("\n---- delete User By Id id: {}", id);
    }

    @Override
    public UserEntity findUserEntityByFullNameAndTitleFromUserDto(UserDto userDto) {
        log.info("\n---- find UserEntity By UserDto - userDto: {}", userDto);
        return userCacheRepo.findUserEntityByFullNameAndTitleFromUserDto(userDto);
    }

    @Override
    public void changeUserDataAndUpdateRepo(UserDto userDto, UserEntity foundUserEntityByUserDto, List<Long> booksIds) {
        UserEntity changedUserEntity = UserEntity.builder()
                .id(foundUserEntityByUserDto.getId())
                .fullName(foundUserEntityByUserDto.getFullName())
                .title(foundUserEntityByUserDto.getTitle())
                .age(userDto.getAge())
                .userHasBooksIdList(booksIds)
                .build();
        log.info("\n---- change User Data And Update Repo - changedUserEntity: {}", changedUserEntity);
        userCacheRepo.deleteUserByUserId(foundUserEntityByUserDto.getId());
        updateUser(changedUserEntity);
    }

    @Override
    public UserEntity checkIfNewUserEntityFromUserDtoAlreadyIsInDB(UserDto userDto){
        log.info("\n---- check If New UserEntity From UserDto Already Is In DB - userDto: {}", userDto);
        return userCacheRepo.findUserEntityByFullNameAndTitleAndAgeFromUserDto(userDto);
    }
}
