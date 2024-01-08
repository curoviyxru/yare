package moe.yare.ui;

import moe.yare.io.ObjReader;
import moe.yare.io.TextureReader;
import moe.yare.math.Vector2i;
import moe.yare.math.Vector3f;
import moe.yare.render.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ViewerFrame extends JFrame {

    private JPanel contentPanel;
    private Canvas canvas;

    private Vector2i mousePoint;

    private SceneList sceneList;
    private InstanceInfo instanceInfo;

    private Vector3f movementVector = new Vector3f(0, 0, 0);

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
                canvas.requestFocusInWindow();
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                synchronized (canvas.getScene().getRenderTexture()) {
                    int dy = e.getY() - mousePoint.getY();
                    int dx = e.getX() - mousePoint.getX();
                    float sens = 0.5f;
                    Camera camera = canvas.getScene().getCurrentCamera();

                    synchronized (camera.getLock()) {
                        camera.getRotation().add(dy * sens, dx * sens, 0);
                        camera.updateCameraMatrix();
                    }

                    mousePoint.set(e.getX(), e.getY());
                }
            }
        });
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 81:
                        movementVector.setY(1);
                        break;
                    case 69:
                        movementVector.setY(-1);
                        break;
                    case 87:
                        movementVector.setZ(1);
                        break;
                    case 83:
                        movementVector.setZ(-1);
                        break;
                    case 68:
                        movementVector.setX(1);
                        break;
                    case 65:
                        movementVector.setX(-1);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 81:
                        if (movementVector.getY() == 1)
                            movementVector.setY(0);
                        break;
                    case 69:
                        if (movementVector.getY() == -1)
                            movementVector.setY(0);
                        break;
                    case 87:
                        if (movementVector.getZ() == 1)
                            movementVector.setZ(0);
                        break;
                    case 83:
                        if (movementVector.getZ() == -1)
                            movementVector.setZ(0);
                        break;
                    case 68:
                        if (movementVector.getX() == 1)
                            movementVector.setX(0);
                        break;
                    case 65:
                        if (movementVector.getX() == -1)
                            movementVector.setX(0);
                        break;
                }
            }
        });
        canvas.requestFocusInWindow();

        addCube();
        new Thread(() -> {
            while (true) {
                canvas.repaint();
            }
        }).start();
        new Thread(() -> {
            float movementSpeed = 0.2f;
            while (true) {
                synchronized (canvas.getScene().getRenderTexture()) {
                    Camera camera = canvas.getScene().getCurrentCamera();

                    synchronized (camera.getLock()) {
                        camera.getTranslation().add(camera.getRotationMatrix().mul(
                                movementVector.getX() * movementSpeed,
                                movementVector.getY() * movementSpeed,
                                movementVector.getZ() * movementSpeed,
                                1));
                        camera.updateCameraMatrix();
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();

        //File menu
        JMenu fileMenu = new JMenu("File");
        addItem(fileMenu, "Add a cube", a -> addCube());
        addItem(fileMenu, "Add a sphere", a -> addSphere());
        addItem(fileMenu, "Add a model", a -> addModel());
        addItem(fileMenu, "Add empty instance", a -> addEmpty());
        addItem(fileMenu, "Clear scene", a -> clearScene());
        fileMenu.addSeparator();
        addItem(fileMenu, "Exit", a -> System.exit(0));
        menuBar.add(fileMenu);

        //View menu
        JMenu viewMenu = new JMenu("View");
        addCheckboxItem(viewMenu, "Show scene list", false, a -> {
            sceneList.setVisible(a);
        });
        addCheckboxItem(viewMenu, "Show instance info", false, a -> {
            instanceInfo.setVisible(a);
        });
        menuBar.add(viewMenu);

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
        }, "Texture color", "Triangle color", "One color", "Disabled");
        menuBar.add(texturingMenu);

        JMenu shadingMenu = new JMenu("Shading");
        addRadioItems(shadingMenu, 2, i -> {
            canvas.getScene().setShadingModel(Scene.ShadingType.values()[i]);
        }, "Flat model", "Gouraud model", "Phong model", "Disabled");
        menuBar.add(shadingMenu);

        return menuBar;
    }

    private void addEmpty() {
        sceneList.getModel().addInstance(new Instance("",
                null,
                new Vector3f(0, 0, 7),
                new Vector3f(0, 0, 0),
                new Vector3f(2, 2, 2)));
    }

    private void clearScene() {
        sceneList.getModel().clearInstances();
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

    private void addCube() {
        Texture texture = null;
        try {
            texture = TextureReader.loadTexture("crate1_diffuse.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        sceneList.getModel().addInstance(new Instance("Crate",
                BasicModels.getCube(texture),
                new Vector3f(0, 0, 7),
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1)));
    }

    private void addSphere() {
        Texture texture = null;
        try {
            texture = TextureReader.loadTexture("earth.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        sceneList.getModel().addInstance(new Instance("Earth",
                BasicModels.getSphere(texture, 30),
                new Vector3f(0, 0, 7),
                new Vector3f(0, 0, 0),
                new Vector3f(2, 2, 2)));
    }

    public static String requestFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showDialog(null, null);
        if (result != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
            return null;
        }

        return chooser.getSelectedFile().getAbsolutePath();
    }

    private void addModel() {
        String file = requestFile();
        if (file == null) {
            return;
        }

        sceneList.getModel().addInstance(new Instance("Loaded model",
                ObjReader.readModel(file),
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
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());

        canvas = new Canvas();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(canvas, gbc);

        instanceInfo = new InstanceInfo();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(instanceInfo, gbc);
        instanceInfo.setVisible(false);

        sceneList = new SceneList(instanceInfo, canvas.getScene());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(sceneList, gbc);
        sceneList.setVisible(false);
    }
}
