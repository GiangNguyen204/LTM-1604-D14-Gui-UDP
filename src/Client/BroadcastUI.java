package Client;

import UDP.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static Client.SoftUI.*;

public class BroadcastUI extends JFrame implements ReceiverService.Listener {

    // ==== Controls ====
    private final JComboBox<String> cboHistory = new JComboBox<>();
    private final SoftUI.PillTextField txtMessage =
            new SoftUI.PillTextField("Hello Broadcast | epoch=" + System.currentTimeMillis());

    private final JButton btnUseHistory = new SoftUI.PillButton("Use", COL_PRIMARY, COL_PRIMARY_DARK, Color.WHITE);
    private final JButton btnSend       = new SoftUI.PillButton("Send", COL_PRIMARY, COL_PRIMARY_DARK, Color.WHITE);
    private final JToggleButton btnListen = new SoftUI.PillToggleButton("Start Listen",
            new Color(0x06B6D4), new Color(0x0369A1), COL_LINE_SOFT);
    private final JButton btnClear      = new SoftUI.PillButton("Clear", new Color(0xE2E8F0), COL_LINE_SOFT, COL_TEXT_DARK);
    private final JButton btnExport     = new SoftUI.PillButton("Export CSV", new Color(0xE2E8F0), COL_LINE_SOFT, COL_TEXT_DARK);

    // Auto send
    private final JToggleButton btnAuto = new SoftUI.PillToggleButton("Auto Send",
            COL_ACCENT, COL_ACCENT.darker(), COL_LINE_SOFT);
    private final JSpinner spnIntervalMs = new JSpinner(new SpinnerNumberModel(1000, 200, 60000, 200));
    private final JCheckBox chkAppendEpoch = new JCheckBox("Append epoch", true);
    private final JCheckBox chkAppendCounter = new JCheckBox("Append #", true);

    private final JLabel lblStatus = new JLabel("Ready", SwingConstants.LEFT);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Time", "Remote IP", "Port", "Message"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    // ==== Services / state ====
    private final Sender sender = new Sender();
    private final ReceiverService receiver = new ReceiverService(this);
    private final List<MessageEvent> buffer = new ArrayList<>();
    private final List<String> history = new ArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "UDP-AutoSender");
        t.setDaemon(true); return t;
    });
    private ScheduledFuture<?> autoTask;
    private volatile int autoCounter = 0;

    public BroadcastUI() {
        applyNimbusTheme();
        super.setTitle("UDP Broadcast Tool – LTM");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 660));
        setLocationRelativeTo(null);

        /* ============ Top background ============ */
        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setBackground(COL_BG_APP);
        topWrap.setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));

        // Card bo góc chứa controls
        RoundedPanel card = new RoundedPanel(20, COL_BG_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Row 1: History
        JPanel row1 = new JPanel(new BorderLayout(8,0));
        row1.setOpaque(false);
        JLabel lbHist = new JLabel("History:");
        lbHist.setFont(lbHist.getFont().deriveFont(Font.BOLD));
        lbHist.setForeground(COL_TEXT_DARK);
        JPanel rightHist = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        rightHist.setOpaque(false);
        cboHistory.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        rightHist.add(cboHistory);
        rightHist.add(btnUseHistory);
        row1.add(lbHist, BorderLayout.WEST);
        row1.add(rightHist, BorderLayout.CENTER);

        // Row 2: Message + actions
        JPanel row2 = new JPanel(new BorderLayout(10,8));
        row2.setOpaque(false);
        JLabel lbMsg = new JLabel("Message:");
        lbMsg.setFont(lbMsg.getFont().deriveFont(Font.BOLD));
        lbMsg.setForeground(COL_TEXT_DARK);
        JPanel rightMsg = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightMsg.setOpaque(false);

        JLabel every = new JLabel("Every (ms):");
        JComponent spn = spnIntervalMs;
        spn.setPreferredSize(new Dimension(90, spn.getPreferredSize().height));

        rightMsg.add(every);
        rightMsg.add(spn);
        rightMsg.add(chkAppendEpoch);
        rightMsg.add(chkAppendCounter);
        rightMsg.add(btnAuto);
        rightMsg.add(btnSend);
        rightMsg.add(btnListen);
        rightMsg.add(btnClear);
        rightMsg.add(btnExport);

        row2.add(lbMsg, BorderLayout.WEST);
        row2.add(txtMessage, BorderLayout.CENTER);
        row2.add(rightMsg, BorderLayout.EAST);

        card.add(row1, BorderLayout.NORTH);
        card.add(row2, BorderLayout.SOUTH);
        topWrap.add(card, BorderLayout.CENTER);

        /* ============ Center table ============ */
        table.setRowHeight(24);
        table.setSelectionBackground(new Color(0xDBEAFE));
        table.setSelectionForeground(COL_TEXT_DARK);
        SoftUI.applyStriping(table);
        SoftUI.applyHeaderStyle(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new SoftUI.RoundedLineBorder(COL_LINE_SOFT, 16));
        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setBackground(COL_BG_APP);
        centerWrap.setBorder(BorderFactory.createEmptyBorder(0,14,14,14));
        centerWrap.add(scroll, BorderLayout.CENTER);

        /* ============ Status bar (pill) ============ */
        JPanel statusWrap = new JPanel(new BorderLayout());
        statusWrap.setBackground(COL_BG_APP);
        statusWrap.setBorder(BorderFactory.createEmptyBorder(0,14,16,14));

        RoundedPanel statusCard = new RoundedPanel(18, COL_PRIMARY);
        statusCard.setLayout(new BorderLayout());
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        statusCard.add(lblStatus, BorderLayout.CENTER);
        statusWrap.add(statusCard, BorderLayout.CENTER);

        /* ============ Root ============ */
        JPanel root = new JPanel(new BorderLayout());
        root.add(topWrap, BorderLayout.NORTH);
        root.add(centerWrap, BorderLayout.CENTER);
        root.add(statusWrap, BorderLayout.SOUTH);
        setContentPane(root);

        // Load history
        loadHistoryToUi();

        // Actions
        btnUseHistory.addActionListener(e -> useSelectedHistory());
        btnSend.addActionListener(e -> doSendOnce());
        btnListen.addActionListener(e -> toggleListen());
        btnClear.addActionListener(e -> clearLog());
        btnExport.addActionListener(e -> exportCsv());
        btnAuto.addActionListener(e -> toggleAutoSend());
        getRootPane().setDefaultButton(btnSend);

        // Release
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                stopAuto();
                receiver.stop();
                scheduler.shutdownNow();
            }
        });
    }

    /* ================= Logic ================= */

    private void loadHistoryToUi() {
        history.clear();
        history.addAll(HistoryStore.load());
        cboHistory.removeAllItems();
        for (String s : history) cboHistory.addItem(s);
    }

    private void addToHistory(String msg) {
        if (msg == null || msg.isBlank()) return;
        history.remove(msg);
        history.add(msg);
        HistoryStore.save(history);
        loadHistoryToUi();
        cboHistory.setSelectedItem(msg);
    }

    private void useSelectedHistory() {
        Object sel = cboHistory.getSelectedItem();
        if (sel != null) {
            txtMessage.setText(sel.toString());
            txtMessage.requestFocus();
            txtMessage.selectAll();
        }
    }

    private String buildMessageForSend() {
        String base = txtMessage.getText().trim();
        if (base.isEmpty()) return null;
        StringBuilder sb = new StringBuilder(base);
        if (chkAppendEpoch.isSelected())   sb.append(" | epoch=").append(System.currentTimeMillis());
        if (chkAppendCounter.isSelected()) sb.append(" | #").append(++autoCounter);
        return sb.toString();
    }

    private void doSendOnce() {
        String msg = buildMessageForSend();
        if (msg == null) {
            JOptionPane.showMessageDialog(this, "Message không được rỗng.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new Thread(() -> {
            setStatus("Sending...");
            try {
                sender.send(msg);
                setStatus("Đã gửi broadcast.");
                addToHistory(txtMessage.getText().trim());
            } catch (Exception ex) {
                setStatus("Gửi thất bại: " + ex.getMessage());
                ex.printStackTrace();
            }
        }, "UDP-Send-Once").start();
    }

    private void toggleAutoSend() {
        if (btnAuto.isSelected()) startAuto(); else stopAuto();
        btnAuto.setText(btnAuto.isSelected() ? "Auto: ON" : "Auto Send");
    }

    private void startAuto() {
        int period = ((Number) spnIntervalMs.getValue()).intValue();
        if (period < 200) period = 200;
        setStatus("Auto sending every " + period + " ms ...");
        autoCounter = 0;
        stopAuto();
        autoTask = scheduler.scheduleAtFixedRate(() -> {
            String msg = buildMessageForSend();
            if (msg == null) return;
            try { sender.send(msg); } catch (Exception ex) { setStatus("Auto send lỗi: " + ex.getMessage()); }
        }, 0, period, TimeUnit.MILLISECONDS);
    }

    private void stopAuto() {
        if (autoTask != null && !autoTask.isCancelled()) {
            autoTask.cancel(true);
            autoTask = null;
        }
        setStatus("Auto stopped.");
    }

    private void toggleListen() {
        if (btnListen.isSelected()) {
            btnListen.setText("Listening...");
            receiver.start();
            setStatus("Listening UDP broadcast on port " + Config.PORT);
        } else {
            receiver.stop();
            btnListen.setText("Start Listen");
            setStatus("Receiver stopping...");
        }
    }

    private void clearLog() {
        buffer.clear();
        model.setRowCount(0);
        setStatus("Cleared.");
    }

    private void exportCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("udp_log.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try { CsvUtils.export(f, buffer); setStatus("Exported: " + f.getAbsolutePath()); }
            catch (Exception ex) { setStatus("Export lỗi: " + ex.getMessage()); ex.printStackTrace(); }
        }
    }

    private void addRow(MessageEvent e) {
        buffer.add(e);
        model.addRow(new Object[]{e.timeText, e.remoteIp, e.remotePort, e.message});
        SwingUtilities.invokeLater(() ->
                table.scrollRectToVisible(table.getCellRect(model.getRowCount()-1, 0, true)));
    }

    private void setStatus(String s) {
        SwingUtilities.invokeLater(() -> lblStatus.setText(s));
    }

    // Listener
    @Override public void onMessage(MessageEvent event) { SwingUtilities.invokeLater(() -> addRow(event)); }
    @Override public void onStatus(String text) { setStatus(text); }
    @Override public void onError(String text, Exception ex) {
        setStatus(text + " – " + ex.getMessage());
        ex.printStackTrace();
        SwingUtilities.invokeLater(() -> { btnListen.setSelected(false); btnListen.setText("Start Listen"); });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BroadcastUI().setVisible(true));
    }
}
