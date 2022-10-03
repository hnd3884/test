package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.Icon;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class MetalDesktopIconUI extends BasicDesktopIconUI
{
    JButton button;
    JLabel label;
    TitleListener titleListener;
    private int width;
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalDesktopIconUI();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        LookAndFeel.installColorsAndFont(this.desktopIcon, "DesktopIcon.background", "DesktopIcon.foreground", "DesktopIcon.font");
        this.width = UIManager.getInt("DesktopIcon.width");
    }
    
    @Override
    protected void installComponents() {
        this.frame = this.desktopIcon.getInternalFrame();
        (this.button = new JButton(this.frame.getTitle(), this.frame.getFrameIcon())).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                MetalDesktopIconUI.this.deiconize();
            }
        });
        this.button.setFont(this.desktopIcon.getFont());
        this.button.setBackground(this.desktopIcon.getBackground());
        this.button.setForeground(this.desktopIcon.getForeground());
        final int height = this.button.getPreferredSize().height;
        (this.label = new JLabel(new MetalBumps(height / 3, height, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl()))).setBorder(new MatteBorder(0, 2, 0, 1, this.desktopIcon.getBackground()));
        this.desktopIcon.setLayout(new BorderLayout(2, 0));
        this.desktopIcon.add(this.button, "Center");
        this.desktopIcon.add(this.label, "West");
    }
    
    @Override
    protected void uninstallComponents() {
        this.desktopIcon.setLayout(null);
        this.desktopIcon.remove(this.label);
        this.desktopIcon.remove(this.button);
        this.button = null;
        this.frame = null;
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.desktopIcon.getInternalFrame().addPropertyChangeListener(this.titleListener = new TitleListener());
    }
    
    @Override
    protected void uninstallListeners() {
        this.desktopIcon.getInternalFrame().removePropertyChangeListener(this.titleListener);
        this.titleListener = null;
        super.uninstallListeners();
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return this.getMinimumSize(component);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        return new Dimension(this.width, this.desktopIcon.getLayout().minimumLayoutSize(this.desktopIcon).height);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return this.getMinimumSize(component);
    }
    
    class TitleListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("title")) {
                MetalDesktopIconUI.this.button.setText((String)propertyChangeEvent.getNewValue());
            }
            if (propertyChangeEvent.getPropertyName().equals("frameIcon")) {
                MetalDesktopIconUI.this.button.setIcon((Icon)propertyChangeEvent.getNewValue());
            }
        }
    }
}
