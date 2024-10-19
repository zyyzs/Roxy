package lol.tgformat;

import lol.tgformat.component.*;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import lol.tgformat.api.event.EventManager;
import lol.tgformat.config.ConfigManager;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.ui.drag.DragManager;
import lol.tgformat.ui.drag.Dragging;
import lol.tgformat.ui.menu.MainMenu;
import lol.tgformat.ui.clickgui.ModuleCollection;
import lol.tgformat.ui.clickgui.SideGUI;
import lol.tgformat.ui.font.FontUtil;
import lol.tgformat.ui.menu.utils.VideoPlayer;
import lol.tgformat.component.MovementComponent;
import lol.tgformat.component.PacketStoringComponent;
import lol.tgformat.utils.player.BlinkHandler;
import lol.tgformat.utils.render.Theme;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.netease.PacketProcessor;
import net.netease.chunk.WorldLoader;
import net.netease.font.FontManager;
import net.viamcp.ViaLoading;
import net.viamcp.vialoadingbase.ViaLoadingBase;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author TG_format
 * @since 2024/5/31 18:35
 */
@Getter
@Renamer
@StringEncryption
@NativeObfuscation
public enum Client {
    instance,;
    private final String name = "Roxy";
    private final String date = "1019";
    private final String version = "1.5";
    private ModuleManager moduleManager;
    private ConfigManager configManager;
    private VideoComponent videoComponent;
    private ModuleCollection moduleCollection;
    private SlotSpoofComponent slotSpoofComponent;
    private BadPacketsComponent badPacketsComponent;
    private SideGUI sideGui;
    private final HashMap<Object, Module> moduleMap = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    public String XuJingLiangSiMa = "许锦良死妈";

    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
    public void onStart() {
        FontManager.init();
        FontUtil.setupFonts();
        Theme.init();
        this.moduleManager = new ModuleManager();
        this.moduleManager.initialize();
        this.moduleCollection = new ModuleCollection();
        for (Module module : ModuleManager.getModules()) {
            moduleMap.put(module.getClass(), module);
        }
        this.moduleCollection.setModules(moduleMap);

        ViaLoading.load();

        EventManager.register(this.moduleManager);
        EventManager.register(new BadPacketUComponent());
        EventManager.register(new PacketStoringComponent());
        EventManager.register(MovementComponent.INSTANCE);
        EventManager.register(new RotationComponent());
        EventManager.register(new PacketProcessor());
        EventManager.register(new WorldLoader());
        EventManager.register(new CommandComponent());
        EventManager.register(new BlinkHandler());

        this.badPacketsComponent = new BadPacketsComponent();
        this.slotSpoofComponent = new SlotSpoofComponent();
        this.videoComponent = new VideoComponent();

        VideoPlayer.init(new File(Minecraft.getMinecraft().mcDataDir, "background.mp4"));
        this.sideGui = new SideGUI();

        this.configManager = new ConfigManager();
        ConfigManager.defaultConfig = new File(Minecraft.getMinecraft().mcDataDir + "/" + name +"/Config.json");
        this.configManager.collectConfigs();

        if (ConfigManager.defaultConfig.exists()) {
            this.configManager.loadConfig(this.configManager.readConfigData(ConfigManager.defaultConfig.toPath()));
        }
        ViaLoadingBase.getInstance().reload(ProtocolVersion.v1_12_2);
    }

    @NativeObfuscation(verificationLock = "User")
    public void onStop() {
        this.configManager.saveDefaultConfig();
        VideoPlayer.stop();

    }
    public Dragging createDrag(Module module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }
    public GuiScreen getMainMenu() {
        return new MainMenu();
    }
}
