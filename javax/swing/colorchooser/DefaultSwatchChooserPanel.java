package javax.swing.colorchooser;

import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JLabel;
import java.awt.Insets;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JColorChooser;
import javax.swing.Icon;
import javax.swing.UIManager;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

class DefaultSwatchChooserPanel extends AbstractColorChooserPanel
{
    SwatchPanel swatchPanel;
    RecentSwatchPanel recentSwatchPanel;
    MouseListener mainSwatchListener;
    MouseListener recentSwatchListener;
    private KeyListener mainSwatchKeyListener;
    private KeyListener recentSwatchKeyListener;
    
    public DefaultSwatchChooserPanel() {
        this.setInheritsPopupMenu(true);
    }
    
    @Override
    public String getDisplayName() {
        return UIManager.getString("ColorChooser.swatchesNameText", this.getLocale());
    }
    
    @Override
    public int getMnemonic() {
        return this.getInt("ColorChooser.swatchesMnemonic", -1);
    }
    
    @Override
    public int getDisplayedMnemonicIndex() {
        return this.getInt("ColorChooser.swatchesDisplayedMnemonicIndex", -1);
    }
    
    @Override
    public Icon getSmallDisplayIcon() {
        return null;
    }
    
    @Override
    public Icon getLargeDisplayIcon() {
        return null;
    }
    
    @Override
    public void installChooserPanel(final JColorChooser colorChooser) {
        super.installChooserPanel(colorChooser);
    }
    
    @Override
    protected void buildChooser() {
        final String string = UIManager.getString("ColorChooser.swatchesRecentText", this.getLocale());
        final GridBagLayout gridBagLayout = new GridBagLayout();
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        final JPanel panel = new JPanel(gridBagLayout);
        (this.swatchPanel = new MainSwatchPanel()).putClientProperty("AccessibleName", this.getDisplayName());
        this.swatchPanel.setInheritsPopupMenu(true);
        (this.recentSwatchPanel = new RecentSwatchPanel()).putClientProperty("AccessibleName", string);
        this.mainSwatchKeyListener = new MainSwatchKeyListener();
        this.mainSwatchListener = new MainSwatchListener();
        this.swatchPanel.addMouseListener(this.mainSwatchListener);
        this.swatchPanel.addKeyListener(this.mainSwatchKeyListener);
        this.recentSwatchListener = new RecentSwatchListener();
        this.recentSwatchKeyListener = new RecentSwatchKeyListener();
        this.recentSwatchPanel.addMouseListener(this.recentSwatchListener);
        this.recentSwatchPanel.addKeyListener(this.recentSwatchKeyListener);
        final JPanel panel2 = new JPanel(new BorderLayout());
        final CompoundBorder compoundBorder = new CompoundBorder(new LineBorder(Color.black), new LineBorder(Color.white));
        panel2.setBorder(compoundBorder);
        panel2.add(this.swatchPanel, "Center");
        gridBagConstraints.anchor = 25;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 2;
        final Insets insets = gridBagConstraints.insets;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        panel.add(panel2, gridBagConstraints);
        gridBagConstraints.insets = insets;
        this.recentSwatchPanel.setInheritsPopupMenu(true);
        final JPanel panel3 = new JPanel(new BorderLayout());
        panel3.setBorder(compoundBorder);
        panel3.setInheritsPopupMenu(true);
        panel3.add(this.recentSwatchPanel, "Center");
        final JLabel label = new JLabel(string);
        label.setLabelFor(this.recentSwatchPanel);
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weighty = 1.0;
        panel.add(label, gridBagConstraints);
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridheight = 0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 2);
        panel.add(panel3, gridBagConstraints);
        panel.setInheritsPopupMenu(true);
        this.add(panel);
    }
    
    @Override
    public void uninstallChooserPanel(final JColorChooser colorChooser) {
        super.uninstallChooserPanel(colorChooser);
        this.swatchPanel.removeMouseListener(this.mainSwatchListener);
        this.swatchPanel.removeKeyListener(this.mainSwatchKeyListener);
        this.recentSwatchPanel.removeMouseListener(this.recentSwatchListener);
        this.recentSwatchPanel.removeKeyListener(this.recentSwatchKeyListener);
        this.swatchPanel = null;
        this.recentSwatchPanel = null;
        this.mainSwatchListener = null;
        this.mainSwatchKeyListener = null;
        this.recentSwatchListener = null;
        this.recentSwatchKeyListener = null;
        this.removeAll();
    }
    
    @Override
    public void updateChooser() {
    }
    
    private class RecentSwatchKeyListener extends KeyAdapter
    {
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            if (32 == keyEvent.getKeyCode()) {
                DefaultSwatchChooserPanel.this.setSelectedColor(DefaultSwatchChooserPanel.this.recentSwatchPanel.getSelectedColor());
            }
        }
    }
    
    private class MainSwatchKeyListener extends KeyAdapter
    {
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            if (32 == keyEvent.getKeyCode()) {
                final Color selectedColor = DefaultSwatchChooserPanel.this.swatchPanel.getSelectedColor();
                DefaultSwatchChooserPanel.this.setSelectedColor(selectedColor);
                DefaultSwatchChooserPanel.this.recentSwatchPanel.setMostRecentColor(selectedColor);
            }
        }
    }
    
    class RecentSwatchListener extends MouseAdapter implements Serializable
    {
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (DefaultSwatchChooserPanel.this.isEnabled()) {
                final Color colorForLocation = DefaultSwatchChooserPanel.this.recentSwatchPanel.getColorForLocation(mouseEvent.getX(), mouseEvent.getY());
                DefaultSwatchChooserPanel.this.recentSwatchPanel.setSelectedColorFromLocation(mouseEvent.getX(), mouseEvent.getY());
                DefaultSwatchChooserPanel.this.setSelectedColor(colorForLocation);
                DefaultSwatchChooserPanel.this.recentSwatchPanel.requestFocusInWindow();
            }
        }
    }
    
    class MainSwatchListener extends MouseAdapter implements Serializable
    {
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (DefaultSwatchChooserPanel.this.isEnabled()) {
                final Color colorForLocation = DefaultSwatchChooserPanel.this.swatchPanel.getColorForLocation(mouseEvent.getX(), mouseEvent.getY());
                DefaultSwatchChooserPanel.this.setSelectedColor(colorForLocation);
                DefaultSwatchChooserPanel.this.swatchPanel.setSelectedColorFromLocation(mouseEvent.getX(), mouseEvent.getY());
                DefaultSwatchChooserPanel.this.recentSwatchPanel.setMostRecentColor(colorForLocation);
                DefaultSwatchChooserPanel.this.swatchPanel.requestFocusInWindow();
            }
        }
    }
}
