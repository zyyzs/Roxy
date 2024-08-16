package lol.tgformat.ui.menu.utils;

import java.nio.ByteBuffer;

import lombok.Getter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/7/12 14:09
 * @ClassName: Renderer
 */
@Renamer
@StringEncryption
public class Renderer {
    private int imageWidth;  // 图像的宽度
    private int imageHeight; // 图像的高度
    private int internalformat; // 图像的内部格式
    // 获取图像缓冲区
    @Getter
    private ByteBuffer imageBuffer; // 图像的缓冲区

    // 设置缓冲区，图像宽度和高度
    public void setBuffer(ByteBuffer buffer, int width, int height) {
        this.internalformat = 6407; // 设置内部格式为GL_RGB
        this.imageWidth = width;    // 设置图像宽度
        this.imageHeight = height;  // 设置图像高度
        this.imageBuffer = buffer;  // 设置图像缓冲区
    }

    // 渲染视频帧
    public void onDrawFrame() {
        // 激活纹理单元GL_TEXTURE0
        GL13.glActiveTexture(33984);
        // 绑定纹理，纹理ID为-1（未绑定任何实际纹理对象）
        GL11.glBindTexture(3553, -1);
        // 设置纹理参数，纹理的缩小过滤参数为GL_NEAREST
        GL11.glTexParameteri(3553, 10241, 9728);
        // 设置纹理参数，纹理的放大过滤参数为GL_NEAREST
        GL11.glTexParameteri(3553, 10240, 9728);
        // 将图像数据加载到当前绑定的纹理中
        GL11.glTexImage2D(3553, 0, this.internalformat, this.imageWidth, this.imageHeight, 0, this.internalformat, 5121, this.imageBuffer);
    }

}

