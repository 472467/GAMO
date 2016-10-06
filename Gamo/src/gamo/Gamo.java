package gamo;

import javax.swing.*;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;

/**
 * Created by bonetrail on 6/9/16.
 */
public class Gamo extends Canvas implements Runnable {

    Frame frame = new JFrame();
    ColorPanel panel = null;
    long time = 0;
    PointerInfo clickedPoint = null;
    boolean mousePressed = false;
    boolean running = true;
    int degradeTicks = 0;
    int steps = 0;
    long fpsTime = 0;
    public static void main(String[] args) {
        (new Thread(new Gamo())).start();

    }

    public Gamo() {
        Container center = new Container();
        frame.createBufferStrategy(2);
        panel = new ColorPanel(frame);
        frame.setLayout(new BorderLayout());

        frame.setFocusable(true);

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyListen());
        frame.addMouseListener(new CustomMouse());
        panel.addMouseListener(new CustomMouse());

        center.setLayout(new GridLayout(1,1));

        center.add(panel);

        frame.add(center, BorderLayout.CENTER);
        frame.setSize(400,400);
        frame.setTitle("XD LMAO");
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });
        
        

    }
    static {
        System.setProperty("sun.java2d.transaccel", "True");
        //System.setProperty("sun.java2d.trace", "timestamp,log,count");
        System.setProperty("sun.java2d.opengl", "True");
        System.setProperty("sun.java2d.d3d", "True");
        System.setProperty("sun.java2d.ddforcevram", "True");
        System.out.println(true);
    }

    public void step(){
        if(System.currentTimeMillis() - fpsTime > 1000){
            fpsTime = System.currentTimeMillis();
            panel.setfps(steps);
            steps = 0;;
        }
        if(mousePressed){
            PointerInfo p = MouseInfo.getPointerInfo();

            calculatePixelAcceleration((int)Math.round((clickedPoint.getLocation().getX()
                            - p.getLocation().getX())/16),
                    (int)Math.round((clickedPoint.getLocation().getY() - p.getLocation().getY())/16));
        }
        calculateOffsets();
        pixelAccelDegradation();
        
        //panel.repaint();
        //panel.paint(bs.getDrawGraphics());
        //bs = panel.bs;
        panel.repaint();
        
        steps++;
    }

    public void calculatePixelAcceleration(int x, int y){

        panel.pixelAccelX+= x;
        panel.pixelAccelY+= y;
        System.out.println(panel.pixelAccelX);
        if(panel.pixelAccelX > panel.maxAccelX && panel.pixelAccelX > 0){
            panel.pixelAccelX = panel.maxAccelX;
        }else if(panel.pixelAccelX < -panel.maxAccelX && panel.pixelAccelX < 0){
            panel.pixelAccelX = -panel.maxAccelX;
        }
        if(panel.pixelAccelY > panel.maxAccelY && panel.pixelAccelY > 0){
            panel.pixelAccelY = panel.maxAccelY;
        }else if(panel.pixelAccelY < -panel.maxAccelY && panel.pixelAccelY < 0){
            panel.pixelAccelY = -panel.maxAccelY;
        }
        System.out.println(panel.pixelAccelX);
        degradeTicks = 10;
    }

    public void pixelAccelDegradation(){
        if(degradeTicks != 0){
            degradeTicks-=1;
        }else{
            if(panel.pixelAccelY != 0){
                if(panel.pixelAccelY > 0){
                    panel.pixelAccelY-=1;
                }else if(panel.pixelAccelY < 0){
                    panel.pixelAccelY+=1;
                }
            }
            if(panel.pixelAccelX != 0){
                if(panel.pixelAccelX > 0){
                    panel.pixelAccelX-=1;
                }else if(panel.pixelAccelX < 0){
                    panel.pixelAccelX+=1;
                }
            }

        }
    }

    public void calculateOffsets(){
        panel.offsetX+= panel.pixelAccelX;
        panel.offsetY+= panel.pixelAccelY;
    }

    @Override
    public void run() {
        while(running){
            /*
            if (System.currentTimeMillis() - time >= 17) {
                step();
                time= System.currentTimeMillis();
            }
*/      
            step();
            try {
                Thread.sleep(17);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class CustomMouse implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            System.out.println("Pressed");
            clickedPoint = MouseInfo.getPointerInfo();
            mousePressed = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePressed = false;
            System.out.println("Released");
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //System.out.println("Pressed");
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //System.out.println("Pressed");
        }
    }

    private class KeyListen implements KeyEventDispatcher{
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            int key = e.getKeyCode();
            if(KeyEvent.KEY_PRESSED == e.getID()) {


                if (key == KeyEvent.VK_RIGHT) {
                    System.out.println("RIGHT");
                    if (panel.mapWidthX * panel.tileSize + panel.offsetX >= 0) {
                        calculatePixelAcceleration(-1,0);
                    }

                }
                if (key == KeyEvent.VK_LEFT) {
                    if (panel.offsetX <= 0) {
                        calculatePixelAcceleration(1,0);
                    }
                }
                if (key == KeyEvent.VK_UP) {
                    if (panel.offsetY <= 0) {
                        calculatePixelAcceleration(0,1);
                    }
                }
                if (key == KeyEvent.VK_DOWN) {
                    if (panel.mapWidthY * panel.tileSize + panel.offsetY >= 0) {
                        calculatePixelAcceleration(0,-1);
                    }
                }
                if(key == KeyEvent.VK_MINUS){
                    double hold = panel.tileSize/2;
                    if ((hold == Math.floor(hold)) && !Double.isInfinite(hold)) {
                        panel.tileSize /= 2;

                    }
                }
                if(key == KeyEvent.VK_EQUALS){
                    panel.tileSize*= 2;
                }
            }
            return false;
        }
    }

}



