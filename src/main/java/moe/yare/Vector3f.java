package moe.yare;

public class Vector3f {

    private float x;
    private float y;
    private float z;

    public Vector3f(float x, float y, float z) {
        set(x, y, z);
    }

    public Vector3f(Vector3f v) {
        set(v);
    }

    public Vector3f set(Vector3f v) {
        x = v.getX();
        y = v.getY();
        z = v.getZ();

        return this;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Vector3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public Vector3f setX(float x) {
        this.x = x;

        return this;
    }

    public Vector3f setY(float y) {
        this.y = y;

        return this;
    }

    public Vector3f setZ(float z) {
        this.z = z;

        return this;
    }

    public Vector3f add(Vector3f v) {
        x += v.getX();
        y += v.getY();
        z += v.getZ();

        return this;
    }

    public Vector3f sub(Vector3f v) {
        x -= v.getX();
        y -= v.getY();
        z -= v.getZ();

        return this;
    }

    public Vector3f mul(float i) {
        x *= i;
        y *= i;
        z *= i;

        return this;
    }
}
