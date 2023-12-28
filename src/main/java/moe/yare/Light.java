package moe.yare;

public class Light {

    public enum Type {
        AMBIENT, POINT, DIRECTIONAL
    }

    private Type type;
    private float intensity;
    private Vector3f vector;

    public Light(Type type, float intensity, Vector3f vector) {
        this.type = type;
        this.intensity = intensity;
        this.vector = vector;
    }

    public Type getType() {
        return type;
    }

    public float getIntensity() {
        return intensity;
    }

    public Vector3f getVector() {
        return vector;
    }
}
