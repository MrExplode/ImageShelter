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
