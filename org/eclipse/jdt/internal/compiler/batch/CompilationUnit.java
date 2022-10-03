package org.eclipse.jdt.internal.compiler.batch;

import java.io.IOException;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.io.File;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

public class CompilationUnit implements ICompilationUnit
{
    public char[] contents;
    public char[] fileName;
    public char[] mainTypeName;
    String encoding;
    public String destinationPath;
    private boolean ignoreOptionalProblems;
    
    public CompilationUnit(final char[] contents, final String fileName, final String encoding) {
        this(contents, fileName, encoding, null);
    }
    
    public CompilationUnit(final char[] contents, final String fileName, final String encoding, final String destinationPath) {
        this(contents, fileName, encoding, destinationPath, false);
    }
    
    public CompilationUnit(final char[] contents, final String fileName, final String encoding, final String destinationPath, final boolean ignoreOptionalProblems) {
        this.contents = contents;
        final char[] fileNameCharArray = fileName.toCharArray();
        switch (File.separatorChar) {
            case '/': {
                if (CharOperation.indexOf('\\', fileNameCharArray) != -1) {
                    CharOperation.replace(fileNameCharArray, '\\', '/');
                    break;
                }
                break;
            }
            case '\\': {
                if (CharOperation.indexOf('/', fileNameCharArray) != -1) {
                    CharOperation.replace(fileNameCharArray, '/', '\\');
                    break;
                }
                break;
            }
        }
        this.fileName = fileNameCharArray;
        final int start = CharOperation.lastIndexOf(File.separatorChar, fileNameCharArray) + 1;
        int end = CharOperation.lastIndexOf('.', fileNameCharArray);
        if (end == -1) {
            end = fileNameCharArray.length;
        }
        this.mainTypeName = CharOperation.subarray(fileNameCharArray, start, end);
        this.encoding = encoding;
        this.destinationPath = destinationPath;
        this.ignoreOptionalProblems = ignoreOptionalProblems;
    }
    
    @Override
    public char[] getContents() {
        if (this.contents != null) {
            return this.contents;
        }
        try {
            return Util.getFileCharContent(new File(new String(this.fileName)), this.encoding);
        }
        catch (final IOException e) {
            this.contents = CharOperation.NO_CHAR;
            throw new AbortCompilationUnit(null, e, this.encoding);
        }
    }
    
    @Override
    public char[] getFileName() {
        return this.fileName;
    }
    
    @Override
    public char[] getMainTypeName() {
        return this.mainTypeName;
    }
    
    @Override
    public char[][] getPackageName() {
        return null;
    }
    
    @Override
    public boolean ignoreOptionalProblems() {
        return this.ignoreOptionalProblems;
    }
    
    @Override
    public String toString() {
        return "CompilationUnit[" + new String(this.fileName) + "]";
    }
}
