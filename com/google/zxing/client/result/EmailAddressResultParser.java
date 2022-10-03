package com.google.zxing.client.result;

import java.util.Map;
import com.google.zxing.Result;

public final class EmailAddressResultParser extends ResultParser
{
    @Override
    public EmailAddressParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (rawText.startsWith("mailto:") || rawText.startsWith("MAILTO:")) {
            String emailAddress = rawText.substring(7);
            final int queryStart = emailAddress.indexOf(63);
            if (queryStart >= 0) {
                emailAddress = emailAddress.substring(0, queryStart);
            }
            final Map<String, String> nameValues = ResultParser.parseNameValuePairs(rawText);
            String subject = null;
            String body = null;
            if (nameValues != null) {
                if (emailAddress.length() == 0) {
                    emailAddress = nameValues.get("to");
                }
                subject = nameValues.get("subject");
                body = nameValues.get("body");
            }
            return new EmailAddressParsedResult(emailAddress, subject, body, rawText);
        }
        if (!EmailDoCoMoResultParser.isBasicallyValidEmailAddress(rawText)) {
            return null;
        }
        String emailAddress = rawText;
        return new EmailAddressParsedResult(emailAddress, null, null, "mailto:" + emailAddress);
    }
}
