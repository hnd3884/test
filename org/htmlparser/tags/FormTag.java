package org.htmlparser.tags;

import org.htmlparser.util.SimpleNodeIterator;
import org.htmlparser.util.NodeList;

public class FormTag extends CompositeTag
{
    public static final String POST = "POST";
    public static final String GET = "GET";
    protected String mFormLocation;
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public FormTag() {
        this.mFormLocation = null;
    }
    
    public String[] getIds() {
        return FormTag.mIds;
    }
    
    public String[] getEnders() {
        return FormTag.mIds;
    }
    
    public String[] getEndTagEnders() {
        return FormTag.mEndTagEnders;
    }
    
    public NodeList getFormInputs() {
        return this.searchFor(InputTag.class, true);
    }
    
    public NodeList getFormTextareas() {
        return this.searchFor(TextareaTag.class, true);
    }
    
    public String getFormLocation() {
        if (null == this.mFormLocation) {
            this.mFormLocation = this.extractFormLocn();
        }
        return this.mFormLocation;
    }
    
    public void setFormLocation(final String url) {
        this.setAttribute("ACTION", this.mFormLocation = url);
    }
    
    public String getFormMethod() {
        String ret = this.getAttribute("METHOD");
        if (null == ret) {
            ret = "GET";
        }
        return ret;
    }
    
    public InputTag getInputTag(final String name) {
        InputTag inputTag = null;
        boolean found = false;
        for (SimpleNodeIterator e = this.getFormInputs().elements(); e.hasMoreNodes() && !found; found = true) {
            inputTag = (InputTag)e.nextNode();
            final String inputTagName = inputTag.getAttribute("NAME");
            if (inputTagName != null && inputTagName.equalsIgnoreCase(name)) {}
        }
        if (found) {
            return inputTag;
        }
        return null;
    }
    
    public String getFormName() {
        return this.getAttribute("NAME");
    }
    
    public TextareaTag getTextAreaTag(final String name) {
        TextareaTag textareaTag = null;
        boolean found = false;
        for (SimpleNodeIterator e = this.getFormTextareas().elements(); e.hasMoreNodes() && !found; found = true) {
            textareaTag = (TextareaTag)e.nextNode();
            final String textAreaName = textareaTag.getAttribute("NAME");
            if (textAreaName != null && textAreaName.equals(name)) {}
        }
        if (found) {
            return textareaTag;
        }
        return null;
    }
    
    public String toString() {
        return "FORM TAG : Form at " + this.getFormLocation() + "; begins at : " + this.getStartPosition() + "; ends at : " + this.getEndPosition();
    }
    
    public String extractFormLocn() {
        String ret = this.getAttribute("ACTION");
        if (null == ret) {
            ret = "";
        }
        else if (null != this.getPage()) {
            ret = this.getPage().getAbsoluteURL(ret);
        }
        return ret;
    }
    
    static {
        mIds = new String[] { "FORM" };
        mEndTagEnders = new String[] { "HTML", "BODY", "TABLE" };
    }
}
