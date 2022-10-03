package org.owasp.esapi;

import java.io.InputStream;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.io.File;
import java.util.Date;
import java.text.DateFormat;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.errors.IntrusionException;

public interface Validator
{
    void addRule(final ValidationRule p0);
    
    ValidationRule getRule(final String p0);
    
    boolean isValidInput(final String p0, final String p1, final String p2, final int p3, final boolean p4) throws IntrusionException;
    
    boolean isValidInput(final String p0, final String p1, final String p2, final int p3, final boolean p4, final ValidationErrorList p5) throws IntrusionException;
    
    boolean isValidInput(final String p0, final String p1, final String p2, final int p3, final boolean p4, final boolean p5) throws IntrusionException;
    
    boolean isValidInput(final String p0, final String p1, final String p2, final int p3, final boolean p4, final boolean p5, final ValidationErrorList p6) throws IntrusionException;
    
    String getValidInput(final String p0, final String p1, final String p2, final int p3, final boolean p4) throws ValidationException, IntrusionException;
    
    String getValidInput(final String p0, final String p1, final String p2, final int p3, final boolean p4, final boolean p5) throws ValidationException, IntrusionException;
    
    String getValidInput(final String p0, final String p1, final String p2, final int p3, final boolean p4, final ValidationErrorList p5) throws IntrusionException;
    
    String getValidInput(final String p0, final String p1, final String p2, final int p3, final boolean p4, final boolean p5, final ValidationErrorList p6) throws IntrusionException;
    
    boolean isValidDate(final String p0, final String p1, final DateFormat p2, final boolean p3) throws IntrusionException;
    
    boolean isValidDate(final String p0, final String p1, final DateFormat p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    Date getValidDate(final String p0, final String p1, final DateFormat p2, final boolean p3) throws ValidationException, IntrusionException;
    
    Date getValidDate(final String p0, final String p1, final DateFormat p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    boolean isValidSafeHTML(final String p0, final String p1, final int p2, final boolean p3) throws IntrusionException;
    
    boolean isValidSafeHTML(final String p0, final String p1, final int p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    String getValidSafeHTML(final String p0, final String p1, final int p2, final boolean p3) throws ValidationException, IntrusionException;
    
    String getValidSafeHTML(final String p0, final String p1, final int p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    boolean isValidCreditCard(final String p0, final String p1, final boolean p2) throws IntrusionException;
    
    boolean isValidCreditCard(final String p0, final String p1, final boolean p2, final ValidationErrorList p3) throws IntrusionException;
    
    String getValidCreditCard(final String p0, final String p1, final boolean p2) throws ValidationException, IntrusionException;
    
    String getValidCreditCard(final String p0, final String p1, final boolean p2, final ValidationErrorList p3) throws IntrusionException;
    
    boolean isValidDirectoryPath(final String p0, final String p1, final File p2, final boolean p3) throws IntrusionException;
    
    boolean isValidDirectoryPath(final String p0, final String p1, final File p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    String getValidDirectoryPath(final String p0, final String p1, final File p2, final boolean p3) throws ValidationException, IntrusionException;
    
    String getValidDirectoryPath(final String p0, final String p1, final File p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    boolean isValidFileName(final String p0, final String p1, final boolean p2) throws IntrusionException;
    
    boolean isValidFileName(final String p0, final String p1, final boolean p2, final ValidationErrorList p3) throws IntrusionException;
    
    boolean isValidFileName(final String p0, final String p1, final List<String> p2, final boolean p3) throws IntrusionException;
    
    boolean isValidFileName(final String p0, final String p1, final List<String> p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    String getValidFileName(final String p0, final String p1, final List<String> p2, final boolean p3) throws ValidationException, IntrusionException;
    
    String getValidFileName(final String p0, final String p1, final List<String> p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    boolean isValidNumber(final String p0, final String p1, final long p2, final long p3, final boolean p4) throws IntrusionException;
    
    boolean isValidNumber(final String p0, final String p1, final long p2, final long p3, final boolean p4, final ValidationErrorList p5) throws IntrusionException;
    
    Double getValidNumber(final String p0, final String p1, final long p2, final long p3, final boolean p4) throws ValidationException, IntrusionException;
    
    Double getValidNumber(final String p0, final String p1, final long p2, final long p3, final boolean p4, final ValidationErrorList p5) throws IntrusionException;
    
    boolean isValidInteger(final String p0, final String p1, final int p2, final int p3, final boolean p4) throws IntrusionException;
    
    boolean isValidInteger(final String p0, final String p1, final int p2, final int p3, final boolean p4, final ValidationErrorList p5) throws IntrusionException;
    
    Integer getValidInteger(final String p0, final String p1, final int p2, final int p3, final boolean p4) throws ValidationException, IntrusionException;
    
    Integer getValidInteger(final String p0, final String p1, final int p2, final int p3, final boolean p4, final ValidationErrorList p5) throws IntrusionException;
    
    boolean isValidDouble(final String p0, final String p1, final double p2, final double p3, final boolean p4) throws IntrusionException;
    
    boolean isValidDouble(final String p0, final String p1, final double p2, final double p3, final boolean p4, final ValidationErrorList p5) throws IntrusionException;
    
    Double getValidDouble(final String p0, final String p1, final double p2, final double p3, final boolean p4) throws ValidationException, IntrusionException;
    
    Double getValidDouble(final String p0, final String p1, final double p2, final double p3, final boolean p4, final ValidationErrorList p5) throws IntrusionException;
    
    boolean isValidFileContent(final String p0, final byte[] p1, final int p2, final boolean p3) throws IntrusionException;
    
    boolean isValidFileContent(final String p0, final byte[] p1, final int p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    byte[] getValidFileContent(final String p0, final byte[] p1, final int p2, final boolean p3) throws ValidationException, IntrusionException;
    
    byte[] getValidFileContent(final String p0, final byte[] p1, final int p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    boolean isValidFileUpload(final String p0, final String p1, final String p2, final File p3, final byte[] p4, final int p5, final boolean p6) throws IntrusionException;
    
    boolean isValidFileUpload(final String p0, final String p1, final String p2, final File p3, final byte[] p4, final int p5, final boolean p6, final ValidationErrorList p7) throws IntrusionException;
    
    void assertValidFileUpload(final String p0, final String p1, final String p2, final File p3, final byte[] p4, final int p5, final List<String> p6, final boolean p7) throws ValidationException, IntrusionException;
    
    void assertValidFileUpload(final String p0, final String p1, final String p2, final File p3, final byte[] p4, final int p5, final List<String> p6, final boolean p7, final ValidationErrorList p8) throws IntrusionException;
    
    boolean isValidListItem(final String p0, final String p1, final List<String> p2) throws IntrusionException;
    
    boolean isValidListItem(final String p0, final String p1, final List<String> p2, final ValidationErrorList p3) throws IntrusionException;
    
    String getValidListItem(final String p0, final String p1, final List<String> p2) throws ValidationException, IntrusionException;
    
    String getValidListItem(final String p0, final String p1, final List<String> p2, final ValidationErrorList p3) throws IntrusionException;
    
    boolean isValidHTTPRequestParameterSet(final String p0, final HttpServletRequest p1, final Set<String> p2, final Set<String> p3) throws IntrusionException;
    
    boolean isValidHTTPRequestParameterSet(final String p0, final HttpServletRequest p1, final Set<String> p2, final Set<String> p3, final ValidationErrorList p4) throws IntrusionException;
    
    void assertValidHTTPRequestParameterSet(final String p0, final HttpServletRequest p1, final Set<String> p2, final Set<String> p3) throws ValidationException, IntrusionException;
    
    void assertValidHTTPRequestParameterSet(final String p0, final HttpServletRequest p1, final Set<String> p2, final Set<String> p3, final ValidationErrorList p4) throws IntrusionException;
    
    boolean isValidPrintable(final String p0, final char[] p1, final int p2, final boolean p3) throws IntrusionException;
    
    boolean isValidPrintable(final String p0, final char[] p1, final int p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    char[] getValidPrintable(final String p0, final char[] p1, final int p2, final boolean p3) throws ValidationException;
    
    char[] getValidPrintable(final String p0, final char[] p1, final int p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    boolean isValidPrintable(final String p0, final String p1, final int p2, final boolean p3) throws IntrusionException;
    
    boolean isValidPrintable(final String p0, final String p1, final int p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    String getValidPrintable(final String p0, final String p1, final int p2, final boolean p3) throws ValidationException;
    
    String getValidPrintable(final String p0, final String p1, final int p2, final boolean p3, final ValidationErrorList p4) throws IntrusionException;
    
    boolean isValidRedirectLocation(final String p0, final String p1, final boolean p2);
    
    boolean isValidRedirectLocation(final String p0, final String p1, final boolean p2, final ValidationErrorList p3);
    
    String getValidRedirectLocation(final String p0, final String p1, final boolean p2) throws ValidationException, IntrusionException;
    
    String getValidRedirectLocation(final String p0, final String p1, final boolean p2, final ValidationErrorList p3) throws IntrusionException;
    
    String safeReadLine(final InputStream p0, final int p1) throws ValidationException;
}
