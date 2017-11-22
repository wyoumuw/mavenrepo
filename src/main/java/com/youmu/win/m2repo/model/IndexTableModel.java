package com.youmu.win.m2repo.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/10
 */
public class IndexTableModel extends AbstractTableModel {

    List<IndexItemModel> data;
    private static final String[] headers = { "Name", "Usages", "Url" };

    public IndexTableModel(List<IndexItemModel> data) {
        this.data = data;
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
    public String getColumnName(int column) {
        return headers[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String content;
        IndexItemModel model = data.get(rowIndex);
        switch (columnIndex) {
        // TODO 修改IndexItemModel要修改
        case 0:
            content = model.getName();
            break;
        case 1:
            content = model.getUsage();
            break;
        case 2:
            content = model.getSubUrl().replace("/artifact/", "");
            break;
        default:
            content = "Unknown";
        }
        return content;
    }

    public List<IndexItemModel> getData() {
        return data;
    }
}
