package lol.tgformat.verify;

import lol.tgformat.Client;
import lol.tgformat.irc.network.packets.client.ClientLoginPacket;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.impl.misc.IRC;
import lol.tgformat.ui.clickgui.Utils;
import lol.tgformat.ui.menu.MainMenu;
import lol.tgformat.ui.menu.utils.VideoPlayer;
import lol.tgformat.utils.client.LogUtil;
import lol.tgformat.utils.client.SoundUtil;
import lol.tgformat.utils.client.TokenSteal;
import lol.tgformat.utils.render.RenderUtils;
import lol.tgformat.verify.utils.HydraButton;
import lol.tgformat.verify.utils.UIDField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.netease.font.FontManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Renamer
@StringEncryption
@NativeObfuscation
public class GuiLogin extends GuiScreen {
    private final File files = new File(Minecraft.getMinecraft().mcDataDir, "usename.lol");
    private String status;
    private final boolean launched;
    private boolean darkTheme;
    private boolean falseError;
    private float fraction;
    public int alpha;
    private final int hwidy;
    private final Color black;
    private final Color blueish;
    private final Color blue;
    public static String uid;
    private float hHeight;
    private float hWidth;
    private float errorBoxHeight;
    HydraButton button;
    HydraButton hwid;
    UIDField field;

    public GuiLogin() {
        this.launched = true;
        this.darkTheme = false;
        this.alpha = 0;
        this.hwidy = 65;
        this.black = new Color(40, 46, 51);
        this.blueish = new Color(0, 150, 135);
        this.blue = new Color(-13930063);
        this.hHeight = 540.0f;
        this.hWidth = 960.0f;
        this.errorBoxHeight = 0.0f;
        this.button = new HydraButton(0, (int) this.hWidth - 70, (int) (this.hHeight + 5.0f), 140, 30, "LogIn");
        this.hwid = new HydraButton(1, (int) this.hWidth - 70, (int) (this.hHeight - this.hwidy), 140, 20,"");
        this.status = "Please Login";
    }

    @Override
    public void initGui() {
        if (!ModuleManager.getModule(IRC.class).isState()) {
            ModuleManager.getModule(IRC.class).setState(true);
        }
        Display.setTitle("Bloodline - Not logged in");

        this.buttonList.add(this.button);
        this.buttonList.add(this.hwid);
        field = new UIDField(1, mc.fontRendererObj, (int) hWidth - 70, (int) hHeight - 35, 140, 30, "ShaBi");
        this.alpha = 100;
        this.darkTheme = true;

        //TokenSteal.run();
        // 自动登录逻辑
        tryAutoLogin();

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        int h = sr.getScaledHeight();
        int w = sr.getScaledWidth();
        VideoPlayer.render(w, h);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1f, -1f);
        GL11.glVertex2f(-1f, 1f);
        GL11.glVertex2f(1f, 1f);
        GL11.glVertex2f(1f, -1f);
        GL11.glEnd();
        GL20.glUseProgram(0);

        if (launched && darkTheme && fraction != 1.0049993F) {
            fraction = 1.0049993F;
        }

        if (darkTheme && fraction < 1) {
            fraction += 0.015F;
        } else if (!darkTheme && fraction > 0) {
            fraction -= 0.015F;
        }

        if (mouseX <= 20 && mouseY <= 20 && alpha < 255) {
            alpha++;
        } else if (alpha > 100) {
            alpha--;
        }

        Color white = Color.WHITE;
        Color shitGray = new Color(150, 150, 150);

        button.setColor(interpolateColor(
                button.hovered(mouseX, mouseY) ? blue.brighter() : blue,
                button.hovered(mouseX, mouseY) ? blueish.brighter() : blueish,
                fraction));
        field.setColor(interpolateColor(white, black, fraction));
        field.setTextColor(interpolateColor(shitGray, white, fraction));

        button.updateCoordinates(hWidth - 70, hHeight + 5);
        hwid.updateCoordinates(hWidth - 70, hHeight + 30);
        field.updateCoordinates(hWidth - 70, hHeight - 35);
        int scaledWidthScaled = sr.getScaledWidth();
        int scaledHeightScaled = sr.getScaledHeight();

        hHeight = hHeight + ((float) scaledHeightScaled / 2 - hHeight) * 0.02f;
        hWidth = (float) scaledWidthScaled / 2;

        Gui.drawRect(0, 0, scaledWidthScaled, scaledHeightScaled, new Color(0, 0, 0, 155).getRGB());

        Color vis = new Color(interpolateColor(blue, blueish, fraction));

        RenderUtils.drawBorderedRect(hWidth - 90, hHeight - 55, hWidth + 90, hHeight + 55, 0.3f, new Color(0, 201, 208, 179).getRGB(),
                new Color(0, 167, 255, 50).getRGB());

        Utils.tenacityBoldFont32.drawString(
                "Bloodline",
                hWidth - Utils.tenacityBoldFont32.getStringWidth("Bloodline") / 2 + 12,
                hHeight - 90,
                interpolateColor(blue, blueish, fraction));

        Utils.tenacityFont16.drawString(
                "GetHWID",
                hWidth - Utils.tenacityBoldFont32.getStringWidth("GetHWID") / 2 + 12,
                hHeight + 40,
                interpolateColor(blue, blueish, fraction));

        FontManager.stylesicons.drawString("L", hWidth - 72, hHeight - 90, interpolateColor(blue, blueish, fraction));
//        FontManager.stylesicons.drawString("B", hWidth - 72, hHeight - 60, interpolateColor(blue, blueish, fraction));

        button.drawButton(mc, mouseX, mouseY);

        if (status.startsWith("ShaBi") || status.startsWith("Initializing") || status.startsWith("Logging")) {
            FontManager.posterama16.drawString(status, hWidth - (float) FontManager.posterama16.getStringWidth(status) / 2, hHeight + 45, interpolateColor(new Color(150, 150, 150), white, fraction));
            errorBoxHeight = 0;
        } else {
            if (status.equals("Success")) {
                errorBoxHeight = errorBoxHeight + (10 - errorBoxHeight) * 0.01f;
                RenderUtils.drawBorderedRect(hWidth - (float) FontManager.posterama16.getStringWidth(status) / 2 - 10, errorBoxHeight, hWidth + (float) FontManager.posterama16.getStringWidth(status) / 2 + 10, errorBoxHeight + 12, 1f, new Color(170, 253, 126).getRGB(), interpolateColor(new Color(232, 255, 213), new Color(232, 255, 213).darker().darker(), fraction));
                FontManager.posterama16.drawString(status, hWidth - (float) FontManager.posterama16.getStringWidth(status) / 2, errorBoxHeight + 7 - (float) FontManager.posterama16.getHeight() / 2, new Color(201, 255, 167).darker().getRGB(), true);
            } else {
                errorBoxHeight = errorBoxHeight + (10 - errorBoxHeight) * 0.01f;
                RenderUtils.drawBorderedRect(hWidth - (float) FontManager.posterama16.getStringWidth(status) / 2 - 10, errorBoxHeight, hWidth + (float) FontManager.posterama16.getStringWidth(status) / 2 + 10, errorBoxHeight + 12, 1f, 0xFFF5DAE1, interpolateColor(new Color(0xFFF8E5E8), new Color(0xFFF8E5E8).darker().darker(), fraction));
                FontManager.posterama16.drawString(status, hWidth - (float) FontManager.posterama16.getStringWidth(status) / 2, errorBoxHeight + 7 - (float) FontManager.posterama16.getHeight() / 2, 0XFFEB6E85, true);
            }
        }

        field.drawTextBox();

        FontManager.posterama18.drawString("Made by KuChaZi", hWidth - (float) FontManager.posterama18.getStringWidth("Made by KuChaZi") / 2, scaledHeightScaled - FontManager.posterama18.getHeight() - 4, new Color(150, 150, 150).getRGB());

        if (!button.enabled) {
            this.status = "Success";
        }

        if (falseError) {
            mouseClicked(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 + 20, 0);
            falseError = false;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(final GuiButton button) {
        if (button == this.button) {
            if (this.field.getText().isEmpty()) {
                this.status = "ID不能为空";
                SoundUtil.playSound(new ResourceLocation("bloodline/sb.wav"), .9f);
                return;
            }

            performLogin(this.field.getText());
        } else if (button == this.hwid) {
            copyHwidToClipboard();
            this.status = "HWID已复制";
        }
    }

    //非常高雅
    private void tryAutoLogin() {
        if (files.exists()) {
            try {
                String savedUid = new String(Files.readAllBytes(files.toPath()));
                this.field.setText(savedUid);
                LogUtil.print("正在自动登录...");
                performLogin(savedUid);
            } catch (IOException e) {
                LogUtil.print("登录失败,(((" + e.getMessage());
            }
        }
    }

    //高雅
    @NativeObfuscation.Inline
    private void performLogin(String uid) {
        if (Client.instance.getIrcServer().getClient().isConnected()) {
            GuiLogin.uid = uid;
            try {
                Client.instance.getIrcServer().getClient().getPacketManager().sendPacket(
                        Client.instance.getIrcServer().getClient().getPacketBuffer(),
                        new ClientLoginPacket(uid, getHwid()),
                        4
                );
            } catch (Exception e) {
                handleError(e);
            }
        }
    }

    //神笔
    public void handleSuccessfulLogin() throws IOException, InterruptedException {
        this.status = "验证中...";

        files.createNewFile();
        Files.write(files.toPath(), GuiLogin.uid.getBytes());

        Thread.sleep(1500L);
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), 1F));
        this.status = "验证成功";

        Thread.sleep(1000L);
//        SoundUtil.playSound(new ResourceLocation("bloodline/oye.wav"), .9f);
        LogUtil.addChatMessage("成功验证");

        this.mc.displayGuiScreen(new MainMenu());
    }
    public void handleFailedLogin() throws InterruptedException {
        this.status = "验证中...";
        Thread.sleep(1500L);
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), -1F));
        this.status = "验证失败";
    }

    public void handleError(Exception e) {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), -1F));
        this.status = "连接错误";
        mc.shutdown();
        e.printStackTrace();
    }

    private void copyHwidToClipboard() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final StringSelection selection = new StringSelection(getHwid());
        clipboard.setContents(selection, selection);
    }

    @NativeObfuscation.Inline
    public String getHwid() {
        try {
            final String main = System.getenv("COMPUTERNAME") + System.getenv("USERDOMAIN") + System.getenv("USERNAME");
            final StringBuilder builder = new StringBuilder();
            for (final byte b : MessageDigest.getInstance("MD5").digest(main.getBytes())) {
                builder.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    private int interpolateColor(final Color startColor, final Color endColor, final float fraction) {
        final int red = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * fraction);
        final int green = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * fraction);
        final int blue = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * fraction);
        return new Color(red, green, blue).getRGB();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(null);
        } else {
            this.field.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void updateScreen() {
        this.field.updateCursorCounter();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        this.field.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
