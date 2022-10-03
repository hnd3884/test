package org.jfree.ui.tabbedui;

import java.beans.PropertyChangeEvent;
import java.awt.Container;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.beans.PropertyChangeListener;
import javax.swing.JApplet;

public class TabbedApplet extends JApplet
{
    private AbstractTabbedUI tabbedUI;
    
    protected final AbstractTabbedUI getTabbedUI() {
        return this.tabbedUI;
    }
    
    public void init(final AbstractTabbedUI tabbedUI) {
        (this.tabbedUI = tabbedUI).addPropertyChangeListener("jMenuBar", new MenuBarChangeListener());
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
                TabbedApplet.this.setJMenuBar(TabbedApplet.this.getTabbedUI().getJMenuBar());
            }
        }
    }
}
