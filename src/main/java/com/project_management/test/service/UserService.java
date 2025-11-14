package com.project_management.test.service;

import com.project_management.test.Dto.UserDto;
import com.project_management.test.Dto.UserUpdateDto;
import com.project_management.test.exception.DatabaseException;
import com.project_management.test.exception.NotFoundException;
import com.project_management.test.exception.ValidationException;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto createUser(UserDto userDto) throws ValidationException;

    UserDto updateUser(UUID id, UserUpdateDto userUpdateDto) throws ValidationException, NotFoundException;

    UserDto findUserById(UUID id) throws ValidationException, DatabaseException;

    List<UserDto> findAllUser() throws DatabaseException, NotFoundException;

    void deleteUser(UUID id) throws DatabaseException;

}
