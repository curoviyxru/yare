package moe.yare.render;

import moe.yare.math.Matrix4f;
import moe.yare.math.Vector3f;

import static moe.yare.render.Transform.*;

public class Instance {

    private Model model;

    private Vector3f translation;
    private Vector3f rotation;
    private Vector3f scaling;
    private Matrix4f transformMatrix;
    private Matrix4f orientationMatrix;

    private final Object lock = new Object();

    public Instance(Model model, Vector3f translation, Vector3f rotation, Vector3f scaling) {
        this.model = model;

        this.translation = translation;
        this.rotation = rotation;
        this.scaling = scaling;

        updateTransformMatrix();
    }

    public void updateTransformMatrix() {
        this.transformMatrix = makeTranslationMatrix(translation)
                .mul(orientationMatrix = makeRotationMatrix(rotation))
                .mul(makeScalingMatrix(scaling));
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScaling() {
        return scaling;
    }

    public Matrix4f getTransformMatrix() {
        return transformMatrix;
    }

    public Matrix4f getOrientationMatrix() {
        return orientationMatrix;
    }

    public void setModel(Model model) {
        synchronized (lock) {
            this.model = model;
        }
    }

    public void setTranslation(Vector3f translation) {
        synchronized (lock) {
            this.translation = translation;
            updateTransformMatrix();
        }
    }

    public void setScaling(Vector3f scaling) {
        synchronized (lock) {
            this.scaling = scaling;
            updateTransformMatrix();
        }
    }

    public void setRotation(Vector3f rotation) {
        synchronized (lock) {
            this.rotation = rotation;
            updateTransformMatrix();
        }
    }

    public Object getLock() {
        return lock;
    }
}
