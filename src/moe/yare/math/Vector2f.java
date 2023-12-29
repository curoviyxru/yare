package moe.yare.math;

public class Vector2f {

    private float x;
    private float y;

    public Vector2f(float x, float y) {
        set(x, y);
    }

    public Vector2f(Vector2f v) {
        set(v.x, v.y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f setX(float x) {
        this.x = x;

        return this;
    }

    public Vector2f setY(float y) {
        this.y = y;

        return this;
    }
}
