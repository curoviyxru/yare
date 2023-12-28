package moe.yare;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

import static moe.yare.Primitives.*;

public class Scene {

    private final static int LM_DIFFUSE = 1;
    private final static int LM_SPECULAR = 2;

    private final static int SM_FLAT = 0;
    private final static int SM_GOURAUD = 1;
    private final static int SM_PHONG = 2;

    private int TRI_COLORS_OFFSET = 0;
    private Color[] TRI_COLORS = new Color[] {
            new Color(255, 0, 0),
            new Color(0, 255, 0),
            new Color(0, 0, 255),
            new Color(255, 255, 0),
            new Color(255, 0, 255),
            new Color(0, 255, 255),
    };
    private Color EDGE_COLOR = new Color(0, 0, 0);

    //TODO: camera movement

    boolean depthBufferingEnabled = true;
    boolean backfaceCullingEnabled = true;
    boolean drawOutlines = false;

    int lightingModel = LM_DIFFUSE | LM_SPECULAR;
    int shadingModel = SM_PHONG;
    boolean useVertexNormals = true;

    private Camera camera = new Camera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));

    private Light[] lights = new Light[] {
            new Light(Light.Type.AMBIENT, 0.2f, new Vector3f(0, 0, 0)),
            new Light(Light.Type.DIRECTIONAL, 0.2f, new Vector3f(-1, 0, 1)),
            new Light(Light.Type.POINT, 0.6f, new Vector3f(-3, 2, -10))
    };

    private final LinkedList<Instance> instances = new LinkedList<>();

    public Scene() {

    }

    public void addInstance(Instance instance) {
        instances.add(instance);
    }

    public void renderScene(Graphics g) {
        for (Instance instance : instances) {
            Matrix4f transform = camera.getCameraMatrix().mul(instance.getTransformMatrix());
            Model clippedModel = transformAndClip(instance.getModel(), transform, instance.getScaling().max());
            if (clippedModel == null) continue;
            renderModel(g, clippedModel);
        }
    }

    private Model transformAndClip(Model model, Matrix4f transform, float scale) {
        Vector4f center = camera.getCameraMatrix().mul(transform).mul(model.getBoundsCenter());
        float radius = model.getBoundsRadius() * scale;

        Plane[] clippingPlanes = camera.getClippingPlanes();
        for (Plane plane : clippingPlanes) {
            float distance = plane.getNormal().dot(center) + plane.getDistance();
            if (distance < -radius) {
                return null;
            }
        }

        Vector3f[] vertices = new Vector3f[model.getVertices().length];
        for (int i = 0; i < vertices.length; ++i) {
            vertices[i] = transform.mul4f(model.getVertices()[i]);
        }

        Triangle[] triangles = new Triangle[model.getTriangles().length];
        System.arraycopy(model.getTriangles(), 0, triangles, 0, triangles.length);
        for (Plane clippingPlane : clippingPlanes) {
            LinkedList<Triangle> newTriangles = new LinkedList<>();
            for (Triangle triangle : triangles) {
                clipTriangle(triangle, clippingPlane, newTriangles, vertices);
            }
            triangles = newTriangles.toArray(new Triangle[0]);
        }

        return new Model(vertices, triangles, center, model.getBoundsRadius());
    }

    private void clipTriangle(Triangle triangle, Plane clippingPlane, LinkedList<Triangle> newTriangles, Vector3f[] vertices) {
        Vector3f v0 = vertices[triangle.getIndexes().getX()];
        Vector3f v1 = vertices[triangle.getIndexes().getY()];
        Vector3f v2 = vertices[triangle.getIndexes().getZ()];

        int in = 0;

        in += (clippingPlane.getNormal().dot(v0) + clippingPlane.getDistance() > 0 ? 1 : 0);
        in += (clippingPlane.getNormal().dot(v1) + clippingPlane.getDistance() > 0 ? 1 : 0);
        in += (clippingPlane.getNormal().dot(v2) + clippingPlane.getDistance() > 0 ? 1 : 0);

        switch (in) {
            case 0:
                //Triangle is clipped out. Nothing to do.
                break;
            case 1:
                //Has one vertex - one clipped triangle.
                //TODO: return [Triangle(A, Intersection(AB, plane), Intersection(AC, plane))]
                break;
            case 2:
                //Has two vertices - two clipped triangles.
                //TODO: return [Triangle(A, B, Intersection(AC, plane)),
                //          Triangle(Intersection(AC, plane), B, Intersection(BC, plane))]
                break;
            case 3:
                newTriangles.add(triangle);
                break;
        }
    }

    private void renderModel(Graphics g, Model model) {
        Vector3f[] vertices = model.getVertices();

        Vector2f[] projected = new Vector2f[vertices.length];
        for (int i = 0; i < vertices.length; ++i) {
            projected[i] = projectVertex(vertices[i]);
        }

        for (Triangle triangle : model.getTriangles()) {
            renderTriangle(g, triangle.getIndexes(), vertices, projected);
        }
    }

    public int[] sortVertexIndexes(Vector3i triangle, Vector2f[] projected) {
        int[] indexes = new int[] { 0, 1, 2 };

        if (projected[triangle.getComponent(indexes[1])].getY() <
                projected[triangle.getComponent(indexes[0])].getY()) {
            int swap = indexes[0];
            indexes[0] = indexes[1];
            indexes[1] = swap;
        }

        if (projected[triangle.getComponent(indexes[2])].getY() <
                projected[triangle.getComponent(indexes[0])].getY()) {
            int swap = indexes[0];
            indexes[0] = indexes[2];
            indexes[2] = swap;
        }

        if (projected[triangle.getComponent(indexes[2])].getY() <
                projected[triangle.getComponent(indexes[1])].getY()) {
            int swap = indexes[2];
            indexes[2] = indexes[1];
            indexes[1] = swap;
        }

        return indexes;
    }

    public Vector3f computeTriangleNormal(Vector3f v0, Vector3f v1, Vector3f v2) {
        v0 = new Vector3f(v0).mul(-1);
        v1 = new Vector3f(v1).add(v0);
        v2 = new Vector3f(v2).add(v0);

        return v1.cross(v2);
    }

    public float[][] edgeInterpolate(float y0, float v0, float y1, float v1, float y2, float v2) {
        float[] v01 = interpolate(y0, v0, y1, v1);
        float[] v12 = interpolate(y1, v1, y2, v2);
        float[] v02 = interpolate(y0, v0, y2, v2);

        float[] v012 = new float[v01.length + v12.length - 1];
        System.arraycopy(v01, 0, v012, 0, v01.length - 1);
        System.arraycopy(v12, 0, v012, v01.length - 1, v12.length);

        return new float[][] { v02, v012 };
    }

    public void renderTriangle(Graphics g, Vector3i triangle, Vector3f[] vertices, Vector2f[] projected) {
        TRI_COLORS_OFFSET = (TRI_COLORS_OFFSET + 1) % TRI_COLORS.length;
        setColor(g, TRI_COLORS[TRI_COLORS_OFFSET]);

        int[] indexes = sortVertexIndexes(triangle, projected);

        Vector3f v0 = vertices[triangle.getComponent(indexes[0])];
        Vector3f v1 = vertices[triangle.getComponent(indexes[1])];
        Vector3f v2 = vertices[triangle.getComponent(indexes[2])];

        Vector3f normal = computeTriangleNormal(vertices[triangle.getX()],
                vertices[triangle.getY()],
                vertices[triangle.getZ()]);

        if (backfaceCullingEnabled) {
            Vector3f vertexToCamera = new Vector3f(camera.getTranslation()).sub(vertices[triangle.getX()]);
            if (vertexToCamera.dot(normal) <= 0) {
                return;
            }
        }

        Vector2f p0 = projected[triangle.getComponent(indexes[0])];
        Vector2f p1 = projected[triangle.getComponent(indexes[1])];
        Vector2f p2 = projected[triangle.getComponent(indexes[2])];

        float[][] ei1 = edgeInterpolate(p0.getY(), p0.getX(), p1.getY(), p1.getX(), p2.getY(), p2.getX());
        float[][] ei2 = edgeInterpolate(p0.getY(), 1.0f / v0.getZ(), p1.getY(), 1.0f / v1.getZ(), p2.getY(), 1.0f / v2.getZ());

        float[] x02 = ei1[0], x012 = ei1[1];
        float[] iz02 = ei2[0], iz012 = ei2[1];

        int m = x02.length / 2;
        float[] x_left, x_right;
        float[] iz_left, iz_right;
        if (x02[m] < x012[m]) {
            x_left = x02;
            x_right = x012;

            iz_left = iz02;
            iz_right = iz012;
        } else {
            x_left = x012;
            x_right = x02;

            iz_left = iz012;
            iz_right = iz02;
        }

        float y0 = p0.getY();
        float y2 = p2.getY();
        for (float y = y0; y <= y2; ++y) {
            float xl = x_left[(int) y - (int) y0], xr = x_right[(int) y - (int) y0];

            float zl = iz_left[(int) y - (int) y0], zr = iz_right[(int) y - (int) y0];
            float[] zs = interpolate(xl, zl, xr, zr);

            for (float x = xl; x <= xr; ++x) {
                if (!depthBufferingEnabled || updateDepthBufferIfCloser(x, y, zs[(int) x - (int) xl])) {
                    putPixel(g, x, y);
                }
            }
        }

        if (drawOutlines) {
            setColor(g, EDGE_COLOR);
            drawLine(g, p0, p1);
            drawLine(g, p0, p2);
            drawLine(g, p2, p1);
        }
    }

    Float[] depthBuffer = new Float[Cw * Ch];

    private void clearDepthBuffer() {
        Arrays.fill(depthBuffer, null);
    }

    private boolean updateDepthBufferIfCloser(float x, float y, float z) {
        x = (Cw >> 1) + (int) x;
        y = (Ch >> 1) - (int) y - 1;

        if (x < 0 || x >= Cw || y < 0 || y >= Ch) {
            return false;
        }

        int offset = (int) x + Cw * (int) y;
        if (depthBuffer[offset] == null || depthBuffer[offset] < z) {
            depthBuffer[offset] = z;
            return true;
        }

        return false;
    }
}
