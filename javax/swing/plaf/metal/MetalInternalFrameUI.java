package javax.swing.plaf.metal;

import java.awt.event.MouseEvent;
import javax.swing.Icon;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.SwingConstants;
import java.beans.PropertyChangeEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.event.MouseInputAdapter;
import javax.swing.LookAndFeel;
import javax.swing.plaf.UIResource;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import java.awt.Container;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.border.Border;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class MetalInternalFrameUI extends BasicInternalFrameUI
{
    private static final PropertyChangeListener metalPropertyChangeListener;
    private static final Border handyEmptyBorder;
    protected static String IS_PALETTE;
    private static String IS_PALETTE_KEY;
    private static String FRAME_TYPE;
    private static String NORMAL_FRAME;
    private static String PALETTE_FRAME;
    private static String OPTION_DIALOG;
    
    public MetalInternalFrameUI(final JInternalFrame internalFrame) {
        super(internalFrame);
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalInternalFrameUI((JInternalFrame)component);
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        final Object clientProperty = component.getClientProperty(MetalInternalFrameUI.IS_PALETTE_KEY);
        if (clientProperty != null) {
            this.setPalette((boolean)clientProperty);
        }
        this.stripContentBorder(this.frame.getContentPane());
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.frame = (JInternalFrame)component;
        final Container contentPane = ((JInternalFrame)component).getContentPane();
        if (contentPane instanceof JComponent) {
            final JComponent component2 = (JComponent)contentPane;
            if (component2.getBorder() == MetalInternalFrameUI.handyEmptyBorder) {
                component2.setBorder(null);
            }
        }
        super.uninstallUI(component);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.frame.addPropertyChangeListener(MetalInternalFrameUI.metalPropertyChangeListener);
    }
    
    @Override
    protected void uninstallListeners() {
        this.frame.removePropertyChangeListener(MetalInternalFrameUI.metalPropertyChangeListener);
        super.uninstallListeners();
    }
    
    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();
        final ActionMap uiActionMap = SwingUtilities.getUIActionMap(this.frame);
        if (uiActionMap != null) {
            uiActionMap.remove("showSystemMenu");
        }
    }
    
    @Override
    protected void uninstallKeyboardActions() {
        super.uninstallKeyboardActions();
    }
    
    @Override
    protected void uninstallComponents() {
        this.titlePane = null;
        super.uninstallComponents();
    }
    
    private void stripContentBorder(final Object o) {
        if (o instanceof JComponent) {
            final JComponent component = (JComponent)o;
            final Border border = component.getBorder();
            if (border == null || border instanceof UIResource) {
                component.setBorder(MetalInternalFrameUI.handyEmptyBorder);
            }
        }
    }
    
    @Override
    protected JComponent createNorthPane(final JInternalFrame internalFrame) {
        return new MetalInternalFrameTitlePane(internalFrame);
    }
    
    private void setFrameType(final String s) {
        if (s.equals(MetalInternalFrameUI.OPTION_DIALOG)) {
            LookAndFeel.installBorder(this.frame, "InternalFrame.optionDialogBorder");
            ((MetalInternalFrameTitlePane)this.titlePane).setPalette(false);
        }
        else if (s.equals(MetalInternalFrameUI.PALETTE_FRAME)) {
            LookAndFeel.installBorder(this.frame, "InternalFrame.paletteBorder");
            ((MetalInternalFrameTitlePane)this.titlePane).setPalette(true);
        }
        else {
            LookAndFeel.installBorder(this.frame, "InternalFrame.border");
            ((MetalInternalFrameTitlePane)this.titlePane).setPalette(false);
        }
    }
    
    public void setPalette(final boolean palette) {
        if (palette) {
            LookAndFeel.installBorder(this.frame, "InternalFrame.paletteBorder");
        }
        else {
            LookAndFeel.installBorder(this.frame, "InternalFrame.border");
        }
        ((MetalInternalFrameTitlePane)this.titlePane).setPalette(palette);
    }
    
    @Override
    protected MouseInputAdapter createBorderListener(final JInternalFrame internalFrame) {
        return new BorderListener1();
    }
    
    static {
        metalPropertyChangeListener = new MetalPropertyChangeHandler();
        handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
        MetalInternalFrameUI.IS_PALETTE = "JInternalFrame.isPalette";
        MetalInternalFrameUI.IS_PALETTE_KEY = "JInternalFrame.isPalette";
        MetalInternalFrameUI.FRAME_TYPE = "JInternalFrame.frameType";
        MetalInternalFrameUI.NORMAL_FRAME = "normal";
        MetalInternalFrameUI.PALETTE_FRAME = "palette";
        MetalInternalFrameUI.OPTION_DIALOG = "optionDialog";
    }
    
    private static class MetalPropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            final JInternalFrame internalFrame = (JInternalFrame)propertyChangeEvent.getSource();
            if (!(internalFrame.getUI() instanceof MetalInternalFrameUI)) {
                return;
            }
            final MetalInternalFrameUI metalInternalFrameUI = (MetalInternalFrameUI)internalFrame.getUI();
            if (propertyName.equals(MetalInternalFrameUI.FRAME_TYPE)) {
                if (propertyChangeEvent.getNewValue() instanceof String) {
                    metalInternalFrameUI.setFrameType((String)propertyChangeEvent.getNewValue());
                }
            }
            else if (propertyName.equals(MetalInternalFrameUI.IS_PALETTE_KEY)) {
                if (propertyChangeEvent.getNewValue() != null) {
                    metalInternalFrameUI.setPalette((boolean)propertyChangeEvent.getNewValue());
                }
                else {
                    metalInternalFrameUI.setPalette(false);
                }
            }
            else if (propertyName.equals("contentPane")) {
                metalInternalFrameUI.stripContentBorder(propertyChangeEvent.getNewValue());
            }
        }
    }
    
    private class BorderListener1 extends BorderListener implements SwingConstants
    {
        Rectangle getIconBounds() {
            final boolean leftToRight = MetalUtils.isLeftToRight(MetalInternalFrameUI.this.frame);
            int n = leftToRight ? 5 : (MetalInternalFrameUI.this.titlePane.getWidth() - 5);
            Rectangle rectangle = null;
            final Icon frameIcon = MetalInternalFrameUI.this.frame.getFrameIcon();
            if (frameIcon != null) {
                if (!leftToRight) {
                    n -= frameIcon.getIconWidth();
                }
                rectangle = new Rectangle(n, MetalInternalFrameUI.this.titlePane.getHeight() / 2 - frameIcon.getIconHeight() / 2, frameIcon.getIconWidth(), frameIcon.getIconHeight());
            }
            return rectangle;
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 2 && mouseEvent.getSource() == MetalInternalFrameUI.this.getNorthPane() && MetalInternalFrameUI.this.frame.isClosable() && !MetalInternalFrameUI.this.frame.isIcon()) {
                final Rectangle iconBounds = this.getIconBounds();
                if (iconBounds != null && iconBounds.contains(mouseEvent.getX(), mouseEvent.getY())) {
                    MetalInternalFrameUI.this.frame.doDefaultCloseAction();
                }
                else {
                    super.mouseClicked(mouseEvent);
                }
            }
            else {
                super.mouseClicked(mouseEvent);
            }
        }
    }
}
