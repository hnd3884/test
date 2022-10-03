package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.core.compiler.CharOperation;

public class AccessRuleSet
{
    private AccessRule[] accessRules;
    public byte classpathEntryType;
    public String classpathEntryName;
    
    public AccessRuleSet(final AccessRule[] accessRules, final byte classpathEntryType, final String classpathEntryName) {
        this.accessRules = accessRules;
        this.classpathEntryType = classpathEntryType;
        this.classpathEntryName = classpathEntryName;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AccessRuleSet)) {
            return false;
        }
        final AccessRuleSet otherRuleSet = (AccessRuleSet)object;
        if (this.classpathEntryType != otherRuleSet.classpathEntryType || (this.classpathEntryName == null && otherRuleSet.classpathEntryName != null) || !this.classpathEntryName.equals(otherRuleSet.classpathEntryName)) {
            return false;
        }
        final int rulesLength = this.accessRules.length;
        if (rulesLength != otherRuleSet.accessRules.length) {
            return false;
        }
        for (int i = 0; i < rulesLength; ++i) {
            if (!this.accessRules[i].equals(otherRuleSet.accessRules[i])) {
                return false;
            }
        }
        return true;
    }
    
    public AccessRule[] getAccessRules() {
        return this.accessRules;
    }
    
    public AccessRestriction getViolatedRestriction(final char[] targetTypeFilePath) {
        int i = 0;
        final int length = this.accessRules.length;
        while (i < length) {
            final AccessRule accessRule = this.accessRules[i];
            if (CharOperation.pathMatch(accessRule.pattern, targetTypeFilePath, true, '/')) {
                switch (accessRule.getProblemId()) {
                    case 16777496:
                    case 16777523: {
                        return new AccessRestriction(accessRule, this.classpathEntryType, this.classpathEntryName);
                    }
                    default: {
                        return null;
                    }
                }
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.hashCode(this.accessRules);
        result = 31 * result + ((this.classpathEntryName == null) ? 0 : this.classpathEntryName.hashCode());
        result = 31 * result + this.classpathEntryType;
        return result;
    }
    
    private int hashCode(final AccessRule[] rules) {
        if (rules == null) {
            return 0;
        }
        int result = 1;
        for (int i = 0, length = rules.length; i < length; ++i) {
            result = 31 * result + ((rules[i] == null) ? 0 : rules[i].hashCode());
        }
        return result;
    }
    
    @Override
    public String toString() {
        return this.toString(true);
    }
    
    public String toString(final boolean wrap) {
        final StringBuffer buffer = new StringBuffer(200);
        buffer.append("AccessRuleSet {");
        if (wrap) {
            buffer.append('\n');
        }
        for (int i = 0, length = this.accessRules.length; i < length; ++i) {
            if (wrap) {
                buffer.append('\t');
            }
            final AccessRule accessRule = this.accessRules[i];
            buffer.append(accessRule);
            if (wrap) {
                buffer.append('\n');
            }
            else if (i < length - 1) {
                buffer.append(", ");
            }
        }
        buffer.append("} [classpath entry: ");
        buffer.append(this.classpathEntryName);
        buffer.append("]");
        return buffer.toString();
    }
}
