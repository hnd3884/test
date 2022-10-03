package javax.swing.plaf.multi;

import javax.swing.UIManager;
import javax.swing.JComponent;
import java.util.Vector;
import javax.swing.plaf.ComponentUI;
import javax.swing.UIDefaults;
import javax.swing.LookAndFeel;

public class MultiLookAndFeel extends LookAndFeel
{
    @Override
    public String getName() {
        return "Multiplexing Look and Feel";
    }
    
    @Override
    public String getID() {
        return "Multiplex";
    }
    
    @Override
    public String getDescription() {
        return "Allows multiple UI instances per component instance";
    }
    
    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }
    
    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }
    
    @Override
    public UIDefaults getDefaults() {
        final String s = "javax.swing.plaf.multi.Multi";
        final Object[] array = { "ButtonUI", s + "ButtonUI", "CheckBoxMenuItemUI", s + "MenuItemUI", "CheckBoxUI", s + "ButtonUI", "ColorChooserUI", s + "ColorChooserUI", "ComboBoxUI", s + "ComboBoxUI", "DesktopIconUI", s + "DesktopIconUI", "DesktopPaneUI", s + "DesktopPaneUI", "EditorPaneUI", s + "TextUI", "FileChooserUI", s + "FileChooserUI", "FormattedTextFieldUI", s + "TextUI", "InternalFrameUI", s + "InternalFrameUI", "LabelUI", s + "LabelUI", "ListUI", s + "ListUI", "MenuBarUI", s + "MenuBarUI", "MenuItemUI", s + "MenuItemUI", "MenuUI", s + "MenuItemUI", "OptionPaneUI", s + "OptionPaneUI", "PanelUI", s + "PanelUI", "PasswordFieldUI", s + "TextUI", "PopupMenuSeparatorUI", s + "SeparatorUI", "PopupMenuUI", s + "PopupMenuUI", "ProgressBarUI", s + "ProgressBarUI", "RadioButtonMenuItemUI", s + "MenuItemUI", "RadioButtonUI", s + "ButtonUI", "RootPaneUI", s + "RootPaneUI", "ScrollBarUI", s + "ScrollBarUI", "ScrollPaneUI", s + "ScrollPaneUI", "SeparatorUI", s + "SeparatorUI", "SliderUI", s + "SliderUI", "SpinnerUI", s + "SpinnerUI", "SplitPaneUI", s + "SplitPaneUI", "TabbedPaneUI", s + "TabbedPaneUI", "TableHeaderUI", s + "TableHeaderUI", "TableUI", s + "TableUI", "TextAreaUI", s + "TextUI", "TextFieldUI", s + "TextUI", "TextPaneUI", s + "TextUI", "ToggleButtonUI", s + "ButtonUI", "ToolBarSeparatorUI", s + "SeparatorUI", "ToolBarUI", s + "ToolBarUI", "ToolTipUI", s + "ToolTipUI", "TreeUI", s + "TreeUI", "ViewportUI", s + "ViewportUI" };
        final MultiUIDefaults multiUIDefaults = new MultiUIDefaults(array.length / 2, 0.75f);
        multiUIDefaults.putDefaults(array);
        return multiUIDefaults;
    }
    
    public static ComponentUI createUIs(final ComponentUI componentUI, final Vector vector, final JComponent component) {
        final ComponentUI ui = UIManager.getDefaults().getUI(component);
        if (ui == null) {
            return null;
        }
        vector.addElement(ui);
        final LookAndFeel[] auxiliaryLookAndFeels = UIManager.getAuxiliaryLookAndFeels();
        if (auxiliaryLookAndFeels != null) {
            for (int i = 0; i < auxiliaryLookAndFeels.length; ++i) {
                final ComponentUI ui2 = auxiliaryLookAndFeels[i].getDefaults().getUI(component);
                if (ui2 != null) {
                    vector.addElement(ui2);
                }
            }
        }
        if (vector.size() == 1) {
            return vector.elementAt(0);
        }
        return componentUI;
    }
    
    protected static ComponentUI[] uisToArray(final Vector vector) {
        if (vector == null) {
            return new ComponentUI[0];
        }
        final int size = vector.size();
        if (size > 0) {
            final ComponentUI[] array = new ComponentUI[size];
            for (int i = 0; i < size; ++i) {
                array[i] = (ComponentUI)vector.elementAt(i);
            }
            return array;
        }
        return null;
    }
}
