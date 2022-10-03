package org.owasp.esapi.reference.validation;

import org.owasp.esapi.StringUtilities;
import org.owasp.esapi.errors.ValidationException;
import java.util.Date;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import java.text.DateFormat;

public class DateValidationRule extends BaseValidationRule
{
    private DateFormat format;
    
    public DateValidationRule(final String typeName, final Encoder encoder, final DateFormat newFormat) {
        super(typeName, encoder);
        this.format = DateFormat.getDateInstance();
        this.setDateFormat(newFormat);
    }
    
    public final void setDateFormat(final DateFormat newFormat) {
        if (newFormat == null) {
            throw new IllegalArgumentException("DateValidationRule.setDateFormat requires a non-null DateFormat");
        }
        (this.format = newFormat).setLenient(ESAPI.securityConfiguration().getLenientDatesAccepted());
    }
    
    @Override
    public Date getValid(final String context, final String input) throws ValidationException {
        return this.safelyParse(context, input);
    }
    
    public Date sanitize(final String context, final String input) {
        Date date = new Date(0L);
        try {
            date = this.safelyParse(context, input);
        }
        catch (final ValidationException ex) {}
        return date;
    }
    
    private Date safelyParse(final String context, final String input) throws ValidationException {
        if (StringUtilities.isEmpty(input)) {
            if (this.allowNull) {
                return null;
            }
            throw new ValidationException(context + ": Input date required", "Input date required: context=" + context + ", input=" + input, context);
        }
        else {
            final String canonical = this.encoder.canonicalize(input);
            try {
                return this.format.parse(canonical);
            }
            catch (final Exception e) {
                throw new ValidationException(context + ": Invalid date must follow the " + this.format.getNumberFormat() + " format", "Invalid date: context=" + context + ", format=" + this.format + ", input=" + input, e, context);
            }
        }
    }
}
