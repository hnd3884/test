package com.adventnet.mfw;

import java.awt.image.ImageObserver;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.Color;
import java.awt.Font;
import com.zoho.conf.Configuration;
import com.zoho.conf.AppResources;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

public class SplashScreen extends JWindow
{
    protected JProgressBar jp;
    protected StartupPanel jpa;
    protected JFrame parentFrame;
    protected boolean showProgess;
    
    public SplashScreen(final JFrame parent, final String ImageLocArg) {
        this(parent, ImageLocArg, false);
    }
    
    public SplashScreen(final JFrame parent, final String ImageLocArg, final boolean showprogbar) {
        super(parent);
        this.showProgess = false;
        this.parentFrame = parent;
        this.showProgess = showprogbar;
        if (this.showProgess) {
            (this.jp = new JProgressBar()).setOrientation(0);
            final Font font = new Font(AppResources.getString("splashscreen.font.name", "arial"), 1, Configuration.getInteger("splashscreen.font.size", 10));
            this.jp.setFont(font);
            this.jp.setStringPainted(true);
            this.jp.setForeground(new Color(AppResources.getInteger("splashscreen.progress.color", Integer.valueOf(173))));
            final BasicProgressBarUI ui = new BasicProgressBarUI() {
                @Override
                protected Color getSelectionBackground() {
                    return new Color(AppResources.getInteger("splashscreen.fontforeground.color", Integer.valueOf(7894)));
                }
                
                @Override
                protected Color getSelectionForeground() {
                    return new Color(AppResources.getInteger("splashscreen.fontbackground.color", Integer.valueOf(255255)));
                }
            };
            this.jp.setUI(ui);
        }
        this.jpa = new StartupPanel(ImageLocArg, this.showProgess);
        this.getContentPane().setLayout(new BorderLayout(0, 0));
        if (this.showProgess) {
            this.setBounds(100, 103, this.jpa.getWidth(), this.jpa.getHeight() + this.jp.getHeight());
            this.getContentPane().add(this.jp, "South");
        }
        else {
            this.setBounds(100, 103, this.jpa.getWidth(), this.jpa.getHeight());
        }
        this.getContentPane().add(this.jpa, "Center");
        this.addWindowListener(new WindowAdapter() {});
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width / 2 - this.getBounds().width / 2, screenSize.height / 2 - this.getBounds().height / 2);
    }
    
    public void showString(final String showStrArg) {
        if (this.showProgess) {
            this.jp.setString(showStrArg);
        }
    }
    
    public void showProgress(final int progress) {
        if (this.showProgess) {
            this.jp.setValue(progress);
        }
    }
    
    static ImageIcon createImageIcon(final String imageName) {
        final ImageIcon im = new ImageIcon(imageName);
        return im;
    }
    
    class StartupPanel extends JPanel
    {
        protected static final int BUFHEIGHT = 40;
        protected ImageIcon aboutIcon;
        protected int iHeight;
        protected int iWidth;
        protected String showstr;
        
        public StartupPanel(final String ImageLocArg, final boolean bool) {
            this.aboutIcon = null;
            try {
                this.showstr = "";
                this.aboutIcon = SplashScreen.createImageIcon(ImageLocArg);
                this.iHeight = this.aboutIcon.getIconHeight();
                this.setSize(this.iWidth = this.aboutIcon.getIconWidth(), this.iHeight);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            if (this.aboutIcon.getImage() != null) {
                g.drawImage(this.aboutIcon.getImage(), 0, 0, this.iWidth, this.iHeight, this);
            }
            g.drawString(this.showstr, 0, this.iHeight - 20);
        }
    }
}
