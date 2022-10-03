package org.owasp.esapi.reference.validation;

import org.owasp.esapi.StringUtilities;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.Encoder;

public class IntegerValidationRule extends BaseValidationRule
{
    private int minValue;
    private int maxValue;
    
    public IntegerValidationRule(final String typeName, final Encoder encoder) {
        super(typeName, encoder);
        this.minValue = Integer.MIN_VALUE;
        this.maxValue = Integer.MAX_VALUE;
    }
    
    public IntegerValidationRule(final String typeName, final Encoder encoder, final int minValue, final int maxValue) {
        super(typeName, encoder);
        this.minValue = Integer.MIN_VALUE;
        this.maxValue = Integer.MAX_VALUE;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    @Override
    public Integer getValid(final String context, final String input) throws ValidationException {
        return this.safelyParse(context, input);
    }
    
    private Integer safelyParse(final String context, String input) throws ValidationException {
        if (input != null) {
            input = input.trim();
        }
        if (StringUtilities.isEmpty(input)) {
            if (this.allowNull) {
                return null;
            }
            throw new ValidationException(context + ": Input number required", "Input number required: context=" + context + ", input=" + input, context);
        }
        else {
            final String canonical = this.encoder.canonicalize(input);
            if (this.minValue > this.maxValue) {
                throw new ValidationException(context + ": Invalid number input: context", "Validation parameter error for number: maxValue ( " + this.maxValue + ") must be greater than minValue ( " + this.minValue + ") for " + context, context);
            }
            try {
                final int i = Integer.valueOf(canonical);
                if (i < this.minValue) {
                    throw new ValidationException("Invalid number input must be between " + this.minValue + " and " + this.maxValue + ": context=" + context, "Invalid number input must be between " + this.minValue + " and " + this.maxValue + ": context=" + context + ", input=" + input, context);
                }
                if (i > this.maxValue) {
                    throw new ValidationException("Invalid number input must be between " + this.minValue + " and " + this.maxValue + ": context=" + context, "Invalid number input must be between " + this.minValue + " and " + this.maxValue + ": context=" + context + ", input=" + input, context);
                }
                return i;
            }
            catch (final NumberFormatException e) {
                throw new ValidationException(context + ": Invalid number input", "Invalid number input format: context=" + context + ", input=" + input, e, context);
            }
        }
    }
    
    public Integer sanitize(final String context, final String input) {
        Integer toReturn = 0;
        try {
            toReturn = this.safelyParse(context, input);
        }
        catch (final ValidationException ex) {}
        return toReturn;
    }
}
