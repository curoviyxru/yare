package moe.yare.ui;

import moe.yare.io.ObjReader;
import moe.yare.io.TextureReader;
import moe.yare.math.Vector3f;
import moe.yare.render.BasicModels;
import moe.yare.render.Color;
import moe.yare.render.Instance;
import moe.yare.render.Scene;

import javax.swing.*;

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
    private Instance instance;

    public ViewerFrame() {
        setContentPane(contentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

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
                if (enableYRotationCheckBox.isSelected()) {
                    instance.setRotation(new Vector3f(0, instance.getRotation().getY() + 1f, 0));
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        showASphereButton.addActionListener(e -> setSphere());
        showACrateButton.addActionListener(e -> setCrate());
        loadAModelButton.addActionListener(e -> setModel());

        flatRadioButton.addActionListener(e -> canvas.getScene().setShadingModel(Scene.ShadingType.FLAT));
        gouraudRadioButton.addActionListener(e -> canvas.getScene().setShadingModel(Scene.ShadingType.GOURAUD));
        phongRadioButton.addActionListener(e -> canvas.getScene().setShadingModel(Scene.ShadingType.PHONG));

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
        canvas.getScene().addInstance(instance = new Instance(BasicModels.getCube(null, TextureReader.loadTexture("crate1_diffuse.png")),
                new Vector3f(0, 0, 7),
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1)));
    }

    private void setSphere() {
        canvas.getScene().clearInstances();
        canvas.getScene().addInstance(instance = new Instance(BasicModels.getSphere(30, new Color(0, 255, 0)),
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
        canvas.getScene().addInstance(instance = new Instance(ObjReader.readModel(chooser.getSelectedFile().getAbsolutePath()),
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
