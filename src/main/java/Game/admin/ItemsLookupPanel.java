package Game.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.sql.SQLException;
import java.util.List;

public class ItemsLookupPanel extends JDialog {

    private final JTextField tfSearch4 = new JTextField();
    private final JTextField tfSearch7 = new JTextField();
    private final ItemTableModel model4 = new ItemTableModel("Vật phẩm nhiên liệu (item4)");
    private final ItemTableModel model7 = new ItemTableModel("Vật phẩm nâng cấp (item7)");
    private final JTable table4 = new JTable(model4);
    private final JTable table7 = new JTable(model7);
    private final ItemsLookupDAO dao = new ItemsLookupDAO();

    public ItemsLookupPanel(Window owner) {
        super(owner, "Tra cứu vật phẩm (item4 / item7)", ModalityType.MODELESS);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8,8));

        add(buildTabs(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        // cấu hình bảng
        setupTable(table4);
        setupTable(table7);

        // nạp dữ liệu ban đầu
        reloadItem4();
        reloadItem7();
    }

    private JComponent buildTabs() {
        JTabbedPane tabs = new JTabbedPane();

        // Tab item4
        JPanel p4 = new JPanel(new BorderLayout(8,8));
        p4.setBorder(new EmptyBorder(8,8,8,8));
        p4.add(buildTopBar(tfSearch4, this::reloadItem4, this::copySelectedFrom4), BorderLayout.NORTH);
        p4.add(new JScrollPane(table4), BorderLayout.CENTER);
        tabs.addTab("Vật phẩm nhiên liệu (item4)", p4);

        // Tab item7
        JPanel p7 = new JPanel(new BorderLayout(8,8));
        p7.setBorder(new EmptyBorder(8,8,8,8));
        p7.add(buildTopBar(tfSearch7, this::reloadItem7, this::copySelectedFrom7), BorderLayout.NORTH);
        p7.add(new JScrollPane(table7), BorderLayout.CENTER);
        tabs.addTab("Vật phẩm nâng cấp (item7)", p7);

        return tabs;
    }

    private JPanel buildTopBar(JTextField tfSearch, Runnable onReload, Runnable onCopy) {
        JPanel top = new JPanel(new BorderLayout(8,8));
        JPanel left = new JPanel(new BorderLayout(6,6));
        left.add(new JLabel("Tìm theo tên:"), BorderLayout.WEST);
        left.add(tfSearch, BorderLayout.CENTER);

        JButton btReload = new JButton("Tải lại");
        JButton btCopy = new JButton("Copy ID,Tên");

        btReload.addActionListener(e -> onReload.run());
        btCopy.addActionListener(e -> onCopy.run());

        tfSearch.addActionListener(e -> onReload.run()); // Enter để tìm

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(btCopy);
        right.add(btReload);

        top.add(left, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel buildBottomBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Đóng");
        close.addActionListener(e -> dispose());
        p.add(close);
        return p;
    }

    private void setupTable(JTable table) {
        table.setRowHeight(24);
        var sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(500); // Tên
    }

    private void reloadItem4() {
        try {
            List<ItemsLookupDAO.ItemRow> rows = dao.listItem4(tfSearch4.getText(), 1000);
            model4.setData(rows);
        } catch (SQLException ex) {
            showError(ex, "Lỗi tải item4");
        }
    }

    private void reloadItem7() {
        try {
            List<ItemsLookupDAO.ItemRow> rows = dao.listItem7(tfSearch7.getText(), 1000);
            model7.setData(rows);
        } catch (SQLException ex) {
            showError(ex, "Lỗi tải item7");
        }
    }

    private void copySelectedFrom4() {
        copySelected(table4, model4);
    }
    private void copySelectedFrom7() {
        copySelected(table7, model7);
    }

    private void copySelected(JTable table, ItemTableModel model) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(this, "Chọn một dòng trước đã"); return; }
        int row = table.convertRowIndexToModel(viewRow);
        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        String text = id + "," + name;
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        JOptionPane.showMessageDialog(this, "Đã copy: " + text);
    }

    private void showError(SQLException ex, String title) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "SQLState=" + ex.getSQLState() + "\nErrorCode=" + ex.getErrorCode() + "\n" + ex.getMessage(),
                title, JOptionPane.ERROR_MESSAGE);
    }

    // ===== Model hiển thị 2 cột: ID, Tên =====
    static class ItemTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Tên"};
        private java.util.List<ItemsLookupDAO.ItemRow> data = java.util.List.of();
        private final String title;

        ItemTableModel(String title) { this.title = title; }

        public void setData(java.util.List<ItemsLookupDAO.ItemRow> data) {
            this.data = data == null ? java.util.List.of() : data;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }
        @Override public Object getValueAt(int r, int c) {
            var row = data.get(r);
            return (c == 0) ? row.id : row.name;
        }
        @Override public Class<?> getColumnClass(int c) { return (c == 0) ? Integer.class : String.class; }
        @Override public boolean isCellEditable(int r, int c) { return false; }
    }
}
