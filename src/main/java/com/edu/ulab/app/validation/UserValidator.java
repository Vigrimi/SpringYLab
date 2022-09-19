package com.edu.ulab.app.validation;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import org.springframework.stereotype.Service;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Service
public class UserValidator {

//    public Set<ConstraintViolation<UserEntity>> isValidUserEntity(UserEntity userEntity){
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        Validator validator = factory.getValidator();
//        return validator.validate(userEntity);
//    }
//
//    public Set<ConstraintViolation<UserDto>> isValidUserDto(UserDto userDto){
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        Validator validator = factory.getValidator();
//        return validator.validate(userDto);
//    }
}
