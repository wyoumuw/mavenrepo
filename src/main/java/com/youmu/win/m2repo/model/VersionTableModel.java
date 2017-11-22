package com.youmu.win.m2repo.model;

import com.youmu.win.m2repo.constant.Constants;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/10
 */
public class VersionTableModel extends AbstractTableModel {
    List<VersionItemModel> data;
    private static final String[] headers = { "Version", "Usages", "Date", "Url", "Operation" };

    private Consumer<Boolean> consumer;

    public VersionTableModel(List<VersionItemModel> data) {
        this.data = data;
    }

    public VersionTableModel setSuccessCallback(Consumer<Boolean> consumer) {
        this.consumer = consumer;
        return this;
    }

    @Override
    public String getColumnName(int column) {
        return headers[column];
    }

    @Override
    public int getRowCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public int getColumnCount() {
        return headers.length;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 4 ? true : false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VersionItemModel model = data.get(rowIndex);
        switch (columnIndex) {
        // TODO 修改VersionItemModel要修改
        case 0:
            return model.getName();
        case 1:
            return model.getUsage();
        case 2:
            return model.getDate();
        case 3:
            return model.getSubUrl().replace("/artifact/", "");
        case 4:
            JButton copyButton = new JButton("copy");
            copyButton.addActionListener(new CopyListener(rowIndex));
            return copyButton;
        }
        return "Unknown";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return 4 == columnIndex ? JButton.class : String.class;
    }

    class CopyListener implements ActionListener {

        int row;

        public CopyListener(int row) {
            this.row = row;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton jButton = (JButton) e.getSource();
            jButton.setEnabled(false);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            VersionItemModel model = Constants.processServiceAdvice.fillDependency(data.get(row));
            StringSelection dependency = new StringSelection(model.getDependency());
            clipboard.setContents(dependency, null);
            if (null != consumer) {
                consumer.accept(Boolean.TRUE);
            }
            jButton.setEnabled(true);
        }
    }

    public List<VersionItemModel> getData() {
        return data;
    }
}
