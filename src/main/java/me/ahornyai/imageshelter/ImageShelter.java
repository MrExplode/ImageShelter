package me.ahornyai.imageshelter;

import com.google.common.base.Stopwatch;
import io.javalin.Javalin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.ahornyai.imageshelter.config.Config;
import me.ahornyai.imageshelter.config.ConfigHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
public class ImageShelter {
    @Getter private static ImageShelter instance;
    private Javalin javalin;
    private ConfigHandler configHandler;

    public ImageShelter() {
        instance = this;
        Stopwatch startWatch = Stopwatch.createStarted();

        try {
            log.info("Loading config...");
            this.configHandler = new ConfigHandler();
        }catch (IOException ex) {
            log.error("Failed to load config. Stopping...");
            ex.printStackTrace();
            System.exit(1);
        }
        this.javalin = Javalin.create().start(getConfig().getPort());

        log.info("ImageShelter started at " + startWatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms.");
    }

    public Config getConfig() {
        return configHandler.getConfig();
    }

    public static void main(String... args) {
        new ImageShelter();
    }
}
