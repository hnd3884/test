package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class TelResultParser extends ResultParser
{
    @Override
    public TelParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (!rawText.startsWith("tel:") && !rawText.startsWith("TEL:")) {
            return null;
        }
        final String telURI = rawText.startsWith("TEL:") ? ("tel:" + rawText.substring(4)) : rawText;
        final int queryStart = rawText.indexOf(63, 4);
        final String number = (queryStart < 0) ? rawText.substring(4) : rawText.substring(4, queryStart);
        return new TelParsedResult(number, telURI, null);
    }
}
