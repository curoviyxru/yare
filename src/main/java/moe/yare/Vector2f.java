package moe.yare;

public class Vector2f {

    private float x;
    private float y;

    public Vector2f(float x, float y) {
        set(x, y);
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

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
