package com.google.zxing.client.result;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

public final class ISBNResultParser extends ResultParser
{
    @Override
    public ISBNParsedResult parse(final Result result) {
        final BarcodeFormat format = result.getBarcodeFormat();
        if (format != BarcodeFormat.EAN_13) {
            return null;
        }
        final String rawText = result.getText();
        final int length = rawText.length();
        if (length != 13) {
            return null;
        }
        if (!rawText.startsWith("978") && !rawText.startsWith("979")) {
            return null;
        }
        return new ISBNParsedResult(rawText);
    }
}
