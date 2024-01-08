package moe.yare.ui;

import moe.yare.io.ObjReader;
import moe.yare.io.TextureReader;
import moe.yare.render.Instance;
import moe.yare.render.Model;
import moe.yare.render.Texture;
import moe.yare.render.Triangle;

import javax.swing.*;
import java.awt.*;

import static moe.yare.ui.ViewerFrame.requestFile;

public class InstanceInfo extends JPanel {

    private JTextField pZ;
    private JTextField pY;
    private JTextField pX;
    private JTextField rX;
    private JTextField rY;
    private JTextField rZ;
    private JTextField sX;
    private JTextField sY;
    private JTextField sZ;
    private JComboBox<String> textureBox;
    private JComboBox<String> modelBox;
    private JTextField nameField;
    private JButton removeButton;

    private Instance instance;
    private SceneListModel sceneListModel;

    public InstanceInfo() {
        setupUI();
        setPreferredSize(new Dimension(400, Integer.MAX_VALUE));
        setInstance(null);

        nameField.addActionListener(e -> {
            try {
                synchronized (instance.getLock()) {
                    instance.setName(nameField.getText());
                }
                if (sceneListModel != null) {
                    sceneListModel.updateInstance(instance);
                }
            } catch (Exception ignored) { }
        });

        pX.addActionListener(e -> {
            try {
                float value = Float.parseFloat(pX.getText());
                synchronized (instance.getLock()) {
                    instance.getTranslation().setX(value);
                    instance.updateTransformMatrix();
                }
            } catch (Exception ignored) { }
            setInstance(instance);
        });

        pY.addActionListener(e -> {
            try {
                float value = Float.parseFloat(pY.getText());
                synchronized (instance.getLock()) {
                    instance.getTranslation().setY(value);
                    instance.updateTransformMatrix();
                }
            } catch (Exception ignored) { }
            setInstance(instance);
        });

        pZ.addActionListener(e -> {
            try {
                float value = Float.parseFloat(pZ.getText());
                synchronized (instance.getLock()) {
                    instance.getTranslation().setZ(value);
                    instance.updateTransformMatrix();
                }
            } catch (Exception ignored) { }
            setInstance(instance);
        });

        rX.addActionListener(e -> {
            try {
                float value = Float.parseFloat(rX.getText());
                synchronized (instance.getLock()) {
                    instance.getRotation().setX(value);
                    instance.updateTransformMatrix();
                }
            } catch (Exception ignored) { }
            setInstance(instance);
        });

        rY.addActionListener(e -> {
            try {
                float value = Float.parseFloat(rY.getText());
                synchronized (instance.getLock()) {
                    instance.getRotation().setY(value);
                    instance.updateTransformMatrix();
                }
            } catch (Exception ignored) { }
            setInstance(instance);
        });

        rZ.addActionListener(e -> {
            try {
                float value = Float.parseFloat(rZ.getText());
                synchronized (instance.getLock()) {
                    instance.getRotation().setZ(value);
                    instance.updateTransformMatrix();
                }
            } catch (Exception ignored) { }
            setInstance(instance);
        });

        sX.addActionListener(e -> {
            try {
                float value = Float.parseFloat(sX.getText());
                synchronized (instance.getLock()) {
                    instance.getScaling().setX(value);
                    instance.updateTransformMatrix();
                }
            } catch (Exception ignored) { }
            setInstance(instance);
        });

        sY.addActionListener(e -> {
            try {
                float value = Float.parseFloat(sY.getText());
                synchronized (instance.getLock()) {
                    instance.getScaling().setY(value);
                    instance.updateTransformMatrix();
                }
            } catch (Exception ignored) { }
            setInstance(instance);
        });

        sZ.addActionListener(e -> {
            try {
                float value = Float.parseFloat(sZ.getText());
                synchronized (instance.getLock()) {
                    instance.getScaling().setZ(value);
                    instance.updateTransformMatrix();
                }
            } catch (Exception ignored) { }
            setInstance(instance);
        });

        textureBox.addItem("Load new...");
        textureBox.addActionListener(e -> {
            String file = requestFile();
            if (file == null) {
                return;
            }

            Texture texture = TextureReader.loadTexture(file);
            synchronized (instance.getLock()) {
                for (Triangle tri : instance.getModel().getTriangles()) {
                    tri.setTexture(texture);
                }
            }
        });

        modelBox.addItem("Load new...");
        modelBox.addActionListener(e -> {
            String file = requestFile();
            if (file == null) {
                return;
            }

            Model model = ObjReader.readModel(file);
            synchronized (instance.getLock()) {
                instance.setModel(model);
            }
        });

        removeButton.addActionListener(e -> {
            if (sceneListModel != null) {
                sceneListModel.removeInstance(instance);
            }
            setInstance(null);
        });
    }

    private void setupUI() {
        final JPanel panel1 = this;
        panel1.setLayout(new GridBagLayout());
        final JLabel label1 = new JLabel();
        label1.setText("Z:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        pZ = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(pZ, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Y:");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("X:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label3, gbc);
        pY = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(pY, gbc);
        pX = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(pX, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Position:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label4, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer1, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Rotation:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Z:");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label6, gbc);
        rZ = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(rZ, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Y:");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label7, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("X:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label8, gbc);
        rY = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(rY, gbc);
        rX = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(rX, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Scaling:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label9, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("Z:");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label10, gbc);
        sZ = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(sZ, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Y:");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label11, gbc);
        final JLabel label12 = new JLabel();
        label12.setText("X:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label12, gbc);
        sY = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(sY, gbc);
        sX = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(sX, gbc);
        modelBox = new JComboBox<String>();
        modelBox.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(modelBox, gbc);
        final JLabel label13 = new JLabel();
        label13.setText("Model:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label13, gbc);
        final JLabel label14 = new JLabel();
        label14.setText("Texture:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label14, gbc);
        textureBox = new JComboBox<String>();
        textureBox.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(textureBox, gbc);
        removeButton = new JButton("Remove instance");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(removeButton, gbc);
        final JLabel label15 = new JLabel();
        label15.setText("Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label15, gbc);
        nameField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(nameField, gbc);
    }

    public void setInstance(Instance instance) {
        this.instance = instance;

        pX.setEnabled(instance != null);
        pY.setEnabled(instance != null);
        pZ.setEnabled(instance != null);
        rX.setEnabled(instance != null);
        rY.setEnabled(instance != null);
        rZ.setEnabled(instance != null);
        sX.setEnabled(instance != null);
        sY.setEnabled(instance != null);
        sZ.setEnabled(instance != null);
        textureBox.setEnabled(instance != null);
        modelBox.setEnabled(instance != null);
        nameField.setEnabled(instance != null);
        removeButton.setEnabled(instance != null);

        if (instance == null) {
            return;
        }

        pX.setText(String.valueOf(instance.getTranslation().getX()));
        pY.setText(String.valueOf(instance.getTranslation().getY()));
        pZ.setText(String.valueOf(instance.getTranslation().getZ()));
        rX.setText(String.valueOf(instance.getRotation().getX()));
        rY.setText(String.valueOf(instance.getRotation().getY()));
        rZ.setText(String.valueOf(instance.getRotation().getZ()));
        sX.setText(String.valueOf(instance.getScaling().getX()));
        sY.setText(String.valueOf(instance.getScaling().getY()));
        sZ.setText(String.valueOf(instance.getScaling().getZ()));
        //TODO textureBox model
        //TODO modelBox model
        nameField.setText(instance.getName() == null ? "" : instance.getName());
    }

    public void setSceneListModel(SceneListModel sceneListModel) {
        this.sceneListModel = sceneListModel;
    }
}
