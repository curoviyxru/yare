package moe.yare;

public class Triangle {

    private Vector3i indexes;
    private Color color;
    private Vector3f[] normals;
    private Texture texture;
    private Vector2f[] uvs;

    public Triangle(Vector3i indexes, Color color, Vector3f[] normals, Texture texture, Vector2f[] uvs) {
        this.indexes = indexes;
        this.color = color;
        this.normals = normals;
        this.texture = texture;
        this.uvs = uvs;
    }

    public Vector3i getIndexes() {
        return indexes;
    }

    public Color getColor() {
        return color;
    }

    public Vector3f[] getNormals() {
        return normals;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getUVs() {
        return uvs;
    }
}
