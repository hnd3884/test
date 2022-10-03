package org.jfree.ui;

import java.awt.Component;
import javax.swing.BorderFactory;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.Container;
import java.awt.Font;
import java.awt.Dialog;

public class FontChooserDialog extends StandardDialog
{
    private FontChooserPanel fontChooserPanel;
    
    public FontChooserDialog(final Dialog owner, final String title, final boolean modal, final Font font) {
        super(owner, title, modal);
        this.setContentPane(this.createContent(font));
    }
    
    public FontChooserDialog(final Frame owner, final String title, final boolean modal, final Font font) {
        super(owner, title, modal);
        this.setContentPane(this.createContent(font));
    }
    
    private JPanel createContent(Font font) {
        final JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        if (font == null) {
            font = new Font("Dialog", 10, 0);
        }
        content.add(this.fontChooserPanel = new FontChooserPanel(font));
        final JPanel buttons = this.createButtonPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        content.add(buttons, "South");
        return content;
    }
    
    public Font getSelectedFont() {
        return this.fontChooserPanel.getSelectedFont();
    }
}
