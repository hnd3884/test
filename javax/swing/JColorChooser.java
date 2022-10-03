package javax.swing;

import javax.accessibility.AccessibleRole;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.GraphicsEnvironment;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import java.awt.Window;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ComponentListener;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Component;
import javax.accessibility.AccessibleContext;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.accessibility.Accessible;

public class JColorChooser extends JComponent implements Accessible
{
    private static final String uiClassID = "ColorChooserUI";
    private ColorSelectionModel selectionModel;
    private JComponent previewPanel;
    private AbstractColorChooserPanel[] chooserPanels;
    private boolean dragEnabled;
    public static final String SELECTION_MODEL_PROPERTY = "selectionModel";
    public static final String PREVIEW_PANEL_PROPERTY = "previewPanel";
    public static final String CHOOSER_PANELS_PROPERTY = "chooserPanels";
    protected AccessibleContext accessibleContext;
    
    public static Color showDialog(final Component component, final String s, final Color color) throws HeadlessException {
        final JColorChooser colorChooser = new JColorChooser((color != null) ? color : Color.white);
        final ColorTracker colorTracker = new ColorTracker(colorChooser);
        final JDialog dialog = createDialog(component, s, true, colorChooser, colorTracker, null);
        dialog.addComponentListener(new ColorChooserDialog.DisposeOnClose());
        dialog.show();
        return colorTracker.getColor();
    }
    
    public static JDialog createDialog(final Component component, final String accessibleDescription, final boolean b, final JColorChooser colorChooser, final ActionListener actionListener, final ActionListener actionListener2) throws HeadlessException {
        final Window windowForComponent = JOptionPane.getWindowForComponent(component);
        ColorChooserDialog colorChooserDialog;
        if (windowForComponent instanceof Frame) {
            colorChooserDialog = new ColorChooserDialog((Frame)windowForComponent, accessibleDescription, b, component, colorChooser, actionListener, actionListener2);
        }
        else {
            colorChooserDialog = new ColorChooserDialog((Dialog)windowForComponent, accessibleDescription, b, component, colorChooser, actionListener, actionListener2);
        }
        colorChooserDialog.getAccessibleContext().setAccessibleDescription(accessibleDescription);
        return colorChooserDialog;
    }
    
    public JColorChooser() {
        this(Color.white);
    }
    
    public JColorChooser(final Color color) {
        this(new DefaultColorSelectionModel(color));
    }
    
    public JColorChooser(final ColorSelectionModel selectionModel) {
        this.previewPanel = ColorChooserComponentFactory.getPreviewPanel();
        this.chooserPanels = new AbstractColorChooserPanel[0];
        this.accessibleContext = null;
        this.selectionModel = selectionModel;
        this.updateUI();
        this.dragEnabled = false;
    }
    
    public ColorChooserUI getUI() {
        return (ColorChooserUI)this.ui;
    }
    
    public void setUI(final ColorChooserUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((ColorChooserUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "ColorChooserUI";
    }
    
    public Color getColor() {
        return this.selectionModel.getSelectedColor();
    }
    
    public void setColor(final Color selectedColor) {
        this.selectionModel.setSelectedColor(selectedColor);
    }
    
    public void setColor(final int n, final int n2, final int n3) {
        this.setColor(new Color(n, n2, n3));
    }
    
    public void setColor(final int n) {
        this.setColor(n >> 16 & 0xFF, n >> 8 & 0xFF, n & 0xFF);
    }
    
    public void setDragEnabled(final boolean dragEnabled) {
        if (dragEnabled && GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        this.dragEnabled = dragEnabled;
    }
    
    public boolean getDragEnabled() {
        return this.dragEnabled;
    }
    
    public void setPreviewPanel(final JComponent previewPanel) {
        if (this.previewPanel != previewPanel) {
            this.firePropertyChange("previewPanel", this.previewPanel, this.previewPanel = previewPanel);
        }
    }
    
    public JComponent getPreviewPanel() {
        return this.previewPanel;
    }
    
    public void addChooserPanel(final AbstractColorChooserPanel abstractColorChooserPanel) {
        final AbstractColorChooserPanel[] chooserPanels = this.getChooserPanels();
        final AbstractColorChooserPanel[] chooserPanels2 = new AbstractColorChooserPanel[chooserPanels.length + 1];
        System.arraycopy(chooserPanels, 0, chooserPanels2, 0, chooserPanels.length);
        chooserPanels2[chooserPanels2.length - 1] = abstractColorChooserPanel;
        this.setChooserPanels(chooserPanels2);
    }
    
    public AbstractColorChooserPanel removeChooserPanel(final AbstractColorChooserPanel abstractColorChooserPanel) {
        int n = -1;
        for (int i = 0; i < this.chooserPanels.length; ++i) {
            if (this.chooserPanels[i] == abstractColorChooserPanel) {
                n = i;
                break;
            }
        }
        if (n == -1) {
            throw new IllegalArgumentException("chooser panel not in this chooser");
        }
        final AbstractColorChooserPanel[] chooserPanels = new AbstractColorChooserPanel[this.chooserPanels.length - 1];
        if (n == this.chooserPanels.length - 1) {
            System.arraycopy(this.chooserPanels, 0, chooserPanels, 0, chooserPanels.length);
        }
        else if (n == 0) {
            System.arraycopy(this.chooserPanels, 1, chooserPanels, 0, chooserPanels.length);
        }
        else {
            System.arraycopy(this.chooserPanels, 0, chooserPanels, 0, n);
            System.arraycopy(this.chooserPanels, n + 1, chooserPanels, n, this.chooserPanels.length - n - 1);
        }
        this.setChooserPanels(chooserPanels);
        return abstractColorChooserPanel;
    }
    
    public void setChooserPanels(final AbstractColorChooserPanel[] chooserPanels) {
        this.firePropertyChange("chooserPanels", this.chooserPanels, this.chooserPanels = chooserPanels);
    }
    
    public AbstractColorChooserPanel[] getChooserPanels() {
        return this.chooserPanels;
    }
    
    public ColorSelectionModel getSelectionModel() {
        return this.selectionModel;
    }
    
    public void setSelectionModel(final ColorSelectionModel selectionModel) {
        this.firePropertyChange("selectionModel", this.selectionModel, this.selectionModel = selectionModel);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ColorChooserUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        final StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < this.chooserPanels.length; ++i) {
            sb.append("[" + this.chooserPanels[i].toString() + "]");
        }
        return super.paramString() + ",chooserPanels=" + sb.toString() + ",previewPanel=" + ((this.previewPanel != null) ? this.previewPanel.toString() : "");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJColorChooser();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJColorChooser extends AccessibleJComponent
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.COLOR_CHOOSER;
        }
    }
}
