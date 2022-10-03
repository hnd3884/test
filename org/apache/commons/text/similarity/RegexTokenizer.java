package org.apache.commons.text.similarity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.ArrayList;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.StringUtils;
import java.util.regex.Pattern;

class RegexTokenizer implements Tokenizer<CharSequence>
{
    private static final Pattern PATTERN;
    
    @Override
    public CharSequence[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text", new Object[0]);
        final Matcher matcher = RegexTokenizer.PATTERN.matcher(text);
        final List<String> tokens = new ArrayList<String>();
        while (matcher.find()) {
            tokens.add(matcher.group(0));
        }
        return tokens.toArray(new String[0]);
    }
    
    static {
        PATTERN = Pattern.compile("(\\w)+");
    }
}
