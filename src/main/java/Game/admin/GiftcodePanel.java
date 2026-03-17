package Game.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;

public class GiftcodePanel extends JDialog {

    private final JTextField tfGiftname = new JTextField();
    private final JButton btRandom = new JButton("Tạo ngẫu nhiên");

    private final JSpinner spVang = new JSpinner(new SpinnerNumberModel(0L, 0L, Long.MAX_VALUE, 1000L));
    private final JSpinner spNgoc = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 10));
    private final JCheckBox cbEmptyBox = new JCheckBox("Empty box?");
    private final JSpinner spLimit = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    private final JTextField tfGiftFor = new JTextField();
    private final JSpinner spLevel = new JSpinner(new SpinnerNumberModel(0, 0, 200, 1));
    private final JCheckBox cbNeedActive = new JCheckBox("Cần active?");

    private final ItemsTableModel itemsModel = new ItemsTableModel();
    private final JTable itemsTable = new JTable(itemsModel);

    private final JButton btAddItem = new JButton("Thêm Item");
    private final JButton btRemoveItem = new JButton("Xoá Item");
    private final JButton btSave = new JButton("Lưu giftcode vào DB");

    private final GiftcodeDAO dao = new GiftcodeDAO();

    public GiftcodePanel(Window owner) {
        super(owner, "Tạo Giftcode", ModalityType.MODELESS);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(12, 12));

        JPanel form = buildFormPanel();
        JPanel list = buildListPanel();

        add(form, BorderLayout.NORTH);
        add(list, BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        btRandom.addActionListener(e -> tfGiftname.setText(genCode()));
        btAddItem.addActionListener(e -> onAddItem());
        btRemoveItem.addActionListener(e -> onRemoveItem());
        btSave.addActionListener(e -> onSave());

        tfGiftname.setText(genCode());
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(12, 12, 0, 12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int r = 0;
        c.gridx = 0;
        c.gridy = r;
        p.add(new JLabel("Giftcode:"), c);
        c.gridx = 1;
        c.gridy = r;
        p.add(tfGiftname, c);
        c.gridx = 2;
        c.gridy = r;
        btRandom.setPreferredSize(new Dimension(150, 28));
        p.add(btRandom, c);
        r++;

        c.gridx = 0;
        c.gridy = r;
        p.add(new JLabel("Vàng (vang):"), c);
        c.gridx = 1;
        c.gridy = r;
        p.add(spVang, c);
        c.gridx = 2;
        c.gridy = r;
        p.add(cbEmptyBox, c);
        r++;

        c.gridx = 0;
        c.gridy = r;
        p.add(new JLabel("Ngọc (ngoc):"), c);
        c.gridx = 1;
        c.gridy = r;
        p.add(spNgoc, c);
        r++;

        c.gridx = 0;
        c.gridy = r;
        p.add(new JLabel("Giới hạn (limit):"), c);
        c.gridx = 1;
        c.gridy = r;
        p.add(spLimit, c);
        r++;

        c.gridx = 0;
        c.gridy = r;
        p.add(new JLabel("Gift cho (gift_for):"), c);
        c.gridx = 1;
        c.gridy = r;
        p.add(tfGiftFor, c);
        r++;

        c.gridx = 0;
        c.gridy = r;
        p.add(new JLabel("Yêu cầu level:"), c);
        c.gridx = 1;
        c.gridy = r;
        p.add(spLevel, c);
        c.gridx = 2;
        c.gridy = r;
        p.add(cbNeedActive, c);
        r++;

        return p;
    }

    private JPanel buildListPanel() {
        JPanel wrap = new JPanel(new BorderLayout(8, 8));
        wrap.setBorder(new EmptyBorder(0, 12, 0, 12));
        itemsTable.setRowHeight(26);
        itemsTable.setFillsViewportHeight(true);
        itemsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        wrap.add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(btAddItem);
        bar.add(btRemoveItem);
        wrap.add(bar, BorderLayout.NORTH);

        // Panel phải: xem nhanh 10 giftcode mới
        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(new EmptyBorder(0, 0, 0, 12));
        JTextArea taList = new JTextArea(14, 26);
        taList.setEditable(false);
        JButton btReload = new JButton("Tải danh sách (mới nhất)");
        btReload.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                var list = dao.list(0, 10);
                for (GiftcodeDAO.Giftcode g : list) {
                    sb.append("#").append(g.id).append("  ").append(g.giftname)
                            .append(" | vang=").append(g.vang)
                            .append(", ngoc=").append(g.ngoc)
                            .append(", limit=").append(g.limit)
                            .append(", level>=").append(g.level)
                            .append(", active?").append(g.needActive)
                            .append("\n");
                }
                taList.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi tải danh sách", JOptionPane.ERROR_MESSAGE);
            }
        });
        right.add(btReload, BorderLayout.NORTH);
        right.add(new JScrollPane(taList), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, wrap, right);
        split.setResizeWeight(0.72);
        JPanel out = new JPanel(new BorderLayout());
        out.add(split, BorderLayout.CENTER);
        return out;
    }

    private JPanel buildBottomBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBorder(new EmptyBorder(8, 12, 12, 12));
        p.add(btSave);
        JButton btClose = new JButton("Đóng");
        btClose.addActionListener(e -> dispose());
        p.add(btClose);
        return p;
    }

    private String genCode() {
        String alphabet = "abcdefghjklmnpqrstuvwxyz23456789"; // bỏ i, o, 0, 1
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder("hso");
        for (int i = 0; i < 8; i++) {
            sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    private void onAddItem() {
        ItemEditor ed = new ItemEditor(this);
        ed.setVisible(true);
        if (ed.ok) {
            itemsModel.addRow(ed.type, ed.id, ed.quantity);
        }
    }

    private void onRemoveItem() {
        int r = itemsTable.getSelectedRow();
        if (r >= 0) {
            itemsModel.removeRow(r);
        }
    }

    private void onSave() {
        String name = tfGiftname.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giftname trống");
            return;
        }
        try {
            if (dao.giftnameExists(name)) {
                JOptionPane.showMessageDialog(this, "Giftname đã tồn tại, hãy random lại");
                return;
            }

            GiftcodeDAO.Giftcode g = new GiftcodeDAO.Giftcode();
            g.giftname = name;
            g.vang = ((Number) spVang.getValue()).longValue();
            if (g.vang != null && g.vang == 0) {
                g.vang = null;
            }
            g.ngoc = ((Number) spNgoc.getValue()).intValue();
            if (g.ngoc != null && g.ngoc == 0) {
                g.ngoc = null;
            }
            g.emptyBox = cbEmptyBox.isSelected();
            g.limit = ((Number) spLimit.getValue()).intValue();
            if (g.limit != null && g.limit == 0) {
                g.limit = null;
            }
            g.giftFor = tfGiftFor.getText().trim();
            g.level = ((Number) spLevel.getValue()).intValue();
            g.needActive = cbNeedActive.isSelected();

            // map từ bảng sang 2 list items (4,7)
            for (ItemsTableModel.Row r : itemsModel.rows) {
                GiftcodeDAO.GiftItem it = new GiftcodeDAO.GiftItem(r.id, r.quantity);
                if (r.type == 4) {
                    g.items4.add(it);
                } else if (r.type == 7) {
                    g.items7.add(it);
                }
            }

            int newId = dao.create(g);
            JOptionPane.showMessageDialog(this, "Đã tạo giftcode #" + newId + "\n" + g.giftname);
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "SQLState=" + ex.getSQLState() + "\nErrorCode=" + ex.getErrorCode() + "\n" + ex.getMessage(),
                    "Lỗi lưu giftcode", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Bảng items: chỉ Type(4/7) / Item ID / Số lượng =====
    static class ItemsTableModel extends AbstractTableModel {

        static class Row {

            int type, id, quantity;
        }
        final java.util.List<Row> rows = new ArrayList<>();
        private final String[] columns = {"Type (4/7)", "Item ID", "Số lượng"};

        public void addRow(int type, int id, int quantity) {
            Row r = new Row();
            r.type = type;
            r.id = id;
            r.quantity = quantity;
            rows.add(r);
            fireTableDataChanged();
        }

        public void removeRow(int index) {
            rows.remove(index);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int c) {
            return columns[c];
        }

        @Override
        public Object getValueAt(int r, int c) {
            Row row = rows.get(r);
            return switch (c) {
                case 0 ->
                    row.type;
                case 1 ->
                    row.id;
                case 2 ->
                    row.quantity;
                default ->
                    "";
            };
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    }

    // ===== Hộp nhập item (chỉ id + số lượng; CHỈ CHO PHÉP type 4 hoặc 7) =====
    static class ItemEditor extends JDialog {

        boolean ok = false;
        int type = 4;
        int id = 0;
        int quantity = 1;

        private final JComboBox<Integer> cbType = new JComboBox<>(new Integer[]{4, 7});
        private final JSpinner spId = new JSpinner(new SpinnerNumberModel(0, 0, 32767, 1));
        private final JSpinner spQ = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));

        ItemEditor(Window owner) {
            super(owner, "Thêm Item", ModalityType.APPLICATION_MODAL);
            setSize(400, 220);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(8, 8));

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(new EmptyBorder(12, 12, 0, 12));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 4, 4, 4);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            int r = 0;
            c.gridx = 0;
            c.gridy = r;
            form.add(new JLabel("Type (4/7):"), c);
            c.gridx = 1;
            c.gridy = r;
            form.add(cbType, c);
            r++;
            c.gridx = 0;
            c.gridy = r;
            form.add(new JLabel("Item ID:"), c);
            c.gridx = 1;
            c.gridy = r;
            form.add(spId, c);
            r++;
            c.gridx = 0;
            c.gridy = r;
            form.add(new JLabel("Số lượng:"), c);
            c.gridx = 1;
            c.gridy = r;
            form.add(spQ, c);
            r++;

            JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton okB = new JButton("Thêm");
            JButton cancelB = new JButton("Huỷ");
            bar.add(okB);
            bar.add(cancelB);

            okB.addActionListener(e -> {
                try {
                    int t = (Integer) cbType.getSelectedItem();
                    int inputId = ((Number) spId.getValue()).intValue();
                    int q = ((Number) spQ.getValue()).intValue();
                    if (inputId <= 0) {
                        throw new IllegalArgumentException("Item ID > 0");
                    }
                    if (q <= 0) {
                        throw new IllegalArgumentException("Số lượng > 0");
                    }
                    type = t;
                    id = inputId;
                    quantity = q;
                    ok = true;
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.ERROR_MESSAGE);
                }
            });
            cancelB.addActionListener(e -> dispose());

            add(form, BorderLayout.CENTER);
            add(bar, BorderLayout.SOUTH);
        }
    }
}
