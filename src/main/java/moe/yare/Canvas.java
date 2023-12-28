package moe.yare;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {

    @Override
    public void paint(Graphics g) {
        Model model = new Model(new Vector3f[] {
                new Vector3f(1, 1, 1),
                new Vector3f(-1, 1, 1),
                new Vector3f(-1, -1, 1),
                new Vector3f(1, -1, 1),
                new Vector3f(1, 1, -1),
                new Vector3f(-1, 1, -1),
                new Vector3f(-1, -1, -1),
                new Vector3f(1, -1, -1),
        }, new Vector3i[] {
                new Vector3i(0, 1, 2),
                new Vector3i(0, 2, 3),
                new Vector3i(4, 0, 3),
                new Vector3i(4, 3, 7),
                new Vector3i(5, 4, 7),
                new Vector3i(5, 7, 6),
                new Vector3i(1, 5, 6),
                new Vector3i(1, 6, 2),
                new Vector3i(4, 5, 1),
                new Vector3i(4, 1, 0),
                new Vector3i(2, 6, 7),
                new Vector3i(2, 7, 3),
        });

        Instance cube1 = new Instance(model,
                new Vector3f(-1.5f, 0, 7),
                new Vector3f(0, 0, 0),
                new Vector3f(1.5f, 1.5f, 1.5f));
        Instance cube2 = new Instance(model,
                new Vector3f(1f, -2, 7.5f),
                new Vector3f(0, 0, 0),
                new Vector3f(2f, 0.5f, 1f));
        Instance cube3 = new Instance(model,
                new Vector3f(0, 0, -20f),
                new Vector3f(0, 0, 0),
                new Vector3f(2f, 0.5f, 1f));

        Scene scene = new Scene();
        scene.addInstance(cube1);
        scene.addInstance(cube2);
        scene.addInstance(cube3);

        scene.renderScene(g);
    }
}
