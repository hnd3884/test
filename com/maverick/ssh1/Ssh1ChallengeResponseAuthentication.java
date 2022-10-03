package com.maverick.ssh1;

import com.maverick.ssh.SshAuthentication;

public class Ssh1ChallengeResponseAuthentication implements SshAuthentication
{
    String c;
    Prompt b;
    
    public String getMethod() {
        return "challenge";
    }
    
    public String getUsername() {
        return this.c;
    }
    
    public void setUsername(final String c) {
        this.c = c;
    }
    
    public void setPrompt(final Prompt b) {
        this.b = b;
    }
    
    public Prompt getPrompt() {
        return this.b;
    }
    
    public interface Prompt
    {
        String getResponse(final String p0);
    }
}
