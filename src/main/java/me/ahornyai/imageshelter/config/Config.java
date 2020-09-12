package me.ahornyai.imageshelter.config;

import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;

@Getter
public class Config {
    private int port = 8282;
    private String[] secrets = new String[]{RandomStringUtils.randomAlphanumeric(32)};
}
