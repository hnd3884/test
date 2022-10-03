package org.owasp.esapi.reference.validation;

import org.owasp.esapi.StringUtilities;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.Encoder;
import java.math.BigDecimal;

public class NumberValidationRule extends BaseValidationRule
{
    private double minValue;
    private double maxValue;
    private static BigDecimal bigBad;
    private static BigDecimal smallBad;
    
    public NumberValidationRule(final String typeName, final Encoder encoder) {
        super(typeName, encoder);
        this.minValue = Double.NEGATIVE_INFINITY;
        this.maxValue = Double.POSITIVE_INFINITY;
    }
    
    public NumberValidationRule(final String typeName, final Encoder encoder, final double minValue, final double maxValue) {
        super(typeName, encoder);
        this.minValue = Double.NEGATIVE_INFINITY;
        this.maxValue = Double.POSITIVE_INFINITY;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    @Override
    public Double getValid(final String context, final String input) throws ValidationException {
        return this.safelyParse(context, input);
    }
    
    public Double sanitize(final String context, final String input) {
        Double toReturn = 0.0;
        try {
            toReturn = this.safelyParse(context, input);
        }
        catch (final ValidationException ex) {}
        return toReturn;
    }
    
    private Double safelyParse(final String context, final String input) throws ValidationException {
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
            BigDecimal bd;
            try {
                bd = new BigDecimal(canonical);
            }
            catch (final NumberFormatException e) {
                throw new ValidationException(context + ": Invalid number input", "Invalid number input format: context=" + context + ", input=" + input, e, context);
            }
            if (bd.compareTo(NumberValidationRule.smallBad) >= 0 && bd.compareTo(NumberValidationRule.bigBad) <= 0) {
                return new Double("2.2250738585072014E-308");
            }
            Double d;
            try {
                d = Double.parseDouble(canonical);
            }
            catch (final NumberFormatException e2) {
                throw new ValidationException(context + ": Invalid number input", "Invalid number input format: context=" + context + ", input=" + input, e2, context);
            }
            if (d.isInfinite()) {
                throw new ValidationException("Invalid number input: context=" + context, "Invalid double input is infinite: context=" + context + ", input=" + input, context);
            }
            if (d.isNaN()) {
                throw new ValidationException("Invalid number input: context=" + context, "Invalid double input is not a number: context=" + context + ", input=" + input, context);
            }
            if (d < this.minValue) {
                throw new ValidationException("Invalid number input must be between " + this.minValue + " and " + this.maxValue + ": context=" + context, "Invalid number input must be between " + this.minValue + " and " + this.maxValue + ": context=" + context + ", input=" + input, context);
            }
            if (d > this.maxValue) {
                throw new ValidationException("Invalid number input must be between " + this.minValue + " and " + this.maxValue + ": context=" + context, "Invalid number input must be between " + this.minValue + " and " + this.maxValue + ": context=" + context + ", input=" + input, context);
            }
            return d;
        }
    }
    
    static {
        final BigDecimal one = new BigDecimal(1);
        final BigDecimal two = new BigDecimal(2);
        final BigDecimal tiny = one.divide(two.pow(1022));
        NumberValidationRule.bigBad = tiny.subtract(one.divide(two.pow(1076)));
        NumberValidationRule.smallBad = tiny.subtract(one.divide(two.pow(1075)));
    }
}
