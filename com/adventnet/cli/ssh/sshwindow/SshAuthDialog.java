package com.adventnet.cli.ssh.sshwindow;

import java.awt.Container;
import javax.swing.JOptionPane;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.TextField;
import java.awt.Component;
import java.awt.Label;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JDialog;

public class SshAuthDialog extends JDialog
{
    private String username;
    private String password;
    private boolean auth;
    
    public SshAuthDialog(final String username, final String password) {
        this.username = null;
        this.password = null;
        this.auth = false;
        this.setModal(true);
        this.username = username;
        this.password = password;
        this.loginDialog();
    }
    
    private void loginDialog() {
        final Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        final Panel panel = new Panel(new GridLayout(2, 1));
        panel.add(new Label("SSH Authorization required"));
        contentPane.add(panel, "North");
        final Panel panel2 = new Panel(new GridLayout(2, 2));
        final TextField textField = new TextField(this.username, 10);
        final TextField textField2 = new TextField(this.password, 10);
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                textField2.requestFocus();
            }
        });
        textField2.setEchoChar('*');
        panel2.add(new Label("User name"));
        panel2.add(textField);
        panel2.add(new Label("Password"));
        panel2.add(textField2);
        contentPane.add(panel2, "Center");
        final Panel panel3 = new Panel();
        final Button button = new Button("Cancel");
        final Button button2 = new Button("Login");
        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                if (textField.getText() == null || textField.getText().length() == 0) {
                    JOptionPane.showMessageDialog(null, "Enter Login Name", "alert", 0);
                    return;
                }
                SshAuthDialog.this.username = textField.getText();
                if (textField2.getText() == null || textField2.getText().length() == 0) {
                    JOptionPane.showMessageDialog(null, "Enter Password", "alert", 0);
                    return;
                }
                SshAuthDialog.this.password = textField2.getText();
                SshAuthDialog.this.auth = true;
                SshAuthDialog.this.setVisible(false);
            }
        };
        button2.addActionListener(actionListener);
        textField2.addActionListener(actionListener);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                SshAuthDialog.this.setVisible(false);
            }
        });
        panel3.add(button2);
        panel3.add(button);
        contentPane.add(panel3, "South");
        this.pack();
        this.setDefaultCloseOperation(2);
        this.setSize(this.getPreferredSize().width + 100, this.getPreferredSize().height);
        this.setLocation(this.getToolkit().getScreenSize().width / 2 - this.getSize().width / 2, this.getToolkit().getScreenSize().height / 2 - this.getSize().height / 2);
    }
    
    public boolean isAuth() {
        return this.auth;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
}
