package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;

public abstract class SubRoutineStatement extends Statement
{
    ExceptionLabel anyExceptionLabel;
    
    public static void reenterAllExceptionHandlers(final SubRoutineStatement[] subroutines, int max, final CodeStream codeStream) {
        if (subroutines == null) {
            return;
        }
        if (max < 0) {
            max = subroutines.length;
        }
        for (final SubRoutineStatement sub : subroutines) {
            sub.enterAnyExceptionHandler(codeStream);
            sub.enterDeclaredExceptionHandlers(codeStream);
        }
    }
    
    public ExceptionLabel enterAnyExceptionHandler(final CodeStream codeStream) {
        if (this.anyExceptionLabel == null) {
            this.anyExceptionLabel = new ExceptionLabel(codeStream, null);
        }
        this.anyExceptionLabel.placeStart();
        return this.anyExceptionLabel;
    }
    
    public void enterDeclaredExceptionHandlers(final CodeStream codeStream) {
    }
    
    public void exitAnyExceptionHandler() {
        if (this.anyExceptionLabel != null) {
            this.anyExceptionLabel.placeEnd();
        }
    }
    
    public void exitDeclaredExceptionHandlers(final CodeStream codeStream) {
    }
    
    public abstract boolean generateSubRoutineInvocation(final BlockScope p0, final CodeStream p1, final Object p2, final int p3, final LocalVariableBinding p4);
    
    public abstract boolean isSubRoutineEscaping();
    
    public void placeAllAnyExceptionHandler() {
        this.anyExceptionLabel.place();
    }
}
