package com.zoho.security.zsecpiidetector.finder;

import java.util.HashMap;
import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.util.Span;
import java.util.Iterator;
import java.util.regex.Pattern;
import com.zoho.security.zsecpiidetector.PIIUtil;
import org.w3c.dom.Element;
import com.zoho.security.zsecpiidetector.types.PIIRegexType;
import java.util.Map;

public class PIIRegex
{
    private static final Map<String, PIIRegexType> PII_REGEX_LIST;
    
    public static void init(final Element regexesElement) {
        for (final Element regex : PIIUtil.getChildNodesByTagName(regexesElement, "regex")) {
            final PIIRegexType regexType = new PIIRegexType(regex.getAttribute("name"), Pattern.compile(regex.getAttribute("value")), Float.parseFloat(regex.getAttribute("score")), Integer.parseInt(regex.getAttribute("min-len")), Integer.parseInt(regex.getAttribute("max-len")));
            PIIRegex.PII_REGEX_LIST.put(regexType.getName(), regexType);
        }
    }
    
    public static Span[] findPII(final String content, final String key) {
        final RegexNameFinder finder = new RegexNameFinder(new Pattern[] { PIIRegex.PII_REGEX_LIST.get(key).getValue() }, key);
        final Span[] spans = finder.find(content);
        float score = 0.0f;
        for (int i = 0; i < spans.length; ++i) {
            final int start = spans[i].getStart();
            final int end = spans[i].getEnd();
            score = PIIRegex.PII_REGEX_LIST.get(spans[i].getType()).getScore();
            final int substringLength = end - start;
            if (isSubstringLengthValid(substringLength, spans[i].getType())) {
                score += 0.2f;
            }
            if (isSubstringValid(content, start, end)) {
                score += 0.2f;
            }
            spans[i] = new Span(start, end, spans[i].getType(), (double)score);
        }
        return spans;
    }
    
    private static boolean isSubstringLengthValid(final int substringLength, final String regexType) {
        final int minLength = PIIRegex.PII_REGEX_LIST.get(regexType).getMinimumLength();
        final int maxLength = PIIRegex.PII_REGEX_LIST.get(regexType).getMaximumLength();
        return substringLength >= minLength && substringLength <= maxLength;
    }
    
    private static boolean isSubstringValid(final String content, final int start, final int end) {
        boolean validSubstring = false;
        if (start == 0 || Character.isWhitespace(content.charAt(start - 1))) {
            validSubstring = (end >= content.length() || Character.isWhitespace(content.charAt(end)));
        }
        return validSubstring && !content.substring(start, end).contains(" ");
    }
    
    static {
        PII_REGEX_LIST = new HashMap<String, PIIRegexType>();
    }
}
