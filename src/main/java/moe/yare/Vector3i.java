package moe.yare;

public class Vector3i {

    private int x;
    private int y;
    private int z;

    public Vector3i(int x, int y, int z) {
        set(x, y, z);
    }

    public Vector3i(Vector3i v) {
        set(v);
    }

    private Vector3i set(Vector3i v) {
        x = v.x;
        y = v.y;
        z = v.z;

        return this;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getComponent(int i) {
        return switch (i) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> 0;
        };
    }
}
