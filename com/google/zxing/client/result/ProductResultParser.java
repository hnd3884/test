package com.google.zxing.client.result;

import com.google.zxing.oned.UPCEReader;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

public final class ProductResultParser extends ResultParser
{
    @Override
    public ProductParsedResult parse(final Result result) {
        final BarcodeFormat format = result.getBarcodeFormat();
        if (format != BarcodeFormat.UPC_A && format != BarcodeFormat.UPC_E && format != BarcodeFormat.EAN_8 && format != BarcodeFormat.EAN_13) {
            return null;
        }
        final String rawText = result.getText();
        for (int length = rawText.length(), x = 0; x < length; ++x) {
            final char c = rawText.charAt(x);
            if (c < '0' || c > '9') {
                return null;
            }
        }
        String normalizedProductID;
        if (format == BarcodeFormat.UPC_E) {
            normalizedProductID = UPCEReader.convertUPCEtoUPCA(rawText);
        }
        else {
            normalizedProductID = rawText;
        }
        return new ProductParsedResult(rawText, normalizedProductID);
    }
}
