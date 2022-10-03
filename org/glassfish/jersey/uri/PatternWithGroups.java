package org.glassfish.jersey.uri;

import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;

public class PatternWithGroups
{
    private static final int[] EMPTY_INT_ARRAY;
    public static final PatternWithGroups EMPTY;
    private final String regex;
    private final Pattern regexPattern;
    private final int[] groupIndexes;
    private static final EmptyStringMatchResult EMPTY_STRING_MATCH_RESULT;
    
    protected PatternWithGroups() {
        this.regex = "";
        this.regexPattern = null;
        this.groupIndexes = PatternWithGroups.EMPTY_INT_ARRAY;
    }
    
    public PatternWithGroups(final String regex) throws PatternSyntaxException {
        this(regex, PatternWithGroups.EMPTY_INT_ARRAY);
    }
    
    public PatternWithGroups(final String regex, final int[] groupIndexes) throws PatternSyntaxException {
        this(compile(regex), groupIndexes);
    }
    
    private static Pattern compile(final String regex) throws PatternSyntaxException {
        return (regex == null || regex.isEmpty()) ? null : Pattern.compile(regex);
    }
    
    public PatternWithGroups(final Pattern regexPattern) throws IllegalArgumentException {
        this(regexPattern, PatternWithGroups.EMPTY_INT_ARRAY);
    }
    
    public PatternWithGroups(final Pattern regexPattern, final int[] groupIndexes) throws IllegalArgumentException {
        if (regexPattern == null) {
            throw new IllegalArgumentException();
        }
        this.regex = regexPattern.toString();
        this.regexPattern = regexPattern;
        this.groupIndexes = groupIndexes.clone();
    }
    
    public final String getRegex() {
        return this.regex;
    }
    
    public final int[] getGroupIndexes() {
        return this.groupIndexes.clone();
    }
    
    public final MatchResult match(final CharSequence cs) {
        if (cs == null) {
            return (this.regexPattern == null) ? PatternWithGroups.EMPTY_STRING_MATCH_RESULT : null;
        }
        if (this.regexPattern == null) {
            return null;
        }
        final Matcher m = this.regexPattern.matcher(cs);
        if (!m.matches()) {
            return null;
        }
        if (cs.length() == 0) {
            return PatternWithGroups.EMPTY_STRING_MATCH_RESULT;
        }
        return (this.groupIndexes.length > 0) ? new GroupIndexMatchResult(m) : m;
    }
    
    public final boolean match(final CharSequence cs, final List<String> groupValues) throws IllegalArgumentException {
        if (groupValues == null) {
            throw new IllegalArgumentException();
        }
        if (cs == null || cs.length() == 0) {
            return this.regexPattern == null;
        }
        if (this.regexPattern == null) {
            return false;
        }
        final Matcher m = this.regexPattern.matcher(cs);
        if (!m.matches()) {
            return false;
        }
        groupValues.clear();
        if (this.groupIndexes.length > 0) {
            for (int i = 0; i < this.groupIndexes.length; ++i) {
                groupValues.add(m.group(this.groupIndexes[i]));
            }
        }
        else {
            for (int i = 1; i <= m.groupCount(); ++i) {
                groupValues.add(m.group(i));
            }
        }
        return true;
    }
    
    public final boolean match(final CharSequence cs, final List<String> groupNames, final Map<String, String> groupValues) throws IllegalArgumentException {
        if (groupValues == null) {
            throw new IllegalArgumentException();
        }
        if (cs == null || cs.length() == 0) {
            return this.regexPattern == null;
        }
        if (this.regexPattern == null) {
            return false;
        }
        final Matcher m = this.regexPattern.matcher(cs);
        if (!m.matches()) {
            return false;
        }
        groupValues.clear();
        for (int i = 0; i < groupNames.size(); ++i) {
            final String name = groupNames.get(i);
            final String currentValue = m.group((this.groupIndexes.length > 0) ? this.groupIndexes[i] : (i + 1));
            final String previousValue = groupValues.get(name);
            if (previousValue != null && !previousValue.equals(currentValue)) {
                return false;
            }
            groupValues.put(name, currentValue);
        }
        return true;
    }
    
    @Override
    public final int hashCode() {
        return this.regex.hashCode();
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final PatternWithGroups that = (PatternWithGroups)obj;
        return this.regex == that.regex || (this.regex != null && this.regex.equals(that.regex));
    }
    
    @Override
    public final String toString() {
        return this.regex;
    }
    
    static {
        EMPTY_INT_ARRAY = new int[0];
        EMPTY = new PatternWithGroups();
        EMPTY_STRING_MATCH_RESULT = new EmptyStringMatchResult();
    }
    
    private static final class EmptyStringMatchResult implements MatchResult
    {
        @Override
        public int start() {
            return 0;
        }
        
        @Override
        public int start(final int group) {
            if (group != 0) {
                throw new IndexOutOfBoundsException();
            }
            return this.start();
        }
        
        @Override
        public int end() {
            return 0;
        }
        
        @Override
        public int end(final int group) {
            if (group != 0) {
                throw new IndexOutOfBoundsException();
            }
            return this.end();
        }
        
        @Override
        public String group() {
            return "";
        }
        
        @Override
        public String group(final int group) {
            if (group != 0) {
                throw new IndexOutOfBoundsException();
            }
            return this.group();
        }
        
        @Override
        public int groupCount() {
            return 0;
        }
    }
    
    private final class GroupIndexMatchResult implements MatchResult
    {
        private final MatchResult result;
        
        GroupIndexMatchResult(final MatchResult r) {
            this.result = r;
        }
        
        @Override
        public int start() {
            return this.result.start();
        }
        
        @Override
        public int start(final int group) {
            if (group > this.groupCount()) {
                throw new IndexOutOfBoundsException();
            }
            return (group > 0) ? this.result.start(PatternWithGroups.this.groupIndexes[group - 1]) : this.result.start();
        }
        
        @Override
        public int end() {
            return this.result.end();
        }
        
        @Override
        public int end(final int group) {
            if (group > this.groupCount()) {
                throw new IndexOutOfBoundsException();
            }
            return (group > 0) ? this.result.end(PatternWithGroups.this.groupIndexes[group - 1]) : this.result.end();
        }
        
        @Override
        public String group() {
            return this.result.group();
        }
        
        @Override
        public String group(final int group) {
            if (group > this.groupCount()) {
                throw new IndexOutOfBoundsException();
            }
            return (group > 0) ? this.result.group(PatternWithGroups.this.groupIndexes[group - 1]) : this.result.group();
        }
        
        @Override
        public int groupCount() {
            return PatternWithGroups.this.groupIndexes.length;
        }
    }
}
