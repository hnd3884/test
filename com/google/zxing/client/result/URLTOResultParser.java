package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class URLTOResultParser extends ResultParser
{
    @Override
    public URIParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (!rawText.startsWith("urlto:") && !rawText.startsWith("URLTO:")) {
            return null;
        }
        final int titleEnd = rawText.indexOf(58, 6);
        if (titleEnd < 0) {
            return null;
        }
        final String title = (titleEnd <= 6) ? null : rawText.substring(6, titleEnd);
        final String uri = rawText.substring(titleEnd + 1);
        return new URIParsedResult(uri, title);
    }
}
