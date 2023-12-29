package moe.yare.ui;

import moe.yare.render.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Canvas extends JPanel {

    private final Scene scene = new Scene(getWidth(), getHeight());
    private final java.awt.Color textColor = new java.awt.Color(0);

    public Canvas() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scene.setSize(getWidth(), getHeight());
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        long before = System.currentTimeMillis();
        scene.clearDepthBuffer();
        scene.renderScene(g);
        long frametime = System.currentTimeMillis() - before;

        g.setColor(textColor);
        g.drawString("Frametime: " + frametime, 0, 14);
        g.drawString("FPS: " + (1000 / Math.max(frametime, 1)), 0, 28);
    }

    public Scene getScene() {
        return scene;
    }
}
