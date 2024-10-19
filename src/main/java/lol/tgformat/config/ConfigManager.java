package lol.tgformat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import lol.tgformat.Client;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.values.Setting;
import lol.tgformat.module.values.impl.*;
import lol.tgformat.utils.client.LogUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigManager {
    public static final List<LocalConfig> localConfigs = new ArrayList<>();
    public static File defaultConfig;

    public final File file = new File(Minecraft.getMinecraft().mcDataDir, "/" + Client.instance.getName() + "/Configs");
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public void collectConfigs() {
        localConfigs.clear();
        file.mkdirs();
        //For each config in the config folder it adds it to the list and removes the ".json" from the name
        Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(f -> localConfigs.add(new LocalConfig(f.getName().split("\\.")[0])));
    }

    /**
     * Saving config method
     *
     * @see ConfigManager#serialize() to serialize the modules and settings
     */
    public boolean saveConfig(String name, String content) {
        LocalConfig localConfig = new LocalConfig(name);
        localConfig.getFile().getParentFile().mkdirs();
        try {
            Files.write(localConfig.getFile().toPath(), content.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveConfig(String name) {
        return saveConfig(name, serialize());
    }

    public boolean delete(String configName) {
        List<LocalConfig> configsMatch = localConfigs.stream().filter(localConfig -> localConfig.getName().equals(configName)).collect(Collectors.toList());
        try {
            LocalConfig configToDelete = configsMatch.get(0);
            Files.deleteIfExists(configToDelete.getFile().toPath());
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void saveDefaultConfig() {
        defaultConfig.getParentFile().mkdirs();
        try {
            Files.write(defaultConfig.toPath(), serialize().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save " + defaultConfig);
        }
    }

    public String serialize() {
        for (Module module : ModuleManager.modules) {
            List<ConfigSetting> settings = new ArrayList<>();
            for (Setting setting : module.getSettingsList()) {
                ConfigSetting cfgSetting = new ConfigSetting(null, null);
                cfgSetting.name = setting.name;
                cfgSetting.value = setting.getConfigValue();
                settings.add(cfgSetting);
            }
            module.cfgSettings = settings.toArray(new ConfigSetting[0]);
        }
        return gson.toJson(ModuleManager.modules);
    }

    public String readConfigData(Path configPath) {
        try {
            return new String(Files.readAllBytes(configPath));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void loadConfig(String data) {
        Module[] modules = gson.fromJson(data, Module[].class);

        for (Module module : ModuleManager.modules) {
            for (Module configModule : modules) {
                if (module.getName().equalsIgnoreCase(configModule.getName())) {
                    try {
                        if (module.isState() != configModule.isState()) {
                            module.state();
                        }
                        for (Setting setting : module.getSettingsList()) {
                            for (ConfigSetting cfgSetting : configModule.cfgSettings) {
                                if (setting.name.equals(cfgSetting.name)) {
                                    if (setting instanceof KeybindSetting) {
                                        module.setKey(Double.valueOf(String.valueOf(cfgSetting.value)).intValue());
                                    }
                                    if (setting instanceof BooleanSetting) {
                                        ((BooleanSetting) setting).setState(Boolean.parseBoolean(String.valueOf(cfgSetting.value)));
                                    }
                                    if (setting instanceof ModeSetting ms) {
                                        String value = String.valueOf(cfgSetting.value);
                                        if (ms.modes.contains(value)) {
                                            ms.setCurrentMode(value);
                                        } else {
                                            ms.setCurrentMode(ms.modes.getFirst());
                                        }
                                    }
                                    if (setting instanceof NumberSetting ss) {
                                        double value;
                                        try {
                                            value = Double.parseDouble(String.valueOf(cfgSetting.value));
                                        } catch (NumberFormatException e) {
                                            value = ss.getDefaultValue();
                                        }
                                        ss.setValue(value);
                                    }
                                    if (setting instanceof MultipleBoolSetting) {
                                        LinkedTreeMap<String, Boolean> boolMap = (LinkedTreeMap<String, Boolean>) cfgSetting.value;
                                        MultipleBoolSetting mbs = (MultipleBoolSetting) setting;
                                        for (String s : boolMap.keySet()) {
                                            BooleanSetting childSetting = mbs.getSetting(s);
                                            if (childSetting != null && boolMap.get(s) != null) {
                                                childSetting.setState(boolMap.get(s));
                                            }
                                        }
                                    }
                                    if (setting instanceof ColorSetting colorSetting) {
                                        if (JsonParser.parseString(cfgSetting.value.toString()).isJsonObject()) {
                                            JsonObject colorObject = JsonParser.parseString(cfgSetting.value.toString()).getAsJsonObject();
                                            colorSetting.setRainbow(true);
                                            float saturation = colorObject.get("saturation").getAsFloat();
                                            int speed = colorObject.get("speed").getAsInt();
                                            colorSetting.getRainbow().setSaturation(saturation);
                                            colorSetting.getRainbow().setSpeed(speed);
                                        } else {
                                            int color = Double.valueOf(String.valueOf(cfgSetting.value)).intValue();
                                            Color c = new Color(color);
                                            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                                            colorSetting.setColor(hsb[0], hsb[1], hsb[2]);
                                        }
                                    }
                                    if (setting instanceof StringSetting) {
                                        String value = String.valueOf(cfgSetting.value);
                                        if (value != null) {
                                            ((StringSetting) setting).setString(value);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.print(e.getMessage());
                    }
                }
            }
        }
    }
}
