package com.sun.security.auth.callback;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.security.auth.callback.ConfirmationCallback;
import sun.security.util.Password;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.Callback;
import jdk.Exported;
import javax.security.auth.callback.CallbackHandler;

@Exported
public class TextCallbackHandler implements CallbackHandler
{
    @Override
    public void handle(final Callback[] array) throws IOException, UnsupportedCallbackException {
        ConfirmationCallback confirmationCallback = null;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] instanceof TextOutputCallback) {
                final TextOutputCallback textOutputCallback = (TextOutputCallback)array[i];
                String string = null;
                switch (textOutputCallback.getMessageType()) {
                    case 0: {
                        string = "";
                        break;
                    }
                    case 1: {
                        string = "Warning: ";
                        break;
                    }
                    case 2: {
                        string = "Error: ";
                        break;
                    }
                    default: {
                        throw new UnsupportedCallbackException(array[i], "Unrecognized message type");
                    }
                }
                final String message = textOutputCallback.getMessage();
                if (message != null) {
                    string += message;
                }
                if (string != null) {
                    System.err.println(string);
                }
            }
            else if (array[i] instanceof NameCallback) {
                final NameCallback nameCallback = (NameCallback)array[i];
                if (nameCallback.getDefaultName() == null) {
                    System.err.print(nameCallback.getPrompt());
                }
                else {
                    System.err.print(nameCallback.getPrompt() + " [" + nameCallback.getDefaultName() + "] ");
                }
                System.err.flush();
                String name = this.readLine();
                if (name.equals("")) {
                    name = nameCallback.getDefaultName();
                }
                nameCallback.setName(name);
            }
            else if (array[i] instanceof PasswordCallback) {
                final PasswordCallback passwordCallback = (PasswordCallback)array[i];
                System.err.print(passwordCallback.getPrompt());
                System.err.flush();
                passwordCallback.setPassword(Password.readPassword(System.in, passwordCallback.isEchoOn()));
            }
            else {
                if (!(array[i] instanceof ConfirmationCallback)) {
                    throw new UnsupportedCallbackException(array[i], "Unrecognized Callback");
                }
                confirmationCallback = (ConfirmationCallback)array[i];
            }
        }
        if (confirmationCallback != null) {
            this.doConfirmation(confirmationCallback);
        }
    }
    
    private String readLine() throws IOException {
        final String line = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (line == null) {
            throw new IOException("Cannot read from System.in");
        }
        return line;
    }
    
    private void doConfirmation(final ConfirmationCallback confirmationCallback) throws IOException, UnsupportedCallbackException {
        final int messageType = confirmationCallback.getMessageType();
        String s = null;
        switch (messageType) {
            case 1: {
                s = "Warning: ";
                break;
            }
            case 2: {
                s = "Error: ";
                break;
            }
            case 0: {
                s = "";
                break;
            }
            default: {
                throw new UnsupportedCallbackException(confirmationCallback, "Unrecognized message type: " + messageType);
            }
        }
        final int optionType = confirmationCallback.getOptionType();
        class OptionInfo
        {
            String name = "Yes";
            int value = 0;
            
            OptionInfo(final int value) {
            }
        }
        OptionInfo[] array = null;
        switch (optionType) {
            case 0: {
                array = new OptionInfo[] { new OptionInfo(0), new OptionInfo(1) };
                break;
            }
            case 1: {
                array = new OptionInfo[] { new OptionInfo(0), new OptionInfo(1), new OptionInfo(2) };
                break;
            }
            case 2: {
                array = new OptionInfo[] { new OptionInfo(3), new OptionInfo(2) };
                break;
            }
            case -1: {
                final String[] options = confirmationCallback.getOptions();
                array = new OptionInfo[options.length];
                for (int i = 0; i < array.length; ++i) {
                    array[i] = new OptionInfo(i);
                }
                break;
            }
            default: {
                throw new UnsupportedCallbackException(confirmationCallback, "Unrecognized option type: " + optionType);
            }
        }
        final int defaultOption = confirmationCallback.getDefaultOption();
        String prompt = confirmationCallback.getPrompt();
        if (prompt == null) {
            prompt = "";
        }
        final String string = s + prompt;
        if (!string.equals("")) {
            System.err.println(string);
        }
        for (int j = 0; j < array.length; ++j) {
            if (optionType == -1) {
                System.err.println(j + ". " + array[j].name + ((j == defaultOption) ? " [default]" : ""));
            }
            else {
                System.err.println(j + ". " + array[j].name + ((array[j].value == defaultOption) ? " [default]" : ""));
            }
        }
        System.err.print("Enter a number: ");
        System.err.flush();
        int value;
        try {
            int int1 = Integer.parseInt(this.readLine());
            if (int1 < 0 || int1 > array.length - 1) {
                int1 = defaultOption;
            }
            value = array[int1].value;
        }
        catch (final NumberFormatException ex) {
            value = defaultOption;
        }
        confirmationCallback.setSelectedIndex(value);
    }
}
