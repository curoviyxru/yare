package moe.yare.render;

import moe.yare.math.Vector4i;

public class Color extends Vector4i {

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public Color(Color color) {
        super(color);
    }

    private static int clamp(float i) {
        if (i < 0) return 0;
        if (i > 255) return 255;
        return (int) i;
    }

    public Color add(Color c) {
        setX(clamp(getX() + c.getX()));
        setY(clamp(getY() + c.getY()));
        setZ(clamp(getZ() + c.getZ()));

        return this;
    }

    public Color mul(float k) {
        setX(clamp(getX() * k));
        setY(clamp(getY() * k));
        setZ(clamp(getZ() * k));

        return this;
    }

    public int rgb() {
        return ((getW() & 0xff) << 24) | ((getX() & 0xff) << 16) | ((getY() & 0xff) << 8) | (getZ() & 0xff);
    }
}
