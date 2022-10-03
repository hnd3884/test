package org.owasp.validator.html.model;

import java.util.Collections;
import java.util.regex.Pattern;
import java.util.List;

public class Property
{
    private final String name;
    private final List<String> allowedValues;
    private final List<Pattern> allowedRegExp;
    private final List<String> shorthandRefs;
    private final List<String> categoryValues;
    
    public Property(final String name, final List<Pattern> allowedRegexp3, final List<String> allowedValue, final List<String> shortHandRefs, final List<String> category_value, final String description, final String onInvalidStr) {
        this.name = name;
        this.allowedRegExp = Collections.unmodifiableList((List<? extends Pattern>)allowedRegexp3);
        this.allowedValues = Collections.unmodifiableList((List<? extends String>)allowedValue);
        this.shorthandRefs = Collections.unmodifiableList((List<? extends String>)shortHandRefs);
        this.categoryValues = Collections.unmodifiableList((List<? extends String>)category_value);
    }
    
    public List<Pattern> getAllowedRegExp() {
        return this.allowedRegExp;
    }
    
    public List<String> getAllowedValues() {
        return this.allowedValues;
    }
    
    public List<String> getShorthandRefs() {
        return this.shorthandRefs;
    }
    
    public List<String> getCategoryValues() {
        return this.categoryValues;
    }
    
    public String getName() {
        return this.name;
    }
}
