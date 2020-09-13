package me.ahornyai.imageshelter.http.responses;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;
}
