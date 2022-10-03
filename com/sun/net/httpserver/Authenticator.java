package com.sun.net.httpserver;

import jdk.Exported;

@Exported
public abstract class Authenticator
{
    public abstract Result authenticate(final HttpExchange p0);
    
    public abstract static class Result
    {
    }
    
    @Exported
    public static class Failure extends Result
    {
        private int responseCode;
        
        public Failure(final int responseCode) {
            this.responseCode = responseCode;
        }
        
        public int getResponseCode() {
            return this.responseCode;
        }
    }
    
    @Exported
    public static class Success extends Result
    {
        private HttpPrincipal principal;
        
        public Success(final HttpPrincipal principal) {
            this.principal = principal;
        }
        
        public HttpPrincipal getPrincipal() {
            return this.principal;
        }
    }
    
    @Exported
    public static class Retry extends Result
    {
        private int responseCode;
        
        public Retry(final int responseCode) {
            this.responseCode = responseCode;
        }
        
        public int getResponseCode() {
            return this.responseCode;
        }
    }
}
