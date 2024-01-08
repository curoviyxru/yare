package moe.yare.render;

import moe.yare.math.Vector2f;
import moe.yare.math.Vector3f;
import moe.yare.math.Vector3i;
import moe.yare.math.Vector4f;

import static java.lang.Math.*;

public class BasicModels {

    public static Model getSphere(Color color1,
                                  Color color2,
                                  Color color3,
                                  Color color4,
                                  Color color5,
                                  Color color6,
                                  Texture texture,
                                  int divs) {
        Color[] colors = new Color[] {
                color1, color2, color3, color4, color5, color6
        };

        Vector3f[] vertices = new Vector3f[(divs + 1) * divs];
        Vector2f[] uv = new Vector2f[(divs + 1) * divs];
        Triangle[] triangles = new Triangle[2 * divs * divs];

        float deltaAngle = 2.0f * (float) PI / divs;

        int ti = 0;
        for (int d = 0; d <= divs; ++d) {
            float y = (2.0f / divs) * (d - (float) divs / 2);
            float radius = (float) sqrt(1.0 - y * y);
            for (int i = 0; i < divs; ++i) {
                vertices[ti] = new Vector3f(radius * (float) cos(i * deltaAngle), y, radius * (float) sin(i * deltaAngle));
                uv[ti] = new Vector2f((float) i / divs, (float) d / divs);
                ti++;
            }
        }

        ti = 0;
        for (int d = 0; d < divs; ++d) {
            for (int i = 0; i < divs; ++i) {
                int i0 = d * divs + i;
                int i1 = (d + 1) * divs + (i + 1) % divs;
                int i2 = divs * d + (i + 1) % divs;
                int i0d = i0 + divs;

                if (i == divs - 1) {
                    triangles[ti++] = new Triangle(new Vector3i(i0, i1, i2),
                            colors[i % colors.length], new Vector3f[] { vertices[i0], vertices[i1], vertices[i2] },
                            texture, new Vector2f[] { uv[i0], new Vector2f(uv[i1]).setX(1), new Vector2f(uv[i2]).setX(1) });
                    triangles[ti++] = new Triangle(new Vector3i(i0, i0d, i1),
                            colors[i % colors.length], new Vector3f[] { vertices[i0], vertices[i0d], vertices[i1] },
                            texture, new Vector2f[] { uv[i0], uv[i0d], new Vector2f(uv[i1]).setX(1) });
                } else {
                    triangles[ti++] = new Triangle(new Vector3i(i0, i1, i2),
                            colors[i % colors.length], new Vector3f[] { vertices[i0], vertices[i1], vertices[i2] },
                            texture, new Vector2f[] { uv[i0], uv[i1], uv[i2] });
                    triangles[ti++] = new Triangle(new Vector3i(i0, i0d, i1),
                            colors[i % colors.length], new Vector3f[] { vertices[i0], vertices[i0d], vertices[i1] },
                            texture, new Vector2f[] { uv[i0], uv[i0d], uv[i1] });
                }
            }
        }

        return new Model(vertices, triangles, new Vector4f(0, 0, 0, 1), 1.0f);
    }

    public static Model getSphere(Texture texture, Color color, int divs) {
        return getSphere(color,
                color,
                color,
                color,
                color,
                color,
                texture,
                divs);
    }

    public static Model getSphere(Texture texture, int divs) {
        return getSphere(new Color(255, 0, 0),
                new Color(0, 255, 0),
                new Color(0, 0, 255),
                new Color(255, 255, 0),
                new Color(255, 0, 255),
                new Color(0, 255, 255),
                texture,
                divs);
    }

    public static Model getCube(Color color1,
                                Color color2,
                                Color color3,
                                Color color4,
                                Color color5,
                                Color color6,
                                Texture texture) {
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
                new Triangle(new Vector3i(0, 1, 2), color1, new Vector3f[] { new Vector3f(0, 0, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0,1 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(0, 2, 3), color1, new Vector3f[] { new Vector3f(0, 0, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0,1 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(4, 0, 3), color2, new Vector3f[] { new Vector3f(1, 0, 0), new Vector3f(1, 0, 0), new Vector3f(1, 0,0 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(4, 3, 7), color2, new Vector3f[] { new Vector3f(1, 0, 0), new Vector3f(1, 0, 0), new Vector3f(1, 0,0 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(5, 4, 7), color3, new Vector3f[] { new Vector3f(0, 0,-1), new Vector3f(0, 0,-1), new Vector3f(0, 0,-1) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(5, 7, 6), color3, new Vector3f[] { new Vector3f(0, 0,-1), new Vector3f(0, 0,-1), new Vector3f(0, 0,-1) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(1, 5, 6), color4, new Vector3f[] { new Vector3f(-1,0, 0), new Vector3f(-1,0, 0), new Vector3f(-1,0,0 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(1, 6, 2), color4, new Vector3f[] { new Vector3f(-1,0, 0), new Vector3f(-1,0, 0), new Vector3f(-1,0,0 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(1, 0, 5), color5, new Vector3f[] { new Vector3f(0, 1, 0), new Vector3f(0, 1, 0), new Vector3f(0, 1,0 ) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(5, 0, 4), color5, new Vector3f[] { new Vector3f(0, 1, 0), new Vector3f(0, 1, 0), new Vector3f(0, 1,0 ) }, texture, new Vector2f[] { new Vector2f(0, 1), new Vector2f(1, 1), new Vector2f(0, 0) }),
                new Triangle(new Vector3i(2, 6, 7), color6, new Vector3f[] { new Vector3f(0, -1,0), new Vector3f(0, -1,0), new Vector3f(0, -1,0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(2, 7, 3), color6, new Vector3f[] { new Vector3f(0, -1,0), new Vector3f(0, -1,0), new Vector3f(0, -1,0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
        });
    }

    public static Model getCube(Color color, Texture texture) {
        return getCube(color, color, color, color, color, color, texture);
    }

    public static Model getCube(Texture texture) {
        return getCube(new Color(255, 0, 0),
                new Color(0, 255, 0),
                new Color(0, 0, 255),
                new Color(255, 255, 0),
                new Color(255, 0, 255),
                new Color(0, 255, 255),
                texture);
    }
}
