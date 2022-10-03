package com.btr.proxy.selector.pac;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import com.btr.proxy.util.Logger;

public class UrlPacScriptSource implements PacScriptSource
{
    private final String scriptUrl;
    private String scriptContent;
    private long expireAtMillis;
    
    public UrlPacScriptSource(final String url) {
        this.expireAtMillis = 0L;
        this.scriptUrl = url;
    }
    
    public synchronized String getScriptContent() throws IOException {
        if (this.scriptContent != null) {
            if (this.expireAtMillis <= 0L || this.expireAtMillis <= System.currentTimeMillis()) {
                return this.scriptContent;
            }
        }
        try {
            if (this.scriptUrl.startsWith("file:/") || this.scriptUrl.indexOf(":/") == -1) {
                this.scriptContent = this.readPacFileContent(this.scriptUrl);
            }
            else {
                this.scriptContent = this.downloadPacContent(this.scriptUrl);
            }
        }
        catch (final IOException e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "Loading script failed.", e);
            this.scriptContent = "";
            throw e;
        }
        return this.scriptContent;
    }
    
    private String readPacFileContent(final String scriptUrl) throws IOException {
        try {
            File file = null;
            if (scriptUrl.indexOf(":/") == -1) {
                file = new File(scriptUrl);
            }
            else {
                file = new File(new URL(scriptUrl).toURI());
            }
            final BufferedReader r = new BufferedReader(new FileReader(file));
            final StringBuilder result = new StringBuilder();
            try {
                String line;
                while ((line = r.readLine()) != null) {
                    result.append(line).append("\n");
                }
            }
            finally {
                r.close();
            }
            return result.toString();
        }
        catch (final Exception e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "File reading error.", e);
            throw new IOException(e.getMessage());
        }
    }
    
    private String downloadPacContent(final String url) throws IOException {
        if (url == null) {
            throw new IOException("Invalid PAC script URL: null");
        }
        this.setPacProxySelectorEnabled(false);
        HttpURLConnection con = null;
        try {
            con = this.setupHTTPConnection(url);
            if (con.getResponseCode() != 200) {
                throw new IOException("Server returned: " + con.getResponseCode() + " " + con.getResponseMessage());
            }
            this.expireAtMillis = con.getExpiration();
            final BufferedReader r = this.getReader(con);
            final String result = this.readAllContent(r);
            r.close();
            return result;
        }
        finally {
            this.setPacProxySelectorEnabled(true);
            if (con != null) {
                con.disconnect();
            }
        }
    }
    
    private void setPacProxySelectorEnabled(final boolean enable) {
        PacProxySelector.setEnabled(enable);
    }
    
    private String readAllContent(final BufferedReader r) throws IOException {
        final StringBuilder result = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString();
    }
    
    private BufferedReader getReader(final HttpURLConnection con) throws UnsupportedEncodingException, IOException {
        final String charsetName = this.parseCharsetFromHeader(con.getContentType());
        final BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream(), charsetName));
        return r;
    }
    
    private HttpURLConnection setupHTTPConnection(final String url) throws IOException, MalformedURLException {
        final HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection(Proxy.NO_PROXY);
        con.setInstanceFollowRedirects(true);
        con.setRequestProperty("accept", "application/x-ns-proxy-autoconfig, */*;q=0.8");
        return con;
    }
    
    String parseCharsetFromHeader(final String contentType) {
        String result = "ISO-8859-1";
        if (contentType != null) {
            final String[] arr$;
            final String[] paramList = arr$ = contentType.split(";");
            for (final String param : arr$) {
                if (param.toLowerCase().trim().startsWith("charset") && param.indexOf("=") != -1) {
                    result = param.substring(param.indexOf("=") + 1).trim();
                }
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return this.scriptUrl;
    }
}
