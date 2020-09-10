package me.ahornyai.imageshelter.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter
public class ConfigHandler {
    private Config config;

    public  ConfigHandler() throws IOException {
        loadOrSave();
    }

    public void loadOrSave() throws IOException {
        try {
            this.config = new Toml().read(new File("config.toml")).to(Config.class);
        }catch (Exception ex) {
            this.config = new Config();

            TomlWriter writer = new TomlWriter();
            writer.write(config, new File("config.toml"));
        }
    }

}
