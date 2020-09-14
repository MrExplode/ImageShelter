package me.ahornyai.imageshelter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.ahornyai.imageshelter.config.Config;
import me.ahornyai.imageshelter.config.ConfigHandler;
import me.ahornyai.imageshelter.http.HttpHandler;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
public class ImageShelter {
    @Getter private static ImageShelter instance;
    private final HttpHandler httpHandler;
    private ConfigHandler configHandler;

    public ImageShelter() {
        instance = this;
        StopWatch startWatch = StopWatch.createStarted();

        try {
            log.info("Loading config...");
            this.configHandler = new ConfigHandler();
        }catch (IOException ex) {
            log.error("Failed to load config. Stopping...");
            ex.printStackTrace();
            System.exit(1);
        }

        this.httpHandler = new HttpHandler(getConfig().getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(this::onStop));

        startWatch.stop();
        log.info("ImageShelter started at " + startWatch.getTime(TimeUnit.MILLISECONDS) + " ms.");
    }

    public void onStop() {
        httpHandler.stop();
    }

    public Config getConfig() {
        return configHandler.getConfig();
    }

    public static void main(String... args) {
        new ImageShelter();
    }
}
