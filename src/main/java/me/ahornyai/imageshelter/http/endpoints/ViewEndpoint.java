package me.ahornyai.imageshelter.http.endpoints;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class ViewEndpoint implements Handler {
    @Override
    public void handle(@NotNull Context ctx) {
        //TODO: path traversal protection

        ctx.result("view endpoint params: " + ctx.pathParam("file") + " " + ctx.pathParam("key"));
    }
}
