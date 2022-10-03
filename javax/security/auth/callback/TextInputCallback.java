package javax.security.auth.callback;

import java.io.Serializable;

public class TextInputCallback implements Callback, Serializable
{
    private static final long serialVersionUID = -8064222478852811804L;
    private String prompt;
    private String defaultText;
    private String inputText;
    
    public TextInputCallback(final String prompt) {
        if (prompt == null || prompt.length() == 0) {
            throw new IllegalArgumentException();
        }
        this.prompt = prompt;
    }
    
    public TextInputCallback(final String prompt, final String defaultText) {
        if (prompt == null || prompt.length() == 0 || defaultText == null || defaultText.length() == 0) {
            throw new IllegalArgumentException();
        }
        this.prompt = prompt;
        this.defaultText = defaultText;
    }
    
    public String getPrompt() {
        return this.prompt;
    }
    
    public String getDefaultText() {
        return this.defaultText;
    }
    
    public void setText(final String inputText) {
        this.inputText = inputText;
    }
    
    public String getText() {
        return this.inputText;
    }
}
