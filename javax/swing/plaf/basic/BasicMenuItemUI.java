package javax.swing.plaf.basic;

import java.beans.PropertyChangeEvent;
import javax.swing.event.MenuDragMouseEvent;
import java.awt.Point;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.awt.event.MouseEvent;
import java.awt.Container;
import javax.swing.MenuSelectionManager;
import javax.swing.MenuElement;
import java.awt.FontMetrics;
import java.awt.Shape;
import sun.swing.SwingUtilities2;
import javax.swing.JMenu;
import javax.swing.ButtonModel;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.text.View;
import java.awt.Dimension;
import javax.swing.KeyStroke;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import java.awt.Insets;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import sun.swing.MenuItemCheckIconFactory;
import sun.swing.MenuItemLayoutHelper;
import java.awt.Component;
import javax.swing.plaf.UIResource;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.ActionMap;
import javax.swing.Action;
import javax.swing.Icon;
import java.beans.PropertyChangeListener;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MouseInputListener;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JMenuItem;
import javax.swing.plaf.MenuItemUI;

public class BasicMenuItemUI extends MenuItemUI
{
    protected JMenuItem menuItem;
    protected Color selectionBackground;
    protected Color selectionForeground;
    protected Color disabledForeground;
    protected Color acceleratorForeground;
    protected Color acceleratorSelectionForeground;
    protected String acceleratorDelimiter;
    protected int defaultTextIconGap;
    protected Font acceleratorFont;
    protected MouseInputListener mouseInputListener;
    protected MenuDragMouseListener menuDragMouseListener;
    protected MenuKeyListener menuKeyListener;
    protected PropertyChangeListener propertyChangeListener;
    Handler handler;
    protected Icon arrowIcon;
    protected Icon checkIcon;
    protected boolean oldBorderPainted;
    private static final boolean TRACE = false;
    private static final boolean VERBOSE = false;
    private static final boolean DEBUG = false;
    
    public BasicMenuItemUI() {
        this.menuItem = null;
        this.arrowIcon = null;
        this.checkIcon = null;
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("doClick"));
        BasicLookAndFeel.installAudioActionMap(lazyActionMap);
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicMenuItemUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.menuItem = (JMenuItem)component;
        this.installDefaults();
        this.installComponents(this.menuItem);
        this.installListeners();
        this.installKeyboardActions();
    }
    
    protected void installDefaults() {
        final String propertyPrefix = this.getPropertyPrefix();
        this.acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");
        if (this.acceleratorFont == null) {
            this.acceleratorFont = UIManager.getFont("MenuItem.font");
        }
        final Object value = UIManager.get(this.getPropertyPrefix() + ".opaque");
        if (value != null) {
            LookAndFeel.installProperty(this.menuItem, "opaque", value);
        }
        else {
            LookAndFeel.installProperty(this.menuItem, "opaque", Boolean.TRUE);
        }
        if (this.menuItem.getMargin() == null || this.menuItem.getMargin() instanceof UIResource) {
            this.menuItem.setMargin(UIManager.getInsets(propertyPrefix + ".margin"));
        }
        LookAndFeel.installProperty(this.menuItem, "iconTextGap", 4);
        this.defaultTextIconGap = this.menuItem.getIconTextGap();
        LookAndFeel.installBorder(this.menuItem, propertyPrefix + ".border");
        this.oldBorderPainted = this.menuItem.isBorderPainted();
        LookAndFeel.installProperty(this.menuItem, "borderPainted", UIManager.getBoolean(propertyPrefix + ".borderPainted"));
        LookAndFeel.installColorsAndFont(this.menuItem, propertyPrefix + ".background", propertyPrefix + ".foreground", propertyPrefix + ".font");
        if (this.selectionBackground == null || this.selectionBackground instanceof UIResource) {
            this.selectionBackground = UIManager.getColor(propertyPrefix + ".selectionBackground");
        }
        if (this.selectionForeground == null || this.selectionForeground instanceof UIResource) {
            this.selectionForeground = UIManager.getColor(propertyPrefix + ".selectionForeground");
        }
        if (this.disabledForeground == null || this.disabledForeground instanceof UIResource) {
            this.disabledForeground = UIManager.getColor(propertyPrefix + ".disabledForeground");
        }
        if (this.acceleratorForeground == null || this.acceleratorForeground instanceof UIResource) {
            this.acceleratorForeground = UIManager.getColor(propertyPrefix + ".acceleratorForeground");
        }
        if (this.acceleratorSelectionForeground == null || this.acceleratorSelectionForeground instanceof UIResource) {
            this.acceleratorSelectionForeground = UIManager.getColor(propertyPrefix + ".acceleratorSelectionForeground");
        }
        this.acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
        if (this.acceleratorDelimiter == null) {
            this.acceleratorDelimiter = "+";
        }
        if (this.arrowIcon == null || this.arrowIcon instanceof UIResource) {
            this.arrowIcon = UIManager.getIcon(propertyPrefix + ".arrowIcon");
        }
        this.updateCheckIcon();
    }
    
    private void updateCheckIcon() {
        final String propertyPrefix = this.getPropertyPrefix();
        if (this.checkIcon == null || this.checkIcon instanceof UIResource) {
            this.checkIcon = UIManager.getIcon(propertyPrefix + ".checkIcon");
            if (MenuItemLayoutHelper.isColumnLayout(BasicGraphicsUtils.isLeftToRight(this.menuItem), this.menuItem)) {
                final MenuItemCheckIconFactory menuItemCheckIconFactory = (MenuItemCheckIconFactory)UIManager.get(propertyPrefix + ".checkIconFactory");
                if (menuItemCheckIconFactory != null && MenuItemLayoutHelper.useCheckAndArrow(this.menuItem) && menuItemCheckIconFactory.isCompatible(this.checkIcon, propertyPrefix)) {
                    this.checkIcon = menuItemCheckIconFactory.getIcon(this.menuItem);
                }
            }
        }
    }
    
    protected void installComponents(final JMenuItem menuItem) {
        BasicHTML.updateRenderer(menuItem, menuItem.getText());
    }
    
    protected String getPropertyPrefix() {
        return "MenuItem";
    }
    
    protected void installListeners() {
        final MouseInputListener mouseInputListener = this.createMouseInputListener(this.menuItem);
        this.mouseInputListener = mouseInputListener;
        if (mouseInputListener != null) {
            this.menuItem.addMouseListener(this.mouseInputListener);
            this.menuItem.addMouseMotionListener(this.mouseInputListener);
        }
        if ((this.menuDragMouseListener = this.createMenuDragMouseListener(this.menuItem)) != null) {
            this.menuItem.addMenuDragMouseListener(this.menuDragMouseListener);
        }
        if ((this.menuKeyListener = this.createMenuKeyListener(this.menuItem)) != null) {
            this.menuItem.addMenuKeyListener(this.menuKeyListener);
        }
        if ((this.propertyChangeListener = this.createPropertyChangeListener(this.menuItem)) != null) {
            this.menuItem.addPropertyChangeListener(this.propertyChangeListener);
        }
    }
    
    protected void installKeyboardActions() {
        this.installLazyActionMap();
        this.updateAcceleratorBinding();
    }
    
    void installLazyActionMap() {
        LazyActionMap.installLazyActionMap(this.menuItem, BasicMenuItemUI.class, this.getPropertyPrefix() + ".actionMap");
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.menuItem = (JMenuItem)component;
        this.uninstallDefaults();
        this.uninstallComponents(this.menuItem);
        this.uninstallListeners();
        this.uninstallKeyboardActions();
        MenuItemLayoutHelper.clearUsedParentClientProperties(this.menuItem);
        this.menuItem = null;
    }
    
    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(this.menuItem);
        LookAndFeel.installProperty(this.menuItem, "borderPainted", this.oldBorderPainted);
        if (this.menuItem.getMargin() instanceof UIResource) {
            this.menuItem.setMargin(null);
        }
        if (this.arrowIcon instanceof UIResource) {
            this.arrowIcon = null;
        }
        if (this.checkIcon instanceof UIResource) {
            this.checkIcon = null;
        }
    }
    
    protected void uninstallComponents(final JMenuItem menuItem) {
        BasicHTML.updateRenderer(menuItem, "");
    }
    
    protected void uninstallListeners() {
        if (this.mouseInputListener != null) {
            this.menuItem.removeMouseListener(this.mouseInputListener);
            this.menuItem.removeMouseMotionListener(this.mouseInputListener);
        }
        if (this.menuDragMouseListener != null) {
            this.menuItem.removeMenuDragMouseListener(this.menuDragMouseListener);
        }
        if (this.menuKeyListener != null) {
            this.menuItem.removeMenuKeyListener(this.menuKeyListener);
        }
        if (this.propertyChangeListener != null) {
            this.menuItem.removePropertyChangeListener(this.propertyChangeListener);
        }
        this.mouseInputListener = null;
        this.menuDragMouseListener = null;
        this.menuKeyListener = null;
        this.propertyChangeListener = null;
        this.handler = null;
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(this.menuItem, null);
        SwingUtilities.replaceUIInputMap(this.menuItem, 2, null);
    }
    
    protected MouseInputListener createMouseInputListener(final JComponent component) {
        return this.getHandler();
    }
    
    protected MenuDragMouseListener createMenuDragMouseListener(final JComponent component) {
        return this.getHandler();
    }
    
    protected MenuKeyListener createMenuKeyListener(final JComponent component) {
        return null;
    }
    
    protected PropertyChangeListener createPropertyChangeListener(final JComponent component) {
        return this.getHandler();
    }
    
    Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    InputMap createInputMap(final int n) {
        if (n == 2) {
            return new ComponentInputMapUIResource(this.menuItem);
        }
        return null;
    }
    
    void updateAcceleratorBinding() {
        final KeyStroke accelerator = this.menuItem.getAccelerator();
        InputMap inputMap = SwingUtilities.getUIInputMap(this.menuItem, 2);
        if (inputMap != null) {
            inputMap.clear();
        }
        if (accelerator != null) {
            if (inputMap == null) {
                inputMap = this.createInputMap(2);
                SwingUtilities.replaceUIInputMap(this.menuItem, 2, inputMap);
            }
            inputMap.put(accelerator, "doClick");
        }
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        Dimension dimension = null;
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            final Dimension preferredSize;
            dimension = (preferredSize = this.getPreferredSize(component));
            preferredSize.width -= (int)(view.getPreferredSpan(0) - view.getMinimumSpan(0));
        }
        return dimension;
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return this.getPreferredMenuItemSize(component, this.checkIcon, this.arrowIcon, this.defaultTextIconGap);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        Dimension dimension = null;
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            final Dimension preferredSize;
            dimension = (preferredSize = this.getPreferredSize(component));
            preferredSize.width += (int)(view.getMaximumSpan(0) - view.getPreferredSpan(0));
        }
        return dimension;
    }
    
    protected Dimension getPreferredMenuItemSize(final JComponent component, final Icon icon, final Icon icon2, final int n) {
        final JMenuItem menuItem = (JMenuItem)component;
        final MenuItemLayoutHelper menuItemLayoutHelper = new MenuItemLayoutHelper(menuItem, icon, icon2, MenuItemLayoutHelper.createMaxRect(), n, this.acceleratorDelimiter, BasicGraphicsUtils.isLeftToRight(menuItem), menuItem.getFont(), this.acceleratorFont, MenuItemLayoutHelper.useCheckAndArrow(this.menuItem), this.getPropertyPrefix());
        final Dimension dimension = new Dimension();
        dimension.width = menuItemLayoutHelper.getLeadingGap();
        MenuItemLayoutHelper.addMaxWidth(menuItemLayoutHelper.getCheckSize(), menuItemLayoutHelper.getAfterCheckIconGap(), dimension);
        if (!menuItemLayoutHelper.isTopLevelMenu() && menuItemLayoutHelper.getMinTextOffset() > 0 && dimension.width < menuItemLayoutHelper.getMinTextOffset()) {
            dimension.width = menuItemLayoutHelper.getMinTextOffset();
        }
        MenuItemLayoutHelper.addMaxWidth(menuItemLayoutHelper.getLabelSize(), menuItemLayoutHelper.getGap(), dimension);
        MenuItemLayoutHelper.addMaxWidth(menuItemLayoutHelper.getAccSize(), menuItemLayoutHelper.getGap(), dimension);
        MenuItemLayoutHelper.addMaxWidth(menuItemLayoutHelper.getArrowSize(), menuItemLayoutHelper.getGap(), dimension);
        dimension.height = MenuItemLayoutHelper.max(menuItemLayoutHelper.getCheckSize().getHeight(), menuItemLayoutHelper.getLabelSize().getHeight(), menuItemLayoutHelper.getAccSize().getHeight(), menuItemLayoutHelper.getArrowSize().getHeight());
        final Insets insets = menuItemLayoutHelper.getMenuItem().getInsets();
        if (insets != null) {
            final Dimension dimension2 = dimension;
            dimension2.width += insets.left + insets.right;
            final Dimension dimension3 = dimension;
            dimension3.height += insets.top + insets.bottom;
        }
        if (dimension.width % 2 == 0) {
            final Dimension dimension4 = dimension;
            ++dimension4.width;
        }
        if (dimension.height % 2 == 0 && Boolean.TRUE != UIManager.get(this.getPropertyPrefix() + ".evenHeight")) {
            final Dimension dimension5 = dimension;
            ++dimension5.height;
        }
        return dimension;
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        this.paint(graphics, component);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        this.paintMenuItem(graphics, component, this.checkIcon, this.arrowIcon, this.selectionBackground, this.selectionForeground, this.defaultTextIconGap);
    }
    
    protected void paintMenuItem(final Graphics graphics, final JComponent component, final Icon icon, final Icon icon2, final Color color, final Color color2, final int n) {
        final Font font = graphics.getFont();
        final Color color3 = graphics.getColor();
        final JMenuItem menuItem = (JMenuItem)component;
        graphics.setFont(menuItem.getFont());
        final Rectangle rectangle = new Rectangle(0, 0, menuItem.getWidth(), menuItem.getHeight());
        this.applyInsets(rectangle, menuItem.getInsets());
        final MenuItemLayoutHelper menuItemLayoutHelper = new MenuItemLayoutHelper(menuItem, icon, icon2, rectangle, n, this.acceleratorDelimiter, BasicGraphicsUtils.isLeftToRight(menuItem), menuItem.getFont(), this.acceleratorFont, MenuItemLayoutHelper.useCheckAndArrow(this.menuItem), this.getPropertyPrefix());
        final MenuItemLayoutHelper.LayoutResult layoutMenuItem = menuItemLayoutHelper.layoutMenuItem();
        this.paintBackground(graphics, menuItem, color);
        this.paintCheckIcon(graphics, menuItemLayoutHelper, layoutMenuItem, color3, color2);
        this.paintIcon(graphics, menuItemLayoutHelper, layoutMenuItem, color3);
        this.paintText(graphics, menuItemLayoutHelper, layoutMenuItem);
        this.paintAccText(graphics, menuItemLayoutHelper, layoutMenuItem);
        this.paintArrowIcon(graphics, menuItemLayoutHelper, layoutMenuItem, color2);
        graphics.setColor(color3);
        graphics.setFont(font);
    }
    
    private void paintIcon(final Graphics graphics, final MenuItemLayoutHelper menuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult, final Color color) {
        if (menuItemLayoutHelper.getIcon() != null) {
            final ButtonModel model = menuItemLayoutHelper.getMenuItem().getModel();
            Icon icon;
            if (!model.isEnabled()) {
                icon = menuItemLayoutHelper.getMenuItem().getDisabledIcon();
            }
            else if (model.isPressed() && model.isArmed()) {
                icon = menuItemLayoutHelper.getMenuItem().getPressedIcon();
                if (icon == null) {
                    icon = menuItemLayoutHelper.getMenuItem().getIcon();
                }
            }
            else {
                icon = menuItemLayoutHelper.getMenuItem().getIcon();
            }
            if (icon != null) {
                icon.paintIcon(menuItemLayoutHelper.getMenuItem(), graphics, layoutResult.getIconRect().x, layoutResult.getIconRect().y);
                graphics.setColor(color);
            }
        }
    }
    
    private void paintCheckIcon(final Graphics graphics, final MenuItemLayoutHelper menuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult, final Color color, final Color color2) {
        if (menuItemLayoutHelper.getCheckIcon() != null) {
            final ButtonModel model = menuItemLayoutHelper.getMenuItem().getModel();
            if (model.isArmed() || (menuItemLayoutHelper.getMenuItem() instanceof JMenu && model.isSelected())) {
                graphics.setColor(color2);
            }
            else {
                graphics.setColor(color);
            }
            if (menuItemLayoutHelper.useCheckAndArrow()) {
                menuItemLayoutHelper.getCheckIcon().paintIcon(menuItemLayoutHelper.getMenuItem(), graphics, layoutResult.getCheckRect().x, layoutResult.getCheckRect().y);
            }
            graphics.setColor(color);
        }
    }
    
    private void paintAccText(final Graphics graphics, final MenuItemLayoutHelper menuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult) {
        if (!menuItemLayoutHelper.getAccText().equals("")) {
            final ButtonModel model = menuItemLayoutHelper.getMenuItem().getModel();
            graphics.setFont(menuItemLayoutHelper.getAccFontMetrics().getFont());
            if (!model.isEnabled()) {
                if (this.disabledForeground != null) {
                    graphics.setColor(this.disabledForeground);
                    SwingUtilities2.drawString(menuItemLayoutHelper.getMenuItem(), graphics, menuItemLayoutHelper.getAccText(), layoutResult.getAccRect().x, layoutResult.getAccRect().y + menuItemLayoutHelper.getAccFontMetrics().getAscent());
                }
                else {
                    graphics.setColor(menuItemLayoutHelper.getMenuItem().getBackground().brighter());
                    SwingUtilities2.drawString(menuItemLayoutHelper.getMenuItem(), graphics, menuItemLayoutHelper.getAccText(), layoutResult.getAccRect().x, layoutResult.getAccRect().y + menuItemLayoutHelper.getAccFontMetrics().getAscent());
                    graphics.setColor(menuItemLayoutHelper.getMenuItem().getBackground().darker());
                    SwingUtilities2.drawString(menuItemLayoutHelper.getMenuItem(), graphics, menuItemLayoutHelper.getAccText(), layoutResult.getAccRect().x - 1, layoutResult.getAccRect().y + menuItemLayoutHelper.getFontMetrics().getAscent() - 1);
                }
            }
            else {
                if (model.isArmed() || (menuItemLayoutHelper.getMenuItem() instanceof JMenu && model.isSelected())) {
                    graphics.setColor(this.acceleratorSelectionForeground);
                }
                else {
                    graphics.setColor(this.acceleratorForeground);
                }
                SwingUtilities2.drawString(menuItemLayoutHelper.getMenuItem(), graphics, menuItemLayoutHelper.getAccText(), layoutResult.getAccRect().x, layoutResult.getAccRect().y + menuItemLayoutHelper.getAccFontMetrics().getAscent());
            }
        }
    }
    
    private void paintText(final Graphics graphics, final MenuItemLayoutHelper menuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult) {
        if (!menuItemLayoutHelper.getText().equals("")) {
            if (menuItemLayoutHelper.getHtmlView() != null) {
                menuItemLayoutHelper.getHtmlView().paint(graphics, layoutResult.getTextRect());
            }
            else {
                this.paintText(graphics, menuItemLayoutHelper.getMenuItem(), layoutResult.getTextRect(), menuItemLayoutHelper.getText());
            }
        }
    }
    
    private void paintArrowIcon(final Graphics graphics, final MenuItemLayoutHelper menuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult, final Color color) {
        if (menuItemLayoutHelper.getArrowIcon() != null) {
            final ButtonModel model = menuItemLayoutHelper.getMenuItem().getModel();
            if (model.isArmed() || (menuItemLayoutHelper.getMenuItem() instanceof JMenu && model.isSelected())) {
                graphics.setColor(color);
            }
            if (menuItemLayoutHelper.useCheckAndArrow()) {
                menuItemLayoutHelper.getArrowIcon().paintIcon(menuItemLayoutHelper.getMenuItem(), graphics, layoutResult.getArrowRect().x, layoutResult.getArrowRect().y);
            }
        }
    }
    
    private void applyInsets(final Rectangle rectangle, final Insets insets) {
        if (insets != null) {
            rectangle.x += insets.left;
            rectangle.y += insets.top;
            rectangle.width -= insets.right + rectangle.x;
            rectangle.height -= insets.bottom + rectangle.y;
        }
    }
    
    protected void paintBackground(final Graphics graphics, final JMenuItem menuItem, final Color color) {
        final ButtonModel model = menuItem.getModel();
        final Color color2 = graphics.getColor();
        final int width = menuItem.getWidth();
        final int height = menuItem.getHeight();
        if (menuItem.isOpaque()) {
            if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) {
                graphics.setColor(color);
                graphics.fillRect(0, 0, width, height);
            }
            else {
                graphics.setColor(menuItem.getBackground());
                graphics.fillRect(0, 0, width, height);
            }
            graphics.setColor(color2);
        }
        else if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) {
            graphics.setColor(color);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(color2);
        }
    }
    
    protected void paintText(final Graphics graphics, final JMenuItem menuItem, final Rectangle rectangle, final String s) {
        final ButtonModel model = menuItem.getModel();
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(menuItem, graphics);
        final int displayedMnemonicIndex = menuItem.getDisplayedMnemonicIndex();
        if (!model.isEnabled()) {
            if (UIManager.get("MenuItem.disabledForeground") instanceof Color) {
                graphics.setColor(UIManager.getColor("MenuItem.disabledForeground"));
                SwingUtilities2.drawStringUnderlineCharAt(menuItem, graphics, s, displayedMnemonicIndex, rectangle.x, rectangle.y + fontMetrics.getAscent());
            }
            else {
                graphics.setColor(menuItem.getBackground().brighter());
                SwingUtilities2.drawStringUnderlineCharAt(menuItem, graphics, s, displayedMnemonicIndex, rectangle.x, rectangle.y + fontMetrics.getAscent());
                graphics.setColor(menuItem.getBackground().darker());
                SwingUtilities2.drawStringUnderlineCharAt(menuItem, graphics, s, displayedMnemonicIndex, rectangle.x - 1, rectangle.y + fontMetrics.getAscent() - 1);
            }
        }
        else {
            if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) {
                graphics.setColor(this.selectionForeground);
            }
            SwingUtilities2.drawStringUnderlineCharAt(menuItem, graphics, s, displayedMnemonicIndex, rectangle.x, rectangle.y + fontMetrics.getAscent());
        }
    }
    
    public MenuElement[] getPath() {
        final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
        final int length = selectedPath.length;
        if (length == 0) {
            return new MenuElement[0];
        }
        final Container parent = this.menuItem.getParent();
        MenuElement[] array;
        if (selectedPath[length - 1].getComponent() == parent) {
            array = new MenuElement[length + 1];
            System.arraycopy(selectedPath, 0, array, 0, length);
            array[length] = this.menuItem;
        }
        else {
            int n;
            for (n = selectedPath.length - 1; n >= 0 && selectedPath[n].getComponent() != parent; --n) {}
            array = new MenuElement[n + 2];
            System.arraycopy(selectedPath, 0, array, 0, n + 1);
            array[n + 1] = this.menuItem;
        }
        return array;
    }
    
    void printMenuElementArray(final MenuElement[] array, final boolean b) {
        System.out.println("Path is(");
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j <= i; ++j) {
                System.out.print("  ");
            }
            final MenuElement menuElement = array[i];
            if (menuElement instanceof JMenuItem) {
                System.out.println(((JMenuItem)menuElement).getText() + ", ");
            }
            else if (menuElement == null) {
                System.out.println("NULL , ");
            }
            else {
                System.out.println("" + menuElement + ", ");
            }
        }
        System.out.println(")");
        if (b) {
            Thread.dumpStack();
        }
    }
    
    protected void doClick(MenuSelectionManager defaultManager) {
        if (!this.isInternalFrameSystemMenu()) {
            BasicLookAndFeel.playSound(this.menuItem, this.getPropertyPrefix() + ".commandSound");
        }
        if (defaultManager == null) {
            defaultManager = MenuSelectionManager.defaultManager();
        }
        defaultManager.clearSelectedPath();
        this.menuItem.doClick(0);
    }
    
    private boolean isInternalFrameSystemMenu() {
        final String actionCommand = this.menuItem.getActionCommand();
        return actionCommand == "Close" || actionCommand == "Minimize" || actionCommand == "Restore" || actionCommand == "Maximize";
    }
    
    protected class MouseInputHandler implements MouseInputListener
    {
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            BasicMenuItemUI.this.getHandler().mouseClicked(mouseEvent);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            BasicMenuItemUI.this.getHandler().mousePressed(mouseEvent);
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            BasicMenuItemUI.this.getHandler().mouseReleased(mouseEvent);
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            BasicMenuItemUI.this.getHandler().mouseEntered(mouseEvent);
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            BasicMenuItemUI.this.getHandler().mouseExited(mouseEvent);
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            BasicMenuItemUI.this.getHandler().mouseDragged(mouseEvent);
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            BasicMenuItemUI.this.getHandler().mouseMoved(mouseEvent);
        }
    }
    
    private static class Actions extends UIAction
    {
        private static final String CLICK = "doClick";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JMenuItem menuItem = (JMenuItem)actionEvent.getSource();
            MenuSelectionManager.defaultManager().clearSelectedPath();
            menuItem.doClick();
        }
    }
    
    class Handler implements MenuDragMouseListener, MouseInputListener, PropertyChangeListener
    {
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (!BasicMenuItemUI.this.menuItem.isEnabled()) {
                return;
            }
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            final Point point = mouseEvent.getPoint();
            if (point.x >= 0 && point.x < BasicMenuItemUI.this.menuItem.getWidth() && point.y >= 0 && point.y < BasicMenuItemUI.this.menuItem.getHeight()) {
                BasicMenuItemUI.this.doClick(defaultManager);
            }
            else {
                defaultManager.processMouseEvent(mouseEvent);
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            if ((mouseEvent.getModifiers() & 0x1C) != 0x0) {
                MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
            }
            else {
                defaultManager.setSelectedPath(BasicMenuItemUI.this.getPath());
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            if ((mouseEvent.getModifiers() & 0x1C) != 0x0) {
                MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
            }
            else {
                final MenuElement[] selectedPath = defaultManager.getSelectedPath();
                if (selectedPath.length > 1 && selectedPath[selectedPath.length - 1] == BasicMenuItemUI.this.menuItem) {
                    final MenuElement[] selectedPath2 = new MenuElement[selectedPath.length - 1];
                    for (int i = 0; i < selectedPath.length - 1; ++i) {
                        selectedPath2[i] = selectedPath[i];
                    }
                    defaultManager.setSelectedPath(selectedPath2);
                }
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void menuDragMouseEntered(final MenuDragMouseEvent menuDragMouseEvent) {
            menuDragMouseEvent.getMenuSelectionManager().setSelectedPath(menuDragMouseEvent.getPath());
        }
        
        @Override
        public void menuDragMouseDragged(final MenuDragMouseEvent menuDragMouseEvent) {
            menuDragMouseEvent.getMenuSelectionManager().setSelectedPath(menuDragMouseEvent.getPath());
        }
        
        @Override
        public void menuDragMouseExited(final MenuDragMouseEvent menuDragMouseEvent) {
        }
        
        @Override
        public void menuDragMouseReleased(final MenuDragMouseEvent menuDragMouseEvent) {
            if (!BasicMenuItemUI.this.menuItem.isEnabled()) {
                return;
            }
            final MenuSelectionManager menuSelectionManager = menuDragMouseEvent.getMenuSelectionManager();
            menuDragMouseEvent.getPath();
            final Point point = menuDragMouseEvent.getPoint();
            if (point.x >= 0 && point.x < BasicMenuItemUI.this.menuItem.getWidth() && point.y >= 0 && point.y < BasicMenuItemUI.this.menuItem.getHeight()) {
                BasicMenuItemUI.this.doClick(menuSelectionManager);
            }
            else {
                menuSelectionManager.clearSelectedPath();
            }
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName == "labelFor" || propertyName == "displayedMnemonic" || propertyName == "accelerator") {
                BasicMenuItemUI.this.updateAcceleratorBinding();
            }
            else if (propertyName == "text" || "font" == propertyName || "foreground" == propertyName) {
                final JMenuItem menuItem = (JMenuItem)propertyChangeEvent.getSource();
                BasicHTML.updateRenderer(menuItem, menuItem.getText());
            }
            else if (propertyName == "iconTextGap") {
                BasicMenuItemUI.this.defaultTextIconGap = ((Number)propertyChangeEvent.getNewValue()).intValue();
            }
            else if (propertyName == "horizontalTextPosition") {
                BasicMenuItemUI.this.updateCheckIcon();
            }
        }
    }
}
