package moe.yare.render;

import moe.yare.math.Matrix4f;
import moe.yare.math.Vector3f;

import static moe.yare.render.Transform.*;

public class Camera {

    private static final float S2 = 1.0f / (float) Math.sqrt(2);

    //TODO: far plane?
    private static final Plane[] clippingPlanes = new Plane[] {
            new Plane(new Vector3f(0, 0, 1), -1), //near
            new Plane(new Vector3f(S2, 0, S2), 0), //left
            new Plane(new Vector3f(-S2, 0, S2), 0), //right
            new Plane(new Vector3f(0, -S2, S2), 0), //top
            new Plane(new Vector3f(0, S2, S2), 0) //bottom
    };

    private Vector3f translation;
    private Vector3f rotation;
    private Matrix4f cameraMatrix;
    private Matrix4f transposedRotationMatrix;

    public Camera(Vector3f translation, Vector3f rotation) {
        this.translation = translation;
        this.rotation = rotation;

        this.cameraMatrix = (transposedRotationMatrix = makeRotationMatrix(rotation).transposed())
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

    public Plane[] getClippingPlanes() {
        return clippingPlanes;
    }

    public Matrix4f getTransposedRotationMatrix() {
        return transposedRotationMatrix;
    }
}
