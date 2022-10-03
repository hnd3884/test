package com.google.zxing.client.result;

import java.util.List;
import java.util.ArrayList;
import com.google.zxing.Result;

public final class BizcardResultParser extends AbstractDoCoMoResultParser
{
    @Override
    public AddressBookParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (!rawText.startsWith("BIZCARD:")) {
            return null;
        }
        final String firstName = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("N:", rawText, true);
        final String lastName = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("X:", rawText, true);
        final String fullName = buildName(firstName, lastName);
        final String title = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("T:", rawText, true);
        final String org = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("C:", rawText, true);
        final String[] addresses = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("A:", rawText, true);
        final String phoneNumber1 = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("B:", rawText, true);
        final String phoneNumber2 = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("M:", rawText, true);
        final String phoneNumber3 = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("F:", rawText, true);
        final String email = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("E:", rawText, true);
        return new AddressBookParsedResult(ResultParser.maybeWrap(fullName), null, buildPhoneNumbers(phoneNumber1, phoneNumber2, phoneNumber3), null, ResultParser.maybeWrap(email), null, null, null, addresses, null, org, null, title, null);
    }
    
    private static String[] buildPhoneNumbers(final String number1, final String number2, final String number3) {
        final List<String> numbers = new ArrayList<String>(3);
        if (number1 != null) {
            numbers.add(number1);
        }
        if (number2 != null) {
            numbers.add(number2);
        }
        if (number3 != null) {
            numbers.add(number3);
        }
        final int size = numbers.size();
        if (size == 0) {
            return null;
        }
        return numbers.toArray(new String[size]);
    }
    
    private static String buildName(final String firstName, final String lastName) {
        if (firstName == null) {
            return lastName;
        }
        return (lastName == null) ? firstName : (firstName + ' ' + lastName);
    }
}
