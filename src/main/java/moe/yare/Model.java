package moe.yare;

public class Model {

    private Vector3f[] vertices;
    private Vector3i[] triangles;

    public Model(Vector3f[] vertices, Vector3i[] triangles) {
        this.vertices = vertices;
        this.triangles = triangles;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public Vector3i[] getTriangles() {
        return triangles;
    }
}
