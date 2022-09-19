package com.edu.ulab.app.storage.cacheRepo;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;

public interface IUserCacheRepo {
    void initUserEntity(UserEntity user);

    long getLastUserId();

    UserEntity getUserEntityById(Long id);

    UserEntity userIdIsOutOfRange();

    boolean userIdIsInDB(Long id);

    void deleteUserByUserId(Long id);

    UserEntity findUserEntityByUserDto(UserDto userDto);
}
