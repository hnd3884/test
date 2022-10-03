package javax.swing.plaf.basic;

import javax.accessibility.AccessibleContext;
import javax.swing.ImageIcon;
import javax.swing.plaf.ComponentUI;
import sun.swing.DefaultLookup;
import java.beans.PropertyVetoException;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.awt.LayoutManager;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import javax.swing.event.InternalFrameEvent;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.ActionMap;
import java.awt.Component;
import sun.swing.SwingUtilities2;
import javax.swing.Action;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import java.awt.Color;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JComponent;

public class BasicInternalFrameTitlePane extends JComponent
{
    protected JMenuBar menuBar;
    protected JButton iconButton;
    protected JButton maxButton;
    protected JButton closeButton;
    protected JMenu windowMenu;
    protected JInternalFrame frame;
    protected Color selectedTitleColor;
    protected Color selectedTextColor;
    protected Color notSelectedTitleColor;
    protected Color notSelectedTextColor;
    protected Icon maxIcon;
    protected Icon minIcon;
    protected Icon iconIcon;
    protected Icon closeIcon;
    protected PropertyChangeListener propertyChangeListener;
    protected Action closeAction;
    protected Action maximizeAction;
    protected Action iconifyAction;
    protected Action restoreAction;
    protected Action moveAction;
    protected Action sizeAction;
    protected static final String CLOSE_CMD;
    protected static final String ICONIFY_CMD;
    protected static final String RESTORE_CMD;
    protected static final String MAXIMIZE_CMD;
    protected static final String MOVE_CMD;
    protected static final String SIZE_CMD;
    private String closeButtonToolTip;
    private String iconButtonToolTip;
    private String restoreButtonToolTip;
    private String maxButtonToolTip;
    private Handler handler;
    
    public BasicInternalFrameTitlePane(final JInternalFrame frame) {
        this.frame = frame;
        this.installTitlePane();
    }
    
    protected void installTitlePane() {
        this.installDefaults();
        this.installListeners();
        this.createActions();
        this.enableActions();
        this.createActionMap();
        this.setLayout(this.createLayout());
        this.assembleSystemMenu();
        this.createButtons();
        this.addSubComponents();
        this.updateProperties();
    }
    
    private void updateProperties() {
        this.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, this.frame.getClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY));
    }
    
    protected void addSubComponents() {
        this.add(this.menuBar);
        this.add(this.iconButton);
        this.add(this.maxButton);
        this.add(this.closeButton);
    }
    
    protected void createActions() {
        this.maximizeAction = new MaximizeAction();
        this.iconifyAction = new IconifyAction();
        this.closeAction = new CloseAction();
        this.restoreAction = new RestoreAction();
        this.moveAction = new MoveAction();
        this.sizeAction = new SizeAction();
    }
    
    ActionMap createActionMap() {
        final ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
        actionMapUIResource.put("showSystemMenu", new ShowSystemMenuAction(true));
        actionMapUIResource.put("hideSystemMenu", new ShowSystemMenuAction(false));
        return actionMapUIResource;
    }
    
    protected void installListeners() {
        if (this.propertyChangeListener == null) {
            this.propertyChangeListener = this.createPropertyChangeListener();
        }
        this.frame.addPropertyChangeListener(this.propertyChangeListener);
    }
    
    protected void uninstallListeners() {
        this.frame.removePropertyChangeListener(this.propertyChangeListener);
        this.handler = null;
    }
    
    protected void installDefaults() {
        this.maxIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
        this.minIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
        this.iconIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
        this.closeIcon = UIManager.getIcon("InternalFrame.closeIcon");
        this.selectedTitleColor = UIManager.getColor("InternalFrame.activeTitleBackground");
        this.selectedTextColor = UIManager.getColor("InternalFrame.activeTitleForeground");
        this.notSelectedTitleColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");
        this.notSelectedTextColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
        this.setFont(UIManager.getFont("InternalFrame.titleFont"));
        this.closeButtonToolTip = UIManager.getString("InternalFrame.closeButtonToolTip");
        this.iconButtonToolTip = UIManager.getString("InternalFrame.iconButtonToolTip");
        this.restoreButtonToolTip = UIManager.getString("InternalFrame.restoreButtonToolTip");
        this.maxButtonToolTip = UIManager.getString("InternalFrame.maxButtonToolTip");
    }
    
    protected void uninstallDefaults() {
    }
    
    protected void createButtons() {
        (this.iconButton = new NoFocusButton("InternalFrameTitlePane.iconifyButtonAccessibleName", "InternalFrameTitlePane.iconifyButtonOpacity")).addActionListener(this.iconifyAction);
        if (this.iconButtonToolTip != null && this.iconButtonToolTip.length() != 0) {
            this.iconButton.setToolTipText(this.iconButtonToolTip);
        }
        (this.maxButton = new NoFocusButton("InternalFrameTitlePane.maximizeButtonAccessibleName", "InternalFrameTitlePane.maximizeButtonOpacity")).addActionListener(this.maximizeAction);
        (this.closeButton = new NoFocusButton("InternalFrameTitlePane.closeButtonAccessibleName", "InternalFrameTitlePane.closeButtonOpacity")).addActionListener(this.closeAction);
        if (this.closeButtonToolTip != null && this.closeButtonToolTip.length() != 0) {
            this.closeButton.setToolTipText(this.closeButtonToolTip);
        }
        this.setButtonIcons();
    }
    
    protected void setButtonIcons() {
        if (this.frame.isIcon()) {
            if (this.minIcon != null) {
                this.iconButton.setIcon(this.minIcon);
            }
            if (this.restoreButtonToolTip != null && this.restoreButtonToolTip.length() != 0) {
                this.iconButton.setToolTipText(this.restoreButtonToolTip);
            }
            if (this.maxIcon != null) {
                this.maxButton.setIcon(this.maxIcon);
            }
            if (this.maxButtonToolTip != null && this.maxButtonToolTip.length() != 0) {
                this.maxButton.setToolTipText(this.maxButtonToolTip);
            }
        }
        else if (this.frame.isMaximum()) {
            if (this.iconIcon != null) {
                this.iconButton.setIcon(this.iconIcon);
            }
            if (this.iconButtonToolTip != null && this.iconButtonToolTip.length() != 0) {
                this.iconButton.setToolTipText(this.iconButtonToolTip);
            }
            if (this.minIcon != null) {
                this.maxButton.setIcon(this.minIcon);
            }
            if (this.restoreButtonToolTip != null && this.restoreButtonToolTip.length() != 0) {
                this.maxButton.setToolTipText(this.restoreButtonToolTip);
            }
        }
        else {
            if (this.iconIcon != null) {
                this.iconButton.setIcon(this.iconIcon);
            }
            if (this.iconButtonToolTip != null && this.iconButtonToolTip.length() != 0) {
                this.iconButton.setToolTipText(this.iconButtonToolTip);
            }
            if (this.maxIcon != null) {
                this.maxButton.setIcon(this.maxIcon);
            }
            if (this.maxButtonToolTip != null && this.maxButtonToolTip.length() != 0) {
                this.maxButton.setToolTipText(this.maxButtonToolTip);
            }
        }
        if (this.closeIcon != null) {
            this.closeButton.setIcon(this.closeIcon);
        }
    }
    
    protected void assembleSystemMenu() {
        this.menuBar = this.createSystemMenuBar();
        this.windowMenu = this.createSystemMenu();
        this.menuBar.add(this.windowMenu);
        this.addSystemMenuItems(this.windowMenu);
        this.enableActions();
    }
    
    protected void addSystemMenuItems(final JMenu menu) {
        menu.add(this.restoreAction).setMnemonic(getButtonMnemonic("restore"));
        menu.add(this.moveAction).setMnemonic(getButtonMnemonic("move"));
        menu.add(this.sizeAction).setMnemonic(getButtonMnemonic("size"));
        menu.add(this.iconifyAction).setMnemonic(getButtonMnemonic("minimize"));
        menu.add(this.maximizeAction).setMnemonic(getButtonMnemonic("maximize"));
        menu.add(new JSeparator());
        menu.add(this.closeAction).setMnemonic(getButtonMnemonic("close"));
    }
    
    private static int getButtonMnemonic(final String s) {
        try {
            return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + s + "Button.mnemonic"));
        }
        catch (final NumberFormatException ex) {
            return -1;
        }
    }
    
    protected JMenu createSystemMenu() {
        return new JMenu("    ");
    }
    
    protected JMenuBar createSystemMenuBar() {
        (this.menuBar = new SystemMenuBar()).setBorderPainted(false);
        return this.menuBar;
    }
    
    protected void showSystemMenu() {
        this.windowMenu.doClick();
    }
    
    public void paintComponent(final Graphics graphics) {
        this.paintTitleBackground(graphics);
        if (this.frame.getTitle() != null) {
            final boolean selected = this.frame.isSelected();
            final Font font = graphics.getFont();
            graphics.setFont(this.getFont());
            if (selected) {
                graphics.setColor(this.selectedTextColor);
            }
            else {
                graphics.setColor(this.notSelectedTextColor);
            }
            final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.frame, graphics);
            final int n = (this.getHeight() + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2;
            Rectangle rectangle = new Rectangle(0, 0, 0, 0);
            if (this.frame.isIconifiable()) {
                rectangle = this.iconButton.getBounds();
            }
            else if (this.frame.isMaximizable()) {
                rectangle = this.maxButton.getBounds();
            }
            else if (this.frame.isClosable()) {
                rectangle = this.closeButton.getBounds();
            }
            String s = this.frame.getTitle();
            int n2;
            if (BasicGraphicsUtils.isLeftToRight(this.frame)) {
                if (rectangle.x == 0) {
                    rectangle.x = this.frame.getWidth() - this.frame.getInsets().right;
                }
                n2 = this.menuBar.getX() + this.menuBar.getWidth() + 2;
                s = this.getTitle(this.frame.getTitle(), fontMetrics, rectangle.x - n2 - 3);
            }
            else {
                n2 = this.menuBar.getX() - 2 - SwingUtilities2.stringWidth(this.frame, fontMetrics, s);
            }
            SwingUtilities2.drawString(this.frame, graphics, s, n2, n);
            graphics.setFont(font);
        }
    }
    
    protected void paintTitleBackground(final Graphics graphics) {
        if (this.frame.isSelected()) {
            graphics.setColor(this.selectedTitleColor);
        }
        else {
            graphics.setColor(this.notSelectedTitleColor);
        }
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
    
    protected String getTitle(final String s, final FontMetrics fontMetrics, final int n) {
        return SwingUtilities2.clipStringIfNecessary(this.frame, fontMetrics, s, n);
    }
    
    protected void postClosingEvent(final JInternalFrame internalFrame) {
        final InternalFrameEvent internalFrameEvent = new InternalFrameEvent(internalFrame, 25550);
        try {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(internalFrameEvent);
        }
        catch (final SecurityException ex) {
            internalFrame.dispatchEvent(internalFrameEvent);
        }
    }
    
    protected void enableActions() {
        this.restoreAction.setEnabled(this.frame.isMaximum() || this.frame.isIcon());
        this.maximizeAction.setEnabled((this.frame.isMaximizable() && !this.frame.isMaximum() && !this.frame.isIcon()) || (this.frame.isMaximizable() && this.frame.isIcon()));
        this.iconifyAction.setEnabled(this.frame.isIconifiable() && !this.frame.isIcon());
        this.closeAction.setEnabled(this.frame.isClosable());
        this.sizeAction.setEnabled(false);
        this.moveAction.setEnabled(false);
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
    
    protected LayoutManager createLayout() {
        return this.getHandler();
    }
    
    static {
        CLOSE_CMD = UIManager.getString("InternalFrameTitlePane.closeButtonText");
        ICONIFY_CMD = UIManager.getString("InternalFrameTitlePane.minimizeButtonText");
        RESTORE_CMD = UIManager.getString("InternalFrameTitlePane.restoreButtonText");
        MAXIMIZE_CMD = UIManager.getString("InternalFrameTitlePane.maximizeButtonText");
        MOVE_CMD = UIManager.getString("InternalFrameTitlePane.moveButtonText");
        SIZE_CMD = UIManager.getString("InternalFrameTitlePane.sizeButtonText");
    }
    
    private class Handler implements LayoutManager, PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName == "selected") {
                BasicInternalFrameTitlePane.this.repaint();
                return;
            }
            if (propertyName == "icon" || propertyName == "maximum") {
                BasicInternalFrameTitlePane.this.setButtonIcons();
                BasicInternalFrameTitlePane.this.enableActions();
                return;
            }
            if ("closable" == propertyName) {
                if (propertyChangeEvent.getNewValue() == Boolean.TRUE) {
                    BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.closeButton);
                }
                else {
                    BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.closeButton);
                }
            }
            else if ("maximizable" == propertyName) {
                if (propertyChangeEvent.getNewValue() == Boolean.TRUE) {
                    BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.maxButton);
                }
                else {
                    BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.maxButton);
                }
            }
            else if ("iconable" == propertyName) {
                if (propertyChangeEvent.getNewValue() == Boolean.TRUE) {
                    BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.iconButton);
                }
                else {
                    BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.iconButton);
                }
            }
            BasicInternalFrameTitlePane.this.enableActions();
            BasicInternalFrameTitlePane.this.revalidate();
            BasicInternalFrameTitlePane.this.repaint();
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return this.minimumLayoutSize(container);
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            int n = 22;
            if (BasicInternalFrameTitlePane.this.frame.isClosable()) {
                n += 19;
            }
            if (BasicInternalFrameTitlePane.this.frame.isMaximizable()) {
                n += 19;
            }
            if (BasicInternalFrameTitlePane.this.frame.isIconifiable()) {
                n += 19;
            }
            final FontMetrics fontMetrics = BasicInternalFrameTitlePane.this.frame.getFontMetrics(BasicInternalFrameTitlePane.this.getFont());
            final String title = BasicInternalFrameTitlePane.this.frame.getTitle();
            final int n2 = (title != null) ? SwingUtilities2.stringWidth(BasicInternalFrameTitlePane.this.frame, fontMetrics, title) : 0;
            int n3;
            if (((title != null) ? title.length() : 0) > 3) {
                final int stringWidth = SwingUtilities2.stringWidth(BasicInternalFrameTitlePane.this.frame, fontMetrics, title.substring(0, 3) + "...");
                n3 = n + ((n2 < stringWidth) ? n2 : stringWidth);
            }
            else {
                n3 = n + n2;
            }
            final Icon frameIcon = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
            int height = fontMetrics.getHeight();
            height += 2;
            int min = 0;
            if (frameIcon != null) {
                min = Math.min(frameIcon.getIconHeight(), 16);
            }
            min += 2;
            final Dimension dimension = new Dimension(n3, Math.max(height, min));
            if (BasicInternalFrameTitlePane.this.getBorder() != null) {
                final Insets borderInsets = BasicInternalFrameTitlePane.this.getBorder().getBorderInsets(container);
                final Dimension dimension2 = dimension;
                dimension2.height += borderInsets.top + borderInsets.bottom;
                final Dimension dimension3 = dimension;
                dimension3.width += borderInsets.left + borderInsets.right;
            }
            return dimension;
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final boolean leftToRight = BasicGraphicsUtils.isLeftToRight(BasicInternalFrameTitlePane.this.frame);
            final int width = BasicInternalFrameTitlePane.this.getWidth();
            final int height = BasicInternalFrameTitlePane.this.getHeight();
            final int iconHeight = BasicInternalFrameTitlePane.this.closeButton.getIcon().getIconHeight();
            final Icon frameIcon = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
            int iconHeight2 = 0;
            if (frameIcon != null) {
                iconHeight2 = frameIcon.getIconHeight();
            }
            BasicInternalFrameTitlePane.this.menuBar.setBounds(leftToRight ? 2 : (width - 16 - 2), (height - iconHeight2) / 2, 16, 16);
            int n = leftToRight ? (width - 16 - 2) : 2;
            if (BasicInternalFrameTitlePane.this.frame.isClosable()) {
                BasicInternalFrameTitlePane.this.closeButton.setBounds(n, (height - iconHeight) / 2, 16, 14);
                n += (leftToRight ? -18 : 18);
            }
            if (BasicInternalFrameTitlePane.this.frame.isMaximizable()) {
                BasicInternalFrameTitlePane.this.maxButton.setBounds(n, (height - iconHeight) / 2, 16, 14);
                n += (leftToRight ? -18 : 18);
            }
            if (BasicInternalFrameTitlePane.this.frame.isIconifiable()) {
                BasicInternalFrameTitlePane.this.iconButton.setBounds(n, (height - iconHeight) / 2, 16, 14);
            }
        }
    }
    
    public class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicInternalFrameTitlePane.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    public class TitlePaneLayout implements LayoutManager
    {
        @Override
        public void addLayoutComponent(final String s, final Component component) {
            BasicInternalFrameTitlePane.this.getHandler().addLayoutComponent(s, component);
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
            BasicInternalFrameTitlePane.this.getHandler().removeLayoutComponent(component);
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return BasicInternalFrameTitlePane.this.getHandler().preferredLayoutSize(container);
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return BasicInternalFrameTitlePane.this.getHandler().minimumLayoutSize(container);
        }
        
        @Override
        public void layoutContainer(final Container container) {
            BasicInternalFrameTitlePane.this.getHandler().layoutContainer(container);
        }
    }
    
    public class CloseAction extends AbstractAction
    {
        public CloseAction() {
            super(UIManager.getString("InternalFrameTitlePane.closeButtonText"));
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicInternalFrameTitlePane.this.frame.isClosable()) {
                BasicInternalFrameTitlePane.this.frame.doDefaultCloseAction();
            }
        }
    }
    
    public class MaximizeAction extends AbstractAction
    {
        public MaximizeAction() {
            super(UIManager.getString("InternalFrameTitlePane.maximizeButtonText"));
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicInternalFrameTitlePane.this.frame.isMaximizable()) {
                if (BasicInternalFrameTitlePane.this.frame.isMaximum() && BasicInternalFrameTitlePane.this.frame.isIcon()) {
                    try {
                        BasicInternalFrameTitlePane.this.frame.setIcon(false);
                    }
                    catch (final PropertyVetoException ex) {}
                }
                else if (!BasicInternalFrameTitlePane.this.frame.isMaximum()) {
                    try {
                        BasicInternalFrameTitlePane.this.frame.setMaximum(true);
                    }
                    catch (final PropertyVetoException ex2) {}
                }
                else {
                    try {
                        BasicInternalFrameTitlePane.this.frame.setMaximum(false);
                    }
                    catch (final PropertyVetoException ex3) {}
                }
            }
        }
    }
    
    public class IconifyAction extends AbstractAction
    {
        public IconifyAction() {
            super(UIManager.getString("InternalFrameTitlePane.minimizeButtonText"));
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicInternalFrameTitlePane.this.frame.isIconifiable()) {
                if (!BasicInternalFrameTitlePane.this.frame.isIcon()) {
                    try {
                        BasicInternalFrameTitlePane.this.frame.setIcon(true);
                    }
                    catch (final PropertyVetoException ex) {}
                }
                else {
                    try {
                        BasicInternalFrameTitlePane.this.frame.setIcon(false);
                    }
                    catch (final PropertyVetoException ex2) {}
                }
            }
        }
    }
    
    public class RestoreAction extends AbstractAction
    {
        public RestoreAction() {
            super(UIManager.getString("InternalFrameTitlePane.restoreButtonText"));
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicInternalFrameTitlePane.this.frame.isMaximizable() && BasicInternalFrameTitlePane.this.frame.isMaximum() && BasicInternalFrameTitlePane.this.frame.isIcon()) {
                try {
                    BasicInternalFrameTitlePane.this.frame.setIcon(false);
                }
                catch (final PropertyVetoException ex) {}
            }
            else if (BasicInternalFrameTitlePane.this.frame.isMaximizable() && BasicInternalFrameTitlePane.this.frame.isMaximum()) {
                try {
                    BasicInternalFrameTitlePane.this.frame.setMaximum(false);
                }
                catch (final PropertyVetoException ex2) {}
            }
            else if (BasicInternalFrameTitlePane.this.frame.isIconifiable() && BasicInternalFrameTitlePane.this.frame.isIcon()) {
                try {
                    BasicInternalFrameTitlePane.this.frame.setIcon(false);
                }
                catch (final PropertyVetoException ex3) {}
            }
        }
    }
    
    public class MoveAction extends AbstractAction
    {
        public MoveAction() {
            super(UIManager.getString("InternalFrameTitlePane.moveButtonText"));
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
        }
    }
    
    private class ShowSystemMenuAction extends AbstractAction
    {
        private boolean show;
        
        public ShowSystemMenuAction(final boolean show) {
            this.show = show;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.show) {
                BasicInternalFrameTitlePane.this.windowMenu.doClick();
            }
            else {
                BasicInternalFrameTitlePane.this.windowMenu.setVisible(false);
            }
        }
    }
    
    public class SizeAction extends AbstractAction
    {
        public SizeAction() {
            super(UIManager.getString("InternalFrameTitlePane.sizeButtonText"));
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
        }
    }
    
    public class SystemMenuBar extends JMenuBar
    {
        @Override
        public boolean isFocusTraversable() {
            return false;
        }
        
        @Override
        public void requestFocus() {
        }
        
        @Override
        public void paint(final Graphics graphics) {
            Icon frameIcon = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
            if (frameIcon == null) {
                frameIcon = (Icon)DefaultLookup.get(BasicInternalFrameTitlePane.this.frame, BasicInternalFrameTitlePane.this.frame.getUI(), "InternalFrame.icon");
            }
            if (frameIcon != null) {
                if (frameIcon instanceof ImageIcon && (frameIcon.getIconWidth() > 16 || frameIcon.getIconHeight() > 16)) {
                    ((ImageIcon)frameIcon).setImage(((ImageIcon)frameIcon).getImage().getScaledInstance(16, 16, 4));
                }
                frameIcon.paintIcon(this, graphics, 0, 0);
            }
        }
        
        @Override
        public boolean isOpaque() {
            return true;
        }
    }
    
    private class NoFocusButton extends JButton
    {
        private String uiKey;
        
        public NoFocusButton(final String uiKey, final String s) {
            this.setFocusPainted(false);
            this.setMargin(new Insets(0, 0, 0, 0));
            this.uiKey = uiKey;
            final Object value = UIManager.get(s);
            if (value instanceof Boolean) {
                this.setOpaque((boolean)value);
            }
        }
        
        @Override
        public boolean isFocusTraversable() {
            return false;
        }
        
        @Override
        public void requestFocus() {
        }
        
        @Override
        public AccessibleContext getAccessibleContext() {
            final AccessibleContext accessibleContext = super.getAccessibleContext();
            if (this.uiKey != null) {
                accessibleContext.setAccessibleName(UIManager.getString(this.uiKey));
                this.uiKey = null;
            }
            return accessibleContext;
        }
    }
}
