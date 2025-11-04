package ClientSender;

import javax.swing.*;

public class SenderMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SenderUI().setVisible(true));
    }
}
