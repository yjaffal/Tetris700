package tetris700;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

/**
 * Tetris Game
 * @author Yasser
 */
public class Main extends JFrame implements ActionListener, KeyListener {

    int rows, columns;
    JPanel[][] panes;
    Shape currentShape;
    Timer gameTimer;
    final Color FULL_COLOR = Color.WHITE;
    final Color EMPTY_COLOR = Color.BLACK;
    boolean fixNextUpdate = false;
    JPanel gamePane;
    JPanel scorePane;
    JLabel scoreLabel;
    JLabel gameOver;

    int score = 0;
    public Main(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        GridLayout g = new GridLayout(rows, columns);
        Container con = getContentPane();
        gamePane = new JPanel();
        con.add(gamePane);
        gamePane.setLayout(g);
        scorePane = new JPanel();
        scoreLabel = new JLabel("score: " + score);
        scorePane.add(scoreLabel);
        con.add(scorePane, BorderLayout.EAST);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(columns * 35, rows * 20);

        panes = new JPanel[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                panes[i][j] = new JPanel();
                panes[i][j].setBackground(EMPTY_COLOR);
                gamePane.add(panes[i][j]);
                panes[i][j].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            }
        }

        currentShape = getNextShape();
        gameTimer = new Timer(1000, this);
        gameTimer.start();
        addKeyListener(this);

        scorePane.setLayout(new BoxLayout(scorePane, BoxLayout.Y_AXIS));

        gameOver = new JLabel();
        gameOver.setForeground(Color.RED);
        gameOver.setVisible(false);
        scorePane.add(gameOver);
        gamePane.requestFocus();
        setVisible(true);
    }

    void resetGame(){
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                panes[i][j].setBackground(EMPTY_COLOR);
            }
        }
        gameOver.setVisible(false);
        score = 0;
        scoreLabel.setText("score: 0");
        currentShape = getNextShape();
        gameTimer.start();
        //gamePane.requestFocus();

    }

    void endGame(){
        gameTimer.stop();
        gameOver.setVisible(true);
        gameOver.setText("GAME OVER, Press R to restart");
    }

    void drawShape(Shape s, Color c) {
        clearScene();
        panes[s.i1][s.j1].setBackground(c);
        panes[s.i2][s.j2].setBackground(c);
        panes[s.i3][s.j3].setBackground(c);
        panes[s.i4][s.j4].setBackground(c);
    }

    void clearScene() {
        for (int i = 0; i < panes.length; i++) {
            for (int j = 0; j < panes[i].length; j++) {
                if (!panes[i][j].getBackground().equals(FULL_COLOR)) {
                    panes[i][j].setBackground(EMPTY_COLOR);
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (fixNextUpdate) {
            drawShape(currentShape, FULL_COLOR);
            currentShape = getNextShape();
            fixNextUpdate = false;
            checkLines();
            return;
        }
        currentShape.moveDown();
        drawShape(currentShape, currentShape.color);
        checkCollisions();
        for(int i = 0; i < columns; i++){
            if(panes[0][i].getBackground().equals(FULL_COLOR)){
                endGame();
                break;
            }
        }

    }

    Shape getNextShape() {
        Shape randomShape = null;

        int rand = ((int) (Math.random() * 10) % 7);
        switch (rand) {
            case 0:
                randomShape = new Square(columns, rows);
                break;
            case 1:
                randomShape = new T(columns, rows);
                break;
            case 2:
                randomShape = new Bar(columns, rows);
                break;
            case 3:
                randomShape = new L(columns, rows);
                break;
            case 4:
                randomShape = new InverseL(columns, rows);
                break;
            case 5:
                randomShape = new S(columns, rows);
                break;
            case 6:
                randomShape = new InverseS(columns, rows);
                break;
        }

        return randomShape;
    }

    void checkCollisions() {
        int max = rows - 1;
        if (currentShape.i1 == max ||
                currentShape.i2 == max ||
                currentShape.i3 == max ||
                currentShape.i4 == max) {
            fixNextUpdate = true;
            return;
        }

        if (panes[currentShape.i1 + 1][currentShape.j1].getBackground().equals(FULL_COLOR)) {
            fixNextUpdate = true;
            return;
        }

        if (panes[currentShape.i2 + 1][currentShape.j2].getBackground().equals(FULL_COLOR)) {
            fixNextUpdate = true;
            return;
        }

        if (panes[currentShape.i3 + 1][currentShape.j3].getBackground().equals(FULL_COLOR)) {
            fixNextUpdate = true;
            return;
        }

        if (panes[currentShape.i4 + 1][currentShape.j4].getBackground().equals(FULL_COLOR)) {
            fixNextUpdate = true;
            return;
        }

    }

    private void checkLines() {
        int score = 0;
        int bonus = 0;
        for (int i = 0; i < rows; i++) {
            int fullCount = 0;

            for (int j = 0; j < columns; j++) {
                if (panes[i][j].getBackground().equals(FULL_COLOR)) {
                    fullCount++;
                }
            }

            if (fullCount == columns) {
                //complete line
                for(int k = i; k > 0; k--){
                    for(int j = 0; j < columns; j++){
                        panes[k][j].setBackground(panes[k - 1][j].getBackground());
                    }
                }
                bonus++;
                score++;
            }
        }

        this.score += score * bonus;
        scoreLabel.setText("score: " + this.score);

        //Increase speed each 20 lines
        if(this.score >= next){
            int time = gameTimer.getDelay();
            time -= 100;
            gameTimer.setDelay(time);
            next += 20;
        }
    }

    int next = 20;

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_SPACE:
                currentShape.rotate();
                break;
            case KeyEvent.VK_RIGHT:
                if (currentShape.getMaxJ() < columns - 1) {
                    if (!panes[currentShape.i1][currentShape.j1 + 1].getBackground().equals(FULL_COLOR) &&
                            !panes[currentShape.i2][currentShape.j2 + 1].getBackground().equals(FULL_COLOR) &&
                            !panes[currentShape.i3][currentShape.j3 + 1].getBackground().equals(FULL_COLOR) &&
                            !panes[currentShape.i4][currentShape.j4 + 1].getBackground().equals(FULL_COLOR)) {
                        currentShape.moveRight();
                    }
                }
                break;
            case KeyEvent.VK_LEFT:
                if (currentShape.getMinJ() > 0) {
                    if (!panes[currentShape.i1][currentShape.j1 - 1].getBackground().equals(FULL_COLOR) &&
                            !panes[currentShape.i2][currentShape.j2 - 1].getBackground().equals(FULL_COLOR) &&
                            !panes[currentShape.i3][currentShape.j3 - 1].getBackground().equals(FULL_COLOR) &&
                            !panes[currentShape.i4][currentShape.j4 - 1].getBackground().equals(FULL_COLOR)) {
                        currentShape.moveLeft();
                    }
                }
                break;
            case KeyEvent.VK_DOWN:
                actionPerformed(null);
                break;
            case KeyEvent.VK_R:
                resetGame();
        }

        drawShape(currentShape, currentShape.color);
        checkCollisions();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Main(20, 8);
    }
}

abstract class Shape {

    int i1, j1, i2, j2, i3, j3, i4, j4;
    int width, height;
    int direction;
    Color color;

    public Shape(int w, int h) {
        width = w;
        height = h;
        color = new Color(100, 100, 100);
        direction = 0;
    }
    protected int ti1, ti2, ti3, ti4;
    protected int tj1, tj2, tj3, tj4;

    public void moveDown() {
        i1++;
        i2++;
        i3++;
        i4++;
    }

    public void moveLeft() {
        int min = getMinJ();
        if (min == 0) {
            return;
        }
        j1--;
        j2--;
        j3--;
        j4--;
    }

    public int getMinJ() {
        return Math.min(Math.min(j1, j2), Math.min(j3, j4));
    }

    public int getMaxJ() {
        return Math.max(Math.max(j1, j2), Math.max(j3, j4));
    }

    public int getMinTJ() {
        return Math.min(Math.min(tj1, tj2), Math.min(tj3, tj4));
    }

    public int getMaxTJ() {
        return Math.max(Math.max(tj1, tj2), Math.max(tj3, tj4));
    }

    public int getMinTI() {
        return Math.min(Math.min(ti1, ti2), Math.min(ti3, ti4));
    }

    public int getMaxTI() {
        return Math.max(Math.max(ti1, ti2), Math.max(ti3, ti4));
    }

    public void moveRight() {
        int max = getMaxJ();
        if (max == width - 1) {
            return;
        }
        j1++;
        j2++;
        j3++;
        j4++;
    }

    public void rotate() {

        ti1 = i1;
        tj1 = j1;
        ti2 = i2;
        tj2 = j2;
        ti3 = i3;
        tj3 = j3;
        ti4 = i4;
        tj4 = j4;

        if (direction == 0) {
            rotateTo1();
        } else if (direction == 1) {
            rotateTo2();
        } else if (direction == 2) {
            rotateTo3();
        } else if (direction == 3) {
            rotateTo0();
        }
        checkAndRestoreValues();

    }

    protected void checkAndRestoreValues() {
        if (getMaxTJ() > width - 1) {
            return;
        }

        if (getMinTJ() < 0) {
            return;
        }

        if(getMinTI() < 0){
            return;
        }

        if(getMaxTI() > height - 1){
            return;
        }

        i1 = ti1;
        j1 = tj1;
        i2 = ti2;
        j2 = tj2;
        i3 = ti3;
        j3 = tj3;
        i4 = ti4;
        j4 = tj4;

        direction++;
        if (direction == 4) {
            direction = 0;
        }
    }

    protected abstract void rotateTo1();

    protected abstract void rotateTo2();

    protected abstract void rotateTo3();

    protected abstract void rotateTo0();
}

class Square extends Shape {

    public Square(int w, int h) {
        super(w, h);
        i1 = j1 = 0;
        i4 = j4 = 1;
        i3 = j2 = 0;
        i2 = j3 = 1;
        this.color = Color.YELLOW;
    }

    @Override
    protected void rotateTo1() {
    }

    @Override
    protected void rotateTo2() {
    }

    @Override
    protected void rotateTo3() {
    }

    @Override
    protected void rotateTo0() {
    }
}

class T extends Shape {

    public T(int w, int h) {
        super(w, h);
        i1 = i2 = i3 = 0;
        i4 = 1;
        j1 = 0;
        j2 = 1;
        j3 = 2;
        j4 = 1;
        color = Color.PINK;
    }

    @Override
    protected void rotateTo1() {
        tj1 += 2;
        ti2++;
        tj2++;
        ti3 += 2;

    }

    @Override
    protected void rotateTo2() {
        tj3 -= 2;
        ti2++;
        tj2--;
        ti1 += 2;
    }

    @Override
    protected void rotateTo3() {
        ti3 -= 2;
        tj2--;
        ti2--;
        tj1 -= 2;
    }

    @Override
    protected void rotateTo0() {
        tj3 += 2;
        ti2 -= 1;
        tj2 += 1;
        ti1 -= 2;
    }
}

class Bar extends Shape {

    public Bar(int w, int h) {
        super(w, h);
        i1 = i2 = i3 = i4 = 0;
        j1 = 0;
        j2 = 1;
        j3 = 2;
        j4 = 3;
        color = Color.CYAN;
    }

    @Override
    protected void rotateTo1() {
        ti1 -= 1;
        tj1++;
        tj3--;
        ti3++;
        tj4 -= 2;
        ti4 += 2;
    }

    @Override
    protected void rotateTo2() {
        tj1--;
        ti1++;
        tj3++;
        ti3--;
        tj4 += 2;
        ti4 -= 2;
    }

    @Override
    protected void rotateTo3() {
        rotateTo1();
    }

    @Override
    protected void rotateTo0() {
        rotateTo2();
    }
}

class L extends Shape{
    public L(int w, int h){
        super(w, h);
        i1 = j1 = 0;
        i2 = 1; j2 = 0;
        i3 = 2; j3 = 0;
        i4 = 2; j4 = 1;
        this.color = Color.ORANGE;
    }

    @Override
    protected void rotateTo1() {
        ti3--; tj3++;
        tj2 += 2;
        ti1+=1; tj1+=3;
    }

    @Override
    protected void rotateTo2() {
        tj3++; ti3++;
        ti2 +=2;
        tj1-=1;
        ti1+=3;
    }

    @Override
    protected void rotateTo3() {
        ti3++;
        tj3--;
        tj2 -=2;
        ti1--;
        tj1-=3;
    }

    @Override
    protected void rotateTo0() {
        tj1++;
        ti1-=3;
        ti2-=2;
        ti3--;
        tj3--;
    }
}

class InverseL extends Shape{

    public InverseL(int w, int h){
        super(w, h);
        j1 = j2 = j3 = 1;
        j4 = 0;
        i1 = 0; i2 = 1; i3 = 2;
        i4 = 2;
        this.color = Color.BLUE;
    }

    @Override
    protected void rotateTo1() {
        tj4++; ti4--;
        tj2++; ti2++;
        tj1+=2; ti1+=2;
    }

    @Override
    protected void rotateTo2() {
        tj4++; ti4++;
        ti2++; tj2--;
        ti1+=2; tj1-=2;
    }

    @Override
    protected void rotateTo3() {
        ti4++; tj4--;
        ti2--; tj2--;
        ti1-=2; tj1-=2;
    }

    @Override
    protected void rotateTo0() {
        tj4--; ti4--;
        tj2++; ti2--;
        ti1-=2; tj1+=2;
    }
}

class S extends Shape{

    public S(int w, int h){
        super(w, h);
        i1 = i2 = 0;
        i3 = i4 = 1;
        j1 = j3 = 1;
        j2 = 2;
        j4 = 0;
        this.color = Color.GREEN;
    }

    @Override
    protected void rotateTo1() {
        tj4+=2;
        ti3-=2;
    }

    @Override
    protected void rotateTo2() {
        tj4-=2;
        ti3+=2;
    }

    @Override
    protected void rotateTo3() {
        rotateTo1();
    }

    @Override
    protected void rotateTo0() {
        rotateTo2();
    }

}

class InverseS extends Shape{

    public InverseS(int w, int h){
        super(w, h);
        i1 = i2 = 0;
        i3 = i4 = 1;
        j1 = 0;
        j2 = j3 = 1;
        j4 = 2;
        this.color = Color.RED;
    }

    @Override
    protected void rotateTo1() {
        tj4-=2;
        ti3-=2;
    }

    @Override
    protected void rotateTo2() {
        tj4+=2;
        ti3+=2;
    }

    @Override
    protected void rotateTo3() {
        rotateTo1();
    }

    @Override
    protected void rotateTo0() {
        rotateTo2();
    }

}
