package gamo;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;


public class ColorPanel extends Canvas {
    int offsetX = 0;
    int offsetY = 0;
    int pixelAccelX = 0;
    int pixelAccelY = 0;
    int maxAccelX = 4;
    int maxAccelY = 4;
    int tileSize = 16;
    int mapWidthX = 500;
    int mapWidthY = 500;
    int fps=0;
    BufferStrategy bs = null;
    Color[][] colorMap = new Color[mapWidthX][mapWidthY];

    public ColorPanel(Frame f){
        bs = f.getBufferStrategy();
        Random r = new Random();

        for(int x = 0; x < mapWidthX; x++){
            for(int y = 0; y < mapWidthY; y++){
                colorMap[x][y]= new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
            }
        }
        
        //setBackground(Color.pink);
    }
    
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        outerloop:
        for(int x = 0; x < mapWidthX; x++){
            for(int y = 0; y < mapWidthY; y++){
                if(tileSize * x >= -offsetX -2 * tileSize){
                    if(tileSize * y >= -offsetY - 2 * tileSize && tileSize * y <= -offsetY + getSize().getHeight()){
                        if(tileSize * x >= -offsetX + getSize().getWidth()){
                            break outerloop;
                        }
                        g2d.setColor(colorMap[x][y]);
                        g2d.fillRect(tileSize * x + offsetX, tileSize * y + offsetY, tileSize, tileSize);
                    }
                }
            }
        }
        g2d.drawString( ""+fps, 40 , 40);
        g2d.dispose();

    }
    public void render(){
        Graphics2D bkG = (Graphics2D) bs.getDrawGraphics();
        
        outerloop:
        for(int x = 0; x < mapWidthX; x++){
            for(int y = 0; y < mapWidthY; y++){
                if(tileSize * x >= -offsetX -2 * tileSize){
                    if(tileSize * y >= -offsetY - 2 * tileSize && tileSize * y <= -offsetY + getSize().getHeight()){
                        if(tileSize * x >= -offsetX + getSize().getWidth()){
                            break outerloop;
                        }
                        bkG.setColor(colorMap[x][y]);
                        bkG.fillRect(tileSize * x + offsetX, tileSize * y + offsetY, tileSize, tileSize);
                    }
                }
            }
        }
        bkG.drawString( ""+fps, 40 , 40);
        
        bkG.dispose();
        bkG.dispose();
        bs.show();

        Toolkit.getDefaultToolkit().sync();
    }
    
    public void feedBS(BufferStrategy bufferStrategy){
        bs = bufferStrategy;
    }

    public void setfps(int fps){
        this.fps = fps;
    }
}
