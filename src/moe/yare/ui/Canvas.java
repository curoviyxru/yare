package moe.yare.ui;

import moe.yare.render.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {

    private final Scene scene = new Scene(0, 0);
    private final Color textColor = new Color(0);
    private BufferedImage renderedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    public Canvas() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                renderedImage = new BufferedImage(Math.max(1, getWidth()), Math.max(1, getHeight()), BufferedImage.TYPE_INT_ARGB);
                scene.setSize(getWidth(), getHeight());
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        long before = System.currentTimeMillis();
        scene.clearDepthBuffer();
        scene.clearRect();
        scene.renderScene();
        renderedImage.setRGB(0,
                0,
                scene.getRenderTexture().getWidth(),
                scene.getRenderTexture().getHeight(),
                scene.getRenderTexture().getRGB(),
                0,
                scene.getRenderTexture().getWidth());
        g.drawImage(renderedImage, 0, 0, null);
        long frametime = System.currentTimeMillis() - before;

        g.setColor(textColor);
        g.drawString("Frametime: " + frametime, 0, 14);
        g.drawString("FPS: " + (1000 / Math.max(frametime, 1)), 0, 28);
    }

    public Scene getScene() {
        return scene;
    }
}
