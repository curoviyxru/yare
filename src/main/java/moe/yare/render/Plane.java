package moe.yare.render;

import moe.yare.math.Vector3f;

public class Plane {

    private Vector3f normal;
    private float distance;

    public Plane(Vector3f normal, float distance) {
        this.normal = normal;
        this.distance = distance;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public float getDistance() {
        return distance;
    }
}
