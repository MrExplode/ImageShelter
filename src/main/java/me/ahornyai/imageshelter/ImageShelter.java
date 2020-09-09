package me.ahornyai.imageshelter;

import com.google.common.base.Stopwatch;
import io.javalin.Javalin;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public class ImageShelter {
    @Getter private static ImageShelter instance;
    private Javalin javalin;

    public ImageShelter() {
        instance = this;
        Stopwatch startWatch = Stopwatch.createStarted();

        this.javalin = Javalin.create().start();

        System.out.println("ImageShelter started at " + startWatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms.");
    }

    public static void main(String... args) {
        new ImageShelter();
    }
}
