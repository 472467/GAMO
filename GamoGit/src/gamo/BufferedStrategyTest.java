package gamo;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

public class BufferedStrategyTest extends JFrame implements Runnable, WindowListener {

    private Thread graphicsThread;
    private boolean running = false;
    private BufferStrategy strategy;

    public BufferedStrategyTest() {
        super("FrameDemo");
        addWindowListener(this);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 500);
        setResizable(true);
        setVisible(true);

        createBufferStrategy(2);
        strategy = getBufferStrategy();

        running = true;
        graphicsThread = new Thread(this);
        graphicsThread.start();
    }

    public static void main(String[] args) {
        new BufferedStrategyTest();
    }

    public void addNotify() {
        super.addNotify();
        createBufferStrategy(2);
        strategy = getBufferStrategy();
    }

    @Override
    public void run() {
        while (running) {
            do {
                do {
                    Graphics g = strategy.getDrawGraphics();

                    g.setColor(Color.GRAY);
                    g.drawRect(0, 0, 500, 500);
                    g.setColor(Color.BLACK);
                    g.drawLine(50, 50, 200, 50);

                    g.dispose();
                } while (running && strategy.contentsRestored());
                strategy.show();

                // Repeat the rendering if the drawing buffer was lost
            } while (running && strategy.contentsLost());
        }
        setVisible(false);
        dispose();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowClosing(WindowEvent e) {
        running = false;
    }
    @Override
    public void windowDeactivated(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowOpened(WindowEvent e) {}
}