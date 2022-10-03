package org.eclipse.jdt.internal.compiler.batch;

import org.eclipse.jdt.core.compiler.CharOperation;
import java.io.File;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;

public abstract class ClasspathLocation implements FileSystem.Classpath, SuffixConstants
{
    public static final int SOURCE = 1;
    public static final int BINARY = 2;
    String path;
    char[] normalizedPath;
    public AccessRuleSet accessRuleSet;
    public String destinationPath;
    
    protected ClasspathLocation(final AccessRuleSet accessRuleSet, final String destinationPath) {
        this.accessRuleSet = accessRuleSet;
        this.destinationPath = destinationPath;
    }
    
    protected AccessRestriction fetchAccessRestriction(final String qualifiedBinaryFileName) {
        if (this.accessRuleSet == null) {
            return null;
        }
        final char[] qualifiedTypeName = qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - ClasspathLocation.SUFFIX_CLASS.length).toCharArray();
        if (File.separatorChar == '\\') {
            CharOperation.replace(qualifiedTypeName, File.separatorChar, '/');
        }
        return this.accessRuleSet.getViolatedRestriction(qualifiedTypeName);
    }
    
    public int getMode() {
        return 3;
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.getMode();
        result = 31 * result + ((this.path == null) ? 0 : this.path.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ClasspathLocation other = (ClasspathLocation)obj;
        final String localPath = this.getPath();
        final String otherPath = other.getPath();
        if (localPath == null) {
            if (otherPath != null) {
                return false;
            }
        }
        else if (!localPath.equals(otherPath)) {
            return false;
        }
        return this.getMode() == other.getMode();
    }
    
    @Override
    public String getPath() {
        return this.path;
    }
}
