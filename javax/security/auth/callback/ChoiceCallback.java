package javax.security.auth.callback;

import java.io.Serializable;

public class ChoiceCallback implements Callback, Serializable
{
    private static final long serialVersionUID = -3975664071579892167L;
    private String prompt;
    private String[] choices;
    private int defaultChoice;
    private boolean multipleSelectionsAllowed;
    private int[] selections;
    
    public ChoiceCallback(final String prompt, final String[] choices, final int defaultChoice, final boolean multipleSelectionsAllowed) {
        if (prompt == null || prompt.length() == 0 || choices == null || choices.length == 0 || defaultChoice < 0 || defaultChoice >= choices.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < choices.length; ++i) {
            if (choices[i] == null || choices[i].length() == 0) {
                throw new IllegalArgumentException();
            }
        }
        this.prompt = prompt;
        this.choices = choices;
        this.defaultChoice = defaultChoice;
        this.multipleSelectionsAllowed = multipleSelectionsAllowed;
    }
    
    public String getPrompt() {
        return this.prompt;
    }
    
    public String[] getChoices() {
        return this.choices;
    }
    
    public int getDefaultChoice() {
        return this.defaultChoice;
    }
    
    public boolean allowMultipleSelections() {
        return this.multipleSelectionsAllowed;
    }
    
    public void setSelectedIndex(final int n) {
        (this.selections = new int[1])[0] = n;
    }
    
    public void setSelectedIndexes(final int[] selections) {
        if (!this.multipleSelectionsAllowed) {
            throw new UnsupportedOperationException();
        }
        this.selections = selections;
    }
    
    public int[] getSelectedIndexes() {
        return this.selections;
    }
}
