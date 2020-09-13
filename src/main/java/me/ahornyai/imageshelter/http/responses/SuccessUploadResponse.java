package me.ahornyai.imageshelter.http.responses;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SuccessUploadResponse {
    private final String fileName;
    private final String encryptionKey;
}
