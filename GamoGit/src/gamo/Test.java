package gamo;

import java.awt.*;
import java.awt.event.*;

public class Test {
    private Frame mainFrame;
    private Label headerLabel;
    private Label statusLabel;
    private Panel controlPanel;
    private TextField textField;

    public Test(){
        prepareGUI();
    }

    public static void main(String[] args){
        Test awtListenerDemo = new Test();
        awtListenerDemo.showKeyListenerDemo();
    }

    private void prepareGUI(){
        mainFrame = new Frame("Java AWT Examples");
        mainFrame.setSize(400,400);
        mainFrame.setLayout(new GridLayout(3, 1));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        headerLabel = new Label();
        headerLabel.setAlignment(Label.CENTER);
        statusLabel = new Label();
        statusLabel.setAlignment(Label.CENTER);
        statusLabel.setSize(350,100);

        controlPanel = new Panel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }

    private void showKeyListenerDemo(){
        headerLabel.setText("Listener in action: KeyListener");

        textField  = new TextField(10);

        textField.addKeyListener(new CustomKeyListener());
        Button okButton = new Button("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Entered text: " + textField.getText());
            }
        });

        controlPanel.add(textField);
        controlPanel.add(okButton);
        mainFrame.setVisible(true);
    }

    class CustomKeyListener implements KeyListener{
        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER){
                statusLabel.setText("Entered text: " + textField.getText());
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    }
}