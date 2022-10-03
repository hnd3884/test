package org.eclipse.jdt.internal.compiler.tool;

import java.util.Locale;
import java.nio.charset.Charset;
import javax.tools.JavaFileObject;
import java.io.File;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import javax.tools.Diagnostic;

public class EclipseDiagnostic implements Diagnostic<EclipseFileObject>
{
    private Kind kind;
    private final int problemId;
    private final String[] problemArguments;
    private final char[] originatingFileName;
    private final int lineNumber;
    private final int columnNumber;
    private final int startPosition;
    private final int endPosition;
    private final DefaultProblemFactory problemFactory;
    
    private EclipseDiagnostic(final Kind kind, final int problemId, final String[] problemArguments, final char[] originatingFileName, final DefaultProblemFactory problemFactory, final int lineNumber, final int columnNumber, final int startPosition, final int endPosition) {
        this.kind = kind;
        this.problemId = problemId;
        this.problemArguments = problemArguments;
        this.originatingFileName = originatingFileName;
        this.problemFactory = problemFactory;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }
    
    private EclipseDiagnostic(final Kind kind, final int problemId, final String[] problemArguments, final char[] originatingFileName, final DefaultProblemFactory problemFactory) {
        this(kind, problemId, problemArguments, originatingFileName, problemFactory, -1, -1, -1, -1);
    }
    
    public static EclipseDiagnostic newInstance(final CategorizedProblem problem, final DefaultProblemFactory factory) {
        if (problem instanceof DefaultProblem) {
            return newInstanceFromDefaultProblem((DefaultProblem)problem, factory);
        }
        return new EclipseDiagnostic(getKind(problem), problem.getID(), problem.getArguments(), problem.getOriginatingFileName(), factory);
    }
    
    private static EclipseDiagnostic newInstanceFromDefaultProblem(final DefaultProblem problem, final DefaultProblemFactory factory) {
        return new EclipseDiagnostic(getKind(problem), problem.getID(), problem.getArguments(), problem.getOriginatingFileName(), factory, problem.getSourceLineNumber(), problem.getSourceColumnNumber(), problem.getSourceStart(), problem.getSourceEnd());
    }
    
    private static Kind getKind(final CategorizedProblem problem) {
        Kind kind = Kind.OTHER;
        if (problem.isError()) {
            kind = Kind.ERROR;
        }
        else if (problem.isWarning()) {
            kind = Kind.WARNING;
        }
        else if (problem instanceof DefaultProblem && ((DefaultProblem)problem).isInfo()) {
            kind = Kind.NOTE;
        }
        return kind;
    }
    
    @Override
    public Kind getKind() {
        return this.kind;
    }
    
    @Override
    public EclipseFileObject getSource() {
        final File f = new File(new String(this.originatingFileName));
        if (f.exists()) {
            return new EclipseFileObject(null, f.toURI(), JavaFileObject.Kind.SOURCE, null);
        }
        return null;
    }
    
    @Override
    public long getPosition() {
        return this.startPosition;
    }
    
    @Override
    public long getStartPosition() {
        return this.startPosition;
    }
    
    @Override
    public long getEndPosition() {
        return this.endPosition;
    }
    
    @Override
    public long getLineNumber() {
        return this.lineNumber;
    }
    
    @Override
    public long getColumnNumber() {
        return this.columnNumber;
    }
    
    @Override
    public String getCode() {
        return Integer.toString(this.problemId);
    }
    
    @Override
    public String getMessage(final Locale locale) {
        if (locale != null) {
            this.problemFactory.setLocale(locale);
        }
        return this.problemFactory.getLocalizedMessage(this.problemId, this.problemArguments);
    }
}
