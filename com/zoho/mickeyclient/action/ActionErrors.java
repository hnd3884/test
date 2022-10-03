package com.zoho.mickeyclient.action;

public enum ActionErrors
{
    ACTION_MISSING("AE01", "If Action has been defined for a menu-item [{0}], then its ActionClass must be defined as well."), 
    FORWARD_MISSING("AE02", "No forward has been assigned to the given input name [{0}]"), 
    FORWARDING_ERROR("AE03", "Exception occurred while forwarding the request for the menu-item : {0}"), 
    FORWARD_PATH_NULL("AE04", "The path of a Forward cannot be null"), 
    NULL_MENUITEM("AE05", "The name of the MenuItem given as input cannot be null"), 
    NULL_FORWARDNAME("AE06", "The forward name given as input cannot be null");
    
    private final String code;
    private final String message;
    
    private ActionErrors(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public String toString() {
        return this.code + " : " + this.message;
    }
}
