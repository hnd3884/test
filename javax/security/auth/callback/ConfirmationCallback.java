package javax.security.auth.callback;

import java.io.Serializable;

public class ConfirmationCallback implements Callback, Serializable
{
    private static final long serialVersionUID = -9095656433782481624L;
    public static final int UNSPECIFIED_OPTION = -1;
    public static final int YES_NO_OPTION = 0;
    public static final int YES_NO_CANCEL_OPTION = 1;
    public static final int OK_CANCEL_OPTION = 2;
    public static final int YES = 0;
    public static final int NO = 1;
    public static final int CANCEL = 2;
    public static final int OK = 3;
    public static final int INFORMATION = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    private String prompt;
    private int messageType;
    private int optionType;
    private int defaultOption;
    private String[] options;
    private int selection;
    
    public ConfirmationCallback(final int messageType, final int optionType, final int defaultOption) {
        this.optionType = -1;
        if (messageType < 0 || messageType > 2 || optionType < 0 || optionType > 2) {
            throw new IllegalArgumentException();
        }
        switch (optionType) {
            case 0: {
                if (defaultOption != 0 && defaultOption != 1) {
                    throw new IllegalArgumentException();
                }
                break;
            }
            case 1: {
                if (defaultOption != 0 && defaultOption != 1 && defaultOption != 2) {
                    throw new IllegalArgumentException();
                }
                break;
            }
            case 2: {
                if (defaultOption != 3 && defaultOption != 2) {
                    throw new IllegalArgumentException();
                }
                break;
            }
        }
        this.messageType = messageType;
        this.optionType = optionType;
        this.defaultOption = defaultOption;
    }
    
    public ConfirmationCallback(final int messageType, final String[] options, final int defaultOption) {
        this.optionType = -1;
        if (messageType < 0 || messageType > 2 || options == null || options.length == 0 || defaultOption < 0 || defaultOption >= options.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < options.length; ++i) {
            if (options[i] == null || options[i].length() == 0) {
                throw new IllegalArgumentException();
            }
        }
        this.messageType = messageType;
        this.options = options;
        this.defaultOption = defaultOption;
    }
    
    public ConfirmationCallback(final String prompt, final int messageType, final int optionType, final int defaultOption) {
        this.optionType = -1;
        if (prompt == null || prompt.length() == 0 || messageType < 0 || messageType > 2 || optionType < 0 || optionType > 2) {
            throw new IllegalArgumentException();
        }
        switch (optionType) {
            case 0: {
                if (defaultOption != 0 && defaultOption != 1) {
                    throw new IllegalArgumentException();
                }
                break;
            }
            case 1: {
                if (defaultOption != 0 && defaultOption != 1 && defaultOption != 2) {
                    throw new IllegalArgumentException();
                }
                break;
            }
            case 2: {
                if (defaultOption != 3 && defaultOption != 2) {
                    throw new IllegalArgumentException();
                }
                break;
            }
        }
        this.prompt = prompt;
        this.messageType = messageType;
        this.optionType = optionType;
        this.defaultOption = defaultOption;
    }
    
    public ConfirmationCallback(final String prompt, final int messageType, final String[] options, final int defaultOption) {
        this.optionType = -1;
        if (prompt == null || prompt.length() == 0 || messageType < 0 || messageType > 2 || options == null || options.length == 0 || defaultOption < 0 || defaultOption >= options.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < options.length; ++i) {
            if (options[i] == null || options[i].length() == 0) {
                throw new IllegalArgumentException();
            }
        }
        this.prompt = prompt;
        this.messageType = messageType;
        this.options = options;
        this.defaultOption = defaultOption;
    }
    
    public String getPrompt() {
        return this.prompt;
    }
    
    public int getMessageType() {
        return this.messageType;
    }
    
    public int getOptionType() {
        return this.optionType;
    }
    
    public String[] getOptions() {
        return this.options;
    }
    
    public int getDefaultOption() {
        return this.defaultOption;
    }
    
    public void setSelectedIndex(final int selection) {
        this.selection = selection;
    }
    
    public int getSelectedIndex() {
        return this.selection;
    }
}
