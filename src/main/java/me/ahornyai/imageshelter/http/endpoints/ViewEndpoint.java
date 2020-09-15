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
