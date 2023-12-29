package moe.yare;

public class Matrix4f {

    private final float[][] matrix;

    public Matrix4f(float[] r1, float[] r2, float[] r3, float[] r4) {
        this.matrix = new float[][] { r1, r2, r3, r4 };
    }

    public Matrix4f() {
        matrix = new float[][] {
                new float[] { 0, 0, 0, 0 },
                new float[] { 0, 0, 0, 0 },
                new float[] { 0, 0, 0, 0 },
                new float[] { 0, 0, 0, 0 }
        };
    }

    public Matrix4f mul(Matrix4f m) {
        Matrix4f result = new Matrix4f();

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    result.matrix[i][j] += matrix[i][k] * m.matrix[k][j];
                }
            }
        }

        return result;
    }

    public Vector4f mul(Vector3f v, float w) {
        return mul(v.getX(), v.getY(), v.getZ(), w);
    }

    public Vector4f mul(Vector4f v) {
        return mul(v.getX(), v.getY(), v.getZ(), v.getW());
    }

    public Vector4f mul(float x, float y, float z, float w) {
        float[] result = new float[] { 0, 0, 0, 0 };
        float[] vector = new float[] { x, y, z, w };

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                result[i] += matrix[i][j] * vector[j];
            }
        }

        return new Vector4f(result[0], result[1], result[2], result[3]);
    }

    public Matrix4f transposed() {
        Matrix4f result = new Matrix4f();

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                result.matrix[i][j] = matrix[j][i];
            }
        }

        return result;
    }
}
