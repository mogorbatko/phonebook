package com.gorbatko.phonebook.resources;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ErrorData {
    private String className;
    private String methodName;
    private String message;
}
