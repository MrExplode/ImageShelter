package me.ahornyai.imageshelter.http.endpoints;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import me.ahornyai.imageshelter.ImageShelter;
import me.ahornyai.imageshelter.http.responses.ErrorResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

public class UploadEndpoint implements Handler {
    @Override
    public void handle(@NotNull Context ctx) {
        if (!ctx.isMultipartFormData()) {
            ctx.json(new ErrorResponse("NOT_FORM_DATA", "The request's content type is not multipart/form-data."))
                .status(404);

            return;
        }

        UploadedFile uploadedFile = ctx.uploadedFile("image");
        String secret = ctx.formParam("secret");

        if (secret == null) {
            ctx.json(new ErrorResponse("MISSING_SECRET", "I need a secret."))
                    .status(404);

            return;
        }

        if (!ArrayUtils.contains(ImageShelter.getInstance().getConfig().getSecrets(), secret)) {
            ctx.json(new ErrorResponse("WRONG_SECRET", "Wrong secret."))
                    .status(403);

            return;
        }

        if (uploadedFile == null) {
            ctx.json(new ErrorResponse("MISSING_IMAGE", "I need an image."))
                    .status(400);

            return;
        }

        //TODO: file validation

        ctx.result("upload endpoint secret: " + secret);
    }
}
