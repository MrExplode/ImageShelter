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
