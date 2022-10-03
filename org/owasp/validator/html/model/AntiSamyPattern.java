package org.owasp.validator.html.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiSamyPattern
{
    private final Pattern pattern;
    
    public AntiSamyPattern(final Pattern pattern) {
        this.pattern = pattern;
    }
    
    public Pattern getPattern() {
        return this.pattern;
    }
    
    public Matcher matcher(final CharSequence input) {
        return this.pattern.matcher(input);
    }
    
    public boolean matches(final String other) {
        return this.matcher(other).matches();
    }
}
