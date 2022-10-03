package org.eclipse.jdt.internal.compiler.batch;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.io.IOException;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import java.io.File;

public class ClasspathSourceJar extends ClasspathJar
{
    private String encoding;
    
    public ClasspathSourceJar(final File file, final boolean closeZipFileAtEnd, final AccessRuleSet accessRuleSet, final String encoding, final String destinationPath) {
        super(file, closeZipFileAtEnd, accessRuleSet, destinationPath);
        this.encoding = encoding;
    }
    
    @Override
    public NameEnvironmentAnswer findClass(final char[] typeName, final String qualifiedPackageName, final String qualifiedBinaryFileName, final boolean asBinaryOnly) {
        if (!this.isPackage(qualifiedPackageName)) {
            return null;
        }
        final ZipEntry sourceEntry = this.zipFile.getEntry(String.valueOf(qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6)) + ".java");
        if (sourceEntry != null) {
            try {
                InputStream stream = null;
                char[] contents = null;
                try {
                    stream = this.zipFile.getInputStream(sourceEntry);
                    contents = Util.getInputStreamAsCharArray(stream, -1, this.encoding);
                }
                finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
                if (stream != null) {
                    stream.close();
                }
                return new NameEnvironmentAnswer(new CompilationUnit(contents, String.valueOf(qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6)) + ".java", this.encoding, this.destinationPath), this.fetchAccessRestriction(qualifiedBinaryFileName));
            }
            catch (final IOException ex) {}
        }
        return null;
    }
    
    @Override
    public NameEnvironmentAnswer findClass(final char[] typeName, final String qualifiedPackageName, final String qualifiedBinaryFileName) {
        return this.findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, false);
    }
    
    @Override
    public int getMode() {
        return 1;
    }
}
