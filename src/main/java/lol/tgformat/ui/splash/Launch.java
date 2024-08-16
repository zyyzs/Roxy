package lol.tgformat.ui.splash;

/**
 * @Author KuChaZi
 * @Date 2024/8/8 21:23
 * @ClassName: Launch
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class Launch extends JPanel implements ActionListener {
    private Timer timer;
    private int frameIndex = 0;
    private Image[] frames;
    private float opacity = 0.0f;
    private boolean fadeIn = true;

    public Launch() {
        // 加载图片序列
        frames = new Image[]{
                new ImageIcon(Objects.requireNonNull(getClass().getResource("assest/minecraft/images/frame1.png"))).getImage(),
                new ImageIcon(Objects.requireNonNull(getClass().getResource("assest/minecraft/images/frame2.png"))).getImage(),
                new ImageIcon(Objects.requireNonNull(getClass().getResource("assest/minecraft/images/frame3.png"))).getImage()
        };

        // 设置定时器来控制动画播放
        timer = new Timer(100, this); // 每100毫秒更新一次
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 设置透明度
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        // 绘制当前帧
        g2d.drawImage(frames[frameIndex], 0, 0, getWidth(), getHeight(), this);

        // 模拟渐变效果
        if (fadeIn) {
            opacity += 0.05f;
            if (opacity >= 1.0f) {
                fadeIn = false;
            }
        } else {
            opacity -= 0.05f;
            if (opacity <= 0.0f) {
                fadeIn = true;
                frameIndex = (frameIndex + 1) % frames.length;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint(); // 每次定时器触发时重绘组件
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Launch Animation");
        Launch animation = new Launch();

        frame.add(animation);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

