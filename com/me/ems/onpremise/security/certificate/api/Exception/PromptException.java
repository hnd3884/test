package com.me.ems.onpremise.security.certificate.api.Exception;

public class PromptException extends Exception
{
    Long promptCode;
    String promtName;
    
    public PromptException(final Long promptCode, final String promtName) {
        this.promptCode = promptCode;
        this.promtName = promtName;
    }
    
    public Long getPromptCode() {
        return this.promptCode;
    }
    
    public void setPromptCode(final Long promptCode) {
        this.promptCode = promptCode;
    }
    
    public String getPromtName() {
        return this.promtName;
    }
    
    public void setPromtName(final String promtName) {
        this.promtName = promtName;
    }
}
