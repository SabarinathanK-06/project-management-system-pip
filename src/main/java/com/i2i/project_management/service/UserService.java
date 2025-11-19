package com.i2i.project_management.service;

import com.i2i.project_management.Dto.UserDto;
import com.i2i.project_management.Dto.UserUpdateDto;
import com.i2i.project_management.exception.DatabaseException;
import com.i2i.project_management.exception.NotFoundException;
import com.i2i.project_management.exception.ValidationException;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto createUser(UserDto userDto) throws ValidationException;

    UserDto updateUser(UUID id, UserUpdateDto userUpdateDto) throws ValidationException, NotFoundException;

    UserDto findUserById(UUID id) throws ValidationException, DatabaseException;

    List<UserDto> findAllUser() throws DatabaseException, NotFoundException;

    void deleteUser(UUID id) throws DatabaseException;

    UserDto updateOwnProfile(String email, UserUpdateDto userUpdateDto) throws ValidationException, NotFoundException;

}
