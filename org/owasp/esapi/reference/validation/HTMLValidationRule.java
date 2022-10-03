package org.owasp.esapi.reference.validation;

import java.io.InputStream;
import java.io.IOException;
import org.owasp.esapi.errors.ConfigurationException;
import org.owasp.esapi.ESAPI;
import java.util.List;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.AntiSamy;
import org.owasp.esapi.StringUtilities;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.Logger;
import org.owasp.validator.html.Policy;

public class HTMLValidationRule extends StringValidationRule
{
    private static Policy antiSamyPolicy;
    private static final Logger LOGGER;
    
    public HTMLValidationRule(final String typeName) {
        super(typeName);
    }
    
    public HTMLValidationRule(final String typeName, final Encoder encoder) {
        super(typeName, encoder);
    }
    
    public HTMLValidationRule(final String typeName, final Encoder encoder, final String whitelistPattern) {
        super(typeName, encoder, whitelistPattern);
    }
    
    @Override
    public String getValid(final String context, final String input) throws ValidationException {
        return this.invokeAntiSamy(context, input);
    }
    
    @Override
    public String sanitize(final String context, final String input) {
        String safe = "";
        try {
            safe = this.invokeAntiSamy(context, input);
        }
        catch (final ValidationException ex) {}
        return safe;
    }
    
    private String invokeAntiSamy(final String context, final String input) throws ValidationException {
        if (StringUtilities.isEmpty(input)) {
            if (this.allowNull) {
                return null;
            }
            throw new ValidationException(context + " is required", "AntiSamy validation error: context=" + context + ", input=" + input, context);
        }
        else {
            final String canonical = super.getValid(context, input);
            try {
                final AntiSamy as = new AntiSamy();
                final CleanResults test = as.scan(canonical, HTMLValidationRule.antiSamyPolicy);
                final List<String> errors = test.getErrorMessages();
                if (!errors.isEmpty()) {
                    HTMLValidationRule.LOGGER.info(Logger.SECURITY_FAILURE, "Cleaned up invalid HTML input: " + errors);
                }
                return test.getCleanHTML().trim();
            }
            catch (final ScanException e) {
                throw new ValidationException(context + ": Invalid HTML input", "Invalid HTML input: context=" + context + " error=" + e.getMessage(), (Throwable)e, context);
            }
            catch (final PolicyException e2) {
                throw new ValidationException(context + ": Invalid HTML input", "Invalid HTML input does not follow rules in antisamy-esapi.xml: context=" + context + " error=" + e2.getMessage(), (Throwable)e2, context);
            }
        }
    }
    
    static {
        HTMLValidationRule.antiSamyPolicy = null;
        LOGGER = ESAPI.getLogger("HTMLValidationRule");
        InputStream resourceStream = null;
        try {
            resourceStream = ESAPI.securityConfiguration().getResourceStream("antisamy-esapi.xml");
        }
        catch (final IOException e) {
            throw new ConfigurationException("Couldn't find antisamy-esapi.xml", e);
        }
        if (resourceStream != null) {
            try {
                HTMLValidationRule.antiSamyPolicy = Policy.getInstance(resourceStream);
            }
            catch (final PolicyException e2) {
                throw new ConfigurationException("Couldn't parse antisamy policy", (Throwable)e2);
            }
        }
    }
}
