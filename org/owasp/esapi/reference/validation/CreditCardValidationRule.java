package org.owasp.esapi.reference.validation;

import org.owasp.esapi.EncoderConstants;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.StringUtilities;
import java.util.regex.Pattern;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

public class CreditCardValidationRule extends BaseValidationRule
{
    private int maxCardLength;
    protected static final String CREDIT_CARD_VALIDATOR_KEY = "CreditCard";
    private StringValidationRule ccrule;
    
    public CreditCardValidationRule(final String typeName, final Encoder encoder) {
        super(typeName, encoder);
        this.maxCardLength = 19;
        this.ccrule = null;
        this.ccrule = this.readDefaultCreditCardRule();
    }
    
    public CreditCardValidationRule(final String typeName, final Encoder encoder, final StringValidationRule validationRule) {
        super(typeName, encoder);
        this.maxCardLength = 19;
        this.ccrule = null;
        this.ccrule = validationRule;
    }
    
    private StringValidationRule readDefaultCreditCardRule() {
        final Pattern p = ESAPI.securityConfiguration().getValidationPattern("CreditCard");
        final StringValidationRule ccr = new StringValidationRule("ccrule", this.encoder, p.pattern());
        ccr.setMaximumLength(this.getMaxCardLength());
        ccr.setAllowNull(false);
        return ccr;
    }
    
    @Override
    public String getValid(final String context, final String input) throws ValidationException {
        if (StringUtilities.isEmpty(input)) {
            if (this.allowNull) {
                return null;
            }
            throw new ValidationException(context + ": Input credit card required", "Input credit card required: context=" + context + ", input=" + input, context);
        }
        else {
            final String canonical = this.ccrule.getValid(context, input);
            if (!this.validCreditCardFormat(canonical)) {
                throw new ValidationException(context + ": Invalid credit card input", "Invalid credit card input: context=" + context, context);
            }
            return canonical;
        }
    }
    
    protected boolean validCreditCardFormat(final String ccNum) {
        final StringBuilder digitsOnly = new StringBuilder();
        for (int i = 0; i < ccNum.length(); ++i) {
            final char c = ccNum.charAt(i);
            if (Character.isDigit(c)) {
                digitsOnly.append(c);
            }
        }
        int sum = 0;
        int digit = 0;
        int addend = 0;
        boolean timesTwo = false;
        for (int j = digitsOnly.length() - 1; j >= 0; --j) {
            digit = Integer.valueOf(digitsOnly.substring(j, j + 1));
            if (timesTwo) {
                addend = digit * 2;
                if (addend > 9) {
                    addend -= 9;
                }
            }
            else {
                addend = digit;
            }
            sum += addend;
            timesTwo = !timesTwo;
        }
        return sum % 10 == 0;
    }
    
    public String sanitize(final String context, final String input) {
        return this.whitelist(input, EncoderConstants.CHAR_DIGITS);
    }
    
    public void setStringValidatorRule(final StringValidationRule ccrule) {
        this.ccrule = ccrule;
    }
    
    public StringValidationRule getStringValidatorRule() {
        return this.ccrule;
    }
    
    public void setMaxCardLength(final int maxCardLength) {
        this.maxCardLength = maxCardLength;
    }
    
    public int getMaxCardLength() {
        return this.maxCardLength;
    }
}
