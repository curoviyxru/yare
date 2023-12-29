package moe.yare.math;

public class Vector4f extends Vector3f {

    private float w;

    public Vector4f(float x, float y, float z, float w) {
        super(x, y, z);
        this.w = w;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }
}
