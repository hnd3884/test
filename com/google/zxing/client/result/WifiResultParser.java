package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class WifiResultParser extends ResultParser
{
    @Override
    public WifiParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (!rawText.startsWith("WIFI:")) {
            return null;
        }
        final boolean trim = false;
        final String ssid = ResultParser.matchSinglePrefixedField("S:", rawText, ';', trim);
        if (ssid == null || ssid.length() == 0) {
            return null;
        }
        final String pass = ResultParser.matchSinglePrefixedField("P:", rawText, ';', trim);
        String type = ResultParser.matchSinglePrefixedField("T:", rawText, ';', trim);
        if (type == null) {
            type = "nopass";
        }
        return new WifiParsedResult(type, ssid, pass);
    }
}
