package moe.yare.math;

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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public void set(Vector3f v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Vector3f add(Vector3f v) {
        x += v.x;
        y += v.y;
        z += v.z;

        return this;
    }

    public Vector3f add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    public Vector3f sub(Vector3f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;

        return this;
    }

    public Vector3f mul(float i) {
        x *= i;
        y *= i;
        z *= i;

        return this;
    }

    public float dot(Vector3f v) {
        return v.x * x + v.y * y + v.z * z;
    }

    public float max() {
        return Math.max(x, Math.max(y, z));
    }

    public Vector3f cross(Vector3f v) {
        return new Vector3f(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public float magnitude() {
        return (float) Math.sqrt(dot(this));
    }
}
