package lol.tgformat;

import lol.tgformat.component.*;
import lol.tgformat.irc.network.SocketManager;
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
import lol.tgformat.verify.GuiLogin;
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
    instance;
    private final String name = "BloodLine_Recode";
    private final String date = "20240822";
    private final String version = "1.0";
    private String username;
    private ModuleManager moduleManager;
    private ConfigManager configManager;
    private VideoComponent videoComponent;
    private ModuleCollection moduleCollection;
    private SlotSpoofComponent slotSpoofComponent;
    private BadPacketsComponent badPacketsComponent;
    private SideGUI sideGui;
    private SocketManager ircServer;
    public static final int validationStatus = 0;
    private final HashMap<Object, Module> moduleMap = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @NativeObfuscation.Inline
    public void onStart() {
        FontManager.init();
        FontUtil.setupFonts();
        Theme.init();
        username = GuiLogin.uid;
        this.moduleManager = new ModuleManager();
        this.moduleManager.initialize();
        this.moduleCollection = new ModuleCollection();
        for (Module module : ModuleManager.getModules()) {
            moduleMap.put(module.getClass(), module);
        }
        this.moduleCollection.setModules(moduleMap);

        ViaLoading.load();

        EventManager.register(this.moduleManager);
        EventManager.register(new FuckLagComponent());
        EventManager.register(new PacketStoringComponent());
        EventManager.register(MovementComponent.INSTANCE);
        EventManager.register(new RotationComponent());
        EventManager.register(new PacketProcessor());
        EventManager.register(new WorldLoader());
        EventManager.register(new CommandComponent());
        EventManager.register(new BlinkHandler());

        ircServer = new SocketManager();
        ircServer.initialize();
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

    @NativeObfuscation.Inline
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
