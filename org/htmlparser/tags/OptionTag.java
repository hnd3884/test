package org.htmlparser.tags;

public class OptionTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return OptionTag.mIds;
    }
    
    public String[] getEnders() {
        return OptionTag.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return OptionTag.mEndTagEnders;
    }
    
    public String getValue() {
        return this.getAttribute("VALUE");
    }
    
    public void setValue(final String value) {
        this.setAttribute("VALUE", value);
    }
    
    public String getOptionText() {
        return this.toPlainTextString();
    }
    
    public String toString() {
        final String output = "OPTION VALUE: " + this.getValue() + " TEXT: " + this.getOptionText() + "\n";
        return output;
    }
    
    static {
        mIds = new String[] { "OPTION" };
        mEnders = new String[] { "INPUT", "TEXTAREA", "SELECT", "OPTION" };
        mEndTagEnders = new String[] { "SELECT", "FORM", "BODY", "HTML" };
    }
}
