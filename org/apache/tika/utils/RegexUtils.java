package org.apache.tika.utils;

import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class RegexUtils
{
    private static final String LINKS_REGEX = "([A-Za-z][A-Za-z0-9+.-]{1,120}:[A-Za-z0-9/](([A-Za-z0-9$_.+!*,;/?:@&~=-])|%[A-Fa-f0-9]{2}){1,333}(#([a-zA-Z0-9][a-zA-Z0-9$_.+!*,;/?:@&~=%-]{0,1000}))?)";
    private static final Pattern LINKS_PATTERN;
    
    public static List<String> extractLinks(final String content) {
        if (content == null || content.length() == 0) {
            return Collections.emptyList();
        }
        final List<String> extractions = new ArrayList<String>();
        final Matcher matcher = RegexUtils.LINKS_PATTERN.matcher(content);
        while (matcher.find()) {
            extractions.add(matcher.group());
        }
        return extractions;
    }
    
    static {
        LINKS_PATTERN = Pattern.compile("([A-Za-z][A-Za-z0-9+.-]{1,120}:[A-Za-z0-9/](([A-Za-z0-9$_.+!*,;/?:@&~=-])|%[A-Fa-f0-9]{2}){1,333}(#([a-zA-Z0-9][a-zA-Z0-9$_.+!*,;/?:@&~=%-]{0,1000}))?)", 10);
    }
}
