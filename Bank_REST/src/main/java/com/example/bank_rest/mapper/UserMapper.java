package com.example.bank_rest.mapper;

import com.example.bank_rest.entity.User;
import com.example.bank_rest.payload.dto.response.UserCreateResponseDto;
import com.example.bank_rest.payload.dto.response.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class, uses = {CardMapper.class})
public interface UserMapper {

    @Mapping(target = "cardResponseDtoList", source = "cards")
    UserResponseDto toDto(User user);

    UserCreateResponseDto toCreateDto(User user);
}
