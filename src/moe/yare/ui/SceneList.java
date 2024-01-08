package moe.yare.ui;

import moe.yare.render.Scene;

import javax.swing.*;
import java.awt.*;

public class SceneList extends JPanel {

    private final SceneListModel model;

    public SceneList(InstanceInfo info, Scene scene) {
        setLayout(new GridBagLayout());

        model = new SceneListModel(scene);

        info.setSceneListModel(model);
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e ->
                info.setInstance(table.getSelectedRow() == -1 ? null : scene.getInstances().get(table.getSelectedRow())));
        JScrollPane scrollPane = new JScrollPane(table);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        setPreferredSize(new Dimension(400, Integer.MAX_VALUE));
    }

    public SceneListModel getModel() {
        return model;
    }
}
