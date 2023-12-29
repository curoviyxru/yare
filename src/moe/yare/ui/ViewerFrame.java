package moe.yare.ui;

import moe.yare.io.ObjReader;
import moe.yare.io.TextureReader;
import moe.yare.math.Vector2i;
import moe.yare.math.Vector3f;
import moe.yare.render.BasicModels;
import moe.yare.render.Color;
import moe.yare.render.Instance;
import moe.yare.render.Scene;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class ViewerFrame extends JFrame {

    private JPanel contentPanel;
    private JCheckBox backfaceCullingCheckBox;
    private JCheckBox drawOutlinesCheckBox;
    private JCheckBox doLightDiffuseCheckBox;
    private JCheckBox doLightSpecularCheckBox;
    private JRadioButton flatRadioButton;
    private JRadioButton gouraudRadioButton;
    private JRadioButton phongRadioButton;
    private JCheckBox useVertexNormalsCheckBox;
    private JCheckBox usePerspectiveCorrectDepthCheckBox;
    private JButton loadAModelButton;
    private JCheckBox enableYRotationCheckBox;
    private JButton showACrateButton;
    private JButton showASphereButton;
    private Canvas canvas;
    private JCheckBox enableXRotationCheckBox;
    private JCheckBox enableZRotationCheckBox;
    private JRadioButton textureColorRadioButton;
    private JRadioButton triangleColorRadioButton;
    private JRadioButton oneColorRadioButton;

    private Instance instance;
    private Vector2i mousePoint;

    public ViewerFrame() {
        setContentPane(contentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setTitle("YARE");

        setCrate();

        new Thread(() -> {
            while (true) {
                canvas.repaint();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (enableXRotationCheckBox.isSelected()
                        || enableYRotationCheckBox.isSelected()
                        || enableZRotationCheckBox.isSelected()) {
                    synchronized (instance.getLock()) {
                        instance.getRotation().add(enableXRotationCheckBox.isSelected() ? 1 : 0,
                                enableYRotationCheckBox.isSelected() ? 1 : 0,
                                enableZRotationCheckBox.isSelected() ? 1 : 0);
                        instance.updateTransformMatrix();
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mousePoint = null;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mousePoint = new Vector2i(e.getX(), e.getY());
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                synchronized (instance.getLock()) {
                    instance.getRotation().add(mousePoint.getY() - e.getY(),
                            mousePoint.getX() - e.getX(),
                            0);
                    instance.updateTransformMatrix();
                    mousePoint.set(e.getX(), e.getY());
                }
            }
        });

        showASphereButton.addActionListener(e -> setSphere());
        showACrateButton.addActionListener(e -> setCrate());
        loadAModelButton.addActionListener(e -> setModel());

        flatRadioButton.addActionListener(e -> canvas.getScene().setShadingModel(Scene.ShadingType.FLAT));
        gouraudRadioButton.addActionListener(e -> canvas.getScene().setShadingModel(Scene.ShadingType.GOURAUD));
        phongRadioButton.addActionListener(e -> canvas.getScene().setShadingModel(Scene.ShadingType.PHONG));

        textureColorRadioButton.addActionListener(e -> canvas.getScene().setTextureMode(Scene.TextureMode.TEXTURE_COLOR));
        triangleColorRadioButton.addActionListener(e -> canvas.getScene().setTextureMode(Scene.TextureMode.TRIANGLE_COLOR));
        oneColorRadioButton.addActionListener(e -> canvas.getScene().setTextureMode(Scene.TextureMode.ONE_COLOR));

        useVertexNormalsCheckBox.addActionListener(e -> canvas.getScene().setUseVertexNormals(useVertexNormalsCheckBox.isSelected()));
        usePerspectiveCorrectDepthCheckBox.addActionListener(e -> canvas.getScene().setUsePerspectiveCorrectDepth(usePerspectiveCorrectDepthCheckBox.isSelected()));
        doLightDiffuseCheckBox.addActionListener(e -> canvas.getScene().setLightDiffuse(doLightDiffuseCheckBox.isSelected()));
        doLightSpecularCheckBox.addActionListener(e -> canvas.getScene().setLightSpecular(doLightSpecularCheckBox.isSelected()));
        drawOutlinesCheckBox.addActionListener(e -> canvas.getScene().setDrawOutlines(drawOutlinesCheckBox.isSelected()));
        backfaceCullingCheckBox.addActionListener(e -> {
            canvas.getScene().setBackfaceCullingEnabled(backfaceCullingCheckBox.isSelected());
            canvas.getScene().setDepthBufferingEnabled(backfaceCullingCheckBox.isSelected());
        });
    }

    private void setCrate() {
        canvas.getScene().clearInstances();
        canvas.getScene().addInstance(instance = new Instance(BasicModels.getCube(TextureReader.loadTexture("crate1_diffuse.png")),
                new Vector3f(0, 0, 7),
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1)));
    }

    private void setSphere() {
        canvas.getScene().clearInstances();
        canvas.getScene().addInstance(instance = new Instance(
                BasicModels.getSphere(30),
                new Vector3f(0, 0, 7),
                new Vector3f(0, 0, 0),
                new Vector3f(2, 2, 2)));
    }

    private void setModel() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showDialog(this, null);
        if (result != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
            return;
        }

        canvas.getScene().clearInstances();
        canvas.getScene().addInstance(instance = new Instance(
                ObjReader.readModel(chooser.getSelectedFile().getAbsolutePath()),
                new Vector3f(0, 0, 7),
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1)));
    }

    private void createUIComponents() {
        canvas = new Canvas();
    }

    public static void main(String[] args)
            throws UnsupportedLookAndFeelException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new ViewerFrame().setVisible(true);
    }
}
