package moe.yare.ui;

import moe.yare.render.Instance;
import moe.yare.render.Scene;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class SceneListModel extends AbstractTableModel {

    private final Scene scene;

    public SceneListModel(Scene scene) {
        this.scene = scene;
    }

    @Override
    public int getRowCount() {
        return scene.getInstances().size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Instance instance = scene.getInstances().get(rowIndex);

        if (columnIndex == 1) {
            return instance.getClass().getSimpleName();
        }

        return instance.getName();
    }

    @Override
    public String getColumnName(int column) {
        if (column == 1) {
            return "Class";
        }

        return "Name";
    }

    public void updateInstance(Instance instance) {
        if (instance == null) {
            return;
        }

        List<Instance> instances = scene.getInstances();
        for (int i = 0; i < instances.size(); ++i) {
            if (instance.equals(instances.get(i))) {
                fireTableRowsUpdated(i, i);
                return;
            }
        }
    }

    public void clearInstances() {
        if (scene.getInstances().isEmpty()) {
            return;
        }

        fireTableRowsDeleted(0, scene.getInstances().size());
        scene.clearInstances();
    }

    public void addInstance(Instance instance) {
        if (instance == null) {
            return;
        }

        scene.addInstance(instance);
        fireTableRowsInserted(scene.getInstances().size(), scene.getInstances().size());
    }
}
