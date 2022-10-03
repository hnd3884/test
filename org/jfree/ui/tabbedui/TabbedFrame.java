package org.jfree.ui.tabbedui;

import java.beans.PropertyChangeEvent;
import java.awt.Container;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;

public class TabbedFrame extends JFrame
{
    private AbstractTabbedUI tabbedUI;
    
    public TabbedFrame() {
    }
    
    public TabbedFrame(final String title) {
        super(title);
    }
    
    protected final AbstractTabbedUI getTabbedUI() {
        return this.tabbedUI;
    }
    
    public void init(final AbstractTabbedUI tabbedUI) {
        (this.tabbedUI = tabbedUI).addPropertyChangeListener("jMenuBar", new MenuBarChangeListener());
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                TabbedFrame.this.getTabbedUI().getCloseAction().actionPerformed(new ActionEvent(this, 1001, null, 0));
            }
        });
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(tabbedUI, "Center");
        this.setContentPane(panel);
        this.setJMenuBar(tabbedUI.getJMenuBar());
    }
    
    private class MenuBarChangeListener implements PropertyChangeListener
    {
        public MenuBarChangeListener() {
        }
        
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("jMenuBar")) {
                TabbedFrame.this.setJMenuBar(TabbedFrame.this.getTabbedUI().getJMenuBar());
            }
        }
    }
}
