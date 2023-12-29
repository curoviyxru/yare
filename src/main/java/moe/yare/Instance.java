package moe.yare;

import static moe.yare.Transform.*;

public class Instance {

    private Model model;

    private Vector3f translation;
    private Vector3f rotation;
    private Vector3f scaling;
    private Matrix4f transformMatrix;
    private Matrix4f orientationMatrix;

    public Instance(Model model, Vector3f translation, Vector3f rotation, Vector3f scaling) {
        this.model = model;

        this.translation = translation;
        this.rotation = rotation;
        this.scaling = scaling;

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
}
