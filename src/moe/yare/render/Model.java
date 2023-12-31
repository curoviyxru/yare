package moe.yare.render;

import moe.yare.math.Vector3f;
import moe.yare.math.Vector4f;

import static java.lang.Math.*;

public class Model {

    private Vector3f[] vertices;
    private Triangle[] triangles;
    private Vector4f boundsCenter;
    private float boundsRadius;

    public Model(Vector3f[] vertices, Triangle[] triangles) {
        this.vertices = vertices;
        this.triangles = triangles;

        Vector4f boundsCenter = makeBoundsCenter();
        this.boundsRadius = boundsCenter.getW();

        boundsCenter.setW(1);
        this.boundsCenter = boundsCenter;
    }

    public Model(Vector3f[] vertices, Triangle[] triangles, Vector4f boundsCenter, float boundsRadius) {
        this.vertices = vertices;
        this.triangles = triangles;
        this.boundsCenter = boundsCenter;
        this.boundsRadius = boundsRadius;
    }

    private Vector4f makeBoundsCenter() {
        float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
        float minZ = Float.MAX_VALUE, maxZ = Float.MIN_VALUE;

        for (Vector3f v : vertices) {
            minX = min(minX, v.getX());
            maxX = max(maxX, v.getX());
            minY = min(minY, v.getY());
            maxY = max(maxY, v.getY());
            minZ = min(minZ, v.getZ());
            maxZ = max(maxZ, v.getZ());
        }

        return new Vector4f((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2,
                max(abs(minX - maxX), max(abs(minY - maxY), abs(minZ - maxZ))) / 2);
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public Triangle[] getTriangles() {
        return triangles;
    }

    public Vector4f getBoundsCenter() {
        return boundsCenter;
    }

    public float getBoundsRadius() {
        return boundsRadius;
    }
}
