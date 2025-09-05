package com.example.bank_rest.mapper;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.payload.dto.response.CardResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapStructConfig.class)
public interface CardMapper {

    @Mapping(target = "number", source = "number", qualifiedByName = "maskCardNumber")
    CardResponseDto toDto(Card card);

    @Named("maskCardNumber")
    static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return "****";
        }
        String first4 = cardNumber.substring(0, 4);
        String last4 = cardNumber.substring(cardNumber.length() - 4);

        String middle = cardNumber.substring(4, cardNumber.length() - 4)
                .replaceAll("\\d", "*");

        StringBuilder maskedMiddle = new StringBuilder();
        for (int i = 0; i < middle.length(); i++) {
            maskedMiddle.append(middle.charAt(i));
            if ((i + 1) % 4 == 0 && i != middle.length() - 1) {
                maskedMiddle.append(" ");
            }
        }

        return first4 + " " + maskedMiddle + " " + last4;
    }

}
