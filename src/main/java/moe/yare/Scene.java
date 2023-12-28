package moe.yare;

import java.awt.*;
import java.util.LinkedList;

import static moe.yare.Primitives.*;

public class Scene {

    //TODO: camera movement

    private Camera camera = new Camera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));

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

        Vector3i[] triangles = new Vector3i[model.getTriangles().length];
        System.arraycopy(model.getTriangles(), 0, triangles, 0, triangles.length);
        for (Plane clippingPlane : clippingPlanes) {
            LinkedList<Vector3i> newTriangles = new LinkedList<>();
            for (Vector3i triangle : triangles) {
                clipTriangle(triangle, clippingPlane, newTriangles, vertices);
            }
            triangles = newTriangles.toArray(new Vector3i[0]);
        }

        return new Model(vertices, triangles, center, model.getBoundsRadius());
    }

    private void clipTriangle(Vector3i triangle, Plane clippingPlane, LinkedList<Vector3i> newTriangles, Vector3f[] vertices) {
        Vector3f v0 = vertices[triangle.getX()];
        Vector3f v1 = vertices[triangle.getY()];
        Vector3f v2 = vertices[triangle.getZ()];

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

        for (Vector3i triangle : model.getTriangles()) {
            renderTriangle(g, triangle, projected);
        }
    }
}
