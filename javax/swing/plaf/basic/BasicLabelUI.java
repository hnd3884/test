package javax.swing.plaf.basic;

import java.awt.Container;
import javax.swing.plaf.InputMapUIResource;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.beans.PropertyChangeEvent;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.LookAndFeel;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Shape;
import javax.swing.text.View;
import java.awt.Component;
import java.awt.Color;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Icon;
import java.awt.FontMetrics;
import javax.swing.JLabel;
import javax.swing.Action;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.LabelUI;

public class BasicLabelUI extends LabelUI implements PropertyChangeListener
{
    protected static BasicLabelUI labelUI;
    private static final Object BASIC_LABEL_UI_KEY;
    private Rectangle paintIconR;
    private Rectangle paintTextR;
    
    public BasicLabelUI() {
        this.paintIconR = new Rectangle();
        this.paintTextR = new Rectangle();
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("press"));
        lazyActionMap.put(new Actions("release"));
    }
    
    protected String layoutCL(final JLabel label, final FontMetrics fontMetrics, final String s, final Icon icon, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3) {
        return SwingUtilities.layoutCompoundLabel(label, fontMetrics, s, icon, label.getVerticalAlignment(), label.getHorizontalAlignment(), label.getVerticalTextPosition(), label.getHorizontalTextPosition(), rectangle, rectangle2, rectangle3, label.getIconTextGap());
    }
    
    protected void paintEnabledText(final JLabel label, final Graphics graphics, final String s, final int n, final int n2) {
        final int displayedMnemonicIndex = label.getDisplayedMnemonicIndex();
        graphics.setColor(label.getForeground());
        SwingUtilities2.drawStringUnderlineCharAt(label, graphics, s, displayedMnemonicIndex, n, n2);
    }
    
    protected void paintDisabledText(final JLabel label, final Graphics graphics, final String s, final int n, final int n2) {
        final int displayedMnemonicIndex = label.getDisplayedMnemonicIndex();
        final Color background = label.getBackground();
        graphics.setColor(background.brighter());
        SwingUtilities2.drawStringUnderlineCharAt(label, graphics, s, displayedMnemonicIndex, n + 1, n2 + 1);
        graphics.setColor(background.darker());
        SwingUtilities2.drawStringUnderlineCharAt(label, graphics, s, displayedMnemonicIndex, n, n2);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final JLabel label = (JLabel)component;
        final String text = label.getText();
        final Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
        if (icon == null && text == null) {
            return;
        }
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(label, graphics);
        final String layout = this.layout(label, fontMetrics, component.getWidth(), component.getHeight());
        if (icon != null) {
            icon.paintIcon(component, graphics, this.paintIconR.x, this.paintIconR.y);
        }
        if (text != null) {
            final View view = (View)component.getClientProperty("html");
            if (view != null) {
                view.paint(graphics, this.paintTextR);
            }
            else {
                final int x = this.paintTextR.x;
                final int n = this.paintTextR.y + fontMetrics.getAscent();
                if (label.isEnabled()) {
                    this.paintEnabledText(label, graphics, layout, x, n);
                }
                else {
                    this.paintDisabledText(label, graphics, layout, x, n);
                }
            }
        }
    }
    
    private String layout(final JLabel label, final FontMetrics fontMetrics, final int n, final int n2) {
        final Insets insets = label.getInsets(null);
        final String text = label.getText();
        final Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
        final Rectangle rectangle = new Rectangle();
        rectangle.x = insets.left;
        rectangle.y = insets.top;
        rectangle.width = n - (insets.left + insets.right);
        rectangle.height = n2 - (insets.top + insets.bottom);
        final Rectangle paintIconR = this.paintIconR;
        final Rectangle paintIconR2 = this.paintIconR;
        final Rectangle paintIconR3 = this.paintIconR;
        final Rectangle paintIconR4 = this.paintIconR;
        final int n3 = 0;
        paintIconR4.height = n3;
        paintIconR3.width = n3;
        paintIconR2.y = n3;
        paintIconR.x = n3;
        final Rectangle paintTextR = this.paintTextR;
        final Rectangle paintTextR2 = this.paintTextR;
        final Rectangle paintTextR3 = this.paintTextR;
        final Rectangle paintTextR4 = this.paintTextR;
        final int n4 = 0;
        paintTextR4.height = n4;
        paintTextR3.width = n4;
        paintTextR2.y = n4;
        paintTextR.x = n4;
        return this.layoutCL(label, fontMetrics, text, icon, rectangle, this.paintIconR, this.paintTextR);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final JLabel label = (JLabel)component;
        final String text = label.getText();
        final Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
        final Insets insets = label.getInsets(null);
        final Font font = label.getFont();
        final int x = insets.left + insets.right;
        final int y = insets.top + insets.bottom;
        if (icon == null && (text == null || (text != null && font == null))) {
            return new Dimension(x, y);
        }
        if (text == null || (icon != null && font == null)) {
            return new Dimension(icon.getIconWidth() + x, icon.getIconHeight() + y);
        }
        final FontMetrics fontMetrics = label.getFontMetrics(font);
        final Rectangle rectangle = new Rectangle();
        final Rectangle rectangle2 = new Rectangle();
        final Rectangle rectangle3 = new Rectangle();
        final Rectangle rectangle4 = rectangle;
        final Rectangle rectangle5 = rectangle;
        final Rectangle rectangle6 = rectangle;
        final Rectangle rectangle7 = rectangle;
        final int n = 0;
        rectangle7.height = n;
        rectangle6.width = n;
        rectangle5.y = n;
        rectangle4.x = n;
        final Rectangle rectangle8 = rectangle2;
        final Rectangle rectangle9 = rectangle2;
        final Rectangle rectangle10 = rectangle2;
        final Rectangle rectangle11 = rectangle2;
        final int n2 = 0;
        rectangle11.height = n2;
        rectangle10.width = n2;
        rectangle9.y = n2;
        rectangle8.x = n2;
        rectangle3.x = x;
        rectangle3.y = y;
        final Rectangle rectangle12 = rectangle3;
        final Rectangle rectangle13 = rectangle3;
        final int n3 = 32767;
        rectangle13.height = n3;
        rectangle12.width = n3;
        this.layoutCL(label, fontMetrics, text, icon, rectangle3, rectangle, rectangle2);
        final Dimension dimension2;
        final Dimension dimension = dimension2 = new Dimension(Math.max(rectangle.x + rectangle.width, rectangle2.x + rectangle2.width) - Math.min(rectangle.x, rectangle2.x), Math.max(rectangle.y + rectangle.height, rectangle2.y + rectangle2.height) - Math.min(rectangle.y, rectangle2.y));
        dimension2.width += x;
        final Dimension dimension3 = dimension;
        dimension3.height += y;
        return dimension;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Dimension preferredSize = this.getPreferredSize(component);
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            final Dimension dimension = preferredSize;
            dimension.width -= (int)(view.getPreferredSpan(0) - view.getMinimumSpan(0));
        }
        return preferredSize;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Dimension preferredSize = this.getPreferredSize(component);
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            final Dimension dimension = preferredSize;
            dimension.width += (int)(view.getMaximumSpan(0) - view.getPreferredSpan(0));
        }
        return preferredSize;
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        super.getBaseline(component, n, n2);
        final JLabel label = (JLabel)component;
        final String text = label.getText();
        if (text == null || "".equals(text) || label.getFont() == null) {
            return -1;
        }
        final FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
        this.layout(label, fontMetrics, n, n2);
        return BasicHTML.getBaseline(label, this.paintTextR.y, fontMetrics.getAscent(), this.paintTextR.width, this.paintTextR.height);
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        if (component.getClientProperty("html") != null) {
            return Component.BaselineResizeBehavior.OTHER;
        }
        switch (((JLabel)component).getVerticalAlignment()) {
            case 1: {
                return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
            }
            case 3: {
                return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
            }
            case 0: {
                return Component.BaselineResizeBehavior.CENTER_OFFSET;
            }
            default: {
                return Component.BaselineResizeBehavior.OTHER;
            }
        }
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.installDefaults((JLabel)component);
        this.installComponents((JLabel)component);
        this.installListeners((JLabel)component);
        this.installKeyboardActions((JLabel)component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults((JLabel)component);
        this.uninstallComponents((JLabel)component);
        this.uninstallListeners((JLabel)component);
        this.uninstallKeyboardActions((JLabel)component);
    }
    
    protected void installDefaults(final JLabel label) {
        LookAndFeel.installColorsAndFont(label, "Label.background", "Label.foreground", "Label.font");
        LookAndFeel.installProperty(label, "opaque", Boolean.FALSE);
    }
    
    protected void installListeners(final JLabel label) {
        label.addPropertyChangeListener(this);
    }
    
    protected void installComponents(final JLabel label) {
        BasicHTML.updateRenderer(label, label.getText());
        label.setInheritsPopupMenu(true);
    }
    
    protected void installKeyboardActions(final JLabel label) {
        final int displayedMnemonic = label.getDisplayedMnemonic();
        final Component label2 = label.getLabelFor();
        if (displayedMnemonic != 0 && label2 != null) {
            LazyActionMap.installLazyActionMap(label, BasicLabelUI.class, "Label.actionMap");
            InputMap uiInputMap = SwingUtilities.getUIInputMap(label, 2);
            if (uiInputMap == null) {
                uiInputMap = new ComponentInputMapUIResource(label);
                SwingUtilities.replaceUIInputMap(label, 2, uiInputMap);
            }
            uiInputMap.clear();
            uiInputMap.put(KeyStroke.getKeyStroke(displayedMnemonic, BasicLookAndFeel.getFocusAcceleratorKeyMask(), false), "press");
        }
        else {
            final InputMap uiInputMap2 = SwingUtilities.getUIInputMap(label, 2);
            if (uiInputMap2 != null) {
                uiInputMap2.clear();
            }
        }
    }
    
    protected void uninstallDefaults(final JLabel label) {
    }
    
    protected void uninstallListeners(final JLabel label) {
        label.removePropertyChangeListener(this);
    }
    
    protected void uninstallComponents(final JLabel label) {
        BasicHTML.updateRenderer(label, "");
    }
    
    protected void uninstallKeyboardActions(final JLabel label) {
        SwingUtilities.replaceUIInputMap(label, 0, null);
        SwingUtilities.replaceUIInputMap(label, 2, null);
        SwingUtilities.replaceUIActionMap(label, null);
    }
    
    public static ComponentUI createUI(final JComponent component) {
        if (System.getSecurityManager() != null) {
            final AppContext appContext = AppContext.getAppContext();
            BasicLabelUI basicLabelUI = (BasicLabelUI)appContext.get(BasicLabelUI.BASIC_LABEL_UI_KEY);
            if (basicLabelUI == null) {
                basicLabelUI = new BasicLabelUI();
                appContext.put(BasicLabelUI.BASIC_LABEL_UI_KEY, basicLabelUI);
            }
            return basicLabelUI;
        }
        return BasicLabelUI.labelUI;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (propertyName == "text" || "font" == propertyName || "foreground" == propertyName) {
            final JLabel label = (JLabel)propertyChangeEvent.getSource();
            BasicHTML.updateRenderer(label, label.getText());
        }
        else if (propertyName == "labelFor" || propertyName == "displayedMnemonic") {
            this.installKeyboardActions((JLabel)propertyChangeEvent.getSource());
        }
    }
    
    static {
        BasicLabelUI.labelUI = new BasicLabelUI();
        BASIC_LABEL_UI_KEY = new Object();
    }
    
    private static class Actions extends UIAction
    {
        private static final String PRESS = "press";
        private static final String RELEASE = "release";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JLabel label = (JLabel)actionEvent.getSource();
            final String name = this.getName();
            if (name == "press") {
                this.doPress(label);
            }
            else if (name == "release") {
                this.doRelease(label, actionEvent.getActionCommand() != null);
            }
        }
        
        private void doPress(final JLabel label) {
            final Component label2 = label.getLabelFor();
            if (label2 != null && label2.isEnabled()) {
                InputMap uiInputMap = SwingUtilities.getUIInputMap(label, 0);
                if (uiInputMap == null) {
                    uiInputMap = new InputMapUIResource();
                    SwingUtilities.replaceUIInputMap(label, 0, uiInputMap);
                }
                final int displayedMnemonic = label.getDisplayedMnemonic();
                this.putOnRelease(uiInputMap, displayedMnemonic, BasicLookAndFeel.getFocusAcceleratorKeyMask());
                this.putOnRelease(uiInputMap, displayedMnemonic, 0);
                this.putOnRelease(uiInputMap, 18, 0);
                label.requestFocus();
            }
        }
        
        private void doRelease(final JLabel label, final boolean b) {
            final Component label2 = label.getLabelFor();
            if (label2 != null && label2.isEnabled()) {
                if (label.hasFocus()) {
                    final InputMap uiInputMap = SwingUtilities.getUIInputMap(label, 0);
                    if (uiInputMap != null) {
                        final int displayedMnemonic = label.getDisplayedMnemonic();
                        this.removeOnRelease(uiInputMap, displayedMnemonic, BasicLookAndFeel.getFocusAcceleratorKeyMask());
                        this.removeOnRelease(uiInputMap, displayedMnemonic, 0);
                        this.removeOnRelease(uiInputMap, 18, 0);
                    }
                    InputMap uiInputMap2 = SwingUtilities.getUIInputMap(label, 2);
                    if (uiInputMap2 == null) {
                        uiInputMap2 = new InputMapUIResource();
                        SwingUtilities.replaceUIInputMap(label, 2, uiInputMap2);
                    }
                    final int displayedMnemonic2 = label.getDisplayedMnemonic();
                    if (b) {
                        this.putOnRelease(uiInputMap2, 18, 0);
                    }
                    else {
                        this.putOnRelease(uiInputMap2, displayedMnemonic2, BasicLookAndFeel.getFocusAcceleratorKeyMask());
                        this.putOnRelease(uiInputMap2, displayedMnemonic2, 0);
                    }
                    if (label2 instanceof Container && ((Container)label2).isFocusCycleRoot()) {
                        label2.requestFocus();
                    }
                    else {
                        SwingUtilities2.compositeRequestFocus(label2);
                    }
                }
                else {
                    final InputMap uiInputMap3 = SwingUtilities.getUIInputMap(label, 2);
                    final int displayedMnemonic3 = label.getDisplayedMnemonic();
                    if (uiInputMap3 != null) {
                        if (b) {
                            this.removeOnRelease(uiInputMap3, displayedMnemonic3, BasicLookAndFeel.getFocusAcceleratorKeyMask());
                            this.removeOnRelease(uiInputMap3, displayedMnemonic3, 0);
                        }
                        else {
                            this.removeOnRelease(uiInputMap3, 18, 0);
                        }
                    }
                }
            }
        }
        
        private void putOnRelease(final InputMap inputMap, final int n, final int n2) {
            inputMap.put(KeyStroke.getKeyStroke(n, n2, true), "release");
        }
        
        private void removeOnRelease(final InputMap inputMap, final int n, final int n2) {
            inputMap.remove(KeyStroke.getKeyStroke(n, n2, true));
        }
    }
}
