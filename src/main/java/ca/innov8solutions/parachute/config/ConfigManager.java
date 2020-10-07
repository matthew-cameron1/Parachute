package ca.innov8solutions.parachute.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;

@Singleton
public class ConfigManager<T> {

    private Plugin plugin;
    private String fileName;
    private Class clazz;
    private Gson gson;
    private String directory;

    @Getter
    private T config;

    public @Inject
    ConfigManager(Plugin plugin, String fileName, Class clazz) {
        this.plugin = plugin;
        this.directory = plugin.getDataFolder().getName();
        this.fileName = fileName;
        this.clazz = clazz;
        this.gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).setPrettyPrinting().create();
    }

    public void init() {
        File f = new File("plugins/" + directory);
        if (!f.exists()) {
            f.mkdir();
        }
        File file = new File("plugins/" + directory + "/" + fileName);
        if (!file.exists()) {
            URL url = plugin.getClass().getResource("/" + fileName);
            try {
                System.out.println("[Parachute] COPYING DEFAULT CONFIG: " + fileName); //todo remove statement if not needed, document code instead
                FileUtils.copyURLToFile(url, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadConfig();
    }

    public void loadConfig() {
        FileReader reader = null;
        try {
            reader = new FileReader(new File("plugins/" + directory + "/" + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.config = (T) gson.fromJson(reader, clazz);

    }

    public void saveConfig() {
        FileWriter fw = null;
        try {
            fw = new FileWriter("plugins/" + directory + "/" + fileName);
            gson.toJson(config, fw);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}