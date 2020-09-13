package me.ahornyai.imageshelter.config;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;

@Getter
@ToString
public class Config {
    private final Integer port = 8282;
    private final String[] secrets = new String[]{RandomStringUtils.randomAlphanumeric(32)};
}
