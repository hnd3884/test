package com.adventnet.telnet.telnetwindow;

import javax.swing.JApplet;

public class TelnetApplet extends JApplet
{
    TelnetFrame frame;
    
    public void init() {
        (this.frame = new TelnetFrame()).init(this);
        this.frame.setVisible(true);
    }
    
    public void start() {
        this.frame.start();
    }
    
    public void stop() {
        this.frame.stop();
    }
}
