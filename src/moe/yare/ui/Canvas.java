package moe.yare.ui;

import moe.yare.render.Camera;
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
    private final long[][] frametimeGraph = new long[GRAPH_PIXEL_WIDTH][2];
    private long lastRenderTime;
    private static final long GRAPH_PIXEL_MILLIS = 200;
    private static final int GRAPH_PIXEL_WIDTH = 100;

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
        //Render scene
        long before = System.currentTimeMillis();
        g.clearRect(0, 0, getWidth(), getHeight());

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

        //Debug info
        g.setColor(textColor);
        lastRenderTime += frametime;

        if (lastRenderTime > GRAPH_PIXEL_MILLIS) {
            lastRenderTime %= GRAPH_PIXEL_MILLIS;

            for (int i = 0; i < frametimeGraph.length - 1; ++i) {
                frametimeGraph[i][0] = frametimeGraph[i + 1][0];
                frametimeGraph[i][1] = frametimeGraph[i + 1][1];
            }

            frametimeGraph[frametimeGraph.length - 1][0] = 0;
            frametimeGraph[frametimeGraph.length - 1][1] = 0;
        }

        frametimeGraph[frametimeGraph.length - 1][0] += frametime;
        frametimeGraph[frametimeGraph.length - 1][1] += 1;

        int graphY = 160;
        int graphX = 0;
        int lX = graphX, lY = graphY;
        for (int i = 0; i < frametimeGraph.length; ++i) {
            long[] ft = frametimeGraph[i];
            if (ft[0] <= 0) {
                lX = graphX + i;
                continue;
            }

            int fps = (int) Math.min(1000 * ft[1] / ft[0], 100);
            int x = graphX + i;
            int y = graphY - fps;

            if (i == 0) {
                lX = x;
                lY = y;
            }

            g.drawLine(lX, lY, x, y);
            lX = x;
            lY = y;
        }

        long avgFrametime = frametimeGraph[frametimeGraph.length - 1][0] / frametimeGraph[frametimeGraph.length - 1][1];
        g.drawString("Frametime: " + avgFrametime, 0, 14);
        g.drawString("FPS: " + (1000 / Math.max(avgFrametime, 1)), 0, 28);

        Camera camera = scene.getCurrentCamera();
        StringBuilder coords = new StringBuilder();
        coords.append("X: ").append(camera.getTranslation().getX())
                .append(" Y: ").append(camera.getTranslation().getY())
                .append(" Z: ").append(camera.getTranslation().getZ());
        g.drawString(coords.toString(), 0, 42);
        coords.setLength(0);
        coords.append(" rX: ").append(camera.getRotation().getX())
                .append(" rY: ").append(camera.getRotation().getY())
                .append(" rZ: ").append(camera.getRotation().getZ());
        g.drawString(coords.toString(), 0, 56);
    }

    public Scene getScene() {
        return scene;
    }
}
