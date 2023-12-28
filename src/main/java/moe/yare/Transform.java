package moe.yare;

import static java.lang.Math.*;

public class Transform {

    public static Matrix4f makeRotationMatrix(Vector3f rotation) {
        return new Matrix4f(
                new float[] { 1, 0, 0, 0 },
                new float[] { 0, (float) cos(toRadians(rotation.getX())), (float) -sin(toRadians(rotation.getX())), 0 },
                new float[] { 0, (float) sin(toRadians(rotation.getX())), (float) cos(toRadians(rotation.getX())), 0 },
                new float[] { 0, 0, 0, 1 }
        ).mul(new Matrix4f(
                new float[] {(float) cos(toRadians(rotation.getY())), 0, (float) sin(toRadians(rotation.getY())), 0 },
                new float[] { 0, 1, 0, 0 },
                new float[] {(float) -sin(toRadians(rotation.getY())), 0, (float) cos(toRadians(rotation.getY())), 0 },
                new float[] { 0, 0, 0, 1 }
        )).mul(new Matrix4f(
                new float[] {(float) cos(toRadians(rotation.getZ())), (float) -sin(toRadians(rotation.getZ())), 0, 0 },
                new float[] {(float) sin(toRadians(rotation.getZ())), (float) cos(toRadians(rotation.getZ())), 0, 0 },
                new float[] { 0, 0, 1, 0 },
                new float[] { 0, 0, 0, 1 }
        ));
    }

    public static  Matrix4f makeScalingMatrix(Vector3f scaling) {
        return new Matrix4f(
                new float[] { scaling.getX(), 0, 0, 0 },
                new float[] { 0, scaling.getY(), 0, 0 },
                new float[] { 0, 0, scaling.getZ(), 0 },
                new float[] { 0, 0, 0, 1 }
        );
    }

    public static  Matrix4f makeTranslationMatrix(Vector3f translation) {
        return new Matrix4f(
                new float[] { 1, 0, 0, translation.getX() },
                new float[] { 0, 1, 0, translation.getY() },
                new float[] { 0, 0, 1, translation.getZ() },
                new float[] { 0, 0, 0, 1 }
        );
    }
}
