package org.owasp.esapi.reference.validation;

import org.owasp.esapi.EncoderConstants;
import org.owasp.esapi.StringUtilities;
import java.util.Iterator;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.util.NullSafe;
import java.util.regex.PatternSyntaxException;
import org.owasp.esapi.Encoder;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;

public class StringValidationRule extends BaseValidationRule
{
    protected List<Pattern> whitelistPatterns;
    protected List<Pattern> blacklistPatterns;
    protected int minLength;
    protected int maxLength;
    protected boolean validateInputAndCanonical;
    
    public StringValidationRule(final String typeName) {
        super(typeName);
        this.whitelistPatterns = new ArrayList<Pattern>();
        this.blacklistPatterns = new ArrayList<Pattern>();
        this.minLength = 0;
        this.maxLength = Integer.MAX_VALUE;
        this.validateInputAndCanonical = true;
    }
    
    public StringValidationRule(final String typeName, final Encoder encoder) {
        super(typeName, encoder);
        this.whitelistPatterns = new ArrayList<Pattern>();
        this.blacklistPatterns = new ArrayList<Pattern>();
        this.minLength = 0;
        this.maxLength = Integer.MAX_VALUE;
        this.validateInputAndCanonical = true;
    }
    
    public StringValidationRule(final String typeName, final Encoder encoder, final String whitelistPattern) {
        super(typeName, encoder);
        this.whitelistPatterns = new ArrayList<Pattern>();
        this.blacklistPatterns = new ArrayList<Pattern>();
        this.minLength = 0;
        this.maxLength = Integer.MAX_VALUE;
        this.validateInputAndCanonical = true;
        this.addWhitelistPattern(whitelistPattern);
    }
    
    public void addWhitelistPattern(final String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }
        try {
            this.whitelistPatterns.add(Pattern.compile(pattern));
        }
        catch (final PatternSyntaxException e) {
            throw new IllegalArgumentException("Validation misconfiguration, problem with specified pattern: " + pattern, e);
        }
    }
    
    public void addWhitelistPattern(final Pattern p) {
        if (p == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }
        this.whitelistPatterns.add(p);
    }
    
    public void addBlacklistPattern(final String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }
        try {
            this.blacklistPatterns.add(Pattern.compile(pattern));
        }
        catch (final PatternSyntaxException e) {
            throw new IllegalArgumentException("Validation misconfiguration, problem with specified pattern: " + pattern, e);
        }
    }
    
    public void addBlacklistPattern(final Pattern p) {
        if (p == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }
        this.blacklistPatterns.add(p);
    }
    
    public void setMinimumLength(final int length) {
        this.minLength = length;
    }
    
    public void setMaximumLength(final int length) {
        this.maxLength = length;
    }
    
    public void setValidateInputAndCanonical(final boolean flag) {
        this.validateInputAndCanonical = flag;
    }
    
    private String checkWhitelist(final String context, final String input, final String orig) throws ValidationException {
        for (final Pattern p : this.whitelistPatterns) {
            if (!p.matcher(input).matches()) {
                throw new ValidationException(context + ": Invalid input. Please conform to regex " + p.pattern() + ((this.maxLength == Integer.MAX_VALUE) ? "" : (" with a maximum length of " + this.maxLength)), "Invalid input: context=" + context + ", type(" + this.getTypeName() + ")=" + p.pattern() + ", input=" + input + (NullSafe.equals(orig, input) ? "" : (", orig=" + orig)), context);
            }
        }
        return input;
    }
    
    private String checkWhitelist(final String context, final String input) throws ValidationException {
        return this.checkWhitelist(context, input, input);
    }
    
    private String checkBlacklist(final String context, final String input, final String orig) throws ValidationException {
        for (final Pattern p : this.blacklistPatterns) {
            if (p.matcher(input).matches()) {
                throw new ValidationException(context + ": Invalid input. Dangerous input matching " + p.pattern() + " detected.", "Dangerous input: context=" + context + ", type(" + this.getTypeName() + ")=" + p.pattern() + ", input=" + input + (NullSafe.equals(orig, input) ? "" : (", orig=" + orig)), context);
            }
        }
        return input;
    }
    
    private String checkBlacklist(final String context, final String input) throws ValidationException {
        return this.checkBlacklist(context, input, input);
    }
    
    private String checkLength(final String context, final String input, final String orig) throws ValidationException {
        if (input.length() < this.minLength) {
            throw new ValidationException(context + ": Invalid input. The minimum length of " + this.minLength + " characters was not met.", "Input does not meet the minimum length of " + this.minLength + " by " + (this.minLength - input.length()) + " characters: context=" + context + ", type=" + this.getTypeName() + "), input=" + input + (NullSafe.equals(input, orig) ? "" : (", orig=" + orig)), context);
        }
        if (input.length() > this.maxLength) {
            throw new ValidationException(context + ": Invalid input. The maximum length of " + this.maxLength + " characters was exceeded.", "Input exceeds maximum allowed length of " + this.maxLength + " by " + (input.length() - this.maxLength) + " characters: context=" + context + ", type=" + this.getTypeName() + ", orig=" + orig + ", input=" + input, context);
        }
        return input;
    }
    
    private String checkLength(final String context, final String input) throws ValidationException {
        return this.checkLength(context, input, input);
    }
    
    private String checkEmpty(final String context, final String input, final String orig) throws ValidationException {
        if (!StringUtilities.isEmpty(input)) {
            return input;
        }
        if (this.allowNull) {
            return null;
        }
        throw new ValidationException(context + ": Input required.", "Input required: context=" + context + "), input=" + input + (NullSafe.equals(input, orig) ? "" : (", orig=" + orig)), context);
    }
    
    private String checkEmpty(final String context, final String input) throws ValidationException {
        return this.checkEmpty(context, input, input);
    }
    
    @Override
    public String getValid(final String context, final String input) throws ValidationException {
        String data = null;
        if (this.checkEmpty(context, input) == null) {
            return null;
        }
        if (this.validateInputAndCanonical) {
            this.checkLength(context, input);
            this.checkWhitelist(context, input);
            this.checkBlacklist(context, input);
            data = this.encoder.canonicalize(input);
        }
        else {
            data = input;
        }
        if (this.checkEmpty(context, data, input) == null) {
            return null;
        }
        this.checkLength(context, data, input);
        this.checkWhitelist(context, data, input);
        this.checkBlacklist(context, data, input);
        return data;
    }
    
    public String sanitize(final String context, final String input) {
        return this.whitelist(input, EncoderConstants.CHAR_ALPHANUMERICS);
    }
}
