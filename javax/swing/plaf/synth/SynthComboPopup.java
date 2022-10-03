package javax.swing.plaf.synth;

import java.awt.Insets;
import javax.swing.plaf.ComboBoxUI;
import java.awt.Rectangle;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboPopup;

class SynthComboPopup extends BasicComboPopup
{
    public SynthComboPopup(final JComboBox comboBox) {
        super(comboBox);
    }
    
    @Override
    protected void configureList() {
        this.list.setFont(this.comboBox.getFont());
        this.list.setCellRenderer(this.comboBox.getRenderer());
        this.list.setFocusable(false);
        this.list.setSelectionMode(0);
        final int selectedIndex = this.comboBox.getSelectedIndex();
        if (selectedIndex == -1) {
            this.list.clearSelection();
        }
        else {
            this.list.setSelectedIndex(selectedIndex);
            this.list.ensureIndexIsVisible(selectedIndex);
        }
        this.installListListeners();
    }
    
    @Override
    protected Rectangle computePopupBounds(final int n, final int n2, final int n3, final int n4) {
        final ComboBoxUI ui = this.comboBox.getUI();
        if (ui instanceof SynthComboBoxUI) {
            final SynthComboBoxUI synthComboBoxUI = (SynthComboBoxUI)ui;
            if (synthComboBoxUI.popupInsets != null) {
                final Insets popupInsets = synthComboBoxUI.popupInsets;
                return super.computePopupBounds(n + popupInsets.left, n2 + popupInsets.top, n3 - popupInsets.left - popupInsets.right, n4 - popupInsets.top - popupInsets.bottom);
            }
        }
        return super.computePopupBounds(n, n2, n3, n4);
    }
}
