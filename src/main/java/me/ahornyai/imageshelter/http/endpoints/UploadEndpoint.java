/*
 * Copyright (c) 2020 Alex Hornyai
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.ahornyai.imageshelter.http.endpoints;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import lombok.extern.slf4j.Slf4j;
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
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
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

        String name = UUID.randomUUID() + uploadedFile.getExtension();
        String requestID = RandomStringUtils.randomAlphanumeric(16);
        SecretKey key;
        byte[] file;
        byte[] encrypted;

        try {
            file = IOUtils.toByteArray(uploadedFile.getContent());
        } catch (IOException ex) {
            log.error("File error (Request id: " + requestID + "):", ex);

            ctx.json(new ErrorResponse("UNEXPECTED_ERROR", "Unexpected error at file content converting to byte array. If you are the server owner please open a github issue with the exception. (Request id: " + requestID + ")"));
            return;
        }

        try {
            key = AESUtil.generateKey();
            encrypted = AESUtil.encrypt(file, key);
        }catch (Exception ex) {
            log.error("Encryption error (Request id: " + requestID + "):", ex);

            ctx.json(new ErrorResponse("UNEXPECTED_ERROR", "Unexpected error with encryption. If you are the server owner please open a github issue with the exception. (Request id: " + requestID + ")"));
            return;
        }

        try {
            FileUtils.writeByteArrayToFile(new File("uploads/" + name), encrypted);

            ctx.json(new SuccessUploadResponse(URLEncoder.encode(name, "UTF-8"), URLEncoder.encode(AESUtil.getKeyAsString(key), "UTF-8")));
        }catch (Exception ex) {
            log.error("File saving error (Request id: " + requestID + "):", ex);

            ctx.json(new ErrorResponse("UNEXPECTED_ERROR", "Unexpected error with file saving. If you are the server owner please open a github issue with the exception. (Request id: " + requestID + ")"));
        }
    }
}
