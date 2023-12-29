package moe.yare.ui;

import moe.yare.io.ObjReader;
import moe.yare.io.TextureReader;
import moe.yare.math.Vector2f;
import moe.yare.math.Vector3f;
import moe.yare.math.Vector3i;
import moe.yare.math.Vector4f;
import moe.yare.render.*;
import moe.yare.render.Color;

import javax.swing.*;
import java.awt.*;

import static java.lang.Math.*;

public class Canvas extends JPanel {

    public Model generateSphere(int divs, moe.yare.render.Color color) {
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

    @Override
    public void paint(Graphics g) {
        moe.yare.render.Color RED =       new moe.yare.render.Color(255, 0, 0);
        moe.yare.render.Color GREEN =     new moe.yare.render.Color(0, 255, 0);
        moe.yare.render.Color BLUE =      new moe.yare.render.Color(0, 0, 255);
        moe.yare.render.Color YELLOW =    new moe.yare.render.Color(255, 255, 0);
        moe.yare.render.Color PURPLE =    new moe.yare.render.Color(255, 0, 255);
        moe.yare.render.Color CYAN =      new Color(0, 255, 255);

        Texture texture = null;
        try {
            texture = TextureReader.loadTexture("crate1_diffuse.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Model model = new Model(new Vector3f[] {
                new Vector3f(1, 1, 1),
                new Vector3f(-1, 1, 1),
                new Vector3f(-1, -1, 1),
                new Vector3f(1, -1, 1),
                new Vector3f(1, 1, -1),
                new Vector3f(-1, 1, -1),
                new Vector3f(-1, -1, -1),
                new Vector3f(1, -1, -1),
        }, new Triangle[] {
                new Triangle(new Vector3i(0, 1, 2), RED,    new Vector3f[] { new Vector3f( 0,  0,  1), new Vector3f( 0,  0,  1), new Vector3f( 0,  0,  1) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(0, 2, 3), RED,    new Vector3f[] { new Vector3f( 0,  0,  1), new Vector3f( 0,  0,  1), new Vector3f( 0,  0,  1) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(4, 0, 3), GREEN,  new Vector3f[] { new Vector3f( 1,  0,  0), new Vector3f( 1,  0,  0), new Vector3f( 1,  0,  0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(4, 3, 7), GREEN,  new Vector3f[] { new Vector3f( 1,  0,  0), new Vector3f( 1,  0,  0), new Vector3f( 1,  0,  0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(5, 4, 7), BLUE,   new Vector3f[] { new Vector3f( 0,  0, -1), new Vector3f( 0,  0, -1), new Vector3f( 0,  0, -1) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(5, 7, 6), BLUE,   new Vector3f[] { new Vector3f( 0,  0, -1), new Vector3f( 0,  0, -1), new Vector3f( 0,  0, -1) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(1, 5, 6), YELLOW, new Vector3f[] { new Vector3f(-1,  0,  0), new Vector3f(-1,  0,  0), new Vector3f(-1,  0,  0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(1, 6, 2), YELLOW, new Vector3f[] { new Vector3f(-1,  0,  0), new Vector3f(-1,  0,  0), new Vector3f(-1,  0,  0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
                new Triangle(new Vector3i(1, 0, 5), PURPLE, new Vector3f[] { new Vector3f( 0,  1,  0), new Vector3f( 0,  1,  0), new Vector3f( 0,  1,  0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(5, 0, 4), PURPLE, new Vector3f[] { new Vector3f( 0,  1,  0), new Vector3f( 0,  1,  0), new Vector3f( 0,  1,  0) }, texture, new Vector2f[] { new Vector2f(0, 1), new Vector2f(1, 1), new Vector2f(0, 0) }),
                new Triangle(new Vector3i(2, 6, 7), CYAN,   new Vector3f[] { new Vector3f( 0, -1,  0), new Vector3f( 0, -1,  0), new Vector3f( 0, -1,  0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(1, 1) }),
                new Triangle(new Vector3i(2, 7, 3), CYAN,   new Vector3f[] { new Vector3f( 0, -1,  0), new Vector3f( 0, -1,  0), new Vector3f( 0, -1,  0) }, texture, new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(0, 1) }),
        });
        Model model1 = null;
        try {
            model1 = ObjReader.readModel("manki.obj");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Instance cube1 = new Instance(model,
                new Vector3f(-1.5f, 0, 7),
                new Vector3f(0, 180, 0),
                new Vector3f(0.75f, 0.75f, 0.75f));
        Instance cube2 = new Instance(model1,
                new Vector3f(1.25f, 2.5f, 7.5f),
                new Vector3f(0, 180, 0),
                new Vector3f(1f, 1f, 1f));
        Instance cube3 = new Instance(model1,
                new Vector3f(0, 0, -20f),
                new Vector3f(0, 180, 0),
                new Vector3f(1f, 1f, 1f));
        Instance cube4 = new Instance(model,
                new Vector3f(1.75f, 0f, 5f),
                new Vector3f(0, -30, 0),
                new Vector3f(0.5f, 0.5f, 0.5f));

        Scene scene = new Scene();
        scene.addInstance(cube1);
        scene.addInstance(cube2);
        scene.addInstance(cube3);
        scene.addInstance(cube4);

        scene.renderScene(g);
    }
}
