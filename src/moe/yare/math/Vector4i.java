package moe.yare.math;

public class Vector4i extends Vector3i {

    private int w;

    public Vector4i(int x, int y, int z, int w) {
        super(x, y, z);
        this.w = w;
    }

    public Vector4i(Vector4i v) {
        this(v.getX(), v.getY(), v.getZ(), v.getW());
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }
}