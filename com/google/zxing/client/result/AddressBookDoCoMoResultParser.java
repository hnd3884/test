package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class AddressBookDoCoMoResultParser extends AbstractDoCoMoResultParser
{
    @Override
    public AddressBookParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (!rawText.startsWith("MECARD:")) {
            return null;
        }
        final String[] rawName = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("N:", rawText, true);
        if (rawName == null) {
            return null;
        }
        final String name = parseName(rawName[0]);
        final String pronunciation = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("SOUND:", rawText, true);
        final String[] phoneNumbers = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("TEL:", rawText, true);
        final String[] emails = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("EMAIL:", rawText, true);
        final String note = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("NOTE:", rawText, false);
        final String[] addresses = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("ADR:", rawText, true);
        String birthday = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("BDAY:", rawText, true);
        if (birthday != null && !ResultParser.isStringOfDigits(birthday, 8)) {
            birthday = null;
        }
        final String url = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("URL:", rawText, true);
        final String org = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("ORG:", rawText, true);
        return new AddressBookParsedResult(ResultParser.maybeWrap(name), pronunciation, phoneNumbers, null, emails, null, null, note, addresses, null, org, birthday, null, url);
    }
    
    private static String parseName(final String name) {
        final int comma = name.indexOf(44);
        if (comma >= 0) {
            return name.substring(comma + 1) + ' ' + name.substring(0, comma);
        }
        return name;
    }
}
