package javax.security.auth.callback;

import java.io.Serializable;

public class NameCallback implements Callback, Serializable
{
    private static final long serialVersionUID = 3770938795909392253L;
    private String prompt;
    private String defaultName;
    private String inputName;
    
    public NameCallback(final String prompt) {
        if (prompt == null || prompt.length() == 0) {
            throw new IllegalArgumentException();
        }
        this.prompt = prompt;
    }
    
    public NameCallback(final String prompt, final String defaultName) {
        if (prompt == null || prompt.length() == 0 || defaultName == null || defaultName.length() == 0) {
            throw new IllegalArgumentException();
        }
        this.prompt = prompt;
        this.defaultName = defaultName;
    }
    
    public String getPrompt() {
        return this.prompt;
    }
    
    public String getDefaultName() {
        return this.defaultName;
    }
    
    public void setName(final String inputName) {
        this.inputName = inputName;
    }
    
    public String getName() {
        return this.inputName;
    }
}
