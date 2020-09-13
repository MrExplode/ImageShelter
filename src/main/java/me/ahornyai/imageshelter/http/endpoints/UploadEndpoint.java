package me.ahornyai.imageshelter.http.endpoints;

import io.javalin.core.util.FileUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import me.ahornyai.imageshelter.ImageShelter;
import me.ahornyai.imageshelter.http.responses.ErrorResponse;
import me.ahornyai.imageshelter.http.responses.SuccessUploadResponse;
import me.ahornyai.imageshelter.utils.AESUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UploadEndpoint implements Handler {
    private static final String[] ALLOWED_EXTENSIONS = ImageShelter.getInstance().getConfig().getAllowedExtensions();

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

        if (Arrays.stream(ALLOWED_EXTENSIONS).noneMatch(uploadedFile.getExtension().substring(1)::equalsIgnoreCase)) {
            ctx.json(new ErrorResponse("WRONG_EXTENSION", "Wrong extension (" + uploadedFile.getExtension().substring(1) + "). Supported extensions: " + ALLOWED_EXTENSIONS))
                    .status(400);

            return;
        }

        //TODO: compressing, secure random filename
        String name = RandomStringUtils.randomAlphanumeric(32) + uploadedFile.getExtension();

        try {
            SecretKey key = AESUtil.generateKey();
            byte[] IV = name.substring(0, 16).getBytes(StandardCharsets.UTF_8);
            byte[] file = IOUtils.toByteArray(uploadedFile.getContent());
            byte[] encrypted = AESUtil.encrypt(file, key, IV);

            FileUtils.writeByteArrayToFile(new File("uploads/" + name), encrypted);

            ctx.json(new SuccessUploadResponse(name, AESUtil.getKeyAsString(key)));
        }catch (Exception ex) {
            ex.printStackTrace();
            ctx.json(new ErrorResponse("ENCRYPTION_ERROR", "Unexpected error with encryption. Please open a github issue."));
        }
    }
}
