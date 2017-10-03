package com.tram98.chip8interpreter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Window extends JFrame
{
    private JPanel contentPane;
    private Screen screen;

    public Window(String title)
    {
        super(title);

        initComponents();                           //initialize all components
        getContentPane().setPreferredSize(Config.size);
        setResizable(false);                        //remove resizing option
        setLocationRelativeTo(null);                //Center the window
        setDefaultCloseOperation(3);                //3=EXIT_ON_CLOSE
        add(screen);                                //add the panel to the window
        pack();
        screen.setBackground(Color.BLACK);          //set background black
        setVisible(true);                           //make the window visible
    }

    private void initComponents()
    {
        screen = new Screen();
        contentPane = new JPanel();
    }

    public void draw(BufferedImage img)
    {
        screen.redraw(img);           //does this work???
    }

    private class Screen extends JPanel
    {
        private BufferedImage img;
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawImage(img,0,0,Window.this.getWidth(),Window.this.getHeight(),null);
        }

        public void redraw(BufferedImage img)
        {
            this.img=img;
            repaint();
        }
    }
}
