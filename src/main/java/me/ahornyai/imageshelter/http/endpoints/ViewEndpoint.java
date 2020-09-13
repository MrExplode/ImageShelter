package me.ahornyai.imageshelter.http.endpoints;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import me.ahornyai.imageshelter.http.responses.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class ViewEndpoint implements Handler {
    private static final Pattern PATH_TRAVERSAL = Pattern.compile("[^0-9A-Za-z.]");

    @Override
    public void handle(@NotNull Context ctx) throws IOException {
        String fileParam = ctx.pathParam("file");
        String keyParam = ctx.pathParam("key");

        if (PATH_TRAVERSAL.matcher(fileParam).find()) {
            ctx.redirect("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
            return;
        }

        File file = new File("uploads" + File.separator + fileParam);

        if (!file.exists()) {
            ctx.json(new ErrorResponse("FILE_DOES_NOT_EXIST","This file does not exist."));

            return;
        }

        if (!file.canRead()) {
            ctx.json(new ErrorResponse("READ_PERMISSION","I don't have read permission to uploads/" + fileParam + " file."));

            return;
        }

        String contentType = Files.probeContentType(file.toPath());

        if (contentType != null) {
            ctx.res.addHeader("Content-Type", contentType);
        }
        //TODO: decrypt, compress

        ctx.result(new FileInputStream(file));
    }
}
