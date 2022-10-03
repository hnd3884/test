package com.google.api.client.http;

import java.io.IOException;
import com.google.api.client.util.Preconditions;

public final class BasicAuthentication implements HttpRequestInitializer, HttpExecuteInterceptor
{
    private final String username;
    private final String password;
    
    public BasicAuthentication(final String username, final String password) {
        this.username = Preconditions.checkNotNull(username);
        this.password = Preconditions.checkNotNull(password);
    }
    
    @Override
    public void initialize(final HttpRequest request) throws IOException {
        request.setInterceptor(this);
    }
    
    @Override
    public void intercept(final HttpRequest request) throws IOException {
        request.getHeaders().setBasicAuthentication(this.username, this.password);
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
}
