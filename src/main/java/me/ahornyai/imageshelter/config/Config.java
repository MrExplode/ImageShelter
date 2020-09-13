package me.ahornyai.imageshelter.config;

import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;

@Getter
public class Config {
    private final Integer port = 8282;

    private final String[] secrets = new String[]{RandomStringUtils.randomAlphanumeric(32)};

    private final String[] allowedExtensions = new String[]{"png", "jpg", "jpeg", "bmp", "gif"};
}
