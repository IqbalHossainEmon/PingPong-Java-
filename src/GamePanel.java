import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    static final int GAME_WIDTH = screenWidth();
    static final int GAME_HEIGHT = (int) (GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 15;
    int PADDLE_HEIGHT = 200;

    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;
    GameStatus status;
    volatile boolean gamePause = false;
    boolean gameStart = false;

    GamePanel() {

        newPaddle(PADDLE_HEIGHT, 0);
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        setFocusable(true);
        gameThread = new Thread(this);
        status = new GameStatus(GAME_WIDTH, GAME_HEIGHT);
        if (!gameStart) {
            GameStatus.gameState = "Press SPACE to start the game";
            score.midBarPosition = 0;
        }

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gamePause && gameStart) {
                    paddle1.keyPressed(e);
                    paddle2.keyPressed(e);
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameStart) {
                    startGame();
                    gameStart = true;
                    status.statusPosition(0);
                    score.midBarPosition = GAME_HEIGHT;
                }
                if (e.getKeyCode() == KeyEvent.VK_P && gameStart) {
                    if (gamePause) {
                        status.statusPosition(0);
                        gamePause = false;
                        score.midBarPosition = GAME_HEIGHT;
                    } else {
                        GameStatus.gameState = "Press P to resume the game";
                        status.statusPosition(1);
                        score.midBarPosition = 0;
                        gamePause = true;
                        repaint();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                paddle1.keyReleased(e);
                paddle2.keyReleased(e);
            }
        });
        setPreferredSize(SCREEN_SIZE);

    }

    void startGame() {
        gameThread.start();
        status.statusPosition(0);
    }

    public void gameOver() {
        if (score.player1 > 10) {
            GameStatus.gameState = "Player 1 Won";

        } else {
            GameStatus.gameState = "Player 2 Won";
        }
        status.statusPosition(1);
        gamePause = true;
        score.midBarPosition = 0;
    }

    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), random.nextInt((GAME_HEIGHT / 2) - (BALL_DIAMETER / 2)),
                BALL_DIAMETER);

    }

    static int screenWidth() {
        return (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    }

    public void newPaddle(int PADDLE_HEIGHT, int paddleId) {
        if (paddleId == 1) {
            paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
            paddle2 = new Paddle((GAME_WIDTH - PADDLE_WIDTH), (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH,
                    paddle2.height, 2);
        } else if (paddleId == 2) {
            paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, paddle1.height, 1);
            paddle2 = new Paddle((GAME_WIDTH - PADDLE_WIDTH), (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH,
                    PADDLE_HEIGHT, 2);
        } else {
            paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
            paddle2 = new Paddle((GAME_WIDTH - PADDLE_WIDTH), (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH,
                    PADDLE_HEIGHT, 2);
        }
    }

    @Override
    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }

    public void draw(Graphics g) {
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
        status.draw(g);
        if (!gameStart) {
            status.statusPosition(1);
        }
    }

    public void move() {
        paddle1.move();
        paddle2.move();
        ball.move();
    }

    public void checkCollision() {
        if (ball.y <= 0) {
            ball.setYDirection(-ball.yVelocity);
        }
        if (ball.y >= (GAME_HEIGHT - BALL_DIAMETER)) {
            ball.setYDirection(-ball.yVelocity);
        }

        if (ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++;
            if (ball.yVelocity > 0) {
                ball.yVelocity++;
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        if (ball.intersects(paddle2)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++;
            if (ball.yVelocity > 0) {
                ball.yVelocity++;
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        if (paddle1.y <= 0) {
            paddle1.y = 0;
        }
        if (paddle1.y >= (GAME_HEIGHT - paddle1.height)) {
            paddle1.y = (GAME_HEIGHT - paddle1.height);
        }
        if (paddle2.y <= 0) {
            paddle2.y = 0;
        }
        if (paddle2.y >= (GAME_HEIGHT - paddle2.height)) {
            paddle2.y = (GAME_HEIGHT - paddle2.height);
        }

        if (ball.x <= 0) {
            score.player2++;
            newPaddle(paddle1.height - 15, 1);
            newBall();
        }
        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            score.player1++;
            newPaddle(paddle2.height - 15, 2);
            newBall();
        }
        if (score.player1 > 10 || score.player2 > 10) {
            gameOver();
        }
    }

    public void run() {

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (gamePause) {
                delta = 0;
            }
            if (delta >= 1) {
                move();
                checkCollision();
                repaint();
                delta--;
            }

        }

    }

}
