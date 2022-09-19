package com.edu.ulab.app.storage.cacheRepo.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.storage.cacheRepo.IUserCacheRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@Slf4j
public class UserCacheRepoImpl implements IUserCacheRepo {
    private final long EMPTY_USER_CACHE_DB = 0L;
    private List<UserEntity> usersEntity = new ArrayList<>();

    @Override
    public void initUserEntity(UserEntity user) {
        this.usersEntity.add(user);
        usersEntity.sort(Comparator.comparing(UserEntity::getId));
        log.info("\n-------19 class UserCacheRepo after adding user in DB:" + usersEntity);
    }

    @Override
    public long getLastUserId() {
        return (usersEntity.isEmpty()) ? EMPTY_USER_CACHE_DB : usersEntity.get(usersEntity.size()-1).getId();
    }

    @Override
    public UserEntity getUserEntityById(Long id) {
        UserEntity userEntity = userIdIsOutOfRange();
        if (userIdIsInDB(id)){
            for (UserEntity entity : usersEntity) {
                if (Objects.equals(entity.getId(), id)){
                    userEntity = entity;
                    break;
                }
            }
        }
        return userEntity;
    }

    @Override
    public UserEntity userIdIsOutOfRange() {
        return UserEntity.builder()
                .id(0L)
                .fullName("Введённый Вами Id пользователя отсутствует в базе.")
                .title("Нет данных.")
                .age(1)
                .userHasBooksIdList(new ArrayList<>())
                .build();
    }

    @Override
    public boolean userIdIsInDB(Long id) {
        return (usersEntity.stream().anyMatch((u) -> Objects.equals(u.getId(), id)));
    }

    @Override
    public void deleteUserByUserId(Long id) {
        log.info("\n------- class UserCacheRepoImpl DB before deleting user id: " + id + " ***" + usersEntity);
        UserEntity foundUser = getUserEntityById(id);
//        List<UserEntity> userToDelete = usersEntity.stream().filter((u) -> Objects.equals(u.getId(), id)).toList();
        usersEntity.remove(foundUser);
        log.info("\n------- class UserCacheRepoImpl DB after deleting user id: " + id + " ***" + usersEntity);
    }

    @Override
    public UserEntity findUserEntityByUserDto(UserDto userDto) {
        UserEntity userEntity = userIdIsOutOfRange();

        for (UserEntity entity : usersEntity) {
            if (entity.getFullName().equalsIgnoreCase(userDto.getFullName()) &&
                    entity.getTitle().equalsIgnoreCase(userDto.getTitle())){
                userEntity = entity;
                break;
            }
        }
        log.info("\n------- class UserCacheRepoImpl findUserEntityByUserDto userDto: " + userDto);
        log.info("\n------- class UserCacheRepoImpl findUserEntityByUserDto userEntity: " + userEntity);
        return userEntity;
    }
}
