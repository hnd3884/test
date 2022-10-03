package com.sun.security.auth.callback;

import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.security.auth.callback.ConfirmationCallback;
import javax.swing.JPasswordField;
import javax.security.auth.callback.PasswordCallback;
import javax.swing.Box;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.TextOutputCallback;
import java.util.ArrayList;
import javax.security.auth.callback.Callback;
import java.awt.Component;
import jdk.Exported;
import javax.security.auth.callback.CallbackHandler;

@Exported(false)
@Deprecated
public class DialogCallbackHandler implements CallbackHandler
{
    private Component parentComponent;
    private static final int JPasswordFieldLen = 8;
    private static final int JTextFieldLen = 8;
    
    public DialogCallbackHandler() {
    }
    
    public DialogCallbackHandler(final Component parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    @Override
    public void handle(final Callback[] array) throws UnsupportedCallbackException {
        final ArrayList list = new ArrayList(3);
        final ArrayList list2 = new ArrayList(2);
        final ConfirmationInfo confirmationInfo = new ConfirmationInfo();
        for (int i = 0; i < array.length; ++i) {
            if (array[i] instanceof TextOutputCallback) {
                final TextOutputCallback textOutputCallback = (TextOutputCallback)array[i];
                switch (textOutputCallback.getMessageType()) {
                    case 0: {
                        confirmationInfo.messageType = 1;
                        break;
                    }
                    case 1: {
                        confirmationInfo.messageType = 2;
                        break;
                    }
                    case 2: {
                        confirmationInfo.messageType = 0;
                        break;
                    }
                    default: {
                        throw new UnsupportedCallbackException(array[i], "Unrecognized message type");
                    }
                }
                list.add(textOutputCallback.getMessage());
            }
            else if (array[i] instanceof NameCallback) {
                final NameCallback nameCallback = (NameCallback)array[i];
                final JLabel label = new JLabel(nameCallback.getPrompt());
                final JTextField textField = new JTextField(8);
                final String defaultName = nameCallback.getDefaultName();
                if (defaultName != null) {
                    textField.setText(defaultName);
                }
                final Box horizontalBox = Box.createHorizontalBox();
                horizontalBox.add(label);
                horizontalBox.add(textField);
                list.add(horizontalBox);
                list2.add(new Action() {
                    @Override
                    public void perform() {
                        nameCallback.setName(textField.getText());
                    }
                });
            }
            else if (array[i] instanceof PasswordCallback) {
                final PasswordCallback passwordCallback = (PasswordCallback)array[i];
                final JLabel label2 = new JLabel(passwordCallback.getPrompt());
                final JPasswordField passwordField = new JPasswordField(8);
                if (!passwordCallback.isEchoOn()) {
                    passwordField.setEchoChar('*');
                }
                final Box horizontalBox2 = Box.createHorizontalBox();
                horizontalBox2.add(label2);
                horizontalBox2.add(passwordField);
                list.add(horizontalBox2);
                list2.add(new Action() {
                    @Override
                    public void perform() {
                        passwordCallback.setPassword(passwordField.getPassword());
                    }
                });
            }
            else {
                if (!(array[i] instanceof ConfirmationCallback)) {
                    throw new UnsupportedCallbackException(array[i], "Unrecognized Callback");
                }
                final ConfirmationCallback callback = (ConfirmationCallback)array[i];
                confirmationInfo.setCallback(callback);
                if (callback.getPrompt() != null) {
                    list.add(callback.getPrompt());
                }
            }
        }
        final int showOptionDialog = JOptionPane.showOptionDialog(this.parentComponent, list.toArray(), "Confirmation", confirmationInfo.optionType, confirmationInfo.messageType, null, confirmationInfo.options, confirmationInfo.initialValue);
        if (showOptionDialog == 0 || showOptionDialog == 0) {
            final Iterator iterator = list2.iterator();
            while (iterator.hasNext()) {
                ((Action)iterator.next()).perform();
            }
        }
        confirmationInfo.handleResult(showOptionDialog);
    }
    
    private static class ConfirmationInfo
    {
        private int[] translations;
        int optionType;
        Object[] options;
        Object initialValue;
        int messageType;
        private ConfirmationCallback callback;
        
        private ConfirmationInfo() {
            this.optionType = 2;
            this.options = null;
            this.initialValue = null;
            this.messageType = 3;
        }
        
        void setCallback(final ConfirmationCallback callback) throws UnsupportedCallbackException {
            this.callback = callback;
            final int optionType = callback.getOptionType();
            switch (optionType) {
                case 0: {
                    this.optionType = 0;
                    this.translations = new int[] { 0, 0, 1, 1, -1, 1 };
                    break;
                }
                case 1: {
                    this.optionType = 1;
                    this.translations = new int[] { 0, 0, 1, 1, 2, 2, -1, 2 };
                    break;
                }
                case 2: {
                    this.optionType = 2;
                    this.translations = new int[] { 0, 3, 2, 2, -1, 2 };
                    break;
                }
                case -1: {
                    this.options = callback.getOptions();
                    this.translations = new int[] { -1, callback.getDefaultOption() };
                    break;
                }
                default: {
                    throw new UnsupportedCallbackException(callback, "Unrecognized option type: " + optionType);
                }
            }
            final int messageType = callback.getMessageType();
            switch (messageType) {
                case 1: {
                    this.messageType = 2;
                    break;
                }
                case 2: {
                    this.messageType = 0;
                    break;
                }
                case 0: {
                    this.messageType = 1;
                    break;
                }
                default: {
                    throw new UnsupportedCallbackException(callback, "Unrecognized message type: " + messageType);
                }
            }
        }
        
        void handleResult(int selectedIndex) {
            if (this.callback == null) {
                return;
            }
            for (int i = 0; i < this.translations.length; i += 2) {
                if (this.translations[i] == selectedIndex) {
                    selectedIndex = this.translations[i + 1];
                    break;
                }
            }
            this.callback.setSelectedIndex(selectedIndex);
        }
    }
    
    private interface Action
    {
        void perform();
    }
}
