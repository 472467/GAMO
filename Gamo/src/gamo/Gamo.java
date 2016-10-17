package gamo;

import javax.swing.*;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;
import gamo.tiles.TILE;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Created by bonetrail on 6/9/16.
 */
public class Gamo extends JFrame implements Runnable, WindowListener {

    long time = 0;
    PointerInfo clickedPoint = null;
    boolean mousePressed = false;
    boolean running = false;
    int degradeTicks = 0;
    int steps = 0;
    long fpsTime = 0;
    BufferStrategy bs;
    int offsetX = 0;
    int offsetY = 0;
    int pixelAccelX = 0;
    int pixelAccelY = 0;
    int maxAccelX = 4;
    int maxAccelY = 4;
    int tileSize = 128;
    int mapWidthX = 500;
    int mapWidthY = 500;
    int fps=0;
    Image i;
    Image gr;
    
    Color[][] colorMap = new Color[mapWidthX][mapWidthY];
    int[][] intMap = new int[mapWidthX][mapWidthY];
    TILE[][] tileMap = new TILE[mapWidthX][mapWidthY];
    
    public static void main(String[] args) {
        
        new Thread() {//sorta fixes horrible stutter issue
        { setDaemon(true); start(); }
        @Override
        public void run() { 
            while(true) {
                try {
                    Thread.sleep(Integer.MAX_VALUE);
                }catch(Throwable t){} } }
            };

        (new Thread(new Gamo())).start();

    }

    public Gamo() {
        try{
            i =  ImageIO.read(new File("a62.png"));
            gr =  ImageIO.read(new File("g.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
        
        Random r = new Random();

        for(int x = 0; x < mapWidthX; x++){
            for(int y = 0; y < mapWidthY; y++){
                colorMap[x][y]= new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
                intMap[x][y]= r.nextInt(2);
            }
        }
        
        //setLayout(new BorderLayout());

        setFocusable(true);

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyListen());
        addMouseListener(new CustomMouse());
        setSize(400,400);
        setTitle("XD LMAO");
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        running = true;
    }
    static {
        System.setProperty("sun.java2d.transaccel", "True");
        //System.setProperty("sun.java2d.trace", "timestamp,log,count");
        System.setProperty("sun.java2d.opengl", "True");
        System.setProperty("sun.java2d.d3d", "True");
        System.setProperty("sun.java2d.ddforcevram", "True");
        System.out.println(true);
    }
    
    @Override
    public void addNotify(){
        super.addNotify();
        createBufferStrategy(2);
        bs = this.getBufferStrategy();
    }

    public void step(){
        if(System.currentTimeMillis() - fpsTime > 1000){
            fpsTime = System.currentTimeMillis();
            fps = steps;
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
        //dispose();
        //bs.show();
        steps++;
    }
    public void render(){
        Graphics g = bs.getDrawGraphics();
        outerloop:
        for(int x = 0; x < mapWidthX; x++){
            for(int y = 0; y < mapWidthY; y++){
                if(tileSize * x >= -offsetX -2 * tileSize){
                    if(tileSize * y >= -offsetY - 2 * tileSize && tileSize * y <= -offsetY + getSize().getHeight()){
                        if(tileSize * x >= -offsetX + getSize().getWidth()){
                            break outerloop;
                        }
                        Random r = new Random();
                        if(intMap[x][y] == 0){
                            g.drawImage(i, tileSize * x + offsetX, tileSize * y + offsetY, null);
                        }else{
                            g.drawImage(gr, tileSize * x + offsetX, tileSize * y + offsetY, null);
                        }
                        
                        
                        
                    }
                }
            }
        }
        g.drawString( ""+fps, 40 , 40);
        g.dispose();
        bs.show();
    }

    public void calculatePixelAcceleration(int x, int y){

        pixelAccelX+= x;
        pixelAccelY+= y;
        System.out.println(pixelAccelX);
        if(pixelAccelX > maxAccelX && pixelAccelX > 0){
            pixelAccelX = maxAccelX;
        }else if(pixelAccelX < -maxAccelX && pixelAccelX < 0){
            pixelAccelX = -maxAccelX;
        }
        if(pixelAccelY > maxAccelY && pixelAccelY > 0){
            pixelAccelY = maxAccelY;
        }else if(pixelAccelY < -maxAccelY && pixelAccelY < 0){
            pixelAccelY = -maxAccelY;
        }
        System.out.println(pixelAccelX);
        degradeTicks = 10;
    }

    public void pixelAccelDegradation(){
        if(degradeTicks != 0){
            degradeTicks-=1;
        }else{
            if(pixelAccelY != 0){
                if(pixelAccelY > 0){
                    pixelAccelY-=1;
                }else if(pixelAccelY < 0){
                    pixelAccelY+=1;
                }
            }
            if(pixelAccelX != 0){
                if(pixelAccelX > 0){
                    pixelAccelX-=1;
                }else if(pixelAccelX < 0){
                    pixelAccelX+=1;
                }
            }

        }
    }

    public void calculateOffsets(){
        offsetX+= pixelAccelX;
        offsetY+= pixelAccelY;
    }

    @Override
    public void run() {
        while(running){
            step();
            render();
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Gamo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        

    }

    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosing(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
    
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
    
    public void genWorld(){
        for(int x = 0; x < mapWidthX; x++){
            for(int y = 0; y < mapWidthY; y++){
                
            }
        }
    }

    private class KeyListen implements KeyEventDispatcher{
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            int key = e.getKeyCode();
            if(KeyEvent.KEY_PRESSED == e.getID()) {


                if (key == KeyEvent.VK_RIGHT) {
                    System.out.println("RIGHT");
                    if (mapWidthX * tileSize + offsetX >= 0) {
                        calculatePixelAcceleration(-1,0);
                    }

                }
                if (key == KeyEvent.VK_LEFT) {
                    if (offsetX <= 0) {
                        calculatePixelAcceleration(1,0);
                    }
                }
                if (key == KeyEvent.VK_UP) {
                    if (offsetY <= 0) {
                        calculatePixelAcceleration(0,1);
                    }
                }
                if (key == KeyEvent.VK_DOWN) {
                    if (mapWidthY * tileSize + offsetY >= 0) {
                        calculatePixelAcceleration(0,-1);
                    }
                }
                if(key == KeyEvent.VK_MINUS){
                    double hold = tileSize/2;
                    if ((hold == Math.floor(hold)) && !Double.isInfinite(hold)) {
                        tileSize /= 2;

                    }
                }
                if(key == KeyEvent.VK_EQUALS){
                    tileSize*= 2;
                }
            }
            return false;
        }
    }
    

}




