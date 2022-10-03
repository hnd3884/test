package org.owasp.validator.html.model;

import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Tag
{
    private final Map<String, Attribute> allowedAttributes;
    private final String name;
    private final String action;
    private final List<String> mandatoryAttributes;
    private final List<Attribute> insertAttributes;
    static final String ANY_NORMAL_WHITESPACES = "(\\s)*";
    static final String OPEN_ATTRIBUTE = "(";
    static final String ATTRIBUTE_DIVIDER = "|";
    static final String CLOSE_ATTRIBUTE = ")";
    private static final String OPEN_TAG_ATTRIBUTES = "(\\s)*(";
    private static final String CLOSE_TAG_ATTRIBUTES = ")*";
    private static final String REGEXP_CHARACTERS = "\\(){}.*?$^-+";
    
    public Tag(final String name, final Map<String, Attribute> tagAttributes, final String action) {
        this.name = name;
        this.allowedAttributes = Collections.unmodifiableMap((Map<? extends String, ? extends Attribute>)tagAttributes);
        this.action = action;
        this.mandatoryAttributes = null;
        this.insertAttributes = null;
    }
    
    public Tag(final String name, final Map<String, Attribute> tagAttributes, final List<String> mandatoryAttributes, final String action, final List<Attribute> insertAttributes) {
        this.name = name;
        this.allowedAttributes = Collections.unmodifiableMap((Map<? extends String, ? extends Attribute>)tagAttributes);
        this.action = action;
        this.mandatoryAttributes = ((mandatoryAttributes != null) ? Collections.unmodifiableList((List<? extends String>)mandatoryAttributes) : null);
        this.insertAttributes = insertAttributes;
    }
    
    public String getAction() {
        return this.action;
    }
    
    public boolean isAction(final String action) {
        return action.equals(this.action);
    }
    
    public Tag mutateAction(final String action) {
        return new Tag(this.name, this.allowedAttributes, action);
    }
    
    public String getRegularExpression() {
        if (this.allowedAttributes.size() == 0) {
            return "^<" + this.name + ">$";
        }
        final StringBuilder regExp = new StringBuilder("<(\\s)*" + this.name + "(\\s)*(");
        final List<Attribute> values = new ArrayList<Attribute>(this.allowedAttributes.values());
        Collections.sort(values, new Comparator<Attribute>() {
            @Override
            public int compare(final Attribute o1, final Attribute o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        final Iterator<Attribute> attributes = values.iterator();
        while (attributes.hasNext()) {
            final Attribute attr = attributes.next();
            regExp.append(attr.matcherRegEx(attributes.hasNext()));
        }
        regExp.append(")*(\\s)*>");
        return regExp.toString();
    }
    
    static String escapeRegularExpressionCharacters(final String allowedValue) {
        String toReturn = allowedValue;
        if (toReturn == null) {
            return null;
        }
        for (int i = 0; i < "\\(){}.*?$^-+".length(); ++i) {
            toReturn = toReturn.replaceAll("\\" + String.valueOf("\\(){}.*?$^-+".charAt(i)), "\\" + "\\(){}.*?$^-+".charAt(i));
        }
        return toReturn;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Attribute getAttributeByName(final String name) {
        return this.allowedAttributes.get(name);
    }
    
    public List<String> getMandatoryAttributes() {
        return this.mandatoryAttributes;
    }
    
    public List<Attribute> getInsertAttributes() {
        return this.insertAttributes;
    }
}
