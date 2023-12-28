package moe.yare;

import javax.swing.*;
import java.awt.*;
import static moe.yare.Primitives.*;

public class Canvas extends JPanel {

    @Override
    public void paint(Graphics g) {
        primitivesTest(g);
    }

    private void primitivesTest(Graphics g) {
        g.setColor(new Color(0x000000));
        drawLine(g, new Vector2f(0, 0), new Vector2f(0, -200));
        drawLine(g, new Vector2f(0, 0), new Vector2f(0, 200));
        drawLine(g, new Vector2f(0, 0), new Vector2f(-200, 0));
        drawLine(g, new Vector2f(0, 0), new Vector2f(200, 0));
        drawLine(g, new Vector2f(0, 0), new Vector2f(-200, 200));
        drawLine(g, new Vector2f(0, 0), new Vector2f(-200, -200));
        drawLine(g, new Vector2f(0, 0), new Vector2f(200, 200));
        drawLine(g, new Vector2f(0, 0), new Vector2f(200, -200));

        g.setColor(new Color(0xFF00FF));
        drawWireframeTriangle(g, new Vector2f(0, 0), new Vector2f(100, 50), new Vector2f(0, 100));
        drawFilledTriangle(g, new Vector2f(0, 100), new Vector2f(100, 150), new Vector2f(0, 200));
        drawShadedTriangle(g, new Vector3f(0, 200, 255), new Vector3f(100, 250, 0), new Vector3f(0, 300, 127));

        var vAf = new Vector3f(-2, -0.5f, 5);
        var vBf = new Vector3f(-2, 0.5f, 5);
        var vCf = new Vector3f(-1, 0.5f, 5);
        var vDf = new Vector3f(-1, -0.5f, 5);

        var vAb = new Vector3f(-2, -0.5f, 6);
        var vBb = new Vector3f(-2, 0.5f, 6);
        var vCb = new Vector3f(-1, 0.5f, 6);
        var vDb = new Vector3f(-1, -0.5f, 6);

        g.setColor(new Color(0x0000FF));
        drawLine(g, projectVertex(vAf), projectVertex(vBf));
        drawLine(g, projectVertex(vBf), projectVertex(vCf));
        drawLine(g, projectVertex(vCf), projectVertex(vDf));
        drawLine(g, projectVertex(vDf), projectVertex(vAf));

        g.setColor(new Color(0xFF0000));
        drawLine(g, projectVertex(vAb), projectVertex(vBb));
        drawLine(g, projectVertex(vBb), projectVertex(vCb));
        drawLine(g, projectVertex(vCb), projectVertex(vDb));
        drawLine(g, projectVertex(vDb), projectVertex(vAb));

        g.setColor(new Color(0x00FF00));
        drawLine(g, projectVertex(vAf), projectVertex(vAb));
        drawLine(g, projectVertex(vBf), projectVertex(vBb));
        drawLine(g, projectVertex(vCf), projectVertex(vCb));
        drawLine(g, projectVertex(vDf), projectVertex(vDb));
    }
}
