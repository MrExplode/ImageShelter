package me.ahornyai.imageshelter.http;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.plugin.json.JavalinJson;
import me.ahornyai.imageshelter.http.endpoints.UploadEndpoint;
import me.ahornyai.imageshelter.http.endpoints.ViewEndpoint;

public class HttpHandler {
    private static final Gson GSON = new Gson();
    private final Javalin javalin;

    public HttpHandler(int port) {
        this.javalin = Javalin.create().start(port);;

        setupJavalinJson();
        makeEndpoints();
    }

    private void setupJavalinJson() {
        JavalinJson.setFromJsonMapper(GSON::fromJson);
        JavalinJson.setToJsonMapper(GSON::toJson);
    }

    private void makeEndpoints() {
        javalin.get("/:file/:key", new ViewEndpoint());
        javalin.post("/upload", new UploadEndpoint());
    }

    public void stop() {
        javalin.stop();
    }
}
