package com.adventnet.tools.update.installer;

import javax.swing.Icon;
import java.applet.Applet;
import com.adventnet.tools.update.CommonUtil;
import java.awt.Component;
import javax.swing.JOptionPane;

public class UMOptionPane extends JOptionPane
{
    public static void showErroDialog(final Component component, final String message) {
        JOptionPane.showMessageDialog(component, message, CommonUtil.getString(MessageConstants.ERROR), 0, Utility.findImage("./com/adventnet/tools/update/installer/images/n_error.png", null, true));
    }
    
    public static void showInformationDialog(final Component component, final String message) {
        JOptionPane.showMessageDialog(component, message, CommonUtil.getString(MessageConstants.INFO), 1, Utility.findImage("./com/adventnet/tools/update/installer/images/n_info.png", null, true));
    }
}
