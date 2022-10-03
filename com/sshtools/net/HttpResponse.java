package com.sshtools.net;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.InputStream;

public class HttpResponse extends HttpHeader
{
    private String d;
    private int c;
    private String e;
    
    public HttpResponse(final InputStream inputStream) throws IOException {
        super.begin = this.readLine(inputStream);
        while (super.begin.trim().length() == 0) {
            super.begin = this.readLine(inputStream);
        }
        this.b();
        this.processHeaderFields(inputStream);
    }
    
    public String getVersion() {
        return this.d;
    }
    
    public int getStatus() {
        return this.c;
    }
    
    public String getReason() {
        return this.e;
    }
    
    private void b() throws IOException {
        final StringTokenizer stringTokenizer = new StringTokenizer(super.begin, " \t\r", false);
        try {
            this.d = stringTokenizer.nextToken();
            this.c = Integer.parseInt(stringTokenizer.nextToken());
            this.e = stringTokenizer.nextToken();
        }
        catch (final NoSuchElementException ex) {
            throw new IOException("Failed to read HTTP repsonse header");
        }
        catch (final NumberFormatException ex2) {
            throw new IOException("Failed to read HTTP resposne header");
        }
    }
    
    public String getAuthenticationMethod() {
        final String headerField = this.getHeaderField("Proxy-Authenticate");
        String substring = null;
        if (headerField != null) {
            substring = headerField.substring(0, headerField.indexOf(32));
        }
        return substring;
    }
    
    public String getAuthenticationRealm() {
        final String headerField = this.getHeaderField("Proxy-Authenticate");
        String substring = "";
        if (headerField != null) {
            for (int i = headerField.indexOf(61); i >= 0; i = headerField.indexOf(61, i + 1)) {
                final int lastIndex = headerField.lastIndexOf(32, i);
                if (lastIndex > -1) {
                    if (headerField.substring(lastIndex + 1, i).equalsIgnoreCase("realm")) {
                        final int n = i + 2;
                        substring = headerField.substring(n, headerField.indexOf(34, n));
                        break;
                    }
                }
            }
        }
        return substring;
    }
}
