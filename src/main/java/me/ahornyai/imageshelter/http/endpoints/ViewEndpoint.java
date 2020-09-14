package me.ahornyai.imageshelter.http.endpoints;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import me.ahornyai.imageshelter.ImageShelter;
import me.ahornyai.imageshelter.http.responses.ErrorResponse;
import me.ahornyai.imageshelter.utils.AESUtil;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ViewEndpoint implements Handler {
    private static String EXPECTED_PATH = null;

    @Override
    public void handle(@NotNull Context ctx) throws IOException {
        if (EXPECTED_PATH == null)
            EXPECTED_PATH = new File(ImageShelter.getInstance().getConfig().getUploadFolder()).getCanonicalPath();

        String fileParam = ctx.pathParam("file");
        String keyParam = ctx.pathParam("key");

        File file = new File(ImageShelter.getInstance().getConfig().getUploadFolder(), fileParam);

        if (!file.getCanonicalPath().startsWith(EXPECTED_PATH)) {
            ctx.redirect("https://www.youtube.com/watch?v=dQw4w9WgXcQ");

            return;
        }

        if (!file.exists()) {
            ctx.json(new ErrorResponse("FILE_DOES_NOT_EXIST","This file does not exist."));

            return;
        }

        if (!file.canRead()) {
            ctx.json(new ErrorResponse("READ_PERMISSION","I don't have read permission to uploads/" + fileParam + " file."));

            return;
        }

        //TODO: compress
        SecretKey secretKey;
        byte[] encrypted;
        byte[] decrypted;

        try {
            secretKey = AESUtil.getKeyFromString(keyParam);
        }catch (Exception ex) {
            ctx.json(new ErrorResponse("BAD_KEY_FORMAT", "Bad key format."));

            return;
        }

        try {
            encrypted = FileUtils.readFileToByteArray(file);
        }catch (Exception ex) {
            ctx.json(new ErrorResponse("READ_ERROR", "Failed to read this file."));

            return;
        }

        try {
            decrypted = AESUtil.decrypt(encrypted, secretKey);
        }catch (Exception ex) {
            ctx.json(new ErrorResponse("DECRYPT_ERROR", "Failed to decrypt (Bad key?)."));

            return;
        }

        String contentType = Files.probeContentType(file.toPath());

        if (contentType != null) {
            ctx.res.addHeader("Content-Type", contentType);
        }

        ctx.result(decrypted);
    }
}
