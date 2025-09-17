package Client;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * SoftUI: tiện ích giao diện bo góc + màu sắc dịu (không cần thư viện ngoài).
 * - Gồm: palette màu, RoundedBorder/Panel, nút pill, toggle pill, textfield bo góc,
 *   renderer cho bảng (striping) & header, và applyNimbusTheme() để bật Nimbus + tinh chỉnh màu.
 */
public final class SoftUI {
    private SoftUI() {}

    /* ======================= Palette màu ======================= */
    public static final Color COL_PRIMARY      = new Color(0x2563EB); // xanh dương nhấn
    public static final Color COL_PRIMARY_DARK = new Color(0x1E3A8A); // xanh dương đậm
    public static final Color COL_ACCENT       = new Color(0x10B981); // xanh ngọc
    public static final Color COL_BG_APP       = new Color(0xF1F5F9); // nền app xám rất nhạt
    public static final Color COL_BG_CARD      = Color.WHITE;         // nền thẻ/trắng
    public static final Color COL_TEXT_DARK    = new Color(0x0F172A); // chữ đậm
    public static final Color COL_LINE_SOFT    = new Color(0xE5E7EB); // viền nhẹ

    /* ======================= Nimbus theme ======================= */
    /** Gọi 1 lần ở đầu app để bật Nimbus và tinh chỉnh tông màu dịu. */
    public static void applyNimbusTheme() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Tinh chỉnh palette Nimbus cho đồng bộ SoftUI
        UIManager.put("control", COL_BG_APP);
        UIManager.put("text", COL_TEXT_DARK);
        UIManager.put("nimbusBlueGrey", COL_LINE_SOFT);
        UIManager.put("nimbusFocus", COL_PRIMARY);
        UIManager.put("Table.alternateRowColor", new Color(0xF8FAFC));
    }

    /* ======================= Bo góc: Border ======================= */
    public static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int arc;
        public RoundedLineBorder(Color color, int arc) {
            this.color = color; this.arc = arc;
        }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(8, 10, 8, 10); }
        @Override public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(8, 10, 8, 10); return insets;
        }
    }

    /* ======================= Panel bo góc ======================= */
    public static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color bg;
        public RoundedPanel(int arc, Color bg) {
            super(new BorderLayout(10, 10));
            this.arc = arc; this.bg = bg;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            // viền trắng trong suốt rất nhẹ để nổi khối
            g2.setColor(new Color(255, 255, 255, 150));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /* ======================= Nút pill (bo góc lớn) ======================= */
    public static class PillButton extends JButton {
        private final Color bg;
        private final Color border;
        public PillButton(String text, Color bg, Color border, Color fg) {
            super(text);
            this.bg = bg; this.border = border;
            setForeground(fg);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(getFont().deriveFont(Font.BOLD));
            setBorder(new RoundedLineBorder(border, 20));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = bg;
            if (getModel().isArmed() || getModel().isPressed()) fill = bg.darker();
            else if (getModel().isRollover()) fill = bg.brighter();
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public Insets getInsets() { return new Insets(8, 16, 8, 16); }
    }

    /* ======================= Toggle pill ======================= */
    public static class PillToggleButton extends JToggleButton {
        private final Color on, onBorder, offBorder;
        public PillToggleButton(String text, Color on, Color onBorder, Color offBorder) {
            super(text);
            this.on = on; this.onBorder = onBorder; this.offBorder = offBorder;
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(getFont().deriveFont(Font.BOLD));
            setBorder(new RoundedLineBorder(offBorder, 20));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isSelected()) {
                g2.setColor(on);
                setForeground(Color.WHITE);
            } else {
                g2.setColor(new Color(0xECFEFF)); // xanh nhạt
                setForeground(COL_TEXT_DARK);
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public Insets getInsets() { return new Insets(8, 16, 8, 16); }
        @Override public void updateUI() { // giữ border khi Look&Feel đổi
            super.updateUI();
            setBorder(new RoundedLineBorder(offBorder, 20));
        }
    }

    /* ======================= TextField bo góc ======================= */
    public static class PillTextField extends JTextField {
        public PillTextField(String text) {
            super(text);
            setOpaque(false);
            setBorder(new RoundedLineBorder(COL_LINE_SOFT, 16));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public Insets getInsets() { return new Insets(6, 10, 6, 10); }
    }

    /* ======================= Table helpers ======================= */
    /** Kẻ sọc hàng xen kẽ (striping) + tắt grid, spacing đẹp mắt. */
    public static void applyStriping(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color stripe = new Color(0xF8FAFC);
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : stripe);
                if (col == 0) c.setFont(c.getFont().deriveFont(Font.BOLD)); // cột Time đậm nhẹ
                return c;
            }
        });
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
    }

    /** Header bảng nổi bật, viền dưới nhẹ, chiều cao lớn hơn. */
    public static void applyHeaderStyle(JTable table) {
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            {
                setOpaque(true);
                setHorizontalAlignment(CENTER);
                setBackground(new Color(0xEEF2FF));
                setForeground(new Color(0x1E3A8A));
                setFont(getFont().deriveFont(Font.BOLD, 13f));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xC7D2FE)));
            }
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                return this;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
    }
}
