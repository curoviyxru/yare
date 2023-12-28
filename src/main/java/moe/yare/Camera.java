package moe.yare;

import static moe.yare.Transform.*;

public class Camera {

    private Vector3f translation;
    private Vector3f rotation;
    private Matrix4f cameraMatrix;

    public Camera(Vector3f translation, Vector3f rotation) {
        this.translation = translation;
        this.rotation = rotation;

        this.cameraMatrix = makeRotationMatrix(rotation)
                .transposed()
                .mul(makeTranslationMatrix(new Vector3f(translation).mul(-1)));
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Matrix4f getCameraMatrix() {
        return cameraMatrix;
    }
}
