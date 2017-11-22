package com.youmu.win.m2repo;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.youmu.win.m2repo.constant.Constants;
import com.youmu.win.m2repo.model.IndexItemModel;
import com.youmu.win.m2repo.model.IndexPageModel;
import com.youmu.win.m2repo.model.IndexTableModel;
import com.youmu.win.m2repo.model.VersionTableModel;

import sun.swing.DefaultLookup;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/10
 */
public class MainFrame extends JFrame {

    public static final String STATE_DEFAULT = "ok";
    public static final String STATE_DEALING = "dealing....";

    JTable indexTable;
    JTable versionTable;
    JTextField searchField;
    JButton searchButton;
    JButton preButton;
    JButton nextButton;
    JTextField currentPageField;
    JLabel jLabel;

    int currentPage = 1;
    int totalPage = 0;

    public MainFrame() throws IOException {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1200, 600));
        this.setLayout(new BorderLayout());
        indexTable = new JTable();
        versionTable = new JTable();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        JPanel jPanel = new JPanel(new FlowLayout());
        preButton = new JButton("pre");
        nextButton = new JButton("next");
        searchField.setEditable(true);
        currentPageField = new JTextField();
        jLabel = new JLabel(STATE_DEFAULT);
        currentPageField.setEditable(false);
        currentPageField.setText(String.valueOf(currentPage));

        // searchField.setMinimumSize(new Dimension(100,0));
        // jPanel.add(BorderLayout.WEST,searchField);
        // jPanel.add(BorderLayout.EAST,searchButton);
        jPanel.add(searchField, 0);
        searchField.setFont(new Font("Menu.font", Font.PLAIN, 18));
        jPanel.add(searchButton, 1);

        JPanel left = new JPanel();
        left.setLayout(new BorderLayout(5, 5));
        JScrollPane scrollPane = new JScrollPane(indexTable);
        left.add(BorderLayout.NORTH, scrollPane);
        left.setPreferredSize(new Dimension(590, 500));

        JPanel foot = new JPanel();
        foot.add(preButton, 0);
        foot.add(currentPageField, 1);
        foot.add(nextButton, 2);
        left.add(BorderLayout.CENTER, foot);
        JScrollPane scrollPane2 = new JScrollPane(versionTable);
        scrollPane2.setPreferredSize(new Dimension(590, 500));
        // init indexTable
        // indexTable.setModel(new IndexTableModel(get()));
        indexTable.setFont(new Font("Menu.font", Font.PLAIN, 18));
        indexTable.setDefaultRenderer(String.class, new TableCellTextAreaRenderer());
        indexTable.setSelectionForeground(Color.gray);
        indexTable.setRowSelectionAllowed(true);
        // init versionTable
        // versionTable.setModel(new IndexTableModel(get()));
        versionTable.setFont(new Font("Menu.font", Font.PLAIN, 18));
        // versionTable.setDefaultRenderer(String.class, new
        // TableCellTextAreaRenderer());
        versionTable.setDefaultRenderer(JButton.class, new TableButtonEditor());
        versionTable.setDefaultEditor(JButton.class, new TableButtonEditor());
        versionTable.setSelectionForeground(Color.gray);
        versionTable.setRowSelectionAllowed(false);

        add(BorderLayout.WEST, left);
        add(BorderLayout.EAST, scrollPane2);
        add(BorderLayout.NORTH, jPanel);
        add(BorderLayout.SOUTH, jLabel);

        // 设置事件
        searchButton.addActionListener(e -> {
            jLabel.setText(STATE_DEALING);
            searchButton.setEnabled(false);
            try {
                if (StringUtils.isBlank(searchField.getText())) {
                    JOptionPane.showMessageDialog(new JTextField(), "query cannot be null");
                    return;
                }
                // clear selection
                indexTable.clearSelection();
                // clear version
                versionTable.setModel(new VersionTableModel(null));
                // getData

                IndexPageModel indexPageModel = Constants.processServiceAdvice
                        .getIndex(searchField.getText(), 1);
                jLabel.setText(STATE_DEFAULT);
                currentPage = 1;
                currentPageField.setText(String.valueOf(currentPage));
                totalPage = indexPageModel.getPages();
                indexTable.setModel(new IndexTableModel(indexPageModel.getList()));
            } finally {
                searchButton.setEnabled(true);
            }
        });

        preButton.addActionListener(e -> {
            jLabel.setText(STATE_DEALING);
            preButton.setEnabled(false);
            try {
                if ((currentPage - 1) < 1) {
                    // not pre page
                    JOptionPane.showMessageDialog(new JTextField(), "haven't pre page");
                    return;
                }
                // clear selection
                indexTable.clearSelection();
                // clear version
                versionTable.setModel(new VersionTableModel(null));
                // getData
                currentPage--;
                currentPageField.setText(String.valueOf(currentPage));

                IndexPageModel indexPageModel = Constants.processServiceAdvice
                        .getIndex(searchField.getText(), currentPage);
                jLabel.setText(STATE_DEFAULT);
                totalPage = indexPageModel.getPages();
                indexTable.setModel(new IndexTableModel(indexPageModel.getList()));
            } finally {
                preButton.setEnabled(true);
            }
        });
        nextButton.addActionListener(e -> {
            jLabel.setText(STATE_DEALING);
            nextButton.setEnabled(false);
            try {
                if ((currentPage + 1) > totalPage) {
                    // not next page
                    JOptionPane.showMessageDialog(new JTextField(), "haven't next page");
                    return;
                }
                // clear selection
                indexTable.clearSelection();
                // clear version
                versionTable.setModel(new VersionTableModel(null));
                // getData
                currentPage++;
                currentPageField.setText(String.valueOf(currentPage));
                IndexPageModel indexPageModel = Constants.processServiceAdvice
                        .getIndex(searchField.getText(), currentPage);
                jLabel.setText(STATE_DEFAULT);
                totalPage = indexPageModel.getPages();
                indexTable.setModel(new IndexTableModel(indexPageModel.getList()));
            } finally {
                nextButton.setEnabled(true);
            }
        });
        indexTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                jLabel.setText(STATE_DEALING);
                indexTable.setEnabled(false);
                indexTable.getSelectionModel().removeListSelectionListener(this);
                try {
                    if (indexTable.getSelectedRow() < 0) {
                        return;
                    }
                    IndexItemModel indexItemModel = ((IndexTableModel) indexTable.getModel())
                            .getData().get(indexTable.getSelectedRow());
                    versionTable.setModel(new VersionTableModel(
                            Constants.processServiceAdvice.getVersion(indexItemModel.getSubUrl()))
                                    .setSuccessCallback(b -> JOptionPane.showMessageDialog(
                                            new JTextField(), "added to clipboard")));
                    jLabel.setText(STATE_DEFAULT);
                } finally {
                    indexTable.setEnabled(true);
                    indexTable.getSelectionModel().addListSelectionListener(this);
                }
            }
        });
    }

    public static java.util.List<IndexItemModel> get() throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = MainFrame.class.getClassLoader().getResourceAsStream("t.html");
            String content = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
            // System.out.println(content);
            Document document = Jsoup.parse(content);
            java.util.List<IndexItemModel> list = Lists.newArrayList();
            Element mainContent = document.getElementById("maincontent");
            // 解析总条数
            Elements h2s = mainContent.getElementsByTag("h2");
            String total = "0";
            for (Element h2 : h2s) {
                Elements bs = h2.getElementsByTag("b");
                if (StringUtils.isNotBlank(h2.text()) && h2.text().contains("result"))
                    ;
                for (Element b : bs) {
                    total = b.text();
                }
                break;
            }
            Elements ims = mainContent.getElementsByClass("im");
            for (Element im : ims) {
                Elements imHeaders = im.getElementsByClass("im-header");
                for (Element imHeader : imHeaders) {
                    Elements as = imHeader.getElementsByTag("a");
                    IndexItemModel indexItemModel = new IndexItemModel();
                    list.add(indexItemModel);
                    for (Element a : as) {
                        if (a.hasAttr("href") && a.hasClass("im-usage")) {
                            indexItemModel.setUsage(a.child(0).text());
                        } else {
                            indexItemModel.setName(a.text());
                            indexItemModel.setSubUrl(a.attr("href"));
                        }

                    }
                }
            }
            list.forEach(System.out::println);
            System.out.println(total);
            return list;
        } finally {
            if (null != inputStream)
                inputStream.close();
        }
    }

    static class TableCellTextAreaRenderer extends JTextArea implements TableCellRenderer {
        public TableCellTextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (table == null) {
                return this;
            }

            Color fg = null;
            Color bg = null;

            JTable.DropLocation dropLocation = table.getDropLocation();
            if (dropLocation != null && !dropLocation.isInsertRow()
                    && !dropLocation.isInsertColumn() && dropLocation.getRow() == row
                    && dropLocation.getColumn() == column) {

                fg = DefaultLookup.getColor(this, ui, "Table.dropCellForeground");
                bg = DefaultLookup.getColor(this, ui, "Table.dropCellBackground");

                isSelected = true;
            }

            if (isSelected) {
                super.setForeground(fg == null ? table.getSelectionForeground() : fg);
                super.setBackground(bg == null ? table.getSelectionBackground() : bg);
            } else {
                Color background = table.getBackground() != null ? table.getBackground()
                        : table.getBackground();
                if (background == null || background instanceof javax.swing.plaf.UIResource) {
                    Color alternateColor = DefaultLookup.getColor(this, ui,
                            "Table.alternateRowColor");
                    if (alternateColor != null && row % 2 != 0) {
                        background = alternateColor;
                    }
                }
                super.setForeground(table.getForeground() != null ? table.getForeground()
                        : table.getForeground());
                super.setBackground(background);
            }

            setFont(table.getFont());
            // 计算当下行的最佳高度
            int maxPreferredHeight = 0;
            for (int i = 0; i < table.getColumnCount(); i++) {
                setText("" + table.getValueAt(row, i));
                setSize(table.getColumnModel().getColumn(column).getWidth(), 0);
                maxPreferredHeight = Math.max(maxPreferredHeight, getPreferredSize().height);
            }
            if (table.getRowHeight(row) != maxPreferredHeight) // 少了这行则处理器瞎忙
                table.setRowHeight(row, maxPreferredHeight);
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    static class TableButtonEditor extends AbstractCellEditor
            implements TableCellEditor, TableCellRenderer {

        public TableButtonEditor() {
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                int row, int column) {
            return (JButton) value;
        }

        @Override
        public Object getCellEditorValue() {
            return "yyy";
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return (JButton) value;
        }
    }
}
