package com.maverick.ssh;

public class PasswordAuthentication implements SshAuthentication
{
    String h;
    String i;
    
    public void setPassword(final String h) {
        this.h = h;
    }
    
    public String getPassword() {
        return this.h;
    }
    
    public String getMethod() {
        return "password";
    }
    
    public void setUsername(final String i) {
        this.i = i;
    }
    
    public String getUsername() {
        return this.i;
    }
}
