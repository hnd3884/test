package com.google.zxing.client.result;

abstract class AbstractDoCoMoResultParser extends ResultParser
{
    static String[] matchDoCoMoPrefixedField(final String prefix, final String rawText, final boolean trim) {
        return ResultParser.matchPrefixedField(prefix, rawText, ';', trim);
    }
    
    static String matchSingleDoCoMoPrefixedField(final String prefix, final String rawText, final boolean trim) {
        return ResultParser.matchSinglePrefixedField(prefix, rawText, ';', trim);
    }
}
