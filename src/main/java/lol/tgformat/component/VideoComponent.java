package lol.tgformat.component;

/**
 * @Author KuChaZi
 * @Date 2024/7/12 14:31
 * @ClassName: VideoManager
 */

import net.minecraft.client.Minecraft;
import net.netease.utils.FileUtil;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.File;

@Renamer
@StringEncryption
public final class VideoComponent {
    public VideoComponent() {
        // 获取视频文件的路径
        File videoFile = new File(Minecraft.getMinecraft().mcDataDir, "background.mp4");

        // 检查视频文件是否存在，如果不存在则解压视频文件
        if (!videoFile.exists()) {
            FileUtil.unpackFile(videoFile, "assets/minecraft/bloodline/background.mp4");
        }
    }
}

