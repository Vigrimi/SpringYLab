package com.edu.ulab.app.storage.cacheRepo.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.storage.cacheRepo.IUserCacheRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
@Slf4j
public class UserCacheRepoImpl implements IUserCacheRepo {
    private final long EMPTY_USER_CACHE_DB = 0L;
    private ConcurrentSkipListMap<Long, UserEntity> usersEntity = new ConcurrentSkipListMap<>();

    @Override
    public void initUserEntity(UserEntity user) {
        log.info("\n-------24 before adding user in DB:" + usersEntity);
        this.usersEntity.put(user.getId(), user);
        log.info("\n-------26 after adding user in DB:" + usersEntity);
    }

    @Override
    public long getLastUserId() {
        boolean usersEntityIsEmpty = usersEntity.isEmpty();
        log.info("\n---- getLastUserId - usersEntityIsEmpty: {}", usersEntityIsEmpty);
        return (usersEntityIsEmpty) ? EMPTY_USER_CACHE_DB : usersEntity.lastKey();
    }

    @Override
    public UserEntity getUserEntityById(Long id) {
        log.info("\n---- getUserEntityById - id: {}", id);
        return userIdIsInDB(id) ? usersEntity.get(id) : userIdIsOutOfRange();
    }

    @Override
    public UserEntity userIdIsOutOfRange() {
        log.info("\n---- userIdIsOutOfRange");
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
        log.info("\n---- userIdIsInDB - id: {}", id);
        return (usersEntity.containsKey(id));
    }

    @Override
    public void deleteUserByUserId(Long id) {
        log.info("\n------- DB before deleting user id: " + id + " ***" + usersEntity);
        UserEntity foundUser = getUserEntityById(id);
//        List<UserEntity> userToDelete = usersEntity.stream().filter((u) -> Objects.equals(u.getId(), id)).toList();
        usersEntity.remove(foundUser.getId());
        log.info("\n------- DB after deleting user id: " + id + " ***" + usersEntity);
    }

    @Override
    public UserEntity findUserEntityByFullNameAndTitleFromUserDto(UserDto userDto) {
        UserEntity userEntity = userIdIsOutOfRange();
        for (Map.Entry<Long, UserEntity> entry : usersEntity.entrySet()) {
            if (entry.getValue().getFullName().equalsIgnoreCase(userDto.getFullName()) &&
                    entry.getValue().getTitle().equalsIgnoreCase(userDto.getTitle())){
                userEntity = entry.getValue();
                break;
            }
        }
        log.info("\n------- findUserEntityByFullNameAndTitleFromUserDto userDto: " + userDto);
        log.info("\n------- findUserEntityByFullNameAndTitleFromUserDto userEntity: " + userEntity);
        return userEntity;
    }

    @Override
    public UserEntity findUserEntityByFullNameAndTitleAndAgeFromUserDto(UserDto userDto) {
        UserEntity userEntity = userIdIsOutOfRange();
        for (Map.Entry<Long, UserEntity> entry : usersEntity.entrySet()) {
            if (entry.getValue().getFullName().equalsIgnoreCase(userDto.getFullName()) &&
                    entry.getValue().getTitle().equalsIgnoreCase(userDto.getTitle()) &&
                    entry.getValue().getAge() == userDto.getAge()){
                userEntity = entry.getValue();
                break;
            }
        }
        log.info("\n------- findUserEntityByFullNameAndTitleAndAgeFromUserDto userDto: " + userDto);
        log.info("\n------- findUserEntityByFullNameAndTitleAndAgeFromUserDto userEntity: " + userEntity);
        return userEntity;
    }
}
