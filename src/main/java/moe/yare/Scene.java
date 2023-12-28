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
            renderInstance(g, instance);
        }
    }

    private void renderInstance(Graphics g, Instance instance) {
        Model model = instance.getModel();
        synchronized (model) {
            Vector3f[] vertices = model.getVertices();

            Vector2f[] projected = new Vector2f[vertices.length];
            for (int i = 0; i < vertices.length; ++i) {
                Vector3f v = vertices[i];
                Vector4f vH = new Vector4f(v.getX(), v.getY(), v.getZ(), 1);
                Matrix4f tm = camera.getCameraMatrix().mul(instance.getTransformMatrix());
                projected[i] = projectVertex(tm.mul(vH));
            }

            for (Vector3i triangle : model.getTriangles()) {
                renderTriangle(g, triangle, projected);
            }
        }
    }
}
