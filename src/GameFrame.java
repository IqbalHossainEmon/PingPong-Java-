import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    GameFrame() {
        GamePanel panel = new GamePanel();
        add(panel);
        setTitle("Ping Pong");
        setResizable(false);
        setBackground(Color.black);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
    }
}
