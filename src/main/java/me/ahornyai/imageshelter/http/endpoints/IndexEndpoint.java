package me.ahornyai.imageshelter.http.endpoints;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import lombok.extern.slf4j.Slf4j;
import me.ahornyai.imageshelter.ImageShelter;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Slf4j
public class IndexEndpoint implements Handler {
    @Override
    public void handle(@NotNull Context ctx) {
        int imageCount = 0;

        File folder = new File(ImageShelter.getInstance().getConfig().getUploadFolder());

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null)
                imageCount = files.length;
        }

        ctx.html("<h3>Stored images: " + imageCount + "</h3><a href=\"https://github.com/ahornyai/ImageShelter\">GitHub</a>");
    }
}
