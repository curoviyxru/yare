package moe.yare;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

import static java.lang.Math.abs;

public class Primitives {

    //TODO: rename this to Utils, remove unused code

    public static float[] interpolate(float i0, float d0, float i1, float d1) {
        if (i0 == i1) {
            return new float[] { d0 };
        }

        LinkedList<Float> values = new LinkedList<>();
        float a = (d1 - d0) / (i1 - i0);
        float d = d0;
        for (float i = i0; i <= i1; ++i) {
            values.add(d);
            d += a;
        }

        Iterator<Float> it = values.iterator();
        int i = 0;
        float[] array = new float[values.size()];
        while (it.hasNext()) {
            array[i++] = it.next();
        }

        return array;
    }

    public static void drawLine(Graphics g, Vector2f p0, Vector2f p1) {
        float dx = p1.getX() - p0.getX();
        float dy = p1.getY() - p0.getY();

        if (abs(dx) > abs(dy)) {
            if (dx < 0) {
                Vector2f t = p0;
                p0 = p1;
                p1 = t;
            }

            float[] ys = interpolate(p0.getX(), p0.getY(), p1.getX(), p1.getY());
            for (float x = p0.getX(); x <= p1.getX(); ++x) {
                putPixel(g, x, ys[(int) x - (int) p0.getX()]);
            }
        } else {
            if (dy < 0) {
                Vector2f t = p0;
                p0 = p1;
                p1 = t;
            }

            float[] xs = interpolate(p0.getY(), p0.getX(), p1.getY(), p1.getX());
            for (float y = p0.getY(); y <= p1.getY(); ++y) {
                putPixel(g, xs[(int) y - (int) p0.getY()], y);
            }
        }
    }

    public static void putPixel(Graphics g, float x, float y, float z) {
        Color color = g.getColor();
        Color shaded = new Color(color.getRed() * (int) z / 255, color.getGreen() * (int) z / 255, color.getBlue() * (int) z / 255);

        x = (Cw >> 1) + (int) x;
        y = (Ch >> 1) - (int) y - 1;

        if (x < 0 || x >= Cw || y < 0 || y >= Ch) {
            return;
        }

        g.setColor(shaded);
        g.drawLine((int) x, (int) y, (int) x, (int) y);
        g.setColor(color);
    }

    public static void putPixel(Graphics g, float x, float y) {
        x = (Cw >> 1) + (int) x;
        y = (Ch >> 1) - (int) y - 1;

        if (x < 0 || x >= Cw || y < 0 || y >= Ch) {
            return;
        }

        g.drawLine((int) x, (int) y, (int) x, (int) y);
    }

    public static void drawWireframeTriangle(Graphics g, Vector2f p0, Vector2f p1, Vector2f p2) {
        drawLine(g, p0, p1);
        drawLine(g, p1, p2);
        drawLine(g, p2, p0);
    }

    public static void drawFilledTriangle(Graphics g, Vector2f p0, Vector2f p1, Vector2f p2) {
        if (p1.getY() < p0.getY()) {
            Vector2f t = p0;
            p0 = p1;
            p1 = t;
        }
        if (p2.getY() < p0.getY()) {
            Vector2f t = p0;
            p0 = p2;
            p2 = t;
        }
        if (p2.getY() < p1.getY()) {
            Vector2f t = p2;
            p2 = p1;
            p1 = t;
        }

        float[] x01 = interpolate(p0.getY(), p0.getX(), p1.getY(), p1.getX());
        float[] x12 = interpolate(p1.getY(), p1.getX(), p2.getY(), p2.getX());
        float[] x02 = interpolate(p0.getY(), p0.getX(), p2.getY(), p2.getX());

        float[] x012 = new float[x01.length + x12.length - 1];
        System.arraycopy(x01, 0, x012, 0, x01.length - 1);
        System.arraycopy(x12, 0, x012, x01.length - 1, x12.length);

        int m = x012.length / 2;
        float[] x_left, x_right;

        if (x02[m] < x012[m]) {
            x_left = x02;
            x_right = x012;
        } else {
            x_left = x012;
            x_right = x02;
        }

        float y0 = p0.getY();
        float y2 = p2.getY();
        for (float y = y0; y <= y2; ++y) {
            for (float x = x_left[(int) y - (int) y0]; x <= x_right[(int) y - (int) y0]; ++x) {
                putPixel(g, x, y);
            }
        }
    }

    public static void drawShadedTriangle(Graphics g, Vector3f p0, Vector3f p1, Vector3f p2) {
        if (p1.getY() < p0.getY()) {
            Vector3f t = p0;
            p0 = p1;
            p1 = t;
        }
        if (p2.getY() < p0.getY()) {
            Vector3f t = p0;
            p0 = p2;
            p2 = t;
        }
        if (p2.getY() < p1.getY()) {
            Vector3f t = p2;
            p2 = p1;
            p1 = t;
        }

        float[] x01 = interpolate(p0.getY(), p0.getX(), p1.getY(), p1.getX());
        float[] z01 = interpolate(p0.getY(), p0.getZ(), p1.getY(), p1.getZ());

        float[] x12 = interpolate(p1.getY(), p1.getX(), p2.getY(), p2.getX());
        float[] z12 = interpolate(p1.getY(), p1.getZ(), p2.getY(), p2.getZ());

        float[] x02 = interpolate(p0.getY(), p0.getX(), p2.getY(), p2.getX());
        float[] z02 = interpolate(p0.getY(), p0.getZ(), p2.getY(), p2.getZ());

        float[] x012 = new float[x01.length + x12.length - 1];
        System.arraycopy(x01, 0, x012, 0, x01.length - 1);
        System.arraycopy(x12, 0, x012, x01.length - 1, x12.length);

        float[] z012 = new float[z01.length + z12.length - 1];
        System.arraycopy(z01, 0, z012, 0, z01.length - 1);
        System.arraycopy(z12, 0, z012, z01.length - 1, z12.length);

        int m = x012.length / 2;
        float[] x_left, x_right;
        float[] z_left, z_right;
        if (x02[m] < x012[m]) {
            x_left = x02;
            z_left = z02;

            x_right = x012;
            z_right = z012;
        } else {
            x_left = x012;
            z_left = z012;

            x_right = x02;
            z_right = z02;
        }

        float y0 = p0.getY();
        float y2 = p2.getY();
        for (float y = y0; y <= y2; ++y) {
            float x_l = x_left[(int) y - (int) y0];
            float x_r = x_right[(int) y - (int) y0];

            float[] zs = interpolate(x_l, z_left[(int) y - (int) y0], x_r, z_right[(int) y - (int) y0]);
            for (float x = x_l; x <= x_r; ++x) {
                putPixel(g, x, y, zs[(int) x - (int) x_l]);
            }
        }
    }

    private static final float D = 1; //distance from the camera
    public static final int Cw = 600; //canvas width
    public static final int Ch = 600; //canvas height
    private static final float Vw = 1; //viewport width
    private static final float Vh = 1; //viewport height

    public static Vector2f viewportToCanvas(float x, float y) {
        return new Vector2f(x * Cw / Vw, y * Ch / Vh);
    }
    public static Vector2f projectVertex(Vector3f v) {
        return viewportToCanvas(v.getX() * D / v.getZ(), v.getY() * D / v.getZ());
    }

    public static void primitivesTest(Graphics g) {
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
