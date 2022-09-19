package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.service.CheckDifferentData;
import com.edu.ulab.app.storage.cacheRepo.IUserCacheRepo;
import com.edu.ulab.app.storage.cacheRepo.impl.UserCacheRepoImpl;
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
        log.info("\n-------user serv impl createUser Set ConstraintViolation UserDto:" + violationSet);
        if (violationSet.isEmpty()){
            userEntity = UserEntity.builder()
                    .id(userCacheRepo.getLastUserId()+1)
                    .fullName(Optional.ofNullable(userDto.getFullName()).orElse(JOHN_DOE_NULL))
                    .title(Optional.ofNullable(userDto.getTitle()).orElse(USER_TITLE_NULL))
                    .age(checkValidHumanAge(userDto.getAge()))
                    .userHasBooksIdList(new ArrayList<>())
                    .build();
        } else {
            userEntity.setFullName("Новый пользователь и его книги не были сохранены из-за ошибок при вводе данных:" +
                    violationSet.toString());
        }
        return userEntity;
    }

    @Override
    public void updateUser(UserEntity changedUserEntity) {
        userCacheRepo.initUserEntity(changedUserEntity);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("--1 user serv impl getUserById: {}", id);
        UserEntity userEntity = userCacheRepo.getUserEntityById(id);
        log.info("--2 user serv impl getUserById: {}", userEntity);
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
    }

    @Override
    public UserEntity findUserEntityByUserDto(UserDto userDto) {
        return userCacheRepo.findUserEntityByUserDto(userDto);
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
        userCacheRepo.deleteUserByUserId(foundUserEntityByUserDto.getId());
        updateUser(changedUserEntity);
    }
}
