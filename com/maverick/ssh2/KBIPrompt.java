package com.maverick.ssh2;

public class KBIPrompt
{
    private String b;
    private String d;
    private boolean c;
    
    public KBIPrompt(final String b, final boolean c) {
        this.b = b;
        this.c = c;
    }
    
    public String getPrompt() {
        return this.b;
    }
    
    public boolean echo() {
        return this.c;
    }
    
    public void setResponse(final String d) {
        this.d = d;
    }
    
    public String getResponse() {
        return this.d;
    }
}
