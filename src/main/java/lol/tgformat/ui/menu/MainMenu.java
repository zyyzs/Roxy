package lol.tgformat.ui.menu;

import lol.tgformat.Client;
import lol.tgformat.ui.altlogin.GuiAltManager;
import lol.tgformat.ui.menu.utils.VideoPlayer;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.client.RegionalAbuseUtil;
import lol.tgformat.utils.timer.MSTimer;

import lol.tgformat.verify.GuiLogin;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.netease.utils.RenderUtil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.lwjgl.opengl.Display;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author KuChaZi
 * @Date 2024/6/24 14:39
 * @ClassName: MainMenu
 */

@Renamer
@StringEncryption
public class MainMenu extends GuiScreen implements GuiYesNoCallback {
    private float currentX = 0f;
    private float currentY = 0f;
    private int photo = 1;
    private final MSTimer time = new MSTimer();
    public MainMenu() {
    }
    private final List<MenuButton> buttons = new ArrayList<>() {{
        add(new MenuButton("SinglePlayer"));
        add(new MenuButton("MultiPlayer"));
        add(new MenuButton("AltManager"));
        add(new MenuButton("Settings"));
        add(new MenuButton("Exit"));
    }};
    @Override
    public void initGui() {
        buttons.forEach(MenuButton::initGui);
        while (!Client.instance.XuJingLiangSiMa.equals("许锦良死妈")) {
            try {
                Thread.sleep(500);
                Runtime.getRuntime().gc();
            } catch (InterruptedException sbxujingl) {
                Runtime.getRuntime().gc();
            }
        }
        super.initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        while (!Client.instance.XuJingLiangSiMa.equals("许锦良死妈")) {
            try {
                Thread.sleep(500);
                Runtime.getRuntime().gc();
            } catch (InterruptedException sbxujingl) {
                Runtime.getRuntime().gc();
            }
        }
        ScaledResolution sr = new ScaledResolution(mc);
        int h = sr.getScaledHeight();
        int w = sr.getScaledWidth();
        float xDiff = ((mouseX - h / 2f) - this.currentX) / sr.getScaleFactor();
        float yDiff = ((mouseY - w / 2f) - this.currentY) / sr.getScaleFactor();
        this.currentX += xDiff * 0.3f;
        this.currentY += yDiff * 0.3f;

        GlStateManager.translate(this.currentX / 30.0f, this.currentY / 15.0f, 0.0f);
        //RenderUtil.drawImageTest(drawBackGround(), -30, -30, sr.getScaledWidth() + 60, sr.getScaledHeight() + 60);
        VideoPlayer.render(w, h);
        GlStateManager.translate(-this.currentX / 30.0f, -this.currentY / 15.0f, 0.0f);

        float buttonWidth = 140;
        float buttonHeight = 25;
        int count = 0;
        for (MenuButton button : buttons) {
            button.x = width / 2f - buttonWidth / 2f;
            button.y = ((height / 2f - buttonHeight / 2f) - 25) + count;
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "SinglePlayer":
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "MultiPlayer":
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case "AltManager": {
                        mc.displayGuiScreen(new GuiAltManager(this));
                        break;
                    }
                    case "Settings":
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case "Exit":
                        mc.shutdown();
                        break;
                }
            };
            button.drawScreen(mouseX, mouseY);
            count += (int) (buttonHeight + 5);
        }

        Display.setTitle("Minecraft 1.8.9 " + " Use: " + GuiLogin.uid + "[" + ("地球入") + "]");

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
    }
}

