package moe.yare.render;

import moe.yare.math.Vector2f;
import moe.yare.math.Vector3f;
import moe.yare.math.Vector3i;
import moe.yare.math.Vector4f;

import static java.lang.Math.*;

public class BasicModels {

    public static Model getSphere(int divs, Color color) {
        Vector3f[] vertices = new Vector3f[(divs + 1) * divs];
        Triangle[] triangles = new Triangle[2 * divs * divs];

        float deltaAngle = 2.0f * (float) PI / divs;
        int ti = 0;

        for (int d = 0; d <= divs; ++d) {
            float y = (2.0f / divs) * (d - (float) divs / 2);
            float radius = (float) sqrt(1.0 - y * y);
            for (int i = 0; i < divs; ++i) {
                vertices[ti++] = new Vector3f(radius * (float) cos(i * deltaAngle), y, radius * (float) sin(i*deltaAngle));
            }
        }

        ti = 0;
        for (int d = 0; d < divs; ++d) {
            for (int i = 0; i < divs; ++i) {
                int i0 = d * divs + i;
                int i1 = (d + 1) * divs + (i + 1) % divs;
                int i2 = divs * d + (i + 1) % divs;

                triangles[ti++] = new Triangle(new Vector3i(i0, i1, i2),
                        color, new Vector3f[] { vertices[i0], vertices[i1], vertices[i2] }, null, null);
                triangles[ti++] = new Triangle(new Vector3i(i0, i0 + divs, i1),
                        color, new Vector3f[] { vertices[i0], vertices[i0 + divs], vertices[i1] }, null, null);
            }
        }

        return new Model(vertices, triangles, new Vector4f(0, 0, 0, 1), 1.0f);
    }

    public static Model getCube(Color color, Texture texture) {
        return new Model(new Vector3f[] {
                new Vector3f(1, 1, 1),
                new Vector3f(-1, 1, 1),
                new Vector3f(-1, -1, 1),
                new Vector3f(1, -1, 1),
                new Vector3f(1, 1, -1),
                new Vector3f(-1, 1, -1),
                new Vector3f(-1, -1, -1),
                new Vector3f(1, -1, -1),
        }, new Triangle[] {
                new Triangle(new Vector3i(0, 1, 2), color, new Vector3f[] { new Vector3f(0, 0, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0,1 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(0, 2, 3), color, new Vector3f[] { new Vector3f(0, 0, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0,1 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(4, 0, 3), color, new Vector3f[] { new Vector3f(1, 0, 0), new Vector3f(1, 0, 0), new Vector3f(1, 0,0 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(4, 3, 7), color, new Vector3f[] { new Vector3f(1, 0, 0), new Vector3f(1, 0, 0), new Vector3f(1, 0,0 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(5, 4, 7), color, new Vector3f[] { new Vector3f(0, 0,-1), new Vector3f(0, 0,-1), new Vector3f(0, 0,-1) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(5, 7, 6), color, new Vector3f[] { new Vector3f(0, 0,-1), new Vector3f(0, 0,-1), new Vector3f(0, 0,-1) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(1, 5, 6), color, new Vector3f[] { new Vector3f(-1,0, 0), new Vector3f(-1,0, 0), new Vector3f(-1,0,0 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(1, 6, 2), color, new Vector3f[] { new Vector3f(-1,0, 0), new Vector3f(-1,0, 0), new Vector3f(-1,0,0 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(1, 0, 5), color, new Vector3f[] { new Vector3f(0, 1, 0), new Vector3f(0, 1, 0), new Vector3f(0, 1,0 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(5, 0, 4), color, new Vector3f[] { new Vector3f(0, 1, 0), new Vector3f(0, 1, 0), new Vector3f(0, 1,0 ) }, texture, new Vector2f[] { new Vector2f(0, 1), new Vector2f(1, 1), new Vector2f(0, 0) }),
                new Triangle(new Vector3i(2, 6, 7), color, new Vector3f[] { new Vector3f(0, -1,0), new Vector3f(0, -1,0), new Vector3f(0, -1,0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(2, 7, 3), color, new Vector3f[] { new Vector3f(0, -1,0), new Vector3f(0, -1,0), new Vector3f(0, -1,0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
        });
    }
}
