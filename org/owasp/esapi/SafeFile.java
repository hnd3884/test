package org.owasp.esapi;

import java.util.regex.Matcher;
import java.net.URI;
import org.owasp.esapi.errors.ValidationException;
import java.util.regex.Pattern;
import java.io.File;

public class SafeFile extends File
{
    private static final long serialVersionUID = 1L;
    private static final Pattern PERCENTS_PAT;
    private static final Pattern FILE_BLACKLIST_PAT;
    private static final Pattern DIR_BLACKLIST_PAT;
    
    public SafeFile(final String path) throws ValidationException {
        super(path);
        this.doDirCheck(this.getParent());
        this.doFileCheck(this.getName());
    }
    
    public SafeFile(final String parent, final String child) throws ValidationException {
        super(parent, child);
        this.doDirCheck(this.getParent());
        this.doFileCheck(this.getName());
    }
    
    public SafeFile(final File parent, final String child) throws ValidationException {
        super(parent, child);
        this.doDirCheck(this.getParent());
        this.doFileCheck(this.getName());
    }
    
    public SafeFile(final URI uri) throws ValidationException {
        super(uri);
        this.doDirCheck(this.getParent());
        this.doFileCheck(this.getName());
    }
    
    private void doDirCheck(final String path) throws ValidationException {
        final Matcher m1 = SafeFile.DIR_BLACKLIST_PAT.matcher(path);
        if (m1.find()) {
            throw new ValidationException("Invalid directory", "Directory path (" + path + ") contains illegal character: " + m1.group());
        }
        final Matcher m2 = SafeFile.PERCENTS_PAT.matcher(path);
        if (m2.find()) {
            throw new ValidationException("Invalid directory", "Directory path (" + path + ") contains encoded characters: " + m2.group());
        }
        final int ch = this.containsUnprintableCharacters(path);
        if (ch != -1) {
            throw new ValidationException("Invalid directory", "Directory path (" + path + ") contains unprintable character: " + ch);
        }
    }
    
    private void doFileCheck(final String path) throws ValidationException {
        final Matcher m1 = SafeFile.FILE_BLACKLIST_PAT.matcher(path);
        if (m1.find()) {
            throw new ValidationException("Invalid directory", "Directory path (" + path + ") contains illegal character: " + m1.group());
        }
        final Matcher m2 = SafeFile.PERCENTS_PAT.matcher(path);
        if (m2.find()) {
            throw new ValidationException("Invalid file", "File path (" + path + ") contains encoded characters: " + m2.group());
        }
        final int ch = this.containsUnprintableCharacters(path);
        if (ch != -1) {
            throw new ValidationException("Invalid file", "File path (" + path + ") contains unprintable character: " + ch);
        }
    }
    
    private int containsUnprintableCharacters(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            final char ch = s.charAt(i);
            if (ch < ' ' || ch > '~') {
                return ch;
            }
        }
        return -1;
    }
    
    static {
        PERCENTS_PAT = Pattern.compile("(%)([0-9a-fA-F])([0-9a-fA-F])");
        FILE_BLACKLIST_PAT = Pattern.compile("([\\\\/:*?<>|])");
        DIR_BLACKLIST_PAT = Pattern.compile("([*?<>|])");
    }
}
