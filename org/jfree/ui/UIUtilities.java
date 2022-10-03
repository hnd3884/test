package org.jfree.ui;

import javax.swing.border.EmptyBorder;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import javax.swing.plaf.BorderUIResource;
import javax.swing.UIManager;

public class UIUtilities
{
    private UIUtilities() {
    }
    
    public static void setupUI() {
        try {
            final String classname = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(classname);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        final UIDefaults defaults = UIManager.getDefaults();
        defaults.put("PopupMenu.border", new BorderUIResource.EtchedBorderUIResource(0, defaults.getColor("controlShadow"), defaults.getColor("controlLtHighlight")));
        final MatteBorder matteborder = new MatteBorder(1, 1, 1, 1, Color.black);
        final EmptyBorder emptyborder = new MatteBorder(2, 2, 2, 2, defaults.getColor("control"));
        final BorderUIResource.CompoundBorderUIResource compBorder = new BorderUIResource.CompoundBorderUIResource(emptyborder, matteborder);
        final BorderUIResource.EmptyBorderUIResource emptyBorderUI = new BorderUIResource.EmptyBorderUIResource(0, 0, 0, 0);
        defaults.put("SplitPane.border", emptyBorderUI);
        defaults.put("Table.scrollPaneBorder", emptyBorderUI);
        defaults.put("ComboBox.border", compBorder);
        defaults.put("TextField.border", compBorder);
        defaults.put("TextArea.border", compBorder);
        defaults.put("CheckBox.border", compBorder);
        defaults.put("ScrollPane.border", emptyBorderUI);
    }
}
