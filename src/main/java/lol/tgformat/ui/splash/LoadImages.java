package lol.tgformat.ui.splash;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @Author KuChaZi
 * @Date 2024/7/27 11:11
 * @ClassName: LoadImages
 */

public class LoadImages {
    public static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }
}

