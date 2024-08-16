package lol.tgformat.ui.menu.utils;

import lol.tgformat.utils.render.RenderUtils;
import lombok.SneakyThrows;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameGrabber.Exception;
import org.bytedeco.javacv.Frame;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * @Author KuChaZi
 * @Date 2024/7/12 14:08
 * @ClassName: VideoPlayer
 */

@Renamer
@StringEncryption
public class VideoPlayer {
    private static FFmpegFrameGrabber frameGrabber;
    private static Renderer renderer;
    private static double frameRate;
    private static int ticks;
    private static boolean flag;
    private static long time;
    private static Tessellator tessellator;
    private static WorldRenderer worldRenderer;
    public static boolean suspended = false;
    private static boolean stopped = false;

    // 初始化视频播放器，加载视频文件
    @SneakyThrows
    public static void init(File file) {
        frameGrabber = new FFmpegFrameGrabber(file.getPath());
        frameGrabber.setPixelFormat(2);
        frameGrabber.setOption("loglevel", "quiet");
        renderer = new Renderer();
        tessellator = Tessellator.getInstance();
        worldRenderer = tessellator.getWorldRenderer();
        time = 0L;
        ticks = 0;
        flag = false;
        stopped = false;
        frameGrabber.start();
        frameRate = frameGrabber.getFrameRate();

        Frame frame;
        do {
            frame = frameGrabber.grab();
        } while (frame != null && frame.image == null);

        if (frame != null) {
            renderer.setBuffer((ByteBuffer) frame.image[0], frame.imageWidth, frame.imageHeight);
            time = System.currentTimeMillis();
            ++ticks;
            Thread thread = getThread();
            thread.start();
        }
    }


    // 获取视频处理线程
    private static Thread getThread() {
        Thread thread = new Thread("Video Background") {
            @Override
            public void run() {
                try {
                    while (true) {
                        synchronized (this) {
                            if (!flag || ((System.currentTimeMillis() - time) > 700.0 / frameRate && !suspended)) {
                                VideoPlayer.doGetBuffer();
                            }
                            if (time == 0) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.interrupt();
                }
            }
        };
        thread.setDaemon(true);
        return thread;
    }

    // 获取视频帧缓冲区
    private static void doGetBuffer() throws Exception {
        int fLength = frameGrabber.getLengthInFrames() - 5;
        if (ticks < fLength) {
            Frame frame = frameGrabber.grab();
            if (frame != null && frame.image != null) {
                if (renderer != null) { // 检查 renderer 是否为空
                    renderer.setBuffer((ByteBuffer)frame.image[0], frame.imageWidth, frame.imageHeight);
                    time = System.currentTimeMillis();
                    ++ticks;
                } else {
                    // 处理 renderer 为空的情况
                    System.err.println("Renderer Error");
                }
            }
        } else {
            ticks = 0;
            frameGrabber.setFrameNumber(0);
        }

        if (!flag) {
            flag = true;
        }
    }

    // 渲染视频帧
    public static void render(int width, int height) {
        if (!stopped) {
            suspended = false;
            renderer.onDrawFrame();

            GlStateManager.enableBlend();
            GL11.glDisable(GL11.GL_DEPTH_TEST); // glDisable(2929)
            GL11.glEnable(GL11.GL_BLEND); // glEnable(3042)

            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO); // glBlendFunc(770, 771, 1, 0)

            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // Color values should be between 0.0F and 1.0F
            GlStateManager.disableAlpha();

            // Set up orthographic projection
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GLU.gluOrtho2D(0.0f, width, height, 0.0f);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();

            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(0.0D, height, 0.0D).tex(0.0D, 1.0D).endVertex();
            worldRenderer.pos(width, height, 0.0D).tex(1.0D, 1.0D).endVertex();
            worldRenderer.pos(width, 0.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
            worldRenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();

            // Restore previous projection matrix
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();

            GL11.glEnable(GL11.GL_DEPTH_TEST); // glEnable(2929)
            GlStateManager.disableBlend();

            RenderUtils.resetColor();
        }
    }

    // 停止视频播放器
    @SneakyThrows
    public static void stop() {
        if (!stopped) {
            tessellator = null;
            worldRenderer = null;
            renderer = null;
            time = 0L;
            ticks = 0;
            frameGrabber.stop();
            frameGrabber.release();
            frameGrabber = null;
            stopped = true;
        }
    }
}

