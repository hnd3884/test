package com.adventnet.cli.config;

import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JOptionPane;
import com.adventnet.cli.config.ios.PasswordDialog;

public class LoginInterfaceImpl implements LoginInterface
{
    PasswordDialog passwdDg;
    
    public LoginInterfaceImpl() {
        this.passwdDg = null;
    }
    
    public String getLoginName(final String s) {
        String string = null;
        final Object showInputDialog = JOptionPane.showInputDialog(null, "Please enter the login name", "Login Name Dialog", -1, null, null, null);
        if (showInputDialog != null) {
            string = showInputDialog.toString();
        }
        return string;
    }
    
    public String getLoginPassword(final String s) {
        String password = null;
        if (this.passwdDg == null) {
            this.passwdDg = new PasswordDialog();
        }
        this.passwdDg.setPasswdLabelText("Please enter the " + s + " password");
        this.passwdDg.setVisible(true);
        if (this.passwdDg.isOkClicked() && this.passwdDg.getPassword() != null) {
            password = this.passwdDg.getPassword();
        }
        return password;
    }
}
