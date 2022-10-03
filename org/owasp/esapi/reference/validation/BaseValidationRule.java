package org.owasp.esapi.reference.validation;

import java.util.HashSet;
import java.util.Set;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.ValidationRule;

public abstract class BaseValidationRule implements ValidationRule
{
    private String typeName;
    protected boolean allowNull;
    protected Encoder encoder;
    
    private BaseValidationRule() {
        this.typeName = null;
        this.allowNull = false;
        this.encoder = null;
    }
    
    public BaseValidationRule(final String typeName) {
        this();
        this.setEncoder(ESAPI.encoder());
        this.setTypeName(typeName);
    }
    
    public BaseValidationRule(final String typeName, final Encoder encoder) {
        this();
        this.setEncoder(encoder);
        this.setTypeName(typeName);
    }
    
    @Override
    public void setAllowNull(final boolean flag) {
        this.allowNull = flag;
    }
    
    @Override
    public String getTypeName() {
        return this.typeName;
    }
    
    @Override
    public final void setTypeName(final String typeName) {
        this.typeName = typeName;
    }
    
    @Override
    public final void setEncoder(final Encoder encoder) {
        this.encoder = encoder;
    }
    
    @Override
    public void assertValid(final String context, final String input) throws ValidationException {
        this.getValid(context, input);
    }
    
    @Override
    public Object getValid(final String context, final String input, final ValidationErrorList errorList) throws ValidationException {
        Object valid = null;
        try {
            valid = this.getValid(context, input);
        }
        catch (final ValidationException e) {
            if (errorList == null) {
                throw e;
            }
            errorList.addError(context, e);
        }
        return valid;
    }
    
    @Override
    public Object getSafe(final String context, final String input) {
        Object valid = null;
        try {
            valid = this.getValid(context, input);
        }
        catch (final ValidationException e) {
            return this.sanitize(context, input);
        }
        return valid;
    }
    
    protected abstract Object sanitize(final String p0, final String p1);
    
    @Override
    public boolean isValid(final String context, final String input) {
        boolean valid = false;
        try {
            this.getValid(context, input);
            valid = true;
        }
        catch (final Exception e) {
            valid = false;
        }
        return valid;
    }
    
    @Override
    public String whitelist(final String input, final char[] whitelist) {
        return this.whitelist(input, charArrayToSet(whitelist));
    }
    
    @Override
    public String whitelist(final String input, final Set<Character> whitelist) {
        final StringBuilder stripped = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            if (whitelist.contains(c)) {
                stripped.append(c);
            }
        }
        return stripped.toString();
    }
    
    public static Set<Character> charArrayToSet(final char[] array) {
        final Set<Character> toReturn = new HashSet<Character>(array.length);
        for (final char c : array) {
            toReturn.add(c);
        }
        return toReturn;
    }
    
    public boolean isAllowNull() {
        return this.allowNull;
    }
    
    public Encoder getEncoder() {
        return this.encoder;
    }
}
