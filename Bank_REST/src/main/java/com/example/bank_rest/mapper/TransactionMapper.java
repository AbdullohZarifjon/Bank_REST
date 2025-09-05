package com.example.bank_rest.mapper;

import com.example.bank_rest.entity.Transaction;
import com.example.bank_rest.payload.dto.response.TransactionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.example.bank_rest.mapper.CardMapper.maskCardNumber;

@Mapper(config = MapStructConfig.class)
public interface TransactionMapper {

    @Mapping(source = "sender.number", target = "fromCard", qualifiedByName = "maskCard")
    @Mapping(source = "receiver.number", target = "toCard", qualifiedByName = "maskCard")
    @Mapping(source = "status", target = "status")
    TransactionResponseDto toDto(Transaction transaction);

    @Named("maskCard")
    static String maskCard(String number) {
        return maskCardNumber(number);
    }
}
