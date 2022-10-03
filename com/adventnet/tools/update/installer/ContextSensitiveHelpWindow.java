package com.adventnet.tools.update.installer;

import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.Point;
import javax.swing.KeyStroke;
import java.awt.Cursor;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.JDialog;
import java.awt.Frame;
import javax.swing.JFrame;
import java.util.ResourceBundle;
import java.awt.Window;
import java.net.URL;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.MouseListener;
import javax.swing.JWindow;

public final class ContextSensitiveHelpWindow extends JWindow implements MouseListener, WindowListener, ActionListener, Runnable
{
    private JTextArea msgArea;
    String clickMsg;
    private JLabel clickLabel;
    URL docURL;
    Thread runTh;
    Window owner;
    private ActionListener actLis;
    private ResourceBundle myBundle;
    
    public ContextSensitiveHelpWindow(final JFrame ownerArg) {
        this(ownerArg, null);
    }
    
    public ContextSensitiveHelpWindow(final JFrame ownerArg, final ResourceBundle bundle) {
        super(ownerArg);
        this.msgArea = new JTextArea();
        this.clickMsg = "Click here to view complete docs";
        this.clickLabel = new JLabel(this.clickMsg);
        this.docURL = null;
        this.myBundle = null;
        this.owner = ownerArg;
        this.init();
    }
    
    public ContextSensitiveHelpWindow(final JDialog ownerArg) {
        this(ownerArg, null);
    }
    
    public ContextSensitiveHelpWindow(final JDialog ownerArg, final ResourceBundle bundle) {
        super(ownerArg);
        this.msgArea = new JTextArea();
        this.clickMsg = "Click here to view complete docs";
        this.clickLabel = new JLabel(this.clickMsg);
        this.docURL = null;
        this.myBundle = null;
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
    
    public void showHelpMessage(final String hlpMsgArg, final URL docURLArg) {
        this.msgArea.setText(hlpMsgArg);
        this.docURL = docURLArg;
        this.clickLabel.setText(this.getString("Click here to view the complete docs"));
        this.clickLabel.setVisible(this.docURL != null);
        this.showCentered();
    }
    
    private void showCentered() {
        final Rectangle rect = this.owner.getBounds();
        final Point point;
        final Point pt = point = new Point((this.owner.getWidth() - this.getWidth()) / 2, (this.owner.getHeight() - this.getHeight()) / 2);
        point.x += rect.x;
        final Point point2 = pt;
        point2.y += rect.y;
        this.setLocation(pt);
        this.setVisible(true);
    }
    
    @Override
    public void mousePressed(final MouseEvent evt) {
        final Object obj = evt.getSource();
        if (obj == this.clickLabel) {
            this.runTh = new Thread(this);
            this.clickLabel.setText("Please wait...loading");
            this.runTh.start();
        }
        else {
            this.disappear(true);
        }
    }
    
    @Override
    public void run() {
        if (!BrowserControl.displayURL(this.docURL.toString())) {
            this.clickLabel.setText("Unable open " + this.docURL.toString());
        }
        else {
            this.clickLabel.setText("Finished");
            this.disappear(true);
            this.dispose();
        }
    }
    
    public static void main(final String[] args) throws Exception {
        final JFrame jf = new JFrame();
        jf.setSize(200, 200);
        jf.setVisible(true);
        final ContextSensitiveHelpWindow win = new ContextSensitiveHelpWindow(jf);
    }
    
    public void disappear(final boolean killThreadArg) {
        this.setVisible(false);
        if (killThreadArg && this.runTh != null && this.runTh.isAlive()) {
            this.runTh.stop();
            this.runTh = null;
        }
        if (this.actLis != null) {
            this.actLis.actionPerformed(new ActionEvent(this, 0, "DISAPPEARED"));
        }
    }
    
    @Override
    public void actionPerformed(final ActionEvent aEvtArg) {
        this.disappear(true);
    }
    
    @Override
    public void windowClosing(final WindowEvent e) {
        this.disappear(true);
        this.dispose();
    }
    
    @Override
    public void windowDeactivated(final WindowEvent e) {
        this.disappear(true);
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
    
    private String getString(final String key) {
        String ret = null;
        if (this.myBundle != null) {
            try {
                ret = this.myBundle.getString(key);
            }
            catch (final Exception ex) {}
        }
        if (ret != null && !ret.equals("")) {
            return ret;
        }
        return key.trim();
    }
}
