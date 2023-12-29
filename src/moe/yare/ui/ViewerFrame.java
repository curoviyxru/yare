package moe.yare.ui;

import moe.yare.io.TextureReader;
import moe.yare.math.Vector3f;
import moe.yare.render.BasicModels;
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

    private Scene scene = new Scene();

    public ViewerFrame() {
        setContentPane(contentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        scene.addInstance(new Instance(BasicModels.getCube(null, TextureReader.loadTexture("crate1_diffuse.png")),
                new Vector3f(-1, 0, 0),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 0, 0)));

        new Thread(() -> {
            canvas.repaint();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
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
