package com.tram98.chip8interpreter;

import java.awt.image.BufferedImage;

public class Main
{
    public static void main(String[] args)
    {
        Window window = new Window("Chip8 Interpreter");
        BufferedImage img = new BufferedImage(64,32,BufferedImage.TYPE_BYTE_BINARY);
        Chip8 chip = new Chip8();
    }
}
