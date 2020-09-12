package me.ahornyai.imageshelter.http.responses;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private int code;
    private String message;
}
