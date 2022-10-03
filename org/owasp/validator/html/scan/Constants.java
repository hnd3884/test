package org.owasp.validator.html.scan;

import java.util.Map;
import java.util.HashSet;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import org.owasp.validator.html.model.Attribute;
import java.util.Collections;
import java.util.Arrays;
import org.owasp.validator.html.Policy;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.List;
import org.owasp.validator.html.model.Tag;

public class Constants
{
    public static final String DEFAULT_ENCODING_ALGORITHM = "UTF-8";
    public static final Tag BASIC_PARAM_TAG_RULE;
    public static final List<String> defaultAllowedEmptyTags;
    public static final List<String> defaultRequireClosingTags;
    private static final String[] allowedEmptyTags;
    private static final String[] requiresClosingTags;
    public static final String DEFAULT_LOCALE_LANG = "en";
    public static final String DEFAULT_LOCALE_LOC = "US";
    public static final String big5CharsToEncode = "<>\"'&:";
    public static final Set<Integer> big5CharsToEncodeSet;
    
    static {
        allowedEmptyTags = new String[] { "br", "hr", "a", "img", "link", "iframe", "script", "object", "applet", "frame", "base", "param", "meta", "input", "textarea", "embed", "basefont", "col" };
        requiresClosingTags = new String[] { "iframe", "script", "link" };
        final Attribute paramNameAttr = new Attribute("name", Arrays.asList(Policy.ANYTHING_REGEXP), Collections.emptyList(), null, null);
        final Attribute paramValueAttr = new Attribute("value", Arrays.asList(Policy.ANYTHING_REGEXP), Collections.emptyList(), null, null);
        final Map<String, Attribute> attrs = new HashMap<String, Attribute>();
        attrs.put(paramNameAttr.getName().toLowerCase(), paramNameAttr);
        attrs.put(paramValueAttr.getName().toLowerCase(), paramValueAttr);
        BASIC_PARAM_TAG_RULE = new Tag("param", attrs, "validate");
        final List<String> allowedEmptyTagsList = new ArrayList<String>();
        allowedEmptyTagsList.addAll(Arrays.asList(Constants.allowedEmptyTags));
        defaultAllowedEmptyTags = Collections.unmodifiableList((List<? extends String>)allowedEmptyTagsList);
        final List<String> requiresClosingTagsList = new ArrayList<String>();
        requiresClosingTagsList.addAll(Arrays.asList(Constants.requiresClosingTags));
        defaultRequireClosingTags = Collections.unmodifiableList((List<? extends String>)requiresClosingTagsList);
        big5CharsToEncodeSet = new HashSet<Integer>() {
            {
                for (int i = 0; i < "<>\"'&:".length(); ++i) {
                    this.add((int)"<>\"'&:".charAt(i));
                }
            }
        };
    }
}
