package com.github.odiszapc.nginxparser;

public class NgxToken
{
    private String token;
    
    public NgxToken(final String token) {
        this.token = token;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public void setToken(final String token) {
        this.token = token;
    }
    
    @Override
    public String toString() {
        return this.token;
    }
}
