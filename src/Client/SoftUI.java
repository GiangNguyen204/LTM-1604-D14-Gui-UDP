package Client;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/** SoftUI: Theme + Animations + Compat lớp cũ (BroadcastUI). */
public class SoftUI {
    private SoftUI(){}

    // ===== Palette =====
    public static final Color COL_PRIMARY       = new Color(0x4F46E5);
    public static final Color COL_PRIMARY_DARK  = new Color(0x4338CA);
    public static final Color COL_SECONDARY     = new Color(0x22C55E);
    public static final Color COL_ACCENT        = new Color(0xF59E0B);
    public static final Color COL_BG_APP        = new Color(0xF6F7FB);
    public static final Color COL_BG_CARD       = Color.WHITE;
    public static final Color COL_TEXT_DARK     = new Color(0x111827);
    public static final Color COL_TEXT_SOFT     = new Color(0x4B5563);
    public static final Color COL_LINE_SOFT     = new Color(0xE5E7EB);

    // ===== Base Look & Feel =====
    public static void applyNimbusTheme() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignore) {}
        UIManager.put("control", COL_BG_APP);
        UIManager.put("background", COL_BG_APP);
        UIManager.put("nimbusBase", COL_PRIMARY_DARK);
        UIManager.put("nimbusBlueGrey", new Color(0xE6E8F0));
        UIManager.put("nimbusSelectionBackground", COL_PRIMARY);
        UIManager.put("nimbusFocus", COL_ACCENT);
    }

    // ===== Gradient panel =====
    public static class GradientPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Color c1, c2;
        public GradientPanel() { this(new Color(0xEEF2FF), new Color(0xECFDF5)); }
        public GradientPanel(Color c1, Color c2) { setOpaque(false); this.c1 = c1; this.c2 = c2; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== Border bo tròn =====
    public static class RoundedLineBorder extends AbstractBorder {
        private static final long serialVersionUID = 1L;
        private final Color color; private final int radius;
        public RoundedLineBorder(Color color, int radius) { this.color=color; this.radius=radius; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x+1, y+1, w-3, h-3, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(8,8,8,8); }
        @Override public Insets getBorderInsets(Component c, Insets in) { in.set(8,8,8,8); return in; }
    }

    // ===== Card helper =====
    public static void styleCard(JComponent comp) {
        comp.setOpaque(true);
        comp.setBackground(COL_BG_CARD);
        comp.setBorder(new RoundedLineBorder(COL_LINE_SOFT, 16));
    }

    // ===== Hover fade animation =====
    public static void addHoverFade(JComponent c, Color from, Color to, int durationMs) {
        final int frames = Math.max(1, durationMs / 15);
        final float[] s = from.getRGBComponents(null), e = to.getRGBComponents(null);
        final float[] d = { e[0]-s[0], e[1]-s[1], e[2]-s[2], e[3]-s[3] };
        final int[] step = {0};
        Timer anim = new Timer(15, e1 -> {
            float p = (++step[0]) / (float) frames;
            if (p >= 1f) { c.setBackground(to); ((Timer)e1.getSource()).stop(); return; }
            c.setBackground(new Color(s[0]+d[0]*p, s[1]+d[1]*p, s[2]+d[2]*p, s[3]+d[3]*p));
        });
        c.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { step[0]=0; anim.start(); }
            @Override public void mouseExited(MouseEvent e)  { step[0]=0; anim.stop(); c.setBackground(from); }
        });
    }

    // ===== Ripple Button =====
    public static class RippleButton extends JButton {
        private static final long serialVersionUID = 1L;
        private Point rippleCenter = new Point(0,0);
        private float rippleRadius = 0f, rippleMax = 0f, rippleAlpha = 0.35f;
        private Timer rippleTimer;
        public RippleButton(String text) {
            super(text);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setBackground(COL_PRIMARY);
            setBorder(new RoundedLineBorder(COL_PRIMARY, 20));
            SoftUI.addHoverFade(this, COL_PRIMARY, COL_PRIMARY_DARK, 150);
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    rippleCenter = e.getPoint();
                    rippleRadius = 0f; rippleMax = (float)Math.hypot(getWidth(), getHeight()); rippleAlpha = 0.35f;
                    if (rippleTimer != null && rippleTimer.isRunning()) rippleTimer.stop();
                    rippleTimer = new Timer(15, ev -> {
                        rippleRadius += rippleMax/18f; rippleAlpha *= 0.92f; repaint();
                        if (rippleRadius >= rippleMax) ((Timer)ev.getSource()).stop();
                    });
                    rippleTimer.start();
                }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (rippleAlpha > 0.02f && rippleRadius > 0f) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, rippleAlpha)));
                g2.setColor(Color.WHITE);
                g2.fillOval((int)(rippleCenter.x - rippleRadius), (int)(rippleCenter.y - rippleRadius),
                        (int)(2*rippleRadius), (int)(2*rippleRadius));
                g2.dispose();
            }
        }
    }
    public static JButton makeRippleButton(String text){ return new RippleButton(text); }

    public static class RippleToggleButton extends JToggleButton {
        private static final long serialVersionUID = 1L;
        private Point rippleCenter = new Point(0,0);
        private float rippleRadius=0f, rippleMax=0f, rippleAlpha=0.35f;
        private Timer rippleTimer;
        public RippleToggleButton(String text){
            super(text);
            setFocusPainted(false); setForeground(Color.WHITE);
            setBackground(COL_SECONDARY);
            setBorder(new RoundedLineBorder(COL_SECONDARY,20));
            SoftUI.addHoverFade(this, COL_SECONDARY, COL_PRIMARY, 150);
            addChangeListener(e -> setText(isSelected()? "Auto: ON":"Auto Send"));
            addMouseListener(new MouseAdapter(){
                @Override public void mousePressed(MouseEvent e){
                    rippleCenter=e.getPoint(); rippleRadius=0f;
                    rippleMax=(float)Math.hypot(getWidth(),getHeight()); rippleAlpha=0.35f;
                    if (rippleTimer!=null && rippleTimer.isRunning()) rippleTimer.stop();
                    rippleTimer=new Timer(15, ev -> {
                        rippleRadius += rippleMax/18f; rippleAlpha *= 0.92f; repaint();
                        if (rippleRadius >= rippleMax) ((Timer)ev.getSource()).stop();
                    });
                    rippleTimer.start();
                }
            });
        }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if (rippleAlpha > 0.02f && rippleRadius > 0f) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, rippleAlpha)));
                g2.setColor(Color.WHITE);
                g2.fillOval((int)(rippleCenter.x - rippleRadius), (int)(rippleCenter.y - rippleRadius),
                        (int)(2*rippleRadius), (int)(2*rippleRadius));
                g2.dispose();
            }
        }
    }
    public static JToggleButton makeRippleToggle(String text){ return new RippleToggleButton(text); }

    // ===== Toast =====
    public static void showToast(Window owner, String text) {
        final JWindow toast = new JWindow(owner);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0,0,0,200));
        panel.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        panel.add(lbl, BorderLayout.CENTER);
        toast.setContentPane(panel);
        toast.pack();
        Point base = owner.getLocationOnScreen();
        int x = base.x + owner.getWidth() - toast.getWidth() - 20;
        int y = base.y + owner.getHeight() - toast.getHeight() - 40;
        toast.setLocation(x, y);
        toast.setVisible(true);
        new Timer(1400, e -> toast.dispose()).start();
    }

    // ===== Table striping + animated row highlight =====
    private static class AnimatedCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final Map<Integer, Float> highlight; // row -> progress (0..1)
        AnimatedCellRenderer(Map<Integer, Float> highlight){ this.highlight = highlight; }
        @Override public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(tbl, val, isSel, hasFocus, row, col);
            if (isSel) return c;
            Color base = (row % 2 == 0) ? Color.WHITE : new Color(0xFAFAFF);
            Float p = highlight.get(row);
            if (p != null && p > 0f) {
                Color hi = new Color(0xFFF7E6);
                int r = (int)(base.getRed()   + (hi.getRed()   - base.getRed())   * p);
                int g = (int)(base.getGreen() + (hi.getGreen() - base.getGreen()) * p);
                int b = (int)(base.getBlue()  + (hi.getBlue()  - base.getBlue())  * p);
                c.setBackground(new Color(r,g,b));
            } else {
                c.setBackground(base);
            }
            return c;
        }
    }

    @SuppressWarnings("unchecked")
    public static void applyStriping(JTable table) {
        Map<Integer, Float> highlight = (Map<Integer, Float>) table.getClientProperty("rowHighlight");
        if (highlight == null) {
            highlight = new HashMap<>();
            table.putClientProperty("rowHighlight", highlight);
        }
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setDefaultRenderer(Object.class, new AnimatedCellRenderer(highlight));
    }

    public static void applyHeaderStyle(JTable table) {
        JTableHeader h = table.getTableHeader();
        h.setOpaque(false);
        h.setBackground(new Color(0xEEF2FF));
        h.setForeground(COL_TEXT_DARK);
        h.setFont(h.getFont().deriveFont(Font.BOLD));
        h.setBorder(new RoundedLineBorder(COL_LINE_SOFT, 12));
    }

    /** Gọi khi thêm một hàng mới vào JTable để highlight mượt */
    @SuppressWarnings("unchecked")
    public static void highlightRow(JTable table, int modelRow) {
        Map<Integer, Float> _hl = (Map<Integer, Float>) table.getClientProperty("rowHighlight");
        if (_hl == null) {
            _hl = new HashMap<>();
            table.putClientProperty("rowHighlight", _hl);
        }
        final Map<Integer, Float> highlight = _hl; // final để dùng trong lambda

        int viewRow = modelRow;
        if (table.getRowSorter()!=null) {
            try { viewRow = table.convertRowIndexToView(modelRow); } catch (Exception ignore) {}
        }
        highlight.put(viewRow, 1f);

        final int rowKey = viewRow;
        final Timer t = new Timer(30, null);
        t.addActionListener(e -> {
            float p = highlight.getOrDefault(rowKey, 0f);
            p -= 0.04f;
            if (p <= 0f) { highlight.remove(rowKey); ((Timer)e.getSource()).stop(); }
            else highlight.put(rowKey, p);
            table.repaint();
        });
        t.start();
    }

    // ===== Compat classes cho BroadcastUI cũ =====
    public static class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        public RoundedPanel(){ super(); setOpaque(true); setBackground(COL_BG_CARD); }
        public RoundedPanel(int radius, Color bg){
            super(); setOpaque(true); setBackground(bg!=null?bg:COL_BG_CARD);
            setBorder(new RoundedLineBorder(COL_LINE_SOFT, Math.max(8, radius)));
        }
    }

    // --- Overload để Eclipse không kêu khi gọi với lớp con ---
    public static void styleButton(AbstractButton b) {
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(COL_PRIMARY);
        b.setBorder(new RoundedLineBorder(COL_PRIMARY, 20));
        SoftUI.addHoverFade(b, COL_PRIMARY, COL_PRIMARY_DARK, 150);
    }
    public static void styleToggle(JToggleButton t) {
        t.setFocusPainted(false);
        t.setForeground(Color.WHITE);
        t.setBackground(COL_SECONDARY);
        t.setBorder(new RoundedLineBorder(COL_SECONDARY, 20));
        SoftUI.addHoverFade(t, COL_SECONDARY, COL_PRIMARY, 150);
        t.addChangeListener(e -> t.setText(t.isSelected() ? "Auto: ON" : "Auto Send"));
    }
    // Overload khớp chính xác kiểu lớp con (fix lỗi Eclipse report)
    public static void styleButton(SoftUI.PillButton b) { styleButton((AbstractButton)b); }
    public static void styleToggle(SoftUI.PillToggleButton t) { styleToggle((JToggleButton)t); }

    public static class PillButton extends JButton {
        private static final long serialVersionUID = 1L;
        public PillButton(){ super(); SoftUI.styleButton(this); }
        public PillButton(String text){ super(text); SoftUI.styleButton(this); }
        public PillButton(String text, Color bg, Color hover, Color border){
            super(text); SoftUI.styleButton(this);
            if (bg != null) setBackground(bg);
            if (border != null) setBorder(new RoundedLineBorder(border, 20));
            if (bg != null && hover != null) SoftUI.addHoverFade(this, bg, hover, 150);
        }
    }
    public static class PillToggleButton extends JToggleButton {
        private static final long serialVersionUID = 1L;
        public PillToggleButton(){ super(); SoftUI.styleToggle(this); }
        public PillToggleButton(String text){ super(text); SoftUI.styleToggle(this); }
        public PillToggleButton(String text, Color bg, Color hover, Color border){
            super(text); SoftUI.styleToggle(this);
            if (bg != null) setBackground(bg);
            if (border != null) setBorder(new RoundedLineBorder(border,20));
            if (bg != null && hover != null) SoftUI.addHoverFade(this, bg, hover, 150);
        }
    }
    public static class PillTextField extends JTextField {
        private static final long serialVersionUID = 1L;
        public PillTextField(){ super(); setBorder(new RoundedLineBorder(COL_LINE_SOFT,16)); }
        public PillTextField(int columns){ super(columns); setBorder(new RoundedLineBorder(COL_LINE_SOFT,16)); }
        public PillTextField(String text){ super(text); setBorder(new RoundedLineBorder(COL_LINE_SOFT,16)); }
    }
}
