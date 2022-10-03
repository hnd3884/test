package javax.swing.plaf.synth;

import javax.swing.UIManager;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JMenu;
import sun.swing.MenuItemLayoutHelper;
import java.awt.Insets;
import javax.swing.plaf.UIResource;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicMenuUI;

public class SynthMenuUI extends BasicMenuUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    private SynthStyle accStyle;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthMenuUI();
    }
    
    @Override
    protected void installDefaults() {
        this.updateStyle(this.menuItem);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.menuItem.addPropertyChangeListener(this);
    }
    
    private void updateStyle(final JMenuItem menuItem) {
        final SynthStyle style = this.style;
        final SynthContext context = this.getContext(menuItem, 1);
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (style != this.style) {
            final String propertyPrefix = this.getPropertyPrefix();
            this.defaultTextIconGap = this.style.getInt(context, propertyPrefix + ".textIconGap", 4);
            if (this.menuItem.getMargin() == null || this.menuItem.getMargin() instanceof UIResource) {
                Insets empty_UIRESOURCE_INSETS = (Insets)this.style.get(context, propertyPrefix + ".margin");
                if (empty_UIRESOURCE_INSETS == null) {
                    empty_UIRESOURCE_INSETS = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
                }
                this.menuItem.setMargin(empty_UIRESOURCE_INSETS);
            }
            this.acceleratorDelimiter = this.style.getString(context, propertyPrefix + ".acceleratorDelimiter", "+");
            if (MenuItemLayoutHelper.useCheckAndArrow(this.menuItem)) {
                this.checkIcon = this.style.getIcon(context, propertyPrefix + ".checkIcon");
                this.arrowIcon = this.style.getIcon(context, propertyPrefix + ".arrowIcon");
            }
            else {
                this.checkIcon = null;
                this.arrowIcon = null;
            }
            ((JMenu)this.menuItem).setDelay(this.style.getInt(context, propertyPrefix + ".delay", 200));
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
        }
        context.dispose();
        final SynthContext context2 = this.getContext(menuItem, Region.MENU_ITEM_ACCELERATOR, 1);
        this.accStyle = SynthLookAndFeel.updateStyle(context2, this);
        context2.dispose();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        super.uninstallUI(component);
        final JComponent menuItemParent = MenuItemLayoutHelper.getMenuItemParent((JMenuItem)component);
        if (menuItemParent != null) {
            menuItemParent.putClientProperty(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, null);
        }
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.menuItem, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        final SynthContext context2 = this.getContext(this.menuItem, Region.MENU_ITEM_ACCELERATOR, 1);
        this.accStyle.uninstallDefaults(context2);
        context2.dispose();
        this.accStyle = null;
        super.uninstallDefaults();
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.menuItem.removePropertyChangeListener(this);
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, this.getComponentState(component));
    }
    
    SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    SynthContext getContext(final JComponent component, final Region region) {
        return this.getContext(component, region, this.getComponentState(component, region));
    }
    
    private SynthContext getContext(final JComponent component, final Region region, final int n) {
        return SynthContext.getContext(component, region, this.accStyle, n);
    }
    
    private int getComponentState(final JComponent component) {
        if (!component.isEnabled()) {
            return 8;
        }
        int componentState;
        if (this.menuItem.isArmed()) {
            componentState = 2;
        }
        else {
            componentState = SynthLookAndFeel.getComponentState(component);
        }
        if (this.menuItem.isSelected()) {
            componentState |= 0x200;
        }
        return componentState;
    }
    
    private int getComponentState(final JComponent component, final Region region) {
        return this.getComponentState(component);
    }
    
    @Override
    protected Dimension getPreferredMenuItemSize(final JComponent component, final Icon icon, final Icon icon2, final int n) {
        final SynthContext context = this.getContext(component);
        final SynthContext context2 = this.getContext(component, Region.MENU_ITEM_ACCELERATOR);
        final Dimension preferredMenuItemSize = SynthGraphicsUtils.getPreferredMenuItemSize(context, context2, component, icon, icon2, n, this.acceleratorDelimiter, MenuItemLayoutHelper.useCheckAndArrow(this.menuItem), this.getPropertyPrefix());
        context.dispose();
        context2.dispose();
        return preferredMenuItemSize;
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintMenuBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        final SynthContext context = this.getContext(this.menuItem, Region.MENU_ITEM_ACCELERATOR);
        final String propertyPrefix = this.getPropertyPrefix();
        SynthGraphicsUtils.paint(synthContext, context, graphics, this.style.getIcon(synthContext, propertyPrefix + ".checkIcon"), this.style.getIcon(synthContext, propertyPrefix + ".arrowIcon"), this.acceleratorDelimiter, this.defaultTextIconGap, this.getPropertyPrefix());
        context.dispose();
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintMenuBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent) || (propertyChangeEvent.getPropertyName().equals("ancestor") && UIManager.getBoolean("Menu.useMenuBarForTopLevelMenus"))) {
            this.updateStyle((JMenuItem)propertyChangeEvent.getSource());
        }
    }
}
