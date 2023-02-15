import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

public class GameStatus extends Rectangle {
    int GAME_WIDTH;
    int GAME_HEIGHT;
    static String gameState;
    int statusPosition;
    int y;

    GameStatus(int GAME_WIDTH, int GAME_HEIGHT) {
        this.GAME_WIDTH = GAME_WIDTH;
        this.GAME_HEIGHT = GAME_HEIGHT;
    }

    void statusPosition(int pos) {
        if (pos > 0) {
            statusPosition = y;
        } else {
            statusPosition = -15;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Consolas", Font.PLAIN, 50));

        FontMetrics fm = g.getFontMetrics();
        int x = (GAME_WIDTH - fm.stringWidth(gameState)) / 2;
        y = (fm.getAscent() + (GAME_HEIGHT - (fm.getAscent() + fm.getDescent())) / 2);
        g.drawString(gameState, x, statusPosition);
    }
}
