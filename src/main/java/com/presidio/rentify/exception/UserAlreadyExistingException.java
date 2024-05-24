package com.presidio.rentify.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAlreadyExistingException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private String fieldValue;

    public UserAlreadyExistingException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s already exists with %s : %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
