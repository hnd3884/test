package org.eclipse.jdt.core;

import org.eclipse.jdt.core.util.ICodeAttribute;
import org.eclipse.jdt.core.util.IMethodInfo;
import java.util.Enumeration;
import org.eclipse.jdt.core.util.IClassFileReader;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.apache.tools.ant.BuildException;
import org.eclipse.jdt.internal.antadapter.AntAdapterMessages;
import org.apache.tools.ant.Task;

public final class CheckDebugAttributes extends Task
{
    private String file;
    private String property;
    
    public void execute() throws BuildException {
        if (this.file == null) {
            throw new BuildException(AntAdapterMessages.getString("checkDebugAttributes.file.argument.cannot.be.null"));
        }
        if (this.property == null) {
            throw new BuildException(AntAdapterMessages.getString("checkDebugAttributes.property.argument.cannot.be.null"));
        }
        try {
            boolean hasDebugAttributes = false;
            if (Util.isClassFileName(this.file)) {
                final IClassFileReader classFileReader = ToolFactory.createDefaultClassFileReader(this.file, 65535);
                hasDebugAttributes = this.checkClassFile(classFileReader);
            }
            else {
                ZipFile jarFile = null;
                try {
                    jarFile = new ZipFile(this.file);
                }
                catch (final ZipException ex) {
                    throw new BuildException(AntAdapterMessages.getString("checkDebugAttributes.file.argument.must.be.a.classfile.or.a.jarfile"));
                }
                finally {
                    if (jarFile != null) {
                        jarFile.close();
                    }
                }
                if (jarFile != null) {
                    jarFile.close();
                }
                IClassFileReader classFileReader2 = null;
                for (Enumeration entries = jarFile.entries(); !hasDebugAttributes && entries.hasMoreElements(); hasDebugAttributes = this.checkClassFile(classFileReader2)) {
                    final ZipEntry entry = entries.nextElement();
                    if (Util.isClassFileName(entry.getName())) {
                        classFileReader2 = ToolFactory.createDefaultClassFileReader(this.file, entry.getName(), 65535);
                    }
                }
            }
            if (hasDebugAttributes) {
                this.getProject().setUserProperty(this.property, "has debug");
            }
        }
        catch (final IOException ex2) {
            throw new BuildException(String.valueOf(AntAdapterMessages.getString("checkDebugAttributes.ioexception.occured")) + this.file);
        }
    }
    
    private boolean checkClassFile(final IClassFileReader classFileReader) {
        final IMethodInfo[] methodInfos = classFileReader.getMethodInfos();
        for (int i = 0, max = methodInfos.length; i < max; ++i) {
            final ICodeAttribute codeAttribute = methodInfos[i].getCodeAttribute();
            if (codeAttribute != null && codeAttribute.getLineNumberAttribute() != null) {
                return true;
            }
        }
        return false;
    }
    
    public void setFile(final String value) {
        this.file = value;
    }
    
    public void setProperty(final String value) {
        this.property = value;
    }
}
