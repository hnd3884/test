package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class BookmarkDoCoMoResultParser extends AbstractDoCoMoResultParser
{
    @Override
    public URIParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (!rawText.startsWith("MEBKM:")) {
            return null;
        }
        final String title = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("TITLE:", rawText, true);
        final String[] rawUri = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("URL:", rawText, true);
        if (rawUri == null) {
            return null;
        }
        final String uri = rawUri[0];
        return URIResultParser.isBasicallyValidURI(uri) ? new URIParsedResult(uri, title) : null;
    }
}
