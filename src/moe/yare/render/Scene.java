package moe.yare.render;

import moe.yare.math.*;

import java.util.*;
import java.util.concurrent.*;

import static java.lang.Math.*;

public class Scene {

    //TODO: unit tests
    //TODO: custom Math methods
    //TODO: separate code in classes
    //TODO: javadoc
    //TODO: antialiasing
    //TODO: fix too early clipping
    //TODO: fix phantom pixels on edges
    //TODO: fix strange UV for loaded models

    public enum ShadingType {
        FLAT, GOURAUD, PHONG
    }

    public enum TextureMode {
        TEXTURE_COLOR, TRIANGLE_COLOR, ONE_COLOR
    }

    private static final float[][] FAA_STUB = new float[][] { null, null };

    private Color edgeColor = new Color(255, 0, 255);
    private Color materialColor = new Color(255, 255, 255);
    private Color backgroundColor = new Color(255, 255, 255);
    private boolean depthBufferingEnabled = true;
    private boolean backfaceCullingEnabled = true;
    private boolean drawOutlines = false;
    private boolean isLightDiffuse = true;
    private boolean isLightSpecular = true;
    private ShadingType shadingModel = ShadingType.PHONG;
    private boolean useVertexNormals = true;
    private boolean usePerspectiveCorrectDepth = true;
    private int Cw; //canvas width
    private int Ch; //canvas height
    private float Vw = 1; //viewport width
    private float Vh = 1; //viewport height
    private Float[] depthBuffer;
    private TextureMode textureMode = TextureMode.TEXTURE_COLOR;
    private final Texture renderTexture = new Texture(0, 0, null);

    private static ScheduledExecutorService THREAD_POOL = Executors.newScheduledThreadPool(12);

    //TODO: camera movement
    //TODO: merge lights/instances/camera into one list
    private final Camera camera = new Camera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));

    private final Light[] lights = new Light[] {
            new Light(Light.Type.AMBIENT, 0.2f, new Vector3f(0, 0, 0)),
            new Light(Light.Type.DIRECTIONAL, 0.2f, new Vector3f(-1, 0, 1)),
            new Light(Light.Type.POINT, 0.6f, new Vector3f(-3, 2, -10))
    };

    private final LinkedList<Instance> instances = new LinkedList<>();

    public Scene(int width, int height) {
        setSize(width, height);
    }

    public void setSize(int width, int height) {
        synchronized (renderTexture) {
            Cw = width;
            Ch = height;
            Vw = (float) width / height;
            Vh = 1;
            renderTexture.emptyTexture(width, height);
            depthBuffer = new Float[width * height];
        }
    }

    public void clearRect() {
        synchronized (renderTexture) {
            renderTexture.fill(backgroundColor);
        }
    }

    public void clearInstances() {
        synchronized (renderTexture) {
            instances.clear();
        }
    }

    public void addInstance(Instance instance) {
        synchronized (renderTexture) {
            instances.add(instance);
        }
    }

    public List<Instance> getInstances() {
        synchronized (renderTexture) {
            return Collections.unmodifiableList(instances);
        }
    }

    public void renderScene() {
        synchronized (renderTexture) {
            CompletionService<Void> service = new ExecutorCompletionService<>(THREAD_POOL);

            for (Instance instance : instances) {
                service.submit(() -> {
                    synchronized (instance.getLock()) {
                        Matrix4f transform = camera.getCameraMatrix().mul(instance.getTransformMatrix());
                        Model clippedModel = transformAndClip(instance.getModel(), transform, instance.getScaling().max());
                        if (clippedModel == null) return null;
                        renderModel(clippedModel, instance.getOrientationMatrix());
                    }

                    return null;
                });
            }

            for (int i = 0; i < instances.size(); ++i) {
                try {
                    service.take().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Model transformAndClip(Model model, Matrix4f transform, float scale) {
        if (model == null) {
            return null;
        }

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

    private synchronized void renderModel(Model model, Matrix4f orientation) {
        Vector3f[] vertices = model.getVertices();

        Vector2f[] projected = new Vector2f[vertices.length];
        for (int i = 0; i < vertices.length; ++i) {
            projected[i] = projectVertex(vertices[i]);
        }

        for (Triangle triangle : model.getTriangles()) {
            renderTriangle(triangle, vertices, projected, orientation);
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

    public void renderTriangle(Triangle triangle, Vector3f[] vertices, Vector2f[] projected, Matrix4f orientation) {
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

        float[][] uz = FAA_STUB, vz = FAA_STUB;
        if (triangle.getTexture() != null) {
            Vector2f[] uvs = triangle.getUVs();
            if (usePerspectiveCorrectDepth) {
                uz = edgeInterpolate(p0.getY(), uvs[indexes[0]].getX() / v0.getZ(),
                                                p1.getY(), uvs[indexes[1]].getX() / v1.getZ(),
                                                p2.getY(), uvs[indexes[2]].getX() / v2.getZ());
                vz = edgeInterpolate(p0.getY(), uvs[indexes[0]].getY() / v0.getZ(),
                                                p1.getY(), uvs[indexes[1]].getY() / v1.getZ(),
                                                p2.getY(), uvs[indexes[2]].getY() / v2.getZ());
            } else {
                uz = edgeInterpolate(p0.getY(), uvs[indexes[0]].getX(),
                                                p1.getY(), uvs[indexes[1]].getX(),
                                                p2.getY(), uvs[indexes[2]].getX());
                vz = edgeInterpolate(p0.getY(), uvs[indexes[0]].getY(),
                                                p1.getY(), uvs[indexes[1]].getY(),
                                                p2.getY(), uvs[indexes[2]].getY());
            }
        }

        Vector4f normal0, normal1, normal2;
        if (useVertexNormals && triangle.getNormals() != null) {
            Matrix4f transform = camera.getTransposedRotationMatrix().mul(orientation);
            normal0 = transform.mul(triangle.getNormals()[indexes[0]], 1);
            normal1 = transform.mul(triangle.getNormals()[indexes[1]], 1);
            normal2 = transform.mul(triangle.getNormals()[indexes[2]], 1);
        } else {
            normal0 = normal1 = normal2 = new Vector4f(normal.getX(), normal.getY(), normal.getZ(), 1);
        }

        float intensity = 0;
        float[][] ii = FAA_STUB, nx = FAA_STUB, ny = FAA_STUB, nz = FAA_STUB;
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
                ii = edgeInterpolate(p0.getY(), i0, p1.getY(), i1, p2.getY(), i2);
            }
            case PHONG -> {
                nx = edgeInterpolate(p0.getY(), normal0.getX(), p1.getY(), normal1.getX(), p2.getY(), normal2.getX());
                ny = edgeInterpolate(p0.getY(), normal0.getY(), p1.getY(), normal1.getY(), p2.getY(), normal2.getY());
                nz = edgeInterpolate(p0.getY(), normal0.getZ(), p1.getY(), normal1.getZ(), p2.getY(), normal2.getZ());
            }
        }

        int m = ei1[0].length / 2;
        float[] x_left, x_right;
        float[] iz_left, iz_right;
        float[] i_left, i_right;
        float[] nx_left, nx_right;
        float[] ny_left, ny_right;
        float[] nz_left, nz_right;
        float[] uz_left, uz_right;
        float[] vz_left, vz_right;
        if (ei1[0][m] < ei1[1][m]) {
            x_left = ei1[0];
            x_right = ei1[1];

            iz_left = ei2[0];
            iz_right = ei2[1];

            i_left = ii[0];
            i_right = ii[1];

            nx_left = nx[0];
            nx_right = nx[1];

            ny_left = ny[0];
            ny_right = ny[1];

            nz_left = nz[0];
            nz_right = nz[1];

            uz_left = uz[0];
            uz_right = uz[1];

            vz_left = vz[0];
            vz_right = vz[1];
        } else {
            x_left = ei1[1];
            x_right = ei1[0];

            iz_left = ei2[1];
            iz_right = ei2[0];

            i_left = ii[1];
            i_right = ii[0];

            nx_left = nx[1];
            nx_right = nx[0];

            ny_left = ny[1];
            ny_right = ny[0];

            nz_left = nz[1];
            nz_right = nz[0];

            uz_left = uz[1];
            uz_right = uz[0];

            vz_left = vz[1];
            vz_right = vz[0];
        }

        float y0 = p0.getY();
        float y2 = p2.getY();
        for (float y = y0; y <= y2; ++y) {
            int yy0 = (int) y - (int) y0;
            float xl = x_left[yy0], xr = x_right[yy0];

            float zl = iz_left[yy0], zr = iz_right[yy0];
            float[] zs = interpolate(xl, zl, xr, zr);

            float[] is = null, nxs = null, nys = null, nzs = null;
            switch (shadingModel) {
                case GOURAUD -> {
                    is = interpolate(xl, i_left[yy0], xr, i_right[yy0]);
                }
                case PHONG -> {
                    nxs = interpolate(xl, nx_left[yy0], xr, nx_right[yy0]);
                    nys = interpolate(xl, ny_left[yy0], xr, ny_right[yy0]);
                    nzs = interpolate(xl, nz_left[yy0], xr, nz_right[yy0]);
                }
            }

            float[] uzs = null, vzs = null;
            if (triangle.getTexture() != null) {
                uzs = interpolate(xl, uz_left[yy0], xr, uz_right[yy0]);
                vzs = interpolate(xl, vz_left[yy0], xr, vz_right[yy0]);
            }

            for (float x = xl; x <= xr; ++x) {
                int xxl = (int) x - (int) xl;
                if (!depthBufferingEnabled || updateDepthBufferIfCloser(x, y, zs[xxl])) {
                    switch (shadingModel) {
                        case GOURAUD -> {
                            assert is != null;
                            intensity = is[xxl];
                        }
                        case PHONG -> {
                            Vector3f vertex = unprojectVertex(x, y, zs[xxl]);
                            assert nxs != null;
                            assert nys != null;
                            assert nzs != null;
                            Vector4f normalV = new Vector4f(nxs[xxl], nys[xxl], nzs[xxl], 1);
                            intensity = computeIllumination(vertex, normalV);
                        }
                    }

                    Color color;
                    switch (textureMode) {
                        case TEXTURE_COLOR:
                            if (triangle.getTexture() != null) {
                                float u, v;

                                assert uzs != null;
                                assert vzs != null;
                                if (usePerspectiveCorrectDepth) {
                                    u = uzs[xxl] / zs[xxl];
                                    v = vzs[xxl] / zs[xxl];
                                } else {
                                    u = uzs[xxl];
                                    v = vzs[xxl];
                                }

                                color = triangle.getTexture().getTexel(u, v);
                                break;
                            }
                        case TRIANGLE_COLOR:
                            if (triangle.getColor() != null) {
                                color = new Color(triangle.getColor());
                                break;
                            }
                        default:
                            color = new Color(materialColor);
                    }

                    putPixel(color.mul(intensity), x, y);
                }
            }
        }

        if (drawOutlines) {
            drawLine(edgeColor, p0, p1);
            drawLine(edgeColor, p0, p2);
            drawLine(edgeColor, p2, p1);
        }
    }

    private float computeIllumination(Vector3f vertex, Vector4f normal) {
        Vector3f nV = new Vector3f(vertex).mul(-1);
        float illumination = 0;

        for (Light light : lights) {
            Vector3f v1 = null;
            switch (light.getType()) {
                case AMBIENT -> {
                    illumination += light.getIntensity();
                    continue;
                }
                case DIRECTIONAL -> v1 = camera.getTransposedRotationMatrix().mul(light.getVector(), 1);
                case POINT -> v1 = camera.getCameraMatrix().mul(light.getVector(), 1).add(nV);
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

    public void clearDepthBuffer() {
        synchronized (renderTexture) {
            Arrays.fill(depthBuffer, null);
        }
    }

    private boolean updateDepthBufferIfCloser(float x, float y, float z) {
        int px = (Cw >> 1) + (int) (x);
        int py = (Ch >> 1) - (int) (y) - 1;

        if (px < 0 || px >= Cw || py < 0 || py >= Ch) {
            return false;
        }

        int offset = (py * Cw) + px;
        if (depthBuffer[offset] == null || depthBuffer[offset] < z) {
            depthBuffer[offset] = z;
            return true;
        }

        return false;
    }

    public float[] interpolate(float i0, float d0, float i1, float d1) {
        if (i0 == i1) {
            return new float[] { d0 };
        }

        LinkedList<Float> values = new LinkedList<>();
        float a = (d1 - d0) / (i1 - i0);
        float d = d0;
        for (float i = i0; i <= i1; ++i) {
            values.add(d);
            d += a;
        }
        values.add(d); //Just a hack to avoid floating-point precision issues.

        Iterator<Float> it = values.iterator();
        int i = 0;
        float[] array = new float[values.size()];
        while (it.hasNext()) {
            array[i++] = it.next();
        }

        return array;
    }

    public void drawLine(Color c, Vector2f p0, Vector2f p1) {
        float dx = p1.getX() - p0.getX();
        float dy = p1.getY() - p0.getY();

        if (abs(dx) > abs(dy)) {
            if (dx < 0) {
                Vector2f t = p0;
                p0 = p1;
                p1 = t;
            }

            float[] ys = interpolate(p0.getX(), p0.getY(), p1.getX(), p1.getY());
            for (float x = p0.getX(); x <= p1.getX(); ++x) {
                putPixel(c, x, ys[(int) x - (int) p0.getX()]);
            }
        } else {
            if (dy < 0) {
                Vector2f t = p0;
                p0 = p1;
                p1 = t;
            }

            float[] xs = interpolate(p0.getY(), p0.getX(), p1.getY(), p1.getX());
            for (float y = p0.getY(); y <= p1.getY(); ++y) {
                putPixel(c, xs[(int) y - (int) p0.getY()], y);
            }
        }
    }

    public void putPixel(Color c, float x, float y) {
        int px, py, offset;

        px = (Cw >> 1) + (int) (x);
        py = (Ch >> 1) - (int) (y) - 1;
        offset = (py * Cw) + px;
        if (offset >= 0 && offset < renderTexture.getRGB().length)
            renderTexture.getRGB()[offset] = c.rgb();

        //TODO: is there other way to do it?

        px = (Cw >> 1) + round(x);
        py = (Ch >> 1) - round(y) - 1;
        offset = (py * Cw) + px;
        if (offset >= 0 && offset < renderTexture.getRGB().length)
            renderTexture.getRGB()[offset] = c.rgb();

        px = (Cw >> 1) + (int) ceil(x);
        py = (Ch >> 1) - (int) ceil(y) - 1;
        offset = (py * Cw) + px;
        if (offset >= 0 && offset < renderTexture.getRGB().length)
            renderTexture.getRGB()[offset] = c.rgb();

        px = (Cw >> 1) + (int) floor(x);
        py = (Ch >> 1) - (int) floor(y) - 1;
        offset = (py * Cw) + px;
        if (offset >= 0 && offset < renderTexture.getRGB().length)
            renderTexture.getRGB()[offset] = c.rgb();
    }

    public Vector2f viewportToCanvas(float x, float y) {
        return new Vector2f(x * Cw / Vw, y * Ch / Vh);
    }

    public Vector2f canvasToViewport(float x, float y) {
        return new Vector2f(x * Vw / Cw, y * Vh / Ch);
    }

    public Vector2f projectVertex(Vector3f v) {
        //distance from the camera
        float d = 1;
        return viewportToCanvas(v.getX() * d / v.getZ(), v.getY() * d / v.getZ());
    }

    public Vector3f unprojectVertex(float x, float y, float z) {
        float oz = 1.0f / z;
        //projection plane Z
        float pZ = 1;
        float ux = x * oz / pZ;
        float uy = y * oz / pZ;
        Vector2f p2d = canvasToViewport(ux, uy);
        return new Vector3f(p2d.getX(), p2d.getY(), oz);
    }

    public boolean isDepthBufferingEnabled() {
        return depthBufferingEnabled;
    }

    public boolean isBackfaceCullingEnabled() {
        return backfaceCullingEnabled;
    }

    public boolean isDrawOutlines() {
        return drawOutlines;
    }

    public boolean isLightDiffuse() {
        return isLightDiffuse;
    }

    public boolean isLightSpecular() {
        return isLightSpecular;
    }

    public ShadingType getShadingModel() {
        return shadingModel;
    }

    public boolean isUseVertexNormals() {
        return useVertexNormals;
    }

    public boolean isUsePerspectiveCorrectDepth() {
        return usePerspectiveCorrectDepth;
    }

    public void setDepthBufferingEnabled(boolean depthBufferingEnabled) {
        this.depthBufferingEnabled = depthBufferingEnabled;
    }

    public void setBackfaceCullingEnabled(boolean backfaceCullingEnabled) {
        this.backfaceCullingEnabled = backfaceCullingEnabled;
    }

    public void setDrawOutlines(boolean drawOutlines) {
        this.drawOutlines = drawOutlines;
    }

    public void setLightDiffuse(boolean lightDiffuse) {
        isLightDiffuse = lightDiffuse;
    }

    public void setLightSpecular(boolean lightSpecular) {
        isLightSpecular = lightSpecular;
    }

    public void setShadingModel(ShadingType shadingModel) {
        this.shadingModel = shadingModel;
    }

    public void setUseVertexNormals(boolean useVertexNormals) {
        this.useVertexNormals = useVertexNormals;
    }

    public void setUsePerspectiveCorrectDepth(boolean usePerspectiveCorrectDepth) {
        this.usePerspectiveCorrectDepth = usePerspectiveCorrectDepth;
    }

    public Color getEdgeColor() {
        return edgeColor;
    }

    public Color getMaterialColor() {
        return materialColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setEdgeColor(Color edgeColor) {
        this.edgeColor = edgeColor;
    }

    public void setMaterialColor(Color materialColor) {
        this.materialColor = materialColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Texture getRenderTexture() {
        synchronized (renderTexture) {
            return renderTexture;
        }
    }

    public TextureMode getTextureMode() {
        return textureMode;
    }

    public void setTextureMode(TextureMode textureMode) {
        this.textureMode = textureMode;
    }

    public Camera getCurrentCamera() {
        synchronized (renderTexture) {
            return camera;
        }
    }
}
