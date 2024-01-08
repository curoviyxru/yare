package moe.yare.ui;

import moe.yare.io.ObjReader;
import moe.yare.io.TextureReader;
import moe.yare.math.Vector2i;
import moe.yare.math.Vector3f;
import moe.yare.render.BasicModels;
import moe.yare.render.Instance;
import moe.yare.render.Scene;
import moe.yare.render.Texture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class ViewerFrame extends JFrame {

    private JPanel contentPanel;
    private Canvas canvas;

    private Instance instance;
    private Vector2i mousePoint;

    public ViewerFrame() {
        setupUI();
        setJMenuBar(createMenu());

        setContentPane(contentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setTitle("YARE");

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

        setCube();
        new Thread(() -> {
            while (true) {
                canvas.repaint();
            }
        }).start();
    }

    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();

        //File menu
        JMenu fileMenu = new JMenu("File");
        addItem(fileMenu, "Load a cube", a -> setCube());
        addItem(fileMenu, "Load a sphere", a -> setSphere());
        addItem(fileMenu, "Load a model", a -> setModel());
        fileMenu.addSeparator();
        addItem(fileMenu, "Exit", a -> System.exit(0));
        menuBar.add(fileMenu);

        //Rendering menu
        JMenu renderMenu = new JMenu("Rendering");
        addCheckboxItem(renderMenu, "Backface culling", true, a -> {
            canvas.getScene().setBackfaceCullingEnabled(a);
            canvas.getScene().setDepthBufferingEnabled(a);
        });
        addCheckboxItem(renderMenu, "Draw outlines", false, a -> {
            canvas.getScene().setDrawOutlines(a);
        });
        addCheckboxItem(renderMenu, "Do light diffuse", true, a -> {
            canvas.getScene().setLightDiffuse(a);
        });
        addCheckboxItem(renderMenu, "Do light specular", true, a -> {
            canvas.getScene().setLightSpecular(a);
        });
        addCheckboxItem(renderMenu, "Use vertex normals", true, a -> {
            canvas.getScene().setUseVertexNormals(a);
        });
        addCheckboxItem(renderMenu, "Use perspective correct depth", true, a -> {
            canvas.getScene().setUsePerspectiveCorrectDepth(a);
        });
        menuBar.add(renderMenu);

        JMenu texturingMenu = new JMenu("Texturing");
        addRadioItems(texturingMenu, 0, i -> {
            canvas.getScene().setTextureMode(Scene.TextureMode.values()[i]);
        }, "Texture color", "Triangle color", "One color");
        menuBar.add(texturingMenu);

        JMenu shadingMenu = new JMenu("Shading");
        addRadioItems(shadingMenu, 2, i -> {
            canvas.getScene().setShadingModel(Scene.ShadingType.values()[i]);
        }, "Flat model", "Gouraud model", "Phong model");
        menuBar.add(shadingMenu);

        return menuBar;
    }

    public interface RadioListener {
        void onAction(int i);
    }

    public interface ComboListener {
        void onAction(boolean b);
    }

    private void addRadioItems(JMenu menu, int selectedIndex, RadioListener listener, String... names) {
        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < names.length; ++i) {
            JMenuItem item = new JRadioButtonMenuItem(names[i]);
            item.setSelected(i == selectedIndex);
            if (listener != null) {
                int finalI = i;
                item.addActionListener(a -> listener.onAction(finalI));
            }
            group.add(item);
            menu.add(item);
        }
    }

    private void addItem(JMenu menu, String name, ActionListener listener) {
        JMenuItem item = new JMenuItem(name);
        if (listener != null) {
            item.addActionListener(listener);
        }
        menu.add(item);
    }

    private void addCheckboxItem(JMenu menu, String name, boolean value, ComboListener listener) {
        JMenuItem item = new JCheckBoxMenuItem(name);
        item.setSelected(value);
        if (listener != null) {
            item.addActionListener(a -> listener.onAction(item.isSelected()));
        }
        menu.add(item);
    }

    private void setCube() {
        Texture texture = null;
        try {
            texture = TextureReader.loadTexture("crate1_diffuse.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        canvas.getScene().clearInstances();
        canvas.getScene().addInstance(instance = new Instance(
                BasicModels.getCube(texture),
                new Vector3f(0, 0, 7),
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1)));
    }

    private void setSphere() {
        Texture texture = null;
        try {
            texture = TextureReader.loadTexture("earth.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        canvas.getScene().clearInstances();
        canvas.getScene().addInstance(instance = new Instance(
                BasicModels.getSphere(texture, 30),
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

    public static void main(String[] args)
            throws UnsupportedLookAndFeelException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new ViewerFrame().setVisible(true);
    }

    private void setupUI() {
        canvas = new Canvas();
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(canvas, gbc);
    }
}
