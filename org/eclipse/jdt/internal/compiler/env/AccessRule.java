package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.core.compiler.CharOperation;

public class AccessRule
{
    public static final int IgnoreIfBetter = 33554432;
    public char[] pattern;
    public int problemId;
    
    public AccessRule(final char[] pattern, final int problemId) {
        this(pattern, problemId, false);
    }
    
    public AccessRule(final char[] pattern, final int problemId, final boolean keepLooking) {
        this.pattern = pattern;
        this.problemId = (keepLooking ? (problemId | 0x2000000) : problemId);
    }
    
    @Override
    public int hashCode() {
        return this.problemId * 17 + CharOperation.hashCode(this.pattern);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof AccessRule)) {
            return false;
        }
        final AccessRule other = (AccessRule)obj;
        return this.problemId == other.problemId && CharOperation.equals(this.pattern, other.pattern);
    }
    
    public int getProblemId() {
        return this.problemId & 0xFDFFFFFF;
    }
    
    public boolean ignoreIfBetter() {
        return (this.problemId & 0x2000000) != 0x0;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("pattern=");
        buffer.append(this.pattern);
        switch (this.getProblemId()) {
            case 16777523: {
                buffer.append(" (NON ACCESSIBLE");
                break;
            }
            case 16777496: {
                buffer.append(" (DISCOURAGED");
                break;
            }
            default: {
                buffer.append(" (ACCESSIBLE");
                break;
            }
        }
        if (this.ignoreIfBetter()) {
            buffer.append(" | IGNORE IF BETTER");
        }
        buffer.append(')');
        return buffer.toString();
    }
}
