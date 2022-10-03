package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.util.regex.Pattern;

public final class EmailDoCoMoResultParser extends AbstractDoCoMoResultParser
{
    private static final Pattern ATEXT_ALPHANUMERIC;
    
    @Override
    public EmailAddressParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (!rawText.startsWith("MATMSG:")) {
            return null;
        }
        final String[] rawTo = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("TO:", rawText, true);
        if (rawTo == null) {
            return null;
        }
        final String to = rawTo[0];
        if (!isBasicallyValidEmailAddress(to)) {
            return null;
        }
        final String subject = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("SUB:", rawText, false);
        final String body = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("BODY:", rawText, false);
        return new EmailAddressParsedResult(to, subject, body, "mailto:" + to);
    }
    
    static boolean isBasicallyValidEmailAddress(final String email) {
        return email != null && EmailDoCoMoResultParser.ATEXT_ALPHANUMERIC.matcher(email).matches() && email.indexOf(64) >= 0;
    }
    
    static {
        ATEXT_ALPHANUMERIC = Pattern.compile("[a-zA-Z0-9@.!#$%&'*+\\-/=?^_`{|}~]+");
    }
}
