package hr.fer.apuw.lab1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HandledErrorDTO {

    private int code;

    private String message;

    private List<ValidationPair> validations;

    public void addValidationPair(ValidationPair validationPair) {
        validations.add(validationPair);
    }
}
