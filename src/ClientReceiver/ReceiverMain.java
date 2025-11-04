package ClientReceiver;

import javax.swing.*;

public class ReceiverMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReceiverUI().setVisible(true));
    }
}
