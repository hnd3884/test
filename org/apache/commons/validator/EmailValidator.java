package org.apache.commons.validator;

import org.apache.oro.text.perl.Perl5Util;

public class EmailValidator
{
    private static final String SPECIAL_CHARS = "\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177";
    private static final String VALID_CHARS = "[^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]";
    private static final String QUOTED_USER = "(\"[^\"]*\")";
    private static final String ATOM = "[^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]+";
    private static final String WORD = "(([^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]|')+|(\"[^\"]*\"))";
    private static final String LEGAL_ASCII_PATTERN = "/^[\\000-\\177]+$/";
    private static final String EMAIL_PATTERN = "/^(.+)@(.+)$/";
    private static final String IP_DOMAIN_PATTERN = "/^\\[(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})\\]$/";
    private static final String TLD_PATTERN = "/^([a-zA-Z]+)$/";
    private static final String USER_PATTERN = "/^\\s*(([^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]|')+|(\"[^\"]*\"))(\\.(([^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]|')+|(\"[^\"]*\")))*$/";
    private static final String DOMAIN_PATTERN = "/^[^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]+(\\.[^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]+)*\\s*$/";
    private static final String ATOM_PATTERN = "/([^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]+)/";
    private static final EmailValidator EMAIL_VALIDATOR;
    
    public static EmailValidator getInstance() {
        return EmailValidator.EMAIL_VALIDATOR;
    }
    
    protected EmailValidator() {
    }
    
    public boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        final Perl5Util matchAsciiPat = new Perl5Util();
        if (!matchAsciiPat.match("/^[\\000-\\177]+$/", email)) {
            return false;
        }
        email = this.stripComments(email);
        final Perl5Util emailMatcher = new Perl5Util();
        return emailMatcher.match("/^(.+)@(.+)$/", email) && !email.endsWith(".") && this.isValidUser(emailMatcher.group(1)) && this.isValidDomain(emailMatcher.group(2));
    }
    
    protected boolean isValidDomain(final String domain) {
        boolean symbolic = false;
        final Perl5Util ipAddressMatcher = new Perl5Util();
        if (ipAddressMatcher.match("/^\\[(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})\\]$/", domain)) {
            return this.isValidIpAddress(ipAddressMatcher);
        }
        final Perl5Util domainMatcher = new Perl5Util();
        symbolic = domainMatcher.match("/^[^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]+(\\.[^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]+)*\\s*$/", domain);
        return symbolic && this.isValidSymbolicDomain(domain);
    }
    
    protected boolean isValidUser(final String user) {
        final Perl5Util userMatcher = new Perl5Util();
        return userMatcher.match("/^\\s*(([^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]|')+|(\"[^\"]*\"))(\\.(([^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]|')+|(\"[^\"]*\")))*$/", user);
    }
    
    protected boolean isValidIpAddress(final Perl5Util ipAddressMatcher) {
        for (int i = 1; i <= 4; ++i) {
            final String ipSegment = ipAddressMatcher.group(i);
            if (ipSegment == null || ipSegment.length() <= 0) {
                return false;
            }
            int iIpSegment = 0;
            try {
                iIpSegment = Integer.parseInt(ipSegment);
            }
            catch (final NumberFormatException e) {
                return false;
            }
            if (iIpSegment > 255) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean isValidSymbolicDomain(String domain) {
        final String[] domainSegment = new String[10];
        boolean match = true;
        int i = 0;
        final Perl5Util atomMatcher = new Perl5Util();
        while (match) {
            match = atomMatcher.match("/([^\\s\\000-\\037\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]\\177]+)/", domain);
            if (match) {
                domainSegment[i] = atomMatcher.group(1);
                final int l = domainSegment[i].length() + 1;
                domain = ((l >= domain.length()) ? "" : domain.substring(l));
                ++i;
            }
        }
        final int len = i;
        if (len < 2) {
            return false;
        }
        final String tld = domainSegment[len - 1];
        if (tld.length() > 1) {
            final Perl5Util matchTldPat = new Perl5Util();
            return matchTldPat.match("/^([a-zA-Z]+)$/", tld);
        }
        return false;
    }
    
    protected String stripComments(final String emailStr) {
        String input;
        String result;
        String commentPat;
        Perl5Util commentMatcher;
        for (input = emailStr, result = emailStr, commentPat = "s/^((?:[^\"\\\\]|\\\\.)*(?:\"(?:[^\"\\\\]|\\\\.)*\"(?:[^\"\\\\]|I111\\\\.)*)*)\\((?:[^()\\\\]|\\\\.)*\\)/$1 /osx", commentMatcher = new Perl5Util(), result = commentMatcher.substitute(commentPat, input); !result.equals(input); input = result, result = commentMatcher.substitute(commentPat, input)) {}
        return result;
    }
    
    static {
        EMAIL_VALIDATOR = new EmailValidator();
    }
}
