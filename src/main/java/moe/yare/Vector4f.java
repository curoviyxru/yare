package moe.yare;

public class Vector4f extends Vector3f {

    //TODO: streamline every vector implementation

    private float w;

    public Vector4f(float x, float y, float z, float w) {
        super(x, y, z);
        this.w = w;
    }

    public float getW() {
        return w;
    }
}
