package lol.tgformat.ui.splash;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Author KuChaZi
 * @Date 2024/7/27 11:11
 * @ClassName: FadeInFadeOutPanel
 */

public class FadeInFadeOutPanel extends JPanel {
    private BufferedImage image;
    private float alpha = 0f;
    private boolean fadingIn = true;

    public FadeInFadeOutPanel(BufferedImage image) {
        this.image = image;
        Timer timer = new Timer(50, e -> updateAlpha());
        timer.start();
    }

    private void updateAlpha() {
        if (fadingIn) {
            alpha += 0.05f;
            if (alpha >= 1f) {
                alpha = 1f;
                fadingIn = false;
            }
        } else {
            alpha -= 0.05f;
            if (alpha <= 0f) {
                alpha = 0f;
                fadingIn = true;
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(image, 0, 0, this);
        }
    }

    public static void run() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fade In Fade Out");
            try {
                BufferedImage image = LoadImages.loadImage("assets/minecraft/bloodline/gs1.png");
                frame.add(new FadeInFadeOutPanel(image));
                frame.setSize(800, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

