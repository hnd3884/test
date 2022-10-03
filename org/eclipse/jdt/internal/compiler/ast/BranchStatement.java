package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;

public abstract class BranchStatement extends Statement
{
    public char[] label;
    public BranchLabel targetLabel;
    public SubRoutineStatement[] subroutines;
    public int initStateIndex;
    
    public BranchStatement(final char[] label, final int sourceStart, final int sourceEnd) {
        this.initStateIndex = -1;
        this.label = label;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        if (this.subroutines != null) {
            for (int i = 0, max = this.subroutines.length; i < max; ++i) {
                final SubRoutineStatement sub = this.subroutines[i];
                final boolean didEscape = sub.generateSubRoutineInvocation(currentScope, codeStream, this.targetLabel, this.initStateIndex, null);
                if (didEscape) {
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, i, codeStream);
                    if (this.initStateIndex != -1) {
                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.initStateIndex);
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.initStateIndex);
                    }
                    return;
                }
            }
        }
        codeStream.goto_(this.targetLabel);
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, -1, codeStream);
        if (this.initStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.initStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.initStateIndex);
        }
    }
    
    @Override
    public void resolve(final BlockScope scope) {
    }
}
