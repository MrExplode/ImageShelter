package me.ahornyai.imageshelter.http.responses;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
}
