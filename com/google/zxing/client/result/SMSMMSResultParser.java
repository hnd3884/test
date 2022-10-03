package com.google.zxing.client.result;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import com.google.zxing.Result;

public final class SMSMMSResultParser extends ResultParser
{
    @Override
    public SMSParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (!rawText.startsWith("sms:") && !rawText.startsWith("SMS:") && !rawText.startsWith("mms:") && !rawText.startsWith("MMS:")) {
            return null;
        }
        final Map<String, String> nameValuePairs = ResultParser.parseNameValuePairs(rawText);
        String subject = null;
        String body = null;
        boolean querySyntax = false;
        if (nameValuePairs != null && !nameValuePairs.isEmpty()) {
            subject = nameValuePairs.get("subject");
            body = nameValuePairs.get("body");
            querySyntax = true;
        }
        final int queryStart = rawText.indexOf(63, 4);
        String smsURIWithoutQuery;
        if (queryStart < 0 || !querySyntax) {
            smsURIWithoutQuery = rawText.substring(4);
        }
        else {
            smsURIWithoutQuery = rawText.substring(4, queryStart);
        }
        int lastComma = -1;
        final List<String> numbers = new ArrayList<String>(1);
        final List<String> vias = new ArrayList<String>(1);
        int comma;
        while ((comma = smsURIWithoutQuery.indexOf(44, lastComma + 1)) > lastComma) {
            final String numberPart = smsURIWithoutQuery.substring(lastComma + 1, comma);
            addNumberVia(numbers, vias, numberPart);
            lastComma = comma;
        }
        addNumberVia(numbers, vias, smsURIWithoutQuery.substring(lastComma + 1));
        return new SMSParsedResult(numbers.toArray(new String[numbers.size()]), vias.toArray(new String[vias.size()]), subject, body);
    }
    
    private static void addNumberVia(final Collection<String> numbers, final Collection<String> vias, final String numberPart) {
        final int numberEnd = numberPart.indexOf(59);
        if (numberEnd < 0) {
            numbers.add(numberPart);
            vias.add(null);
        }
        else {
            numbers.add(numberPart.substring(0, numberEnd));
            final String maybeVia = numberPart.substring(numberEnd + 1);
            String via;
            if (maybeVia.startsWith("via=")) {
                via = maybeVia.substring(4);
            }
            else {
                via = null;
            }
            vias.add(via);
        }
    }
}
