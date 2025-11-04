package ClientReceiver;

import Client.CsvUtils;
import Client.SoftUI;
import UDP.Config;
import UDP.MessageEvent;
import Client.ReceiverService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReceiverUI extends JFrame implements ReceiverService.Listener {
    private static final long serialVersionUID = 1L;

    private JButton btnListen, btnClear, btnExport;
    private JLabel lblStatus;
    private JTable tbl;
    private DefaultTableModel model;

    private final List<MessageEvent> buffer = new ArrayList<>();
    private final ReceiverService receiver = new ReceiverService(this);

    public ReceiverUI() {
        setTitle("UDP Receiver");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);
        SoftUI.applyNimbusTheme();

        getContentPane().setLayout(new BorderLayout(12,12));
        getContentPane().setBackground(SoftUI.COL_BG_APP);

        getContentPane().add(buildTopCard(), BorderLayout.NORTH);
        getContentPane().add(buildTableCard(), BorderLayout.CENTER);
        getContentPane().add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JComponent buildTopCard() {
        SoftUI.GradientPanel card = new SoftUI.GradientPanel();
        card.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        card.setBorder(new SoftUI.RoundedLineBorder(SoftUI.COL_LINE_SOFT, 20));

        btnListen = SoftUI.makeRippleButton("Start Listen");
        btnClear  = SoftUI.makeRippleButton("Clear");
        btnExport = SoftUI.makeRippleButton("Export");

        card.add(btnListen); card.add(btnClear); card.add(btnExport);

        btnListen.addActionListener(e -> toggleListen());
        btnClear.addActionListener(e -> clearLog());
        btnExport.addActionListener(e -> exportCsv());
        return card;
    }

    private JComponent buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(8,8));
        SoftUI.styleCard(card);

        model = new DefaultTableModel(new Object[]{"Time", "Remote IP", "Port", "Message"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl = new JTable(model);
        tbl.setRowHeight(26);
        tbl.setAutoCreateRowSorter(true);
        SoftUI.applyStriping(tbl);
        SoftUI.applyHeaderStyle(tbl);

        tbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2 && tbl.getSelectedRow()>=0) {
                    int vr = tbl.getSelectedRow();
                    int mr = tbl.convertRowIndexToModel(vr);
                    showDetailDialog(mr);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(new SoftUI.RoundedLineBorder(SoftUI.COL_LINE_SOFT, 14));
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildStatusBar() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);

        JPanel pill = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pill.setBackground(SoftUI.COL_PRIMARY);
        pill.setBorder(new SoftUI.RoundedLineBorder(SoftUI.COL_PRIMARY, 20));

        lblStatus = new JLabel("Ready");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(8,16,8,8));
        pill.add(lblStatus);

        root.add(pill, BorderLayout.WEST);
        return root;
    }

    // ===== ReceiverService callbacks =====
    @Override public void onMessage(MessageEvent e) { SwingUtilities.invokeLater(() -> addRow(e)); }
    @Override public void onStatus(String text) { setStatus(text); }
    @Override public void onError(String text, Exception ex) {
        setStatus(text + (ex != null ? " - " + ex.getMessage() : ""));
        SwingUtilities.invokeLater(() -> btnListen.setText("Start Listen"));
    }

    // ===== actions =====
    private void toggleListen() {
        if ("Start Listen".equals(btnListen.getText())) {
            try {
                receiver.start();
                btnListen.setText("Listening...");
                setStatus("Listening UDP broadcast on port " + Config.PORT);
                SoftUI.showToast(this, "Listening…");
            } catch (Exception ex) {
                setStatus("Start failed: " + ex.getMessage());
            }
        } else {
            try { receiver.stop(); } catch (Exception ignore) {}
            btnListen.setText("Start Listen");
            setStatus("Stopped.");
            SoftUI.showToast(this, "Stopped");
        }
    }

    private void clearLog() {
        buffer.clear();
        model.setRowCount(0);
        setStatus("Cleared.");
        SoftUI.showToast(this, "Cleared");
    }

    private void exportCsv() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("udp_log.csv"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                CsvUtils.export(fc.getSelectedFile(), buffer);
                setStatus("Exported: " + fc.getSelectedFile().getAbsolutePath());
                SoftUI.showToast(this, "Exported ✓");
            }
        } catch (Exception ex) {
            setStatus("Export failed: " + ex.getMessage());
        }
    }

    // ===== helpers =====
    private void addRow(MessageEvent e) {
        buffer.add(e);
        model.addRow(new Object[]{ e.timeText, e.remoteIp, e.remotePort, e.message });
        int last = model.getRowCount()-1;
        if (last>=0) {
            tbl.scrollRectToVisible(tbl.getCellRect(last, 0, true));
            SoftUI.highlightRow(tbl, last); // hiệu ứng nháy nhẹ dòng mới
        }
    }

    private void setStatus(String s) { SwingUtilities.invokeLater(() -> lblStatus.setText(s)); }

    private void showDetailDialog(int mr) {
        String time = Objects.toString(model.getValueAt(mr,0),"");
        String ip   = Objects.toString(model.getValueAt(mr,1),"");
        String port = Objects.toString(model.getValueAt(mr,2),"");
        String msg  = Objects.toString(model.getValueAt(mr,3),"");

        JTextArea area = new JTextArea(msg, 12, 60);
        area.setEditable(false); area.setLineWrap(true); area.setWrapStyleWord(true);
        JScrollPane sc = new JScrollPane(area);
        sc.setPreferredSize(new Dimension(760, 280));

        JPanel panel = new JPanel(new BorderLayout(8,8));
        JLabel meta = new JLabel("Time: " + time + "   From: " + ip + ":" + port);
        meta.setForeground(SoftUI.COL_TEXT_SOFT);
        panel.add(meta, BorderLayout.NORTH);
        panel.add(sc, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Message Detail", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReceiverUI().setVisible(true));
    }
}
