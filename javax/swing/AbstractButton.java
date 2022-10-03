package javax.swing;

import javax.accessibility.AccessibleKeyBinding;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import java.text.BreakIterator;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;
import java.awt.Shape;
import javax.swing.text.Position;
import java.awt.geom.Rectangle2D;
import java.awt.Point;
import javax.swing.text.View;
import java.util.Enumeration;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleExtendedComponent;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleAction;
import java.io.Serializable;
import java.beans.PropertyChangeEvent;
import javax.accessibility.AccessibleContext;
import java.awt.Image;
import javax.accessibility.AccessibleState;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.LayoutManager;
import java.awt.Container;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ButtonUI;
import java.awt.Graphics;
import java.beans.Transient;
import javax.swing.plaf.UIResource;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeListener;
import java.awt.Insets;
import java.awt.ItemSelectable;

public abstract class AbstractButton extends JComponent implements ItemSelectable, SwingConstants
{
    public static final String MODEL_CHANGED_PROPERTY = "model";
    public static final String TEXT_CHANGED_PROPERTY = "text";
    public static final String MNEMONIC_CHANGED_PROPERTY = "mnemonic";
    public static final String MARGIN_CHANGED_PROPERTY = "margin";
    public static final String VERTICAL_ALIGNMENT_CHANGED_PROPERTY = "verticalAlignment";
    public static final String HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY = "horizontalAlignment";
    public static final String VERTICAL_TEXT_POSITION_CHANGED_PROPERTY = "verticalTextPosition";
    public static final String HORIZONTAL_TEXT_POSITION_CHANGED_PROPERTY = "horizontalTextPosition";
    public static final String BORDER_PAINTED_CHANGED_PROPERTY = "borderPainted";
    public static final String FOCUS_PAINTED_CHANGED_PROPERTY = "focusPainted";
    public static final String ROLLOVER_ENABLED_CHANGED_PROPERTY = "rolloverEnabled";
    public static final String CONTENT_AREA_FILLED_CHANGED_PROPERTY = "contentAreaFilled";
    public static final String ICON_CHANGED_PROPERTY = "icon";
    public static final String PRESSED_ICON_CHANGED_PROPERTY = "pressedIcon";
    public static final String SELECTED_ICON_CHANGED_PROPERTY = "selectedIcon";
    public static final String ROLLOVER_ICON_CHANGED_PROPERTY = "rolloverIcon";
    public static final String ROLLOVER_SELECTED_ICON_CHANGED_PROPERTY = "rolloverSelectedIcon";
    public static final String DISABLED_ICON_CHANGED_PROPERTY = "disabledIcon";
    public static final String DISABLED_SELECTED_ICON_CHANGED_PROPERTY = "disabledSelectedIcon";
    protected ButtonModel model;
    private String text;
    private Insets margin;
    private Insets defaultMargin;
    private Icon defaultIcon;
    private Icon pressedIcon;
    private Icon disabledIcon;
    private Icon selectedIcon;
    private Icon disabledSelectedIcon;
    private Icon rolloverIcon;
    private Icon rolloverSelectedIcon;
    private boolean paintBorder;
    private boolean paintFocus;
    private boolean rolloverEnabled;
    private boolean contentAreaFilled;
    private int verticalAlignment;
    private int horizontalAlignment;
    private int verticalTextPosition;
    private int horizontalTextPosition;
    private int iconTextGap;
    private int mnemonic;
    private int mnemonicIndex;
    private long multiClickThreshhold;
    private boolean borderPaintedSet;
    private boolean rolloverEnabledSet;
    private boolean iconTextGapSet;
    private boolean contentAreaFilledSet;
    private boolean setLayout;
    boolean defaultCapable;
    private Handler handler;
    protected ChangeListener changeListener;
    protected ActionListener actionListener;
    protected ItemListener itemListener;
    protected transient ChangeEvent changeEvent;
    private boolean hideActionText;
    private Action action;
    private PropertyChangeListener actionPropertyChangeListener;
    
    public AbstractButton() {
        this.model = null;
        this.text = "";
        this.margin = null;
        this.defaultMargin = null;
        this.defaultIcon = null;
        this.pressedIcon = null;
        this.disabledIcon = null;
        this.selectedIcon = null;
        this.disabledSelectedIcon = null;
        this.rolloverIcon = null;
        this.rolloverSelectedIcon = null;
        this.paintBorder = true;
        this.paintFocus = true;
        this.rolloverEnabled = false;
        this.contentAreaFilled = true;
        this.verticalAlignment = 0;
        this.horizontalAlignment = 0;
        this.verticalTextPosition = 0;
        this.horizontalTextPosition = 11;
        this.iconTextGap = 4;
        this.mnemonicIndex = -1;
        this.multiClickThreshhold = 0L;
        this.borderPaintedSet = false;
        this.rolloverEnabledSet = false;
        this.iconTextGapSet = false;
        this.contentAreaFilledSet = false;
        this.setLayout = false;
        this.defaultCapable = true;
        this.changeListener = null;
        this.actionListener = null;
        this.itemListener = null;
        this.hideActionText = false;
    }
    
    public void setHideActionText(final boolean hideActionText) {
        if (hideActionText != this.hideActionText) {
            this.hideActionText = hideActionText;
            if (this.getAction() != null) {
                this.setTextFromAction(this.getAction(), false);
            }
            this.firePropertyChange("hideActionText", !hideActionText, hideActionText);
        }
    }
    
    public boolean getHideActionText() {
        return this.hideActionText;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        final String text2 = this.text;
        this.firePropertyChange("text", text2, this.text = text);
        this.updateDisplayedMnemonicIndex(text, this.getMnemonic());
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", text2, text);
        }
        if (text == null || text2 == null || !text.equals(text2)) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public boolean isSelected() {
        return this.model.isSelected();
    }
    
    public void setSelected(final boolean selected) {
        this.isSelected();
        this.model.setSelected(selected);
    }
    
    public void doClick() {
        this.doClick(68);
    }
    
    public void doClick(final int n) {
        final Dimension size = this.getSize();
        this.model.setArmed(true);
        this.model.setPressed(true);
        this.paintImmediately(new Rectangle(0, 0, size.width, size.height));
        try {
            Thread.currentThread();
            Thread.sleep(n);
        }
        catch (final InterruptedException ex) {}
        this.model.setPressed(false);
        this.model.setArmed(false);
    }
    
    public void setMargin(Insets defaultMargin) {
        if (defaultMargin instanceof UIResource) {
            this.defaultMargin = defaultMargin;
        }
        else if (this.margin instanceof UIResource) {
            this.defaultMargin = this.margin;
        }
        if (defaultMargin == null && this.defaultMargin != null) {
            defaultMargin = this.defaultMargin;
        }
        final Insets margin = this.margin;
        this.firePropertyChange("margin", margin, this.margin = defaultMargin);
        if (margin == null || !margin.equals(defaultMargin)) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public Insets getMargin() {
        return (this.margin == null) ? null : ((Insets)this.margin.clone());
    }
    
    public Icon getIcon() {
        return this.defaultIcon;
    }
    
    public void setIcon(final Icon defaultIcon) {
        final Icon defaultIcon2 = this.defaultIcon;
        this.defaultIcon = defaultIcon;
        if (defaultIcon != defaultIcon2 && this.disabledIcon instanceof UIResource) {
            this.disabledIcon = null;
        }
        this.firePropertyChange("icon", defaultIcon2, defaultIcon);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", defaultIcon2, defaultIcon);
        }
        if (defaultIcon != defaultIcon2) {
            if (defaultIcon == null || defaultIcon2 == null || defaultIcon.getIconWidth() != defaultIcon2.getIconWidth() || defaultIcon.getIconHeight() != defaultIcon2.getIconHeight()) {
                this.revalidate();
            }
            this.repaint();
        }
    }
    
    public Icon getPressedIcon() {
        return this.pressedIcon;
    }
    
    public void setPressedIcon(final Icon pressedIcon) {
        final Icon pressedIcon2 = this.pressedIcon;
        this.firePropertyChange("pressedIcon", pressedIcon2, this.pressedIcon = pressedIcon);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", pressedIcon2, pressedIcon);
        }
        if (pressedIcon != pressedIcon2 && this.getModel().isPressed()) {
            this.repaint();
        }
    }
    
    public Icon getSelectedIcon() {
        return this.selectedIcon;
    }
    
    public void setSelectedIcon(final Icon selectedIcon) {
        final Icon selectedIcon2 = this.selectedIcon;
        this.selectedIcon = selectedIcon;
        if (selectedIcon != selectedIcon2 && this.disabledSelectedIcon instanceof UIResource) {
            this.disabledSelectedIcon = null;
        }
        this.firePropertyChange("selectedIcon", selectedIcon2, selectedIcon);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", selectedIcon2, selectedIcon);
        }
        if (selectedIcon != selectedIcon2 && this.isSelected()) {
            this.repaint();
        }
    }
    
    public Icon getRolloverIcon() {
        return this.rolloverIcon;
    }
    
    public void setRolloverIcon(final Icon rolloverIcon) {
        final Icon rolloverIcon2 = this.rolloverIcon;
        this.firePropertyChange("rolloverIcon", rolloverIcon2, this.rolloverIcon = rolloverIcon);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", rolloverIcon2, rolloverIcon);
        }
        this.setRolloverEnabled(true);
        if (rolloverIcon != rolloverIcon2) {
            this.repaint();
        }
    }
    
    public Icon getRolloverSelectedIcon() {
        return this.rolloverSelectedIcon;
    }
    
    public void setRolloverSelectedIcon(final Icon rolloverSelectedIcon) {
        final Icon rolloverSelectedIcon2 = this.rolloverSelectedIcon;
        this.firePropertyChange("rolloverSelectedIcon", rolloverSelectedIcon2, this.rolloverSelectedIcon = rolloverSelectedIcon);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", rolloverSelectedIcon2, rolloverSelectedIcon);
        }
        this.setRolloverEnabled(true);
        if (rolloverSelectedIcon != rolloverSelectedIcon2 && this.isSelected()) {
            this.repaint();
        }
    }
    
    @Transient
    public Icon getDisabledIcon() {
        if (this.disabledIcon == null) {
            this.disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, this.getIcon());
            if (this.disabledIcon != null) {
                this.firePropertyChange("disabledIcon", null, this.disabledIcon);
            }
        }
        return this.disabledIcon;
    }
    
    public void setDisabledIcon(final Icon disabledIcon) {
        final Icon disabledIcon2 = this.disabledIcon;
        this.firePropertyChange("disabledIcon", disabledIcon2, this.disabledIcon = disabledIcon);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", disabledIcon2, disabledIcon);
        }
        if (disabledIcon != disabledIcon2 && !this.isEnabled()) {
            this.repaint();
        }
    }
    
    public Icon getDisabledSelectedIcon() {
        if (this.disabledSelectedIcon == null) {
            if (this.selectedIcon == null) {
                return this.getDisabledIcon();
            }
            this.disabledSelectedIcon = UIManager.getLookAndFeel().getDisabledSelectedIcon(this, this.getSelectedIcon());
        }
        return this.disabledSelectedIcon;
    }
    
    public void setDisabledSelectedIcon(final Icon disabledSelectedIcon) {
        final Icon disabledSelectedIcon2 = this.disabledSelectedIcon;
        this.firePropertyChange("disabledSelectedIcon", disabledSelectedIcon2, this.disabledSelectedIcon = disabledSelectedIcon);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", disabledSelectedIcon2, disabledSelectedIcon);
        }
        if (disabledSelectedIcon != disabledSelectedIcon2) {
            if (disabledSelectedIcon == null || disabledSelectedIcon2 == null || disabledSelectedIcon.getIconWidth() != disabledSelectedIcon2.getIconWidth() || disabledSelectedIcon.getIconHeight() != disabledSelectedIcon2.getIconHeight()) {
                this.revalidate();
            }
            if (!this.isEnabled() && this.isSelected()) {
                this.repaint();
            }
        }
    }
    
    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }
    
    public void setVerticalAlignment(final int n) {
        if (n == this.verticalAlignment) {
            return;
        }
        this.firePropertyChange("verticalAlignment", this.verticalAlignment, this.verticalAlignment = this.checkVerticalKey(n, "verticalAlignment"));
        this.repaint();
    }
    
    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
    
    public void setHorizontalAlignment(final int n) {
        if (n == this.horizontalAlignment) {
            return;
        }
        this.firePropertyChange("horizontalAlignment", this.horizontalAlignment, this.horizontalAlignment = this.checkHorizontalKey(n, "horizontalAlignment"));
        this.repaint();
    }
    
    public int getVerticalTextPosition() {
        return this.verticalTextPosition;
    }
    
    public void setVerticalTextPosition(final int n) {
        if (n == this.verticalTextPosition) {
            return;
        }
        this.firePropertyChange("verticalTextPosition", this.verticalTextPosition, this.verticalTextPosition = this.checkVerticalKey(n, "verticalTextPosition"));
        this.revalidate();
        this.repaint();
    }
    
    public int getHorizontalTextPosition() {
        return this.horizontalTextPosition;
    }
    
    public void setHorizontalTextPosition(final int n) {
        if (n == this.horizontalTextPosition) {
            return;
        }
        this.firePropertyChange("horizontalTextPosition", this.horizontalTextPosition, this.horizontalTextPosition = this.checkHorizontalKey(n, "horizontalTextPosition"));
        this.revalidate();
        this.repaint();
    }
    
    public int getIconTextGap() {
        return this.iconTextGap;
    }
    
    public void setIconTextGap(final int iconTextGap) {
        final int iconTextGap2 = this.iconTextGap;
        this.iconTextGap = iconTextGap;
        this.iconTextGapSet = true;
        this.firePropertyChange("iconTextGap", iconTextGap2, iconTextGap);
        if (iconTextGap != iconTextGap2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    protected int checkHorizontalKey(final int n, final String s) {
        if (n == 2 || n == 0 || n == 4 || n == 10 || n == 11) {
            return n;
        }
        throw new IllegalArgumentException(s);
    }
    
    protected int checkVerticalKey(final int n, final String s) {
        if (n == 1 || n == 0 || n == 3) {
            return n;
        }
        throw new IllegalArgumentException(s);
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        if (this.isRolloverEnabled()) {
            this.getModel().setRollover(false);
        }
    }
    
    public void setActionCommand(final String actionCommand) {
        this.getModel().setActionCommand(actionCommand);
    }
    
    public String getActionCommand() {
        String s = this.getModel().getActionCommand();
        if (s == null) {
            s = this.getText();
        }
        return s;
    }
    
    public void setAction(final Action action) {
        final Action action2 = this.getAction();
        if (this.action == null || !this.action.equals(action)) {
            this.action = action;
            if (action2 != null) {
                this.removeActionListener(action2);
                action2.removePropertyChangeListener(this.actionPropertyChangeListener);
                this.actionPropertyChangeListener = null;
            }
            this.configurePropertiesFromAction(this.action);
            if (this.action != null) {
                if (!this.isListener(ActionListener.class, this.action)) {
                    this.addActionListener(this.action);
                }
                this.actionPropertyChangeListener = this.createActionPropertyChangeListener(this.action);
                this.action.addPropertyChangeListener(this.actionPropertyChangeListener);
            }
            this.firePropertyChange("action", action2, this.action);
        }
    }
    
    private boolean isListener(final Class clazz, final ActionListener actionListener) {
        boolean b = false;
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == clazz && listenerList[i + 1] == actionListener) {
                b = true;
            }
        }
        return b;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    protected void configurePropertiesFromAction(final Action action) {
        this.setMnemonicFromAction(action);
        this.setTextFromAction(action, false);
        AbstractAction.setToolTipTextFromAction(this, action);
        this.setIconFromAction(action);
        this.setActionCommandFromAction(action);
        AbstractAction.setEnabledFromAction(this, action);
        if (AbstractAction.hasSelectedKey(action) && this.shouldUpdateSelectedStateFromAction()) {
            this.setSelectedFromAction(action);
        }
        this.setDisplayedMnemonicIndexFromAction(action, false);
    }
    
    @Override
    void clientPropertyChanged(final Object o, final Object o2, final Object o3) {
        if (o == "hideActionText") {
            final boolean hideActionText = o3 instanceof Boolean && (boolean)o3;
            if (this.getHideActionText() != hideActionText) {
                this.setHideActionText(hideActionText);
            }
        }
    }
    
    boolean shouldUpdateSelectedStateFromAction() {
        return false;
    }
    
    protected void actionPropertyChanged(final Action selectedFromAction, final String s) {
        if (s == "Name") {
            this.setTextFromAction(selectedFromAction, true);
        }
        else if (s == "enabled") {
            AbstractAction.setEnabledFromAction(this, selectedFromAction);
        }
        else if (s == "ShortDescription") {
            AbstractAction.setToolTipTextFromAction(this, selectedFromAction);
        }
        else if (s == "SmallIcon") {
            this.smallIconChanged(selectedFromAction);
        }
        else if (s == "MnemonicKey") {
            this.setMnemonicFromAction(selectedFromAction);
        }
        else if (s == "ActionCommandKey") {
            this.setActionCommandFromAction(selectedFromAction);
        }
        else if (s == "SwingSelectedKey" && AbstractAction.hasSelectedKey(selectedFromAction) && this.shouldUpdateSelectedStateFromAction()) {
            this.setSelectedFromAction(selectedFromAction);
        }
        else if (s == "SwingDisplayedMnemonicIndexKey") {
            this.setDisplayedMnemonicIndexFromAction(selectedFromAction, true);
        }
        else if (s == "SwingLargeIconKey") {
            this.largeIconChanged(selectedFromAction);
        }
    }
    
    private void setDisplayedMnemonicIndexFromAction(final Action action, final boolean b) {
        final Integer n = (action == null) ? null : ((Integer)action.getValue("SwingDisplayedMnemonicIndexKey"));
        if (b || n != null) {
            int intValue;
            if (n == null) {
                intValue = -1;
            }
            else {
                intValue = n;
                final String text = this.getText();
                if (text == null || intValue >= text.length()) {
                    intValue = -1;
                }
            }
            this.setDisplayedMnemonicIndex(intValue);
        }
    }
    
    private void setMnemonicFromAction(final Action action) {
        final Integer n = (action == null) ? null : ((Integer)action.getValue("MnemonicKey"));
        this.setMnemonic((n == null) ? 0 : ((int)n));
    }
    
    private void setTextFromAction(final Action action, final boolean b) {
        final boolean hideActionText = this.getHideActionText();
        if (!b) {
            this.setText((action != null && !hideActionText) ? ((String)action.getValue("Name")) : null);
        }
        else if (!hideActionText) {
            this.setText((String)action.getValue("Name"));
        }
    }
    
    void setIconFromAction(final Action action) {
        Icon icon = null;
        if (action != null) {
            icon = (Icon)action.getValue("SwingLargeIconKey");
            if (icon == null) {
                icon = (Icon)action.getValue("SmallIcon");
            }
        }
        this.setIcon(icon);
    }
    
    void smallIconChanged(final Action iconFromAction) {
        if (iconFromAction.getValue("SwingLargeIconKey") == null) {
            this.setIconFromAction(iconFromAction);
        }
    }
    
    void largeIconChanged(final Action iconFromAction) {
        this.setIconFromAction(iconFromAction);
    }
    
    private void setActionCommandFromAction(final Action action) {
        this.setActionCommand((action != null) ? ((String)action.getValue("ActionCommandKey")) : null);
    }
    
    private void setSelectedFromAction(final Action action) {
        boolean selected = false;
        if (action != null) {
            selected = AbstractAction.isSelected(action);
        }
        if (selected != this.isSelected()) {
            this.setSelected(selected);
            if (!selected && this.isSelected() && this.getModel() instanceof DefaultButtonModel) {
                final ButtonGroup group = ((DefaultButtonModel)this.getModel()).getGroup();
                if (group != null) {
                    group.clearSelection();
                }
            }
        }
    }
    
    protected PropertyChangeListener createActionPropertyChangeListener(final Action action) {
        return this.createActionPropertyChangeListener0(action);
    }
    
    PropertyChangeListener createActionPropertyChangeListener0(final Action action) {
        return new ButtonActionPropertyChangeListener(this, action);
    }
    
    public boolean isBorderPainted() {
        return this.paintBorder;
    }
    
    public void setBorderPainted(final boolean paintBorder) {
        final boolean paintBorder2 = this.paintBorder;
        this.paintBorder = paintBorder;
        this.borderPaintedSet = true;
        this.firePropertyChange("borderPainted", paintBorder2, this.paintBorder);
        if (paintBorder != paintBorder2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    @Override
    protected void paintBorder(final Graphics graphics) {
        if (this.isBorderPainted()) {
            super.paintBorder(graphics);
        }
    }
    
    public boolean isFocusPainted() {
        return this.paintFocus;
    }
    
    public void setFocusPainted(final boolean paintFocus) {
        final boolean paintFocus2 = this.paintFocus;
        this.firePropertyChange("focusPainted", paintFocus2, this.paintFocus = paintFocus);
        if (paintFocus != paintFocus2 && this.isFocusOwner()) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public boolean isContentAreaFilled() {
        return this.contentAreaFilled;
    }
    
    public void setContentAreaFilled(final boolean contentAreaFilled) {
        final boolean contentAreaFilled2 = this.contentAreaFilled;
        this.contentAreaFilled = contentAreaFilled;
        this.contentAreaFilledSet = true;
        this.firePropertyChange("contentAreaFilled", contentAreaFilled2, this.contentAreaFilled);
        if (contentAreaFilled != contentAreaFilled2) {
            this.repaint();
        }
    }
    
    public boolean isRolloverEnabled() {
        return this.rolloverEnabled;
    }
    
    public void setRolloverEnabled(final boolean rolloverEnabled) {
        final boolean rolloverEnabled2 = this.rolloverEnabled;
        this.rolloverEnabled = rolloverEnabled;
        this.rolloverEnabledSet = true;
        this.firePropertyChange("rolloverEnabled", rolloverEnabled2, this.rolloverEnabled);
        if (rolloverEnabled != rolloverEnabled2) {
            this.repaint();
        }
    }
    
    public int getMnemonic() {
        return this.mnemonic;
    }
    
    public void setMnemonic(final int mnemonic) {
        this.getMnemonic();
        this.model.setMnemonic(mnemonic);
        this.updateMnemonicProperties();
    }
    
    public void setMnemonic(final char c) {
        int mnemonic = c;
        if (mnemonic >= 97 && mnemonic <= 122) {
            mnemonic -= 32;
        }
        this.setMnemonic(mnemonic);
    }
    
    public void setDisplayedMnemonicIndex(final int mnemonicIndex) throws IllegalArgumentException {
        final int mnemonicIndex2 = this.mnemonicIndex;
        if (mnemonicIndex == -1) {
            this.mnemonicIndex = -1;
        }
        else {
            final String text = this.getText();
            final int n = (text == null) ? 0 : text.length();
            if (mnemonicIndex < -1 || mnemonicIndex >= n) {
                throw new IllegalArgumentException("index == " + mnemonicIndex);
            }
        }
        this.firePropertyChange("displayedMnemonicIndex", mnemonicIndex2, this.mnemonicIndex = mnemonicIndex);
        if (mnemonicIndex != mnemonicIndex2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public int getDisplayedMnemonicIndex() {
        return this.mnemonicIndex;
    }
    
    private void updateDisplayedMnemonicIndex(final String s, final int n) {
        this.setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(s, n));
    }
    
    private void updateMnemonicProperties() {
        final int mnemonic = this.model.getMnemonic();
        if (this.mnemonic != mnemonic) {
            this.firePropertyChange("mnemonic", this.mnemonic, this.mnemonic = mnemonic);
            this.updateDisplayedMnemonicIndex(this.getText(), this.mnemonic);
            this.revalidate();
            this.repaint();
        }
    }
    
    public void setMultiClickThreshhold(final long multiClickThreshhold) {
        if (multiClickThreshhold < 0L) {
            throw new IllegalArgumentException("threshhold must be >= 0");
        }
        this.multiClickThreshhold = multiClickThreshhold;
    }
    
    public long getMultiClickThreshhold() {
        return this.multiClickThreshhold;
    }
    
    public ButtonModel getModel() {
        return this.model;
    }
    
    public void setModel(final ButtonModel model) {
        final ButtonModel model2 = this.getModel();
        if (model2 != null) {
            model2.removeChangeListener(this.changeListener);
            model2.removeActionListener(this.actionListener);
            model2.removeItemListener(this.itemListener);
            this.changeListener = null;
            this.actionListener = null;
            this.itemListener = null;
        }
        if ((this.model = model) != null) {
            this.changeListener = this.createChangeListener();
            this.actionListener = this.createActionListener();
            this.itemListener = this.createItemListener();
            model.addChangeListener(this.changeListener);
            model.addActionListener(this.actionListener);
            model.addItemListener(this.itemListener);
            this.updateMnemonicProperties();
            super.setEnabled(model.isEnabled());
        }
        else {
            this.mnemonic = 0;
        }
        this.updateDisplayedMnemonicIndex(this.getText(), this.mnemonic);
        this.firePropertyChange("model", model2, model);
        if (model != model2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public ButtonUI getUI() {
        return (ButtonUI)this.ui;
    }
    
    public void setUI(final ButtonUI ui) {
        super.setUI(ui);
        if (this.disabledIcon instanceof UIResource) {
            this.setDisabledIcon(null);
        }
        if (this.disabledSelectedIcon instanceof UIResource) {
            this.setDisabledSelectedIcon(null);
        }
    }
    
    @Override
    public void updateUI() {
    }
    
    @Override
    protected void addImpl(final Component component, final Object o, final int n) {
        if (!this.setLayout) {
            this.setLayout(new OverlayLayout(this));
        }
        super.addImpl(component, o, n);
    }
    
    @Override
    public void setLayout(final LayoutManager layout) {
        this.setLayout = true;
        super.setLayout(layout);
    }
    
    public void addChangeListener(final ChangeListener changeListener) {
        this.listenerList.add(ChangeListener.class, changeListener);
    }
    
    public void removeChangeListener(final ChangeListener changeListener) {
        this.listenerList.remove(ChangeListener.class, changeListener);
    }
    
    public ChangeListener[] getChangeListeners() {
        return this.listenerList.getListeners(ChangeListener.class);
    }
    
    protected void fireStateChanged() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ChangeListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listenerList[i + 1]).stateChanged(this.changeEvent);
            }
        }
    }
    
    public void addActionListener(final ActionListener actionListener) {
        this.listenerList.add(ActionListener.class, actionListener);
    }
    
    public void removeActionListener(final ActionListener actionListener) {
        if (actionListener != null && this.getAction() == actionListener) {
            this.setAction(null);
        }
        else {
            this.listenerList.remove(ActionListener.class, actionListener);
        }
    }
    
    public ActionListener[] getActionListeners() {
        return this.listenerList.getListeners(ActionListener.class);
    }
    
    protected ChangeListener createChangeListener() {
        return this.getHandler();
    }
    
    protected void fireActionPerformed(final ActionEvent actionEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        ActionEvent actionEvent2 = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ActionListener.class) {
                if (actionEvent2 == null) {
                    String s = actionEvent.getActionCommand();
                    if (s == null) {
                        s = this.getActionCommand();
                    }
                    actionEvent2 = new ActionEvent(this, 1001, s, actionEvent.getWhen(), actionEvent.getModifiers());
                }
                ((ActionListener)listenerList[i + 1]).actionPerformed(actionEvent2);
            }
        }
    }
    
    protected void fireItemStateChanged(final ItemEvent itemEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        ItemEvent itemEvent2 = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ItemListener.class) {
                if (itemEvent2 == null) {
                    itemEvent2 = new ItemEvent(this, 701, this, itemEvent.getStateChange());
                }
                ((ItemListener)listenerList[i + 1]).itemStateChanged(itemEvent2);
            }
        }
        if (this.accessibleContext != null) {
            if (itemEvent.getStateChange() == 1) {
                this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.SELECTED);
                this.accessibleContext.firePropertyChange("AccessibleValue", 0, 1);
            }
            else {
                this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.SELECTED, null);
                this.accessibleContext.firePropertyChange("AccessibleValue", 1, 0);
            }
        }
    }
    
    protected ActionListener createActionListener() {
        return this.getHandler();
    }
    
    protected ItemListener createItemListener() {
        return this.getHandler();
    }
    
    @Override
    public void setEnabled(final boolean b) {
        if (!b && this.model.isRollover()) {
            this.model.setRollover(false);
        }
        super.setEnabled(b);
        this.model.setEnabled(b);
    }
    
    @Deprecated
    public String getLabel() {
        return this.getText();
    }
    
    @Deprecated
    public void setLabel(final String text) {
        this.setText(text);
    }
    
    @Override
    public void addItemListener(final ItemListener itemListener) {
        this.listenerList.add(ItemListener.class, itemListener);
    }
    
    @Override
    public void removeItemListener(final ItemListener itemListener) {
        this.listenerList.remove(ItemListener.class, itemListener);
    }
    
    public ItemListener[] getItemListeners() {
        return this.listenerList.getListeners(ItemListener.class);
    }
    
    @Override
    public Object[] getSelectedObjects() {
        if (!this.isSelected()) {
            return null;
        }
        return new Object[] { this.getText() };
    }
    
    protected void init(final String text, final Icon icon) {
        if (text != null) {
            this.setText(text);
        }
        if (icon != null) {
            this.setIcon(icon);
        }
        this.updateUI();
        this.setAlignmentX(0.0f);
        this.setAlignmentY(0.5f);
    }
    
    @Override
    public boolean imageUpdate(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
        Icon icon = null;
        if (!this.model.isEnabled()) {
            if (this.model.isSelected()) {
                icon = this.getDisabledSelectedIcon();
            }
            else {
                icon = this.getDisabledIcon();
            }
        }
        else if (this.model.isPressed() && this.model.isArmed()) {
            icon = this.getPressedIcon();
        }
        else if (this.isRolloverEnabled() && this.model.isRollover()) {
            if (this.model.isSelected()) {
                icon = this.getRolloverSelectedIcon();
            }
            else {
                icon = this.getRolloverIcon();
            }
        }
        else if (this.model.isSelected()) {
            icon = this.getSelectedIcon();
        }
        if (icon == null) {
            icon = this.getIcon();
        }
        return icon != null && SwingUtilities.doesIconReferenceImage(icon, image) && super.imageUpdate(image, n, n2, n3, n4, n5);
    }
    
    @Override
    void setUIProperty(final String s, final Object o) {
        if (s == "borderPainted") {
            if (!this.borderPaintedSet) {
                this.setBorderPainted((boolean)o);
                this.borderPaintedSet = false;
            }
        }
        else if (s == "rolloverEnabled") {
            if (!this.rolloverEnabledSet) {
                this.setRolloverEnabled((boolean)o);
                this.rolloverEnabledSet = false;
            }
        }
        else if (s == "iconTextGap") {
            if (!this.iconTextGapSet) {
                this.setIconTextGap(((Number)o).intValue());
                this.iconTextGapSet = false;
            }
        }
        else if (s == "contentAreaFilled") {
            if (!this.contentAreaFilledSet) {
                this.setContentAreaFilled((boolean)o);
                this.contentAreaFilledSet = false;
            }
        }
        else {
            super.setUIProperty(s, o);
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",defaultIcon=" + ((this.defaultIcon != null && this.defaultIcon != this) ? this.defaultIcon.toString() : "") + ",disabledIcon=" + ((this.disabledIcon != null && this.disabledIcon != this) ? this.disabledIcon.toString() : "") + ",disabledSelectedIcon=" + ((this.disabledSelectedIcon != null && this.disabledSelectedIcon != this) ? this.disabledSelectedIcon.toString() : "") + ",margin=" + this.margin + ",paintBorder=" + (this.paintBorder ? "true" : "false") + ",paintFocus=" + (this.paintFocus ? "true" : "false") + ",pressedIcon=" + ((this.pressedIcon != null && this.pressedIcon != this) ? this.pressedIcon.toString() : "") + ",rolloverEnabled=" + (this.rolloverEnabled ? "true" : "false") + ",rolloverIcon=" + ((this.rolloverIcon != null && this.rolloverIcon != this) ? this.rolloverIcon.toString() : "") + ",rolloverSelectedIcon=" + ((this.rolloverSelectedIcon != null && this.rolloverSelectedIcon != this) ? this.rolloverSelectedIcon.toString() : "") + ",selectedIcon=" + ((this.selectedIcon != null && this.selectedIcon != this) ? this.selectedIcon.toString() : "") + ",text=" + this.text;
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    private static class ButtonActionPropertyChangeListener extends ActionPropertyChangeListener<AbstractButton>
    {
        ButtonActionPropertyChangeListener(final AbstractButton abstractButton, final Action action) {
            super(abstractButton, action);
        }
        
        @Override
        protected void actionPropertyChanged(final AbstractButton abstractButton, final Action action, final PropertyChangeEvent propertyChangeEvent) {
            if (AbstractAction.shouldReconfigure(propertyChangeEvent)) {
                abstractButton.configurePropertiesFromAction(action);
            }
            else {
                abstractButton.actionPropertyChanged(action, propertyChangeEvent.getPropertyName());
            }
        }
    }
    
    protected class ButtonChangeListener implements ChangeListener, Serializable
    {
        ButtonChangeListener() {
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            AbstractButton.this.getHandler().stateChanged(changeEvent);
        }
    }
    
    class Handler implements ActionListener, ChangeListener, ItemListener, Serializable
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            changeEvent.getSource();
            AbstractButton.this.updateMnemonicProperties();
            if (AbstractButton.this.isEnabled() != AbstractButton.this.model.isEnabled()) {
                AbstractButton.this.setEnabled(AbstractButton.this.model.isEnabled());
            }
            AbstractButton.this.fireStateChanged();
            AbstractButton.this.repaint();
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            AbstractButton.this.fireActionPerformed(actionEvent);
        }
        
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
            AbstractButton.this.fireItemStateChanged(itemEvent);
            if (AbstractButton.this.shouldUpdateSelectedStateFromAction()) {
                final Action action = AbstractButton.this.getAction();
                if (action != null && AbstractAction.hasSelectedKey(action)) {
                    final boolean selected = AbstractButton.this.isSelected();
                    if (AbstractAction.isSelected(action) != selected) {
                        action.putValue("SwingSelectedKey", selected);
                    }
                }
            }
        }
    }
    
    protected abstract class AccessibleAbstractButton extends AccessibleJComponent implements AccessibleAction, AccessibleValue, AccessibleText, AccessibleExtendedComponent
    {
        @Override
        public String getAccessibleName() {
            String s = this.accessibleName;
            if (s == null) {
                s = (String)AbstractButton.this.getClientProperty("AccessibleName");
            }
            if (s == null) {
                s = AbstractButton.this.getText();
            }
            if (s == null) {
                s = super.getAccessibleName();
            }
            return s;
        }
        
        @Override
        public AccessibleIcon[] getAccessibleIcon() {
            final Icon icon = AbstractButton.this.getIcon();
            if (icon instanceof Accessible) {
                final AccessibleContext accessibleContext = ((Accessible)icon).getAccessibleContext();
                if (accessibleContext != null && accessibleContext instanceof AccessibleIcon) {
                    return new AccessibleIcon[] { (AccessibleIcon)accessibleContext };
                }
            }
            return null;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (AbstractButton.this.getModel().isArmed()) {
                accessibleStateSet.add(AccessibleState.ARMED);
            }
            if (AbstractButton.this.isFocusOwner()) {
                accessibleStateSet.add(AccessibleState.FOCUSED);
            }
            if (AbstractButton.this.getModel().isPressed()) {
                accessibleStateSet.add(AccessibleState.PRESSED);
            }
            if (AbstractButton.this.isSelected()) {
                accessibleStateSet.add(AccessibleState.CHECKED);
            }
            return accessibleStateSet;
        }
        
        @Override
        public AccessibleRelationSet getAccessibleRelationSet() {
            final AccessibleRelationSet accessibleRelationSet = super.getAccessibleRelationSet();
            if (!accessibleRelationSet.contains(AccessibleRelation.MEMBER_OF)) {
                final ButtonModel model = AbstractButton.this.getModel();
                if (model != null && model instanceof DefaultButtonModel) {
                    final ButtonGroup group = ((DefaultButtonModel)model).getGroup();
                    if (group != null) {
                        final int buttonCount = group.getButtonCount();
                        final Object[] target = new Object[buttonCount];
                        final Enumeration<AbstractButton> elements = group.getElements();
                        for (int i = 0; i < buttonCount; ++i) {
                            if (elements.hasMoreElements()) {
                                target[i] = elements.nextElement();
                            }
                        }
                        final AccessibleRelation accessibleRelation = new AccessibleRelation(AccessibleRelation.MEMBER_OF);
                        accessibleRelation.setTarget(target);
                        accessibleRelationSet.add(accessibleRelation);
                    }
                }
            }
            return accessibleRelationSet;
        }
        
        @Override
        public AccessibleAction getAccessibleAction() {
            return this;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }
        
        @Override
        public int getAccessibleActionCount() {
            return 1;
        }
        
        @Override
        public String getAccessibleActionDescription(final int n) {
            if (n == 0) {
                return UIManager.getString("AbstractButton.clickText");
            }
            return null;
        }
        
        @Override
        public boolean doAccessibleAction(final int n) {
            if (n == 0) {
                AbstractButton.this.doClick();
                return true;
            }
            return false;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            if (AbstractButton.this.isSelected()) {
                return 1;
            }
            return 0;
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number n) {
            if (n == null) {
                return false;
            }
            if (n.intValue() == 0) {
                AbstractButton.this.setSelected(false);
            }
            else {
                AbstractButton.this.setSelected(true);
            }
            return true;
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            return 0;
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            return 1;
        }
        
        @Override
        public AccessibleText getAccessibleText() {
            if (AbstractButton.this.getClientProperty("html") != null) {
                return this;
            }
            return null;
        }
        
        @Override
        public int getIndexAtPoint(final Point point) {
            final View view = (View)AbstractButton.this.getClientProperty("html");
            if (view == null) {
                return -1;
            }
            final Rectangle textRectangle = this.getTextRectangle();
            if (textRectangle == null) {
                return -1;
            }
            return view.viewToModel((float)point.x, (float)point.y, new Rectangle2D.Float((float)textRectangle.x, (float)textRectangle.y, (float)textRectangle.width, (float)textRectangle.height), new Position.Bias[1]);
        }
        
        @Override
        public Rectangle getCharacterBounds(final int n) {
            final View view = (View)AbstractButton.this.getClientProperty("html");
            if (view != null) {
                final Rectangle textRectangle = this.getTextRectangle();
                if (textRectangle == null) {
                    return null;
                }
                final Rectangle2D.Float float1 = new Rectangle2D.Float((float)textRectangle.x, (float)textRectangle.y, (float)textRectangle.width, (float)textRectangle.height);
                try {
                    return view.modelToView(n, float1, Position.Bias.Forward).getBounds();
                }
                catch (final BadLocationException ex) {
                    return null;
                }
            }
            return null;
        }
        
        @Override
        public int getCharCount() {
            final View view = (View)AbstractButton.this.getClientProperty("html");
            if (view != null) {
                final Document document = view.getDocument();
                if (document instanceof StyledDocument) {
                    return ((StyledDocument)document).getLength();
                }
            }
            return AbstractButton.this.accessibleContext.getAccessibleName().length();
        }
        
        @Override
        public int getCaretPosition() {
            return -1;
        }
        
        @Override
        public String getAtIndex(final int n, final int n2) {
            if (n2 < 0 || n2 >= this.getCharCount()) {
                return null;
            }
            switch (n) {
                case 1: {
                    try {
                        return this.getText(n2, 1);
                    }
                    catch (final BadLocationException ex) {
                        return null;
                    }
                }
                case 2: {
                    try {
                        final String text = this.getText(0, this.getCharCount());
                        final BreakIterator wordInstance = BreakIterator.getWordInstance(this.getLocale());
                        wordInstance.setText(text);
                        return text.substring(wordInstance.previous(), wordInstance.following(n2));
                    }
                    catch (final BadLocationException ex2) {
                        return null;
                    }
                }
                case 3: {
                    try {
                        final String text2 = this.getText(0, this.getCharCount());
                        final BreakIterator sentenceInstance = BreakIterator.getSentenceInstance(this.getLocale());
                        sentenceInstance.setText(text2);
                        return text2.substring(sentenceInstance.previous(), sentenceInstance.following(n2));
                    }
                    catch (final BadLocationException ex3) {
                        return null;
                    }
                    break;
                }
            }
            return null;
        }
        
        @Override
        public String getAfterIndex(final int n, final int n2) {
            if (n2 < 0 || n2 >= this.getCharCount()) {
                return null;
            }
            switch (n) {
                case 1: {
                    if (n2 + 1 >= this.getCharCount()) {
                        return null;
                    }
                    try {
                        return this.getText(n2 + 1, 1);
                    }
                    catch (final BadLocationException ex) {
                        return null;
                    }
                }
                case 2: {
                    try {
                        final String text = this.getText(0, this.getCharCount());
                        final BreakIterator wordInstance = BreakIterator.getWordInstance(this.getLocale());
                        wordInstance.setText(text);
                        final int following = wordInstance.following(n2);
                        if (following == -1 || following >= text.length()) {
                            return null;
                        }
                        final int following2 = wordInstance.following(following);
                        if (following2 == -1 || following2 >= text.length()) {
                            return null;
                        }
                        return text.substring(following, following2);
                    }
                    catch (final BadLocationException ex2) {
                        return null;
                    }
                }
                case 3: {
                    try {
                        final String text2 = this.getText(0, this.getCharCount());
                        final BreakIterator sentenceInstance = BreakIterator.getSentenceInstance(this.getLocale());
                        sentenceInstance.setText(text2);
                        final int following3 = sentenceInstance.following(n2);
                        if (following3 == -1 || following3 > text2.length()) {
                            return null;
                        }
                        final int following4 = sentenceInstance.following(following3);
                        if (following4 == -1 || following4 > text2.length()) {
                            return null;
                        }
                        return text2.substring(following3, following4);
                    }
                    catch (final BadLocationException ex3) {
                        return null;
                    }
                    break;
                }
            }
            return null;
        }
        
        @Override
        public String getBeforeIndex(final int n, final int n2) {
            if (n2 < 0 || n2 > this.getCharCount() - 1) {
                return null;
            }
            switch (n) {
                case 1: {
                    if (n2 == 0) {
                        return null;
                    }
                    try {
                        return this.getText(n2 - 1, 1);
                    }
                    catch (final BadLocationException ex) {
                        return null;
                    }
                }
                case 2: {
                    try {
                        final String text = this.getText(0, this.getCharCount());
                        final BreakIterator wordInstance = BreakIterator.getWordInstance(this.getLocale());
                        wordInstance.setText(text);
                        wordInstance.following(n2);
                        final int previous = wordInstance.previous();
                        final int previous2 = wordInstance.previous();
                        if (previous2 == -1) {
                            return null;
                        }
                        return text.substring(previous2, previous);
                    }
                    catch (final BadLocationException ex2) {
                        return null;
                    }
                }
                case 3: {
                    try {
                        final String text2 = this.getText(0, this.getCharCount());
                        final BreakIterator sentenceInstance = BreakIterator.getSentenceInstance(this.getLocale());
                        sentenceInstance.setText(text2);
                        sentenceInstance.following(n2);
                        final int previous3 = sentenceInstance.previous();
                        final int previous4 = sentenceInstance.previous();
                        if (previous4 == -1) {
                            return null;
                        }
                        return text2.substring(previous4, previous3);
                    }
                    catch (final BadLocationException ex3) {
                        return null;
                    }
                    break;
                }
            }
            return null;
        }
        
        @Override
        public AttributeSet getCharacterAttribute(final int n) {
            final View view = (View)AbstractButton.this.getClientProperty("html");
            if (view != null) {
                final Document document = view.getDocument();
                if (document instanceof StyledDocument) {
                    final Element characterElement = ((StyledDocument)document).getCharacterElement(n);
                    if (characterElement != null) {
                        return characterElement.getAttributes();
                    }
                }
            }
            return null;
        }
        
        @Override
        public int getSelectionStart() {
            return -1;
        }
        
        @Override
        public int getSelectionEnd() {
            return -1;
        }
        
        @Override
        public String getSelectedText() {
            return null;
        }
        
        private String getText(final int n, final int n2) throws BadLocationException {
            final View view = (View)AbstractButton.this.getClientProperty("html");
            if (view != null) {
                final Document document = view.getDocument();
                if (document instanceof StyledDocument) {
                    return ((StyledDocument)document).getText(n, n2);
                }
            }
            return null;
        }
        
        private Rectangle getTextRectangle() {
            final String text = AbstractButton.this.getText();
            final Icon icon = AbstractButton.this.isEnabled() ? AbstractButton.this.getIcon() : AbstractButton.this.getDisabledIcon();
            if (icon == null && text == null) {
                return null;
            }
            final Rectangle rectangle = new Rectangle();
            final Rectangle rectangle2 = new Rectangle();
            final Rectangle rectangle3 = new Rectangle();
            final Insets insets = AbstractButton.this.getInsets(new Insets(0, 0, 0, 0));
            rectangle3.x = insets.left;
            rectangle3.y = insets.top;
            rectangle3.width = AbstractButton.this.getWidth() - (insets.left + insets.right);
            rectangle3.height = AbstractButton.this.getHeight() - (insets.top + insets.bottom);
            SwingUtilities.layoutCompoundLabel(AbstractButton.this, this.getFontMetrics(this.getFont()), text, icon, AbstractButton.this.getVerticalAlignment(), AbstractButton.this.getHorizontalAlignment(), AbstractButton.this.getVerticalTextPosition(), AbstractButton.this.getHorizontalTextPosition(), rectangle3, rectangle, rectangle2, 0);
            return rectangle2;
        }
        
        @Override
        AccessibleExtendedComponent getAccessibleExtendedComponent() {
            return this;
        }
        
        @Override
        public String getToolTipText() {
            return AbstractButton.this.getToolTipText();
        }
        
        @Override
        public String getTitledBorderText() {
            return super.getTitledBorderText();
        }
        
        @Override
        public AccessibleKeyBinding getAccessibleKeyBinding() {
            final int mnemonic = AbstractButton.this.getMnemonic();
            if (mnemonic == 0) {
                return null;
            }
            return new ButtonKeyBinding(mnemonic);
        }
        
        class ButtonKeyBinding implements AccessibleKeyBinding
        {
            int mnemonic;
            
            ButtonKeyBinding(final int mnemonic) {
                this.mnemonic = mnemonic;
            }
            
            @Override
            public int getAccessibleKeyBindingCount() {
                return 1;
            }
            
            @Override
            public Object getAccessibleKeyBinding(final int n) {
                if (n != 0) {
                    throw new IllegalArgumentException();
                }
                return KeyStroke.getKeyStroke(this.mnemonic, 0);
            }
        }
    }
}
