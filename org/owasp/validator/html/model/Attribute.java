package org.owasp.validator.html.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.List;

public class Attribute
{
    private final String name;
    private final String description;
    private final String onInvalid;
    private final List<String> allowedValues;
    private final Pattern[] allowedRegExps;
    private final Set<String> allowedValuesLower;
    private final List<String> insertValues;
    private final Map<String, List<String>> criteriaAttributes;
    
    public Attribute(final String name, final List<Pattern> allowedRegexps, final List<String> allowedValues, final String onInvalidStr, final String description) {
        this.name = name;
        this.allowedRegExps = allowedRegexps.toArray(new Pattern[allowedRegexps.size()]);
        this.allowedValues = Collections.unmodifiableList((List<? extends String>)allowedValues);
        final Set<String> allowedValuesLower = new HashSet<String>();
        for (final String allowedValue : allowedValues) {
            allowedValuesLower.add(allowedValue.toLowerCase());
        }
        this.allowedValuesLower = allowedValuesLower;
        this.onInvalid = onInvalidStr;
        this.description = description;
        this.insertValues = null;
        this.criteriaAttributes = null;
    }
    
    public Attribute(final String name, final List<String> insertValues, final Map<String, List<String>> criteriaAttributes) {
        this.name = name;
        this.allowedRegExps = null;
        this.allowedValues = null;
        this.allowedValuesLower = null;
        this.onInvalid = null;
        this.description = null;
        this.insertValues = insertValues;
        this.criteriaAttributes = criteriaAttributes;
    }
    
    public boolean matchesAllowedExpression(final String value) {
        final String input = value.toLowerCase();
        for (final Pattern pattern : this.allowedRegExps) {
            if (pattern != null && pattern.matcher(input).matches()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsAllowedValue(final String valueInLowerCase) {
        return this.allowedValuesLower.contains(valueInLowerCase);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getOnInvalid() {
        return this.onInvalid;
    }
    
    public Attribute mutate(final String onInvalid, final String description) {
        return new Attribute(this.name, Arrays.asList(this.allowedRegExps), this.allowedValues, (onInvalid != null && onInvalid.length() != 0) ? onInvalid : this.onInvalid, (description != null && description.length() != 0) ? description : this.description);
    }
    
    public String matcherRegEx(final boolean hasNext) {
        final StringBuilder regExp = new StringBuilder();
        regExp.append(this.getName()).append("(\\s)*").append("=").append("(\\s)*").append("\"").append("(");
        final boolean hasRegExps = this.allowedRegExps.length > 0;
        if (this.allowedRegExps.length + this.allowedValues.size() > 0) {
            final Iterator<String> allowedValues = this.allowedValues.iterator();
            while (allowedValues.hasNext()) {
                final String allowedValue = allowedValues.next();
                regExp.append(Tag.escapeRegularExpressionCharacters(allowedValue));
                if (allowedValues.hasNext() || hasRegExps) {
                    regExp.append("|");
                }
            }
            final Iterator<Pattern> allowedRegExps = Arrays.asList((Pattern[])this.allowedRegExps).iterator();
            while (allowedRegExps.hasNext()) {
                final Pattern allowedRegExp = allowedRegExps.next();
                regExp.append(allowedRegExp.pattern());
                if (allowedRegExps.hasNext()) {
                    regExp.append("|");
                }
            }
            if (this.allowedRegExps.length + this.allowedValues.size() > 0) {
                regExp.append(")");
            }
            regExp.append("\"(\\s)*");
            if (hasNext) {
                regExp.append("|");
            }
        }
        return regExp.toString();
    }
    
    public List<String> getAllowedValues() {
        final List<String> allowedValues = new ArrayList<String>();
        for (final String allowedValue : this.allowedValuesLower) {
            allowedValues.add(allowedValue);
        }
        return allowedValues;
    }
    
    public List<String> getInsertValues() {
        return this.insertValues;
    }
    
    public boolean isCriteriaMatched(final String name, final String value, final Attribute attribute) {
        if (this.criteriaAttributes == null) {
            return true;
        }
        if (this.criteriaAttributes.containsKey(name)) {
            final List<String> criteriaAttributeValues = this.criteriaAttributes.get(name);
            if (criteriaAttributeValues.contains(value)) {
                return true;
            }
            if (attribute.containsAllowedValue(value.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
