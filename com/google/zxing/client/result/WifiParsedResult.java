package com.google.zxing.client.result;

public final class WifiParsedResult extends ParsedResult
{
    private final String ssid;
    private final String networkEncryption;
    private final String password;
    
    public WifiParsedResult(final String networkEncryption, final String ssid, final String password) {
        super(ParsedResultType.WIFI);
        this.ssid = ssid;
        this.networkEncryption = networkEncryption;
        this.password = password;
    }
    
    public String getSsid() {
        return this.ssid;
    }
    
    public String getNetworkEncryption() {
        return this.networkEncryption;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    @Override
    public String getDisplayResult() {
        final StringBuilder result = new StringBuilder(80);
        ParsedResult.maybeAppend(this.ssid, result);
        ParsedResult.maybeAppend(this.networkEncryption, result);
        ParsedResult.maybeAppend(this.password, result);
        return result.toString();
    }
}
