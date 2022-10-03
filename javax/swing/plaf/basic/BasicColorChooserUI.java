package javax.swing.plaf.basic;

import java.awt.Container;
import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.plaf.UIResource;
import javax.swing.LookAndFeel;
import java.awt.event.MouseListener;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import sun.swing.DefaultLookup;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.plaf.ComponentUI;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.TransferHandler;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JColorChooser;
import javax.swing.plaf.ColorChooserUI;

public class BasicColorChooserUI extends ColorChooserUI
{
    protected JColorChooser chooser;
    JTabbedPane tabbedPane;
    JPanel singlePanel;
    JPanel previewPanelHolder;
    JComponent previewPanel;
    boolean isMultiPanel;
    private static TransferHandler defaultTransferHandler;
    protected AbstractColorChooserPanel[] defaultChoosers;
    protected ChangeListener previewListener;
    protected PropertyChangeListener propertyChangeListener;
    private Handler handler;
    
    public BasicColorChooserUI() {
        this.isMultiPanel = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicColorChooserUI();
    }
    
    protected AbstractColorChooserPanel[] createDefaultChoosers() {
        return ColorChooserComponentFactory.getDefaultChooserPanels();
    }
    
    protected void uninstallDefaultChoosers() {
        final AbstractColorChooserPanel[] chooserPanels = this.chooser.getChooserPanels();
        for (int i = 0; i < chooserPanels.length; ++i) {
            this.chooser.removeChooserPanel(chooserPanels[i]);
        }
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.chooser = (JColorChooser)component;
        super.installUI(component);
        this.installDefaults();
        this.installListeners();
        (this.tabbedPane = new JTabbedPane()).setName("ColorChooser.tabPane");
        this.tabbedPane.setInheritsPopupMenu(true);
        this.tabbedPane.getAccessibleContext().setAccessibleDescription(this.tabbedPane.getName());
        (this.singlePanel = new JPanel(new CenterLayout())).setName("ColorChooser.panel");
        this.singlePanel.setInheritsPopupMenu(true);
        this.chooser.setLayout(new BorderLayout());
        this.defaultChoosers = this.createDefaultChoosers();
        this.chooser.setChooserPanels(this.defaultChoosers);
        (this.previewPanelHolder = new JPanel(new CenterLayout())).setName("ColorChooser.previewPanelHolder");
        if (DefaultLookup.getBoolean(this.chooser, this, "ColorChooser.showPreviewPanelText", true)) {
            this.previewPanelHolder.setBorder(new TitledBorder(UIManager.getString("ColorChooser.previewText", this.chooser.getLocale())));
        }
        this.previewPanelHolder.setInheritsPopupMenu(true);
        this.installPreviewPanel();
        this.chooser.applyComponentOrientation(component.getComponentOrientation());
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.chooser.remove(this.tabbedPane);
        this.chooser.remove(this.singlePanel);
        this.chooser.remove(this.previewPanelHolder);
        this.uninstallDefaultChoosers();
        this.uninstallListeners();
        this.uninstallPreviewPanel();
        this.uninstallDefaults();
        this.previewPanelHolder = null;
        this.previewPanel = null;
        this.defaultChoosers = null;
        this.chooser = null;
        this.tabbedPane = null;
        this.handler = null;
    }
    
    protected void installPreviewPanel() {
        JComponent previewPanel = this.chooser.getPreviewPanel();
        if (previewPanel == null) {
            previewPanel = ColorChooserComponentFactory.getPreviewPanel();
        }
        else if (JPanel.class.equals(previewPanel.getClass()) && 0 == previewPanel.getComponentCount()) {
            previewPanel = null;
        }
        if ((this.previewPanel = previewPanel) != null) {
            this.chooser.add(this.previewPanelHolder, "South");
            previewPanel.setForeground(this.chooser.getColor());
            this.previewPanelHolder.add(previewPanel);
            previewPanel.addMouseListener(this.getHandler());
            previewPanel.setInheritsPopupMenu(true);
        }
    }
    
    protected void uninstallPreviewPanel() {
        if (this.previewPanel != null) {
            this.previewPanel.removeMouseListener(this.getHandler());
            this.previewPanelHolder.remove(this.previewPanel);
        }
        this.chooser.remove(this.previewPanelHolder);
    }
    
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(this.chooser, "ColorChooser.background", "ColorChooser.foreground", "ColorChooser.font");
        LookAndFeel.installProperty(this.chooser, "opaque", Boolean.TRUE);
        final TransferHandler transferHandler = this.chooser.getTransferHandler();
        if (transferHandler == null || transferHandler instanceof UIResource) {
            this.chooser.setTransferHandler(BasicColorChooserUI.defaultTransferHandler);
        }
    }
    
    protected void uninstallDefaults() {
        if (this.chooser.getTransferHandler() instanceof UIResource) {
            this.chooser.setTransferHandler(null);
        }
    }
    
    protected void installListeners() {
        this.propertyChangeListener = this.createPropertyChangeListener();
        this.chooser.addPropertyChangeListener(this.propertyChangeListener);
        this.previewListener = this.getHandler();
        this.chooser.getSelectionModel().addChangeListener(this.previewListener);
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    protected void uninstallListeners() {
        this.chooser.removePropertyChangeListener(this.propertyChangeListener);
        this.chooser.getSelectionModel().removeChangeListener(this.previewListener);
        this.previewListener = null;
    }
    
    private void selectionChanged(final ColorSelectionModel colorSelectionModel) {
        final JComponent previewPanel = this.chooser.getPreviewPanel();
        if (previewPanel != null) {
            previewPanel.setForeground(colorSelectionModel.getSelectedColor());
            previewPanel.repaint();
        }
        final AbstractColorChooserPanel[] chooserPanels = this.chooser.getChooserPanels();
        if (chooserPanels != null) {
            for (final AbstractColorChooserPanel abstractColorChooserPanel : chooserPanels) {
                if (abstractColorChooserPanel != null) {
                    abstractColorChooserPanel.updateChooser();
                }
            }
        }
    }
    
    static {
        BasicColorChooserUI.defaultTransferHandler = new ColorTransferHandler();
    }
    
    private class Handler implements ChangeListener, MouseListener, PropertyChangeListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            BasicColorChooserUI.this.selectionChanged((ColorSelectionModel)changeEvent.getSource());
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (BasicColorChooserUI.this.chooser.getDragEnabled()) {
                BasicColorChooserUI.this.chooser.getTransferHandler().exportAsDrag(BasicColorChooserUI.this.chooser, mouseEvent, 1);
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName == "chooserPanels") {
                final AbstractColorChooserPanel[] array = (AbstractColorChooserPanel[])propertyChangeEvent.getOldValue();
                final AbstractColorChooserPanel[] array2 = (AbstractColorChooserPanel[])propertyChangeEvent.getNewValue();
                for (int i = 0; i < array.length; ++i) {
                    final Container parent = array[i].getParent();
                    if (parent != null) {
                        final Container parent2 = parent.getParent();
                        if (parent2 != null) {
                            parent2.remove(parent);
                        }
                        array[i].uninstallChooserPanel(BasicColorChooserUI.this.chooser);
                    }
                }
                final int length = array2.length;
                if (length == 0) {
                    BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.tabbedPane);
                    return;
                }
                if (length == 1) {
                    BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.tabbedPane);
                    final JPanel panel = new JPanel(new CenterLayout());
                    panel.setInheritsPopupMenu(true);
                    panel.add(array2[0]);
                    BasicColorChooserUI.this.singlePanel.add(panel, "Center");
                    BasicColorChooserUI.this.chooser.add(BasicColorChooserUI.this.singlePanel);
                }
                else {
                    if (array.length < 2) {
                        BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.singlePanel);
                        BasicColorChooserUI.this.chooser.add(BasicColorChooserUI.this.tabbedPane, "Center");
                    }
                    for (int j = 0; j < array2.length; ++j) {
                        final JPanel panel2 = new JPanel(new CenterLayout());
                        panel2.setInheritsPopupMenu(true);
                        final String displayName = array2[j].getDisplayName();
                        final int mnemonic = array2[j].getMnemonic();
                        panel2.add(array2[j]);
                        BasicColorChooserUI.this.tabbedPane.addTab(displayName, panel2);
                        if (mnemonic > 0) {
                            BasicColorChooserUI.this.tabbedPane.setMnemonicAt(j, mnemonic);
                            final int displayedMnemonicIndex = array2[j].getDisplayedMnemonicIndex();
                            if (displayedMnemonicIndex >= 0) {
                                BasicColorChooserUI.this.tabbedPane.setDisplayedMnemonicIndexAt(j, displayedMnemonicIndex);
                            }
                        }
                    }
                }
                BasicColorChooserUI.this.chooser.applyComponentOrientation(BasicColorChooserUI.this.chooser.getComponentOrientation());
                for (int k = 0; k < array2.length; ++k) {
                    array2[k].installChooserPanel(BasicColorChooserUI.this.chooser);
                }
            }
            else if (propertyName == "previewPanel") {
                BasicColorChooserUI.this.uninstallPreviewPanel();
                BasicColorChooserUI.this.installPreviewPanel();
            }
            else if (propertyName == "selectionModel") {
                ((ColorSelectionModel)propertyChangeEvent.getOldValue()).removeChangeListener(BasicColorChooserUI.this.previewListener);
                final ColorSelectionModel colorSelectionModel = (ColorSelectionModel)propertyChangeEvent.getNewValue();
                colorSelectionModel.addChangeListener(BasicColorChooserUI.this.previewListener);
                BasicColorChooserUI.this.selectionChanged(colorSelectionModel);
            }
            else if (propertyName == "componentOrientation") {
                final ComponentOrientation componentOrientation = (ComponentOrientation)propertyChangeEvent.getNewValue();
                final JColorChooser colorChooser = (JColorChooser)propertyChangeEvent.getSource();
                if (componentOrientation != propertyChangeEvent.getOldValue()) {
                    colorChooser.applyComponentOrientation(componentOrientation);
                    colorChooser.updateUI();
                }
            }
        }
    }
    
    public class PropertyHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicColorChooserUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    static class ColorTransferHandler extends TransferHandler implements UIResource
    {
        ColorTransferHandler() {
            super("color");
        }
    }
}
