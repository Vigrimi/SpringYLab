package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import java.util.List;

public interface UserService {
    UserEntity createUser(UserDto userDto);

    void updateUser(UserEntity changedUserEntity);

    UserDto getUserById(Long id);

    void deleteUserById(Long id);

    UserEntity findUserEntityByFullNameAndTitleFromUserDto(UserDto userDto);

    void changeUserDataAndUpdateRepo(UserDto userDto, UserEntity foundUserEntityByUserDto, List<Long> booksIds);

    UserEntity checkIfNewUserEntityFromUserDtoAlreadyIsInDB(UserDto userDto);
}
