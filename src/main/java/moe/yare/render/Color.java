package moe.yare.render;

import moe.yare.math.Vector3i;

public class Color extends Vector3i {

    public Color(int r, int g, int b) {
        super(r, g, b);
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
}
