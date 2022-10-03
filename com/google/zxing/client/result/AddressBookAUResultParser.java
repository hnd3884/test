package com.google.zxing.client.result;

import java.util.List;
import java.util.ArrayList;
import com.google.zxing.Result;

public final class AddressBookAUResultParser extends ResultParser
{
    @Override
    public AddressBookParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (!rawText.contains("MEMORY") || !rawText.contains("\r\n")) {
            return null;
        }
        final String name = ResultParser.matchSinglePrefixedField("NAME1:", rawText, '\r', true);
        final String pronunciation = ResultParser.matchSinglePrefixedField("NAME2:", rawText, '\r', true);
        final String[] phoneNumbers = matchMultipleValuePrefix("TEL", 3, rawText, true);
        final String[] emails = matchMultipleValuePrefix("MAIL", 3, rawText, true);
        final String note = ResultParser.matchSinglePrefixedField("MEMORY:", rawText, '\r', false);
        final String address = ResultParser.matchSinglePrefixedField("ADD:", rawText, '\r', true);
        final String[] addresses = (String[])((address == null) ? null : new String[] { address });
        return new AddressBookParsedResult(ResultParser.maybeWrap(name), pronunciation, phoneNumbers, null, emails, null, null, note, addresses, null, null, null, null, null);
    }
    
    private static String[] matchMultipleValuePrefix(final String prefix, final int max, final String rawText, final boolean trim) {
        List<String> values = null;
        for (int i = 1; i <= max; ++i) {
            final String value = ResultParser.matchSinglePrefixedField(prefix + i + ':', rawText, '\r', trim);
            if (value == null) {
                break;
            }
            if (values == null) {
                values = new ArrayList<String>(max);
            }
            values.add(value);
        }
        if (values == null) {
            return null;
        }
        return values.toArray(new String[values.size()]);
    }
}
