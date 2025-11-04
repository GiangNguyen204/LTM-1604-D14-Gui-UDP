package ClientSender;

import Client.HistoryStore;
import Client.SoftUI;
import UDP.Sender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class SenderUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextArea txtMessage;
    private JButton btnSend;
    private JToggleButton btnAuto;
    private JSpinner spnIntervalMs;
    private JCheckBox chkAppendEpoch, chkAppendCounter;
    private JLabel lblStatus;
    private JTable tblHistory;
    private DefaultTableModel historyModel;

    private final UDP.Sender sender = new Sender();
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> { Thread t=new Thread(r,"auto-sender"); t.setDaemon(true); return t; });
    private ScheduledFuture<?> autoTask;
    private int autoCounter = 0;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SenderUI() {
        setTitle("UDP Sender");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        SoftUI.applyNimbusTheme();
        getContentPane().setLayout(new BorderLayout(12,12));
        getContentPane().setBackground(SoftUI.COL_BG_APP);

        getContentPane().add(buildTop(), BorderLayout.NORTH);
        getContentPane().add(buildHistory(), BorderLayout.CENTER);
        getContentPane().add(buildStatus(), BorderLayout.SOUTH);

        loadHistory();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel(new BorderLayout(10,10));
        top.setBackground(SoftUI.COL_BG_CARD);
        top.setBorder(new SoftUI.RoundedLineBorder(SoftUI.COL_LINE_SOFT,16));

        JLabel lbl = new JLabel("Message");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        txtMessage = new JTextArea(3,40);
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(txtMessage);
        sp.setBorder(new SoftUI.RoundedLineBorder(SoftUI.COL_LINE_SOFT,12));

        JPanel ctrls = new JPanel(new GridBagLayout());
        ctrls.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6); gc.fill = GridBagConstraints.HORIZONTAL;

        spnIntervalMs = new JSpinner(new SpinnerNumberModel(1000,200,60000,200));
        chkAppendEpoch = new JCheckBox("Append epoch");
        chkAppendCounter = new JCheckBox("Append #");
        btnAuto = SoftUI.makeRippleToggle("Auto Send");
        btnSend = SoftUI.makeRippleButton("Send");

        gc.gridx=0; gc.gridy=0; ctrls.add(new JLabel("Every (ms)"), gc);
        gc.gridx=1; gc.gridy=0; ctrls.add(spnIntervalMs, gc);
        gc.gridx=0; gc.gridy=1; gc.gridwidth=2; ctrls.add(chkAppendEpoch, gc);
        gc.gridx=0; gc.gridy=2; gc.gridwidth=2; ctrls.add(chkAppendCounter, gc);
        gc.gridx=0; gc.gridy=3; gc.gridwidth=2; ctrls.add(btnAuto, gc);
        gc.gridx=0; gc.gridy=4; gc.gridwidth=2; ctrls.add(btnSend, gc);

        JPanel left = new JPanel(new BorderLayout(8,8));
        left.setOpaque(false);
        left.add(lbl, BorderLayout.NORTH);
        left.add(sp, BorderLayout.CENTER);

        top.add(left, BorderLayout.CENTER);
        top.add(ctrls, BorderLayout.EAST);

        btnSend.addActionListener(e -> doSendOnce());
        btnAuto.addActionListener(e -> toggleAuto());
        return top;
    }

    private JPanel buildHistory() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBackground(SoftUI.COL_BG_CARD);
        p.setBorder(new SoftUI.RoundedLineBorder(SoftUI.COL_LINE_SOFT,16));

        historyModel = new DefaultTableModel(new Object[]{"Time","Message"},0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        tblHistory = new JTable(historyModel);
        tblHistory.setRowHeight(24);
        tblHistory.setAutoCreateRowSorter(true);
        SoftUI.applyStriping(tblHistory);
        SoftUI.applyHeaderStyle(tblHistory);

        tblHistory.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2 && tblHistory.getSelectedRow()>=0) {
                    int mr = tblHistory.convertRowIndexToModel(tblHistory.getSelectedRow());
                    String t = Objects.toString(historyModel.getValueAt(mr,0),"");
                    String m = Objects.toString(historyModel.getValueAt(mr,1),"");
                    showDetail(t,m);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tblHistory);
        sp.setBorder(new SoftUI.RoundedLineBorder(SoftUI.COL_LINE_SOFT,12));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStatus() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        lblStatus = new JLabel("Ready");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        pill.setBackground(SoftUI.COL_PRIMARY);
        pill.setBorder(new SoftUI.RoundedLineBorder(SoftUI.COL_PRIMARY,20));
        pill.add(lblStatus);
        p.add(pill, BorderLayout.WEST);
        return p;
    }

    private void toggleAuto() {
        if (autoTask != null && !autoTask.isCancelled()) stopAuto(); else startAuto();
    }
    private void startAuto() {
        int period = (int) spnIntervalMs.getValue();
        setStatus("Auto sending every " + period + " ms");
        autoTask = scheduler.scheduleAtFixedRate(() -> doSendOnceInternal(true), 0, period, TimeUnit.MILLISECONDS);
        SoftUI.showToast(this, "Auto: ON");
    }
    private void stopAuto() {
        if (autoTask != null) { autoTask.cancel(true); autoTask = null; setStatus("Auto stopped"); SoftUI.showToast(this, "Auto: OFF"); }
    }
    private void doSendOnce() { doSendOnceInternal(false); }

    private void doSendOnceInternal(boolean auto) {
        String base = txtMessage.getText()==null?"":txtMessage.getText().trim();
        if (base.isEmpty()) { setStatus("Message không được rỗng."); return; }
        String msg = buildMsg(base, auto);
        new Thread(() -> {
            try {
                sender.send(msg);
                setStatus("Đã gửi!");
                SwingUtilities.invokeLater(() -> SoftUI.showToast(this, "Sent ✓"));
                int row = appendHistory(System.currentTimeMillis(), base);
                SwingUtilities.invokeLater(() -> SoftUI.highlightRow(tblHistory, row));
            } catch (Exception ex) { setStatus("Gửi thất bại: "+ex.getMessage()); }
        }).start();
    }

    private String buildMsg(String base, boolean auto) {
        StringBuilder sb = new StringBuilder(base);
        if (chkAppendEpoch.isSelected()) sb.append(" | epoch=").append(System.currentTimeMillis());
        if (chkAppendCounter.isSelected()) { if (!auto) autoCounter=0; sb.append(" | #").append(++autoCounter); }
        return sb.toString();
    }

    private void loadHistory() {
        historyModel.setRowCount(0);
        List<String[]> pairs = HistoryStore.loadPairs();
        for (String[] p : pairs) {
            String epoch = p[0]==null?"":p[0];
            String msg   = p[1]==null?"":p[1];
            String time  = "";
            if (!epoch.isEmpty()) try {
                time = TIME_FMT.format(Instant.ofEpochMilli(Long.parseLong(epoch)).atZone(ZoneId.systemDefault()));
            } catch (Exception ignore) {}
            historyModel.addRow(new Object[]{time,msg});
        }
    }

    private int appendHistory(long epochMillis, String msg) {
        final int[] rowIdx = { -1 };
        SwingUtilities.invokeLater(() -> {
            String time = TIME_FMT.format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()));
            historyModel.addRow(new Object[]{time,msg});
            int last = historyModel.getRowCount()-1;
            rowIdx[0] = last;
            // persist
            List<String[]> all = new ArrayList<>();
            for (int i=0;i<historyModel.getRowCount();i++) {
                String t = Objects.toString(historyModel.getValueAt(i,0),"");
                String m = Objects.toString(historyModel.getValueAt(i,1),"");
                String ep = Long.toString(System.currentTimeMillis());
                try {
                    ep = Long.toString(LocalDateTime.parse(t,TIME_FMT).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                } catch (Exception ignore) {}
                all.add(new String[]{ep,m});
            }
            HistoryStore.savePairs(all);
            // autoscroll
            tblHistory.scrollRectToVisible(tblHistory.getCellRect(last, 0, true));
        });
        // best effort index (sau invokeLater có thể +1): trả về chỉ số mới nhất hiện có
        return Math.max(0, historyModel.getRowCount()-1);
    }

    private void showDetail(String time, String msg) {
        JTextArea area = new JTextArea(msg,12,60);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane sc = new JScrollPane(area);
        sc.setPreferredSize(new Dimension(700,260));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Time: "+time), BorderLayout.NORTH);
        panel.add(sc, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, panel, "Message Detail", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setStatus(String s) { SwingUtilities.invokeLater(() -> lblStatus.setText(s)); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SenderUI().setVisible(true));
    }
}
