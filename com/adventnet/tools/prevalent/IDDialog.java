package com.adventnet.tools.prevalent;

import java.io.IOException;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Point;
import javax.swing.KeyStroke;
import java.awt.Cursor;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Frame;
import javax.swing.JFrame;
import java.awt.Window;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.MouseListener;
import javax.swing.JWindow;

public class IDDialog extends JWindow implements MouseListener, WindowListener, ActionListener
{
    private JTextArea msgArea;
    private String clickMsg;
    private JLabel clickLabel;
    private String docURL;
    private Window owner;
    private ActionListener actLis;
    
    public IDDialog(final JFrame ownerArg) {
        super(ownerArg);
        this.msgArea = new JTextArea();
        this.clickMsg = "Click here to view complete docs";
        this.clickLabel = new JLabel(this.clickMsg);
        this.docURL = null;
        this.owner = ownerArg;
        this.init();
    }
    
    public void setActionListener(final ActionListener actLisArg) {
        this.actLis = actLisArg;
    }
    
    private void init() {
        this.getContentPane().setBackground(new Color(255, 255, 237));
        ((JComponent)this.getContentPane()).setBorder(BorderFactory.createLineBorder(Color.black, 1));
        this.msgArea.setOpaque(false);
        this.msgArea.setEditable(false);
        this.msgArea.setLineWrap(true);
        this.msgArea.setWrapStyleWord(true);
        this.msgArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        this.getContentPane().add(this.msgArea);
        this.getContentPane().add(this.clickLabel, "South");
        this.addMouseListener(this);
        this.msgArea.addMouseListener(this);
        this.clickLabel.addMouseListener(this);
        this.clickLabel.setCursor(Cursor.getPredefinedCursor(12));
        this.clickLabel.setBackground(this.msgArea.getBackground());
        this.clickLabel.setForeground(Color.blue);
        this.addWindowListener(this);
        final KeyStroke escStroke = KeyStroke.getKeyStroke(27, 0);
        this.getRootPane().registerKeyboardAction(this, " ", escStroke, 2);
        this.setSize(450, 150);
    }
    
    public void showHelpMessage(final String hlpMsgArg, final String docURLArg, final Point point) {
        this.msgArea.setText(hlpMsgArg);
        this.docURL = docURLArg;
        this.clickLabel.setText(this.docURL);
        this.clickLabel.setVisible(this.docURL != null);
        this.showCentered(point);
    }
    
    private void showCentered(final Point point) {
        final Point pt = point;
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension dim = toolkit.getScreenSize();
        final int total = (int)dim.getWidth();
        final int ori = pt.x + 450;
        if (ori > total) {
            final int diff = ori - total;
            pt.x -= diff;
            this.setLocation(pt);
        }
        else {
            this.setLocation(pt);
        }
        this.setVisible(true);
    }
    
    @Override
    public void mousePressed(final MouseEvent evt) {
        final Object obj = evt.getSource();
        if (obj == this.clickLabel) {
            this.displayURL(this.docURL);
        }
        else {
            this.disappear();
        }
    }
    
    public void disappear() {
        this.setVisible(false);
        if (this.actLis != null) {
            this.actLis.actionPerformed(new ActionEvent(this, 0, "DISAPPEARED"));
        }
    }
    
    @Override
    public void actionPerformed(final ActionEvent aEvtArg) {
        this.disappear();
    }
    
    @Override
    public void windowClosing(final WindowEvent e) {
        this.disappear();
        this.dispose();
    }
    
    @Override
    public void windowDeactivated(final WindowEvent e) {
        this.disappear();
    }
    
    @Override
    public void windowOpened(final WindowEvent e) {
    }
    
    @Override
    public void windowClosed(final WindowEvent e) {
    }
    
    @Override
    public void windowIconified(final WindowEvent e) {
    }
    
    @Override
    public void windowDeiconified(final WindowEvent e) {
    }
    
    @Override
    public void windowActivated(final WindowEvent e) {
    }
    
    @Override
    public void mouseExited(final MouseEvent evt) {
    }
    
    @Override
    public void mouseReleased(final MouseEvent evt) {
    }
    
    @Override
    public void mouseClicked(final MouseEvent evt) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent evt) {
    }
    
    private void displayURL(String url) {
        final String WIN_ID = "Windows";
        final String WIN_PATH = "rundll32";
        final String WIN_FLAG = "url.dll,FileProtocolHandler";
        final String UNIX_PATH = "netscape";
        final String UNIX_FLAG = "-remote openURL";
        final boolean windows = this.isWindowsPlatform();
        String cmd = null;
        try {
            if (windows) {
                if (url.startsWith("file")) {
                    url = url.replace('/', '\\');
                    final String curl = url = "file://" + url.substring(7);
                }
                cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
                final Process p = Runtime.getRuntime().exec(cmd);
            }
            else {
                if (url.startsWith("http") || url.startsWith("mailto")) {
                    cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
                }
                else {
                    cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + "file://" + System.getProperty("user.dir") + "/" + url + ")";
                }
                Process p = Runtime.getRuntime().exec(cmd);
                try {
                    final int exitCode = p.waitFor();
                    if (exitCode != 0) {
                        cmd = UNIX_PATH + " " + url;
                        p = Runtime.getRuntime().exec(cmd);
                        final Thread th = new Thread(new BrowserInvoker(p, url));
                        th.setPriority(3);
                        th.start();
                    }
                }
                catch (final InterruptedException x) {
                    System.err.println("Error bringing up browser, cmd='" + cmd + "'");
                    System.err.println("Caught: " + x);
                }
            }
        }
        catch (final IOException x2) {
            System.err.println("Could not invoke browser, command=" + cmd);
            System.err.println("Caught: " + x2);
            final Thread th2 = new Thread(new BrowserInvoker(null, url));
            th2.setPriority(3);
            th2.start();
        }
    }
    
    public boolean isWindowsPlatform() {
        final String os = System.getProperty("os.name");
        return os != null && os.startsWith("Windows");
    }
    
    class BrowserInvoker implements Runnable
    {
        Process p;
        String url;
        
        public BrowserInvoker(final Process pArg, final String urlArg) {
            this.p = pArg;
            this.url = urlArg;
        }
        
        @Override
        public void run() {
            int exitCode = -1;
            if (this.p != null) {
                try {
                    exitCode = this.p.waitFor();
                }
                catch (final Exception e) {
                    System.err.println(e);
                }
            }
        }
    }
}
