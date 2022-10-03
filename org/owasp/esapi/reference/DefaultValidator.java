package org.owasp.esapi.reference;

import java.util.ArrayList;
import org.owasp.esapi.errors.ValidationAvailabilityException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.owasp.esapi.reference.validation.IntegerValidationRule;
import org.owasp.esapi.reference.validation.NumberValidationRule;
import java.util.Iterator;
import java.io.IOException;
import java.util.List;
import java.io.File;
import org.owasp.esapi.reference.validation.CreditCardValidationRule;
import org.owasp.esapi.reference.validation.HTMLValidationRule;
import org.owasp.esapi.reference.validation.DateValidationRule;
import java.util.Date;
import java.text.DateFormat;
import java.util.regex.Pattern;
import org.owasp.esapi.reference.validation.StringValidationRule;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.ESAPI;
import java.util.HashMap;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.ValidationRule;
import java.util.Map;
import org.owasp.esapi.Validator;

public class DefaultValidator implements Validator
{
    private static volatile Validator instance;
    private Map<String, ValidationRule> rules;
    private Encoder encoder;
    private static Validator fileValidator;
    
    public static Validator getInstance() {
        if (DefaultValidator.instance == null) {
            synchronized (Validator.class) {
                if (DefaultValidator.instance == null) {
                    DefaultValidator.instance = new DefaultValidator();
                }
            }
        }
        return DefaultValidator.instance;
    }
    
    public DefaultValidator() {
        this.rules = new HashMap<String, ValidationRule>();
        this.encoder = null;
        this.encoder = ESAPI.encoder();
    }
    
    public DefaultValidator(final Encoder encoder) {
        this.rules = new HashMap<String, ValidationRule>();
        this.encoder = null;
        this.encoder = encoder;
    }
    
    @Override
    public void addRule(final ValidationRule rule) {
        this.rules.put(rule.getTypeName(), rule);
    }
    
    @Override
    public ValidationRule getRule(final String name) {
        return this.rules.get(name);
    }
    
    @Override
    public boolean isValidInput(final String context, final String input, final String type, final int maxLength, final boolean allowNull) throws IntrusionException {
        return this.isValidInput(context, input, type, maxLength, allowNull, true);
    }
    
    @Override
    public boolean isValidInput(final String context, final String input, final String type, final int maxLength, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        return this.isValidInput(context, input, type, maxLength, allowNull, true, errors);
    }
    
    @Override
    public boolean isValidInput(final String context, final String input, final String type, final int maxLength, final boolean allowNull, final boolean canonicalize) throws IntrusionException {
        try {
            this.getValidInput(context, input, type, maxLength, allowNull, canonicalize);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidInput(final String context, final String input, final String type, final int maxLength, final boolean allowNull, final boolean canonicalize, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidInput(context, input, type, maxLength, allowNull, canonicalize);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public String getValidInput(final String context, final String input, final String type, final int maxLength, final boolean allowNull) throws ValidationException {
        return this.getValidInput(context, input, type, maxLength, allowNull, true);
    }
    
    @Override
    public String getValidInput(final String context, final String input, final String type, final int maxLength, final boolean allowNull, final boolean canonicalize) throws ValidationException {
        final StringValidationRule rvr = new StringValidationRule(type, this.encoder);
        final Pattern p = ESAPI.securityConfiguration().getValidationPattern(type);
        if (p != null) {
            rvr.addWhitelistPattern(p);
            rvr.setMaximumLength(maxLength);
            rvr.setAllowNull(allowNull);
            rvr.setValidateInputAndCanonical(canonicalize);
            return rvr.getValid(context, input);
        }
        throw new IllegalArgumentException("The selected type [" + type + "] was not set via the ESAPI validation configuration");
    }
    
    @Override
    public String getValidInput(final String context, final String input, final String type, final int maxLength, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        return this.getValidInput(context, input, type, maxLength, allowNull, true, errors);
    }
    
    @Override
    public String getValidInput(final String context, final String input, final String type, final int maxLength, final boolean allowNull, final boolean canonicalize, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidInput(context, input, type, maxLength, allowNull, canonicalize);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return "";
        }
    }
    
    @Override
    public boolean isValidDate(final String context, final String input, final DateFormat format, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidDate(context, input, format, allowNull);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidDate(final String context, final String input, final DateFormat format, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidDate(context, input, format, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public Date getValidDate(final String context, final String input, final DateFormat format, final boolean allowNull) throws ValidationException, IntrusionException {
        final DateValidationRule dvr = new DateValidationRule("SimpleDate", this.encoder, format);
        dvr.setAllowNull(allowNull);
        return dvr.getValid(context, input);
    }
    
    @Override
    public Date getValidDate(final String context, final String input, final DateFormat format, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidDate(context, input, format, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return null;
        }
    }
    
    @Override
    public boolean isValidSafeHTML(final String context, final String input, final int maxLength, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidSafeHTML(context, input, maxLength, allowNull);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidSafeHTML(final String context, final String input, final int maxLength, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidSafeHTML(context, input, maxLength, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public String getValidSafeHTML(final String context, final String input, final int maxLength, final boolean allowNull) throws ValidationException, IntrusionException {
        final HTMLValidationRule hvr = new HTMLValidationRule("safehtml", this.encoder);
        hvr.setMaximumLength(maxLength);
        hvr.setAllowNull(allowNull);
        hvr.setValidateInputAndCanonical(false);
        return hvr.getValid(context, input);
    }
    
    @Override
    public String getValidSafeHTML(final String context, final String input, final int maxLength, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidSafeHTML(context, input, maxLength, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return "";
        }
    }
    
    @Override
    public boolean isValidCreditCard(final String context, final String input, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidCreditCard(context, input, allowNull);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidCreditCard(final String context, final String input, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidCreditCard(context, input, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public String getValidCreditCard(final String context, final String input, final boolean allowNull) throws ValidationException, IntrusionException {
        final CreditCardValidationRule ccvr = new CreditCardValidationRule("creditcard", this.encoder);
        ccvr.setAllowNull(allowNull);
        return ccvr.getValid(context, input);
    }
    
    @Override
    public String getValidCreditCard(final String context, final String input, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidCreditCard(context, input, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return "";
        }
    }
    
    @Override
    public boolean isValidDirectoryPath(final String context, final String input, final File parent, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidDirectoryPath(context, input, parent, allowNull);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidDirectoryPath(final String context, final String input, final File parent, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidDirectoryPath(context, input, parent, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public String getValidDirectoryPath(final String context, final String input, final File parent, final boolean allowNull) throws ValidationException, IntrusionException {
        try {
            if (this.isEmpty(input)) {
                if (allowNull) {
                    return null;
                }
                throw new ValidationException(context + ": Input directory path required", "Input directory path required: context=" + context + ", input=" + input, context);
            }
            else {
                final File dir = new File(input);
                if (!dir.exists()) {
                    throw new ValidationException(context + ": Invalid directory name", "Invalid directory, does not exist: context=" + context + ", input=" + input);
                }
                if (!dir.isDirectory()) {
                    throw new ValidationException(context + ": Invalid directory name", "Invalid directory, not a directory: context=" + context + ", input=" + input);
                }
                if (!parent.exists()) {
                    throw new ValidationException(context + ": Invalid directory name", "Invalid directory, specified parent does not exist: context=" + context + ", input=" + input + ", parent=" + parent);
                }
                if (!parent.isDirectory()) {
                    throw new ValidationException(context + ": Invalid directory name", "Invalid directory, specified parent is not a directory: context=" + context + ", input=" + input + ", parent=" + parent);
                }
                if (!dir.getCanonicalPath().startsWith(parent.getCanonicalPath())) {
                    throw new ValidationException(context + ": Invalid directory name", "Invalid directory, not inside specified parent: context=" + context + ", input=" + input + ", parent=" + parent);
                }
                final String canonicalPath = dir.getCanonicalPath();
                final String canonical = DefaultValidator.fileValidator.getValidInput(context, canonicalPath, "DirectoryName", 255, false);
                if (!canonical.equals(input)) {
                    throw new ValidationException(context + ": Invalid directory name", "Invalid directory name does not match the canonical path: context=" + context + ", input=" + input + ", canonical=" + canonical, context);
                }
                return canonical;
            }
        }
        catch (final Exception e) {
            throw new ValidationException(context + ": Invalid directory name", "Failure to validate directory path: context=" + context + ", input=" + input, e, context);
        }
    }
    
    @Override
    public String getValidDirectoryPath(final String context, final String input, final File parent, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidDirectoryPath(context, input, parent, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return "";
        }
    }
    
    @Override
    public boolean isValidFileName(final String context, final String input, final boolean allowNull) throws IntrusionException {
        return this.isValidFileName(context, input, ESAPI.securityConfiguration().getAllowedFileExtensions(), allowNull);
    }
    
    @Override
    public boolean isValidFileName(final String context, final String input, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        return this.isValidFileName(context, input, ESAPI.securityConfiguration().getAllowedFileExtensions(), allowNull, errors);
    }
    
    @Override
    public boolean isValidFileName(final String context, final String input, final List<String> allowedExtensions, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidFileName(context, input, allowedExtensions, allowNull);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidFileName(final String context, final String input, final List<String> allowedExtensions, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidFileName(context, input, allowedExtensions, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public String getValidFileName(final String context, final String input, final List<String> allowedExtensions, final boolean allowNull) throws ValidationException, IntrusionException {
        if (allowedExtensions == null || allowedExtensions.isEmpty()) {
            throw new ValidationException("Internal Error", "getValidFileName called with an empty or null list of allowed Extensions, therefore no files can be uploaded");
        }
        String canonical = "";
        try {
            if (this.isEmpty(input)) {
                if (allowNull) {
                    return null;
                }
                throw new ValidationException(context + ": Input file name required", "Input required: context=" + context + ", input=" + input, context);
            }
            else {
                canonical = new File(input).getCanonicalFile().getName();
                this.getValidInput(context, input, "FileName", 255, true);
                final File f = new File(canonical);
                final String c = f.getCanonicalPath();
                final String cpath = c.substring(c.lastIndexOf(File.separator) + 1);
                if (!input.equals(cpath)) {
                    throw new ValidationException(context + ": Invalid file name", "Invalid directory name does not match the canonical path: context=" + context + ", input=" + input + ", canonical=" + canonical, context);
                }
            }
        }
        catch (final IOException e) {
            throw new ValidationException(context + ": Invalid file name", "Invalid file name does not exist: context=" + context + ", canonical=" + canonical, e, context);
        }
        for (final String ext : allowedExtensions) {
            if (input.toLowerCase().endsWith(ext.toLowerCase())) {
                return canonical;
            }
        }
        throw new ValidationException(context + ": Invalid file name does not have valid extension ( " + allowedExtensions + ")", "Invalid file name does not have valid extension ( " + allowedExtensions + "): context=" + context + ", input=" + input, context);
    }
    
    @Override
    public String getValidFileName(final String context, final String input, final List<String> allowedParameters, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidFileName(context, input, allowedParameters, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return "";
        }
    }
    
    @Override
    public boolean isValidNumber(final String context, final String input, final long minValue, final long maxValue, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidNumber(context, input, minValue, maxValue, allowNull);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidNumber(final String context, final String input, final long minValue, final long maxValue, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidNumber(context, input, minValue, maxValue, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public Double getValidNumber(final String context, final String input, final long minValue, final long maxValue, final boolean allowNull) throws ValidationException, IntrusionException {
        final Double minDoubleValue = new Double((double)minValue);
        final Double maxDoubleValue = new Double((double)maxValue);
        return this.getValidDouble(context, input, minDoubleValue, maxDoubleValue, allowNull);
    }
    
    @Override
    public Double getValidNumber(final String context, final String input, final long minValue, final long maxValue, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidNumber(context, input, minValue, maxValue, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return null;
        }
    }
    
    @Override
    public boolean isValidDouble(final String context, final String input, final double minValue, final double maxValue, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidDouble(context, input, minValue, maxValue, allowNull);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidDouble(final String context, final String input, final double minValue, final double maxValue, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidDouble(context, input, minValue, maxValue, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public Double getValidDouble(final String context, final String input, final double minValue, final double maxValue, final boolean allowNull) throws ValidationException, IntrusionException {
        final NumberValidationRule nvr = new NumberValidationRule("number", this.encoder, minValue, maxValue);
        nvr.setAllowNull(allowNull);
        return nvr.getValid(context, input);
    }
    
    @Override
    public Double getValidDouble(final String context, final String input, final double minValue, final double maxValue, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidDouble(context, input, minValue, maxValue, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return new Double(Double.NaN);
        }
    }
    
    @Override
    public boolean isValidInteger(final String context, final String input, final int minValue, final int maxValue, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidInteger(context, input, minValue, maxValue, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidInteger(final String context, final String input, final int minValue, final int maxValue, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidInteger(context, input, minValue, maxValue, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public Integer getValidInteger(final String context, final String input, final int minValue, final int maxValue, final boolean allowNull) throws ValidationException, IntrusionException {
        final IntegerValidationRule ivr = new IntegerValidationRule("number", this.encoder, minValue, maxValue);
        ivr.setAllowNull(allowNull);
        return ivr.getValid(context, input);
    }
    
    @Override
    public Integer getValidInteger(final String context, final String input, final int minValue, final int maxValue, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidInteger(context, input, minValue, maxValue, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return null;
        }
    }
    
    @Override
    public boolean isValidFileContent(final String context, final byte[] input, final int maxBytes, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidFileContent(context, input, maxBytes, allowNull);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidFileContent(final String context, final byte[] input, final int maxBytes, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidFileContent(context, input, maxBytes, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public byte[] getValidFileContent(final String context, final byte[] input, final int maxBytes, final boolean allowNull) throws ValidationException, IntrusionException {
        if (this.isEmpty(input)) {
            if (allowNull) {
                return null;
            }
            throw new ValidationException(context + ": Input required", "Input required: context=" + context + ", input=" + input, context);
        }
        else {
            final long esapiMaxBytes = ESAPI.securityConfiguration().getAllowedFileUploadSize();
            if (input.length > esapiMaxBytes) {
                throw new ValidationException(context + ": Invalid file content can not exceed " + esapiMaxBytes + " bytes", "Exceeded ESAPI max length", context);
            }
            if (input.length > maxBytes) {
                throw new ValidationException(context + ": Invalid file content can not exceed " + maxBytes + " bytes", "Exceeded maxBytes ( " + input.length + ")", context);
            }
            return input;
        }
    }
    
    @Override
    public byte[] getValidFileContent(final String context, final byte[] input, final int maxBytes, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidFileContent(context, input, maxBytes, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return new byte[0];
        }
    }
    
    @Override
    public boolean isValidFileUpload(final String context, final String directorypath, final String filename, final File parent, final byte[] content, final int maxBytes, final boolean allowNull) throws IntrusionException {
        return this.isValidFileName(context, filename, allowNull) && this.isValidDirectoryPath(context, directorypath, parent, allowNull) && this.isValidFileContent(context, content, maxBytes, allowNull);
    }
    
    @Override
    public boolean isValidFileUpload(final String context, final String directorypath, final String filename, final File parent, final byte[] content, final int maxBytes, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        return this.isValidFileName(context, filename, allowNull, errors) && this.isValidDirectoryPath(context, directorypath, parent, allowNull, errors) && this.isValidFileContent(context, content, maxBytes, allowNull, errors);
    }
    
    @Override
    public void assertValidFileUpload(final String context, final String directorypath, final String filename, final File parent, final byte[] content, final int maxBytes, final List<String> allowedExtensions, final boolean allowNull) throws ValidationException, IntrusionException {
        this.getValidFileName(context, filename, allowedExtensions, allowNull);
        this.getValidDirectoryPath(context, directorypath, parent, allowNull);
        this.getValidFileContent(context, content, maxBytes, allowNull);
    }
    
    @Override
    public void assertValidFileUpload(final String context, final String filepath, final String filename, final File parent, final byte[] content, final int maxBytes, final List<String> allowedExtensions, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.assertValidFileUpload(context, filepath, filename, parent, content, maxBytes, allowedExtensions, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
        }
    }
    
    @Override
    public boolean isValidListItem(final String context, final String input, final List<String> list) {
        try {
            this.getValidListItem(context, input, list);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidListItem(final String context, final String input, final List<String> list, final ValidationErrorList errors) {
        try {
            this.getValidListItem(context, input, list);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public String getValidListItem(final String context, final String input, final List<String> list) throws ValidationException, IntrusionException {
        if (list.contains(input)) {
            return input;
        }
        throw new ValidationException(context + ": Invalid list item", "Invalid list item: context=" + context + ", input=" + input, context);
    }
    
    @Override
    public String getValidListItem(final String context, final String input, final List<String> list, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidListItem(context, input, list);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return input;
        }
    }
    
    @Override
    public boolean isValidHTTPRequestParameterSet(final String context, final HttpServletRequest request, final Set<String> requiredNames, final Set<String> optionalNames) {
        try {
            this.assertValidHTTPRequestParameterSet(context, request, requiredNames, optionalNames);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidHTTPRequestParameterSet(final String context, final HttpServletRequest request, final Set<String> requiredNames, final Set<String> optionalNames, final ValidationErrorList errors) {
        try {
            this.assertValidHTTPRequestParameterSet(context, request, requiredNames, optionalNames);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public void assertValidHTTPRequestParameterSet(final String context, final HttpServletRequest request, final Set<String> required, final Set<String> optional) throws ValidationException, IntrusionException {
        final Set<String> actualNames = request.getParameterMap().keySet();
        final Set<String> missing = new HashSet<String>(required);
        missing.removeAll(actualNames);
        if (missing.size() > 0) {
            throw new ValidationException(context + ": Invalid HTTP request missing parameters", "Invalid HTTP request missing parameters " + missing + ": context=" + context, context);
        }
        final Set<String> extra = new HashSet<String>(actualNames);
        extra.removeAll(required);
        extra.removeAll(optional);
        if (extra.size() > 0) {
            throw new ValidationException(context + ": Invalid HTTP request extra parameters " + extra, "Invalid HTTP request extra parameters " + extra + ": context=" + context, context);
        }
    }
    
    @Override
    public void assertValidHTTPRequestParameterSet(final String context, final HttpServletRequest request, final Set<String> required, final Set<String> optional, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.assertValidHTTPRequestParameterSet(context, request, required, optional);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
        }
    }
    
    @Override
    public boolean isValidPrintable(final String context, final char[] input, final int maxLength, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidPrintable(context, input, maxLength, allowNull);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidPrintable(final String context, final char[] input, final int maxLength, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidPrintable(context, input, maxLength, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public char[] getValidPrintable(final String context, final char[] input, final int maxLength, final boolean allowNull) throws ValidationException, IntrusionException {
        if (this.isEmpty(input)) {
            if (allowNull) {
                return null;
            }
            throw new ValidationException(context + ": Input bytes required", "Input bytes required: HTTP request is null", context);
        }
        else {
            if (input.length > maxLength) {
                throw new ValidationException(context + ": Input bytes can not exceed " + maxLength + " bytes", "Input exceeds maximum allowed length of " + maxLength + " by " + (input.length - maxLength) + " bytes: context=" + context + ", input=" + new String(input), context);
            }
            for (int i = 0; i < input.length; ++i) {
                if (input[i] <= ' ' || input[i] >= '~') {
                    throw new ValidationException(context + ": Invalid input bytes: context=" + context, "Invalid non-ASCII input bytes, context=" + context + ", input=" + new String(input), context);
                }
            }
            return input;
        }
    }
    
    @Override
    public char[] getValidPrintable(final String context, final char[] input, final int maxLength, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidPrintable(context, input, maxLength, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return input;
        }
    }
    
    @Override
    public boolean isValidPrintable(final String context, final String input, final int maxLength, final boolean allowNull) throws IntrusionException {
        try {
            this.getValidPrintable(context, input, maxLength, allowNull);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidPrintable(final String context, final String input, final int maxLength, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            this.getValidPrintable(context, input, maxLength, allowNull);
            return true;
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return false;
        }
    }
    
    @Override
    public String getValidPrintable(final String context, final String input, final int maxLength, final boolean allowNull) throws ValidationException, IntrusionException {
        try {
            final String canonical = this.encoder.canonicalize(input);
            return new String(this.getValidPrintable(context, canonical.toCharArray(), maxLength, allowNull));
        }
        catch (final Exception e) {
            throw new ValidationException(context + ": Invalid printable input", "Invalid encoding of printable input, context=" + context + ", input=" + input, e, context);
        }
    }
    
    @Override
    public String getValidPrintable(final String context, final String input, final int maxLength, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidPrintable(context, input, maxLength, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return input;
        }
    }
    
    @Override
    public boolean isValidRedirectLocation(final String context, final String input, final boolean allowNull) throws IntrusionException {
        return ESAPI.validator().isValidInput(context, input, "Redirect", 512, allowNull);
    }
    
    @Override
    public boolean isValidRedirectLocation(final String context, final String input, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        return ESAPI.validator().isValidInput(context, input, "Redirect", 512, allowNull, errors);
    }
    
    @Override
    public String getValidRedirectLocation(final String context, final String input, final boolean allowNull) throws ValidationException, IntrusionException {
        return ESAPI.validator().getValidInput(context, input, "Redirect", 512, allowNull);
    }
    
    @Override
    public String getValidRedirectLocation(final String context, final String input, final boolean allowNull, final ValidationErrorList errors) throws IntrusionException {
        try {
            return this.getValidRedirectLocation(context, input, allowNull);
        }
        catch (final ValidationException e) {
            errors.addError(context, e);
            return input;
        }
    }
    
    @Override
    public String safeReadLine(final InputStream in, final int max) throws ValidationException {
        if (max <= 0) {
            throw new ValidationAvailabilityException("Invalid input", "Invalid readline. Must read a positive number of bytes from the stream");
        }
        final StringBuilder sb = new StringBuilder();
        int count = 0;
        try {
            while (true) {
                final int c = in.read();
                if (c == -1) {
                    if (sb.length() == 0) {
                        return null;
                    }
                    break;
                }
                else {
                    if (c == 10) {
                        break;
                    }
                    if (c == 13) {
                        break;
                    }
                    if (++count > max) {
                        throw new ValidationAvailabilityException("Invalid input", "Invalid readLine. Read more than maximum characters allowed (" + max + ")");
                    }
                    sb.append((char)c);
                }
            }
            return sb.toString();
        }
        catch (final IOException e) {
            throw new ValidationAvailabilityException("Invalid input", "Invalid readLine. Problem reading from input stream", e);
        }
    }
    
    private final boolean isEmpty(final String input) {
        return input == null || input.trim().length() == 0;
    }
    
    private final boolean isEmpty(final byte[] input) {
        return input == null || input.length == 0;
    }
    
    private final boolean isEmpty(final char[] input) {
        return input == null || input.length == 0;
    }
    
    static {
        DefaultValidator.instance = null;
        DefaultValidator.fileValidator = null;
        final List<String> list = new ArrayList<String>();
        list.add("HTMLEntityCodec");
        list.add("PercentCodec");
        final Encoder fileEncoder = new DefaultEncoder(list);
        DefaultValidator.fileValidator = new DefaultValidator(fileEncoder);
    }
}
