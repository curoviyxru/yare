package moe.yare;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

import static moe.yare.Primitives.*;

public class Scene {

    public enum ShadingType {
        FLAT, GOURAUD, PHONG
    }

    private Color EDGE_COLOR = new Color(0, 0, 0);

    //TODO: camera movement

    boolean depthBufferingEnabled = true;
    boolean backfaceCullingEnabled = true;
    boolean drawOutlines = false;
    boolean isLightDiffuse = true;
    boolean isLightSpecular = true;
    ShadingType shadingModel = ShadingType.PHONG;
    boolean useVertexNormals = true;
    boolean usePerspectiveCorrectDepth = true;

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
            renderModel(g, clippedModel, instance.getOrientationMatrix());
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
            vertices[i] = transform.mul(model.getVertices()[i], 1);
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

    private void renderModel(Graphics g, Model model, Matrix4f orientation) {
        Vector3f[] vertices = model.getVertices();

        Vector2f[] projected = new Vector2f[vertices.length];
        for (int i = 0; i < vertices.length; ++i) {
            projected[i] = projectVertex(vertices[i]);
        }

        for (Triangle triangle : model.getTriangles()) {
            setColor(g, triangle.getColor());
            renderTriangle(g, triangle, vertices, projected, orientation);
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

    //TODO: remove redundant float[] (these that are before left/right condition)
    //TODO: extract (int) y - (int) y0 to variable
    public void renderTriangle(Graphics g, Triangle triangle, Vector3f[] vertices, Vector2f[] projected, Matrix4f orientation) {
        Vector3i triangleIndexes = triangle.getIndexes();
        int[] indexes = sortVertexIndexes(triangleIndexes, projected);

        Vector3f v0 = vertices[triangleIndexes.getComponent(indexes[0])];
        Vector3f v1 = vertices[triangleIndexes.getComponent(indexes[1])];
        Vector3f v2 = vertices[triangleIndexes.getComponent(indexes[2])];

        Vector3f normal = computeTriangleNormal(vertices[triangleIndexes.getX()],
                vertices[triangleIndexes.getY()],
                vertices[triangleIndexes.getZ()]);

        if (backfaceCullingEnabled) {
            Vector3f vertexToCamera = new Vector3f(camera.getTranslation()).sub(vertices[triangleIndexes.getX()]);
            if (vertexToCamera.dot(normal) <= 0) {
                return;
            }
        }

        Vector2f p0 = projected[triangleIndexes.getComponent(indexes[0])];
        Vector2f p1 = projected[triangleIndexes.getComponent(indexes[1])];
        Vector2f p2 = projected[triangleIndexes.getComponent(indexes[2])];

        float[][] ei1 = edgeInterpolate(p0.getY(), p0.getX(), p1.getY(), p1.getX(), p2.getY(), p2.getX());
        float[][] ei2 = edgeInterpolate(p0.getY(), 1.0f / v0.getZ(), p1.getY(), 1.0f / v1.getZ(), p2.getY(), 1.0f / v2.getZ());

        float[] x02 = ei1[0], x012 = ei1[1];
        float[] iz02 = ei2[0], iz012 = ei2[1];

        float[] uz02 = null, uz012 = null, vz02 = null, vz012 = null;
        if (triangle.getTexture() != null) {
            Vector2f[] uvs = triangle.getUVs();
            if (usePerspectiveCorrectDepth) {
                float[][] uz = edgeInterpolate(p0.getY(), uvs[indexes[0]].getX() / v0.getZ(),
                                                p1.getY(), uvs[indexes[1]].getX() / v1.getZ(),
                                                p2.getY(), uvs[indexes[2]].getX() / v2.getZ());
                uz02 = uz[0];
                uz012 = uz[1];
                float[][] vz = edgeInterpolate(p0.getY(), uvs[indexes[0]].getY() / v0.getZ(),
                                                p1.getY(), uvs[indexes[1]].getY() / v1.getZ(),
                                                p2.getY(), uvs[indexes[2]].getY() / v2.getZ());
                vz02 = vz[0];
                vz012 = vz[1];
            } else {
                float[][] uz = edgeInterpolate(p0.getY(), uvs[indexes[0]].getX(),
                                                p1.getY(), uvs[indexes[1]].getX(),
                                                p2.getY(), uvs[indexes[2]].getX());
                uz02 = uz[0];
                uz012 = uz[1];
                float[][] vz = edgeInterpolate(p0.getY(), uvs[indexes[0]].getY(),
                                                p1.getY(), uvs[indexes[1]].getY(),
                                                p2.getY(), uvs[indexes[2]].getY());
                vz02 = vz[0];
                vz012 = vz[1];
            }
        }

        Vector4f normal0, normal1, normal2;
        if (useVertexNormals) {
            Matrix4f transform = camera.getTransposedRotationMatrix().mul(orientation);
            normal0 = transform.mul(triangle.getNormals()[indexes[0]], 1);
            normal1 = transform.mul(triangle.getNormals()[indexes[1]], 1);
            normal2 = transform.mul(triangle.getNormals()[indexes[2]], 1);
        } else {
            normal0 = normal1 = normal2 = new Vector4f(normal.getX(), normal.getY(), normal.getZ(), 1);
        }

        float intensity = 0;
        float[] i02 = null, i012 = null, nx02 = null, nx012 = null, ny02 = null, ny012 = null, nz02 = null, nz012 = null;
        switch (shadingModel) {
            case FLAT -> {
                Vector3f center = new Vector3f((v0.getX() + v1.getX() + v2.getX()) / 3,
                        (v0.getY() + v1.getY() + v2.getY()) / 3,
                        (v0.getZ() + v1.getZ() + v2.getZ()) / 3);
                intensity = computeIllumination(center, normal0);
            }
            case GOURAUD -> {
                float i0 = computeIllumination(v0, normal0);
                float i1 = computeIllumination(v1, normal1);
                float i2 = computeIllumination(v2, normal2);
                float[][] ei = edgeInterpolate(p0.getY(), i0, p1.getY(), i1, p2.getY(), i2);
                i02 = ei[0];
                i012 = ei[1];
            }
            case PHONG -> {
                float[][] xei = edgeInterpolate(p0.getY(), normal0.getX(), p1.getY(), normal1.getX(), p2.getY(), normal2.getX());
                nx02 = xei[0];
                nx012 = xei[1];
                float[][] yei = edgeInterpolate(p0.getY(), normal0.getY(), p1.getY(), normal1.getY(), p2.getY(), normal2.getY());
                ny02 = yei[0];
                ny012 = yei[1];
                float[][] zei = edgeInterpolate(p0.getY(), normal0.getZ(), p1.getY(), normal1.getZ(), p2.getY(), normal2.getZ());
                nz02 = zei[0];
                nz012 = zei[1];
            }
        }

        int m = x02.length / 2;
        float[] x_left, x_right;
        float[] iz_left, iz_right;
        float[] i_left, i_right;
        float[] nx_left, nx_right;
        float[] ny_left, ny_right;
        float[] nz_left, nz_right;
        float[] uz_left, uz_right;
        float[] vz_left, vz_right;
        if (x02[m] < x012[m]) {
            x_left = x02;
            x_right = x012;

            iz_left = iz02;
            iz_right = iz012;

            i_left = i02;
            i_right = i012;

            nx_left = nx02;
            nx_right = nx012;

            ny_left = ny02;
            ny_right = ny012;

            nz_left = nz02;
            nz_right = nz012;

            uz_left = uz02;
            uz_right = uz012;

            vz_left = vz02;
            vz_right = vz012;
        } else {
            x_left = x012;
            x_right = x02;

            iz_left = iz012;
            iz_right = iz02;

            i_left = i012;
            i_right = i02;

            nx_left = nx012;
            nx_right = nx02;

            ny_left = ny012;
            ny_right = ny02;

            nz_left = nz012;
            nz_right = nz02;

            uz_left = uz012;
            uz_right = uz02;

            vz_left = vz012;
            vz_right = vz02;
        }

        float y0 = p0.getY();
        float y2 = p2.getY();
        for (float y = y0; y <= y2; ++y) {
            float xl = x_left[(int) y - (int) y0], xr = x_right[(int) y - (int) y0];

            float zl = iz_left[(int) y - (int) y0], zr = iz_right[(int) y - (int) y0];
            float[] zs = interpolate(xl, zl, xr, zr);

            float[] is = null, nxs = null, nys = null, nzs = null;
            switch (shadingModel) {
                case GOURAUD -> {
                    assert i_left != null;
                    assert i_right != null;
                    is = interpolate(xl, i_left[(int) y - (int) y0], xr, i_right[(int) y - (int) y0]);
                }
                case PHONG -> {
                    assert nx_left != null;
                    assert nx_right != null;
                    nxs = interpolate(xl, nx_left[(int) y - (int) y0], xr, nx_right[(int) y - (int) y0]);
                    assert ny_left != null;
                    assert ny_right != null;
                    nys = interpolate(xl, ny_left[(int) y - (int) y0], xr, ny_right[(int) y - (int) y0]);
                    assert nz_left != null;
                    assert nz_right != null;
                    nzs = interpolate(xl, nz_left[(int) y - (int) y0], xr, nz_right[(int) y - (int) y0]);
                }
            }

            float[] uzs = null, vzs = null;
            if (triangle.getTexture() != null) {
                assert uz_left != null;
                assert uz_right != null;
                uzs = interpolate(xl, uz_left[(int) y - (int) y0], xr, uz_right[(int) y - (int) y0]);
                assert vz_left != null;
                assert vz_right != null;
                vzs = interpolate(xl, vz_left[(int) y - (int) y0], xr, vz_right[(int) y - (int) y0]);
            }

            for (float x = xl; x <= xr; ++x) {
                if (!depthBufferingEnabled || updateDepthBufferIfCloser(x, y, zs[(int) x - (int) xl])) {
                    switch (shadingModel) {
                        case GOURAUD -> {
                            assert is != null;
                            intensity = is[(int) x - (int) xl];
                        }
                        case PHONG -> {
                            Vector3f vertex = unprojectVertex(x, y, zs[(int) x - (int) xl]);
                            assert nxs != null;
                            assert nys != null;
                            assert nzs != null;
                            Vector4f normalV = new Vector4f(nxs[(int) x - (int) xl], nys[(int) x - (int) xl], nzs[(int) x - (int) xl], 1);
                            intensity = computeIllumination(vertex, normalV);
                        }
                    }

                    Color color;
                    if (triangle.getTexture() != null) {
                        float u, v;

                        assert uzs != null;
                        assert vzs != null;
                        if (usePerspectiveCorrectDepth) {
                            u = uzs[(int) x - (int) xl] / zs[(int) x - (int) xl];
                            v = vzs[(int) x - (int) xl] / zs[(int) x - (int) xl];
                        } else {
                            u = uzs[(int) x - (int) xl];
                            v = vzs[(int) x - (int) xl];
                        }

                        color = triangle.getTexture().getTexel(u, v);
                    } else {
                        color = new Color(triangle.getColor());
                    }

                    setColor(g, color.mul(intensity));
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

    private float computeIllumination(Vector3f vertex, Vector4f normal) {
        Vector3f nV = new Vector3f(vertex).mul(-1);
        float illumination = 0;

        for (int l = 0; l < lights.length; ++l) {
            Light light = lights[l];

            Vector3f v1 = null;
            switch (light.getType()) {
                case AMBIENT -> {
                    illumination += light.getIntensity();
                    continue;
                }
                case DIRECTIONAL ->
                        v1 = camera.getTransposedRotationMatrix().mul(light.getVector(), 1);
                case POINT ->
                        v1 = camera.getCameraMatrix().mul(light.getVector(), 1).add(nV);
            }

            if (isLightDiffuse) {
                float cosAlpha = v1.dot(normal) / (v1.magnitude() * normal.magnitude());
                if (cosAlpha > 0) {
                    illumination += cosAlpha * light.getIntensity();
                }
            }

            if (isLightSpecular) {
                Vector3f reflected = new Vector3f(normal)
                        .mul(2 * normal.dot(v1))
                        .add(new Vector3f(v1).mul(-1));
                Vector3f view = new Vector3f(camera.getTranslation()).add(nV);

                float cosBeta = reflected.dot(view) / (reflected.magnitude() * view.magnitude());
                if (cosBeta > 0) {
                    float specular = 50;
                    illumination += (float) Math.pow(cosBeta, specular) * light.getIntensity();
                }
            }
        }

        return illumination;
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
