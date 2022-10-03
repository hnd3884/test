package com.google.zxing.client.result;

import java.util.regex.Matcher;
import com.google.zxing.Result;
import java.util.regex.Pattern;

public final class URIResultParser extends ResultParser
{
    private static final String PATTERN_END = "(:\\d{1,5})?(/|\\?|$)";
    private static final Pattern URL_WITH_PROTOCOL_PATTERN;
    private static final Pattern URL_WITHOUT_PROTOCOL_PATTERN;
    
    @Override
    public URIParsedResult parse(final Result result) {
        String rawText = result.getText();
        if (rawText.startsWith("URL:")) {
            rawText = rawText.substring(4);
        }
        rawText = rawText.trim();
        return isBasicallyValidURI(rawText) ? new URIParsedResult(rawText, null) : null;
    }
    
    static boolean isBasicallyValidURI(final CharSequence uri) {
        Matcher m = URIResultParser.URL_WITH_PROTOCOL_PATTERN.matcher(uri);
        if (m.find() && m.start() == 0) {
            return true;
        }
        m = URIResultParser.URL_WITHOUT_PROTOCOL_PATTERN.matcher(uri);
        return m.find() && m.start() == 0;
    }
    
    static {
        URL_WITH_PROTOCOL_PATTERN = Pattern.compile("[a-zA-Z0-9]{2,}://[a-zA-Z0-9\\-]+(\\.[a-zA-Z0-9\\-]+)*(:\\d{1,5})?(/|\\?|$)");
        URL_WITHOUT_PROTOCOL_PATTERN = Pattern.compile("([a-zA-Z0-9\\-]+\\.)+[a-zA-Z0-9\\-]{2,}(:\\d{1,5})?(/|\\?|$)");
    }
}
