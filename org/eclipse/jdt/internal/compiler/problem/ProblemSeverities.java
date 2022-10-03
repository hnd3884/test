package org.eclipse.jdt.internal.compiler.problem;

public interface ProblemSeverities
{
    public static final int Warning = 0;
    public static final int Error = 1;
    public static final int AbortCompilation = 2;
    public static final int AbortCompilationUnit = 4;
    public static final int AbortType = 8;
    public static final int AbortMethod = 16;
    public static final int Abort = 30;
    public static final int Optional = 32;
    public static final int SecondaryError = 64;
    public static final int Fatal = 128;
    public static final int Ignore = 256;
    public static final int InternalError = 512;
    public static final int Info = 1024;
    public static final int CoreSeverityMASK = 1281;
}
