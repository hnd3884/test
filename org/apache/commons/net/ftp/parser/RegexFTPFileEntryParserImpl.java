package org.apache.commons.net.ftp.parser;

import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.apache.commons.net.ftp.FTPFileEntryParserImpl;

public abstract class RegexFTPFileEntryParserImpl extends FTPFileEntryParserImpl
{
    private Pattern pattern;
    private MatchResult result;
    protected Matcher _matcher_;
    
    public RegexFTPFileEntryParserImpl(final String regex) {
        this.pattern = null;
        this.result = null;
        this._matcher_ = null;
        this.compileRegex(regex, 0);
    }
    
    public RegexFTPFileEntryParserImpl(final String regex, final int flags) {
        this.pattern = null;
        this.result = null;
        this._matcher_ = null;
        this.compileRegex(regex, flags);
    }
    
    public boolean matches(final String s) {
        this.result = null;
        this._matcher_ = this.pattern.matcher(s);
        if (this._matcher_.matches()) {
            this.result = this._matcher_.toMatchResult();
        }
        return null != this.result;
    }
    
    public int getGroupCnt() {
        if (this.result == null) {
            return 0;
        }
        return this.result.groupCount();
    }
    
    public String group(final int matchnum) {
        if (this.result == null) {
            return null;
        }
        return this.result.group(matchnum);
    }
    
    public String getGroupsAsString() {
        final StringBuilder b = new StringBuilder();
        for (int i = 1; i <= this.result.groupCount(); ++i) {
            b.append(i).append(") ").append(this.result.group(i)).append(System.getProperty("line.separator"));
        }
        return b.toString();
    }
    
    public boolean setRegex(final String regex) {
        this.compileRegex(regex, 0);
        return true;
    }
    
    public boolean setRegex(final String regex, final int flags) {
        this.compileRegex(regex, flags);
        return true;
    }
    
    private void compileRegex(final String regex, final int flags) {
        try {
            this.pattern = Pattern.compile(regex, flags);
        }
        catch (final PatternSyntaxException pse) {
            throw new IllegalArgumentException("Unparseable regex supplied: " + regex);
        }
    }
}
