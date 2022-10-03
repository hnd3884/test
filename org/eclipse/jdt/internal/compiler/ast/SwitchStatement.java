package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.codegen.CaseLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.flow.SwitchFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class SwitchStatement extends Statement
{
    public Expression expression;
    public Statement[] statements;
    public BlockScope scope;
    public int explicitDeclarations;
    public BranchLabel breakLabel;
    public CaseStatement[] cases;
    public CaseStatement defaultCase;
    public int blockStart;
    public int caseCount;
    int[] constants;
    String[] stringConstants;
    public static final int CASE = 0;
    public static final int FALLTHROUGH = 1;
    public static final int ESCAPING = 2;
    private static final char[] SecretStringVariableName;
    public SyntheticMethodBinding synthetic;
    int preSwitchInitStateIndex;
    int mergedInitStateIndex;
    CaseStatement[] duplicateCaseStatements;
    int duplicateCaseStatementsCounter;
    private LocalVariableBinding dispatchStringCopy;
    
    static {
        SecretStringVariableName = " switchDispatchString".toCharArray();
    }
    
    public SwitchStatement() {
        this.preSwitchInitStateIndex = -1;
        this.mergedInitStateIndex = -1;
        this.duplicateCaseStatements = null;
        this.duplicateCaseStatementsCounter = 0;
        this.dispatchStringCopy = null;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        try {
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo);
            if ((this.expression.implicitConversion & 0x400) != 0x0 || (this.expression.resolvedType != null && (this.expression.resolvedType.id == 11 || this.expression.resolvedType.isEnum()))) {
                this.expression.checkNPE(currentScope, flowContext, flowInfo, 1);
            }
            final BranchLabel branchLabel = new BranchLabel();
            this.breakLabel = branchLabel;
            final SwitchFlowContext switchContext = new SwitchFlowContext(flowContext, this, branchLabel, true);
            FlowInfo caseInits = FlowInfo.DEAD_END;
            this.preSwitchInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
            int caseIndex = 0;
            if (this.statements != null) {
                int complaintLevel;
                final int initialComplaintLevel = complaintLevel = (((flowInfo.reachMode() & 0x3) != 0x0) ? 1 : 0);
                int fallThroughState = 0;
                for (int i = 0, max = this.statements.length; i < max; ++i) {
                    final Statement statement = this.statements[i];
                    if (caseIndex < this.caseCount && statement == this.cases[caseIndex]) {
                        this.scope.enclosingCase = this.cases[caseIndex];
                        ++caseIndex;
                        if (fallThroughState == 1 && (statement.bits & 0x20000000) == 0x0) {
                            this.scope.problemReporter().possibleFallThroughCase(this.scope.enclosingCase);
                        }
                        caseInits = caseInits.mergedWith(flowInfo.unconditionalInits());
                        complaintLevel = initialComplaintLevel;
                        fallThroughState = 0;
                    }
                    else if (statement == this.defaultCase) {
                        this.scope.enclosingCase = this.defaultCase;
                        if (fallThroughState == 1 && (statement.bits & 0x20000000) == 0x0) {
                            this.scope.problemReporter().possibleFallThroughCase(this.scope.enclosingCase);
                        }
                        caseInits = caseInits.mergedWith(flowInfo.unconditionalInits());
                        complaintLevel = initialComplaintLevel;
                        fallThroughState = 0;
                    }
                    else {
                        fallThroughState = 1;
                    }
                    if ((complaintLevel = statement.complainIfUnreachable(caseInits, this.scope, complaintLevel, true)) < 2) {
                        caseInits = statement.analyseCode(this.scope, switchContext, caseInits);
                        if (caseInits == FlowInfo.DEAD_END) {
                            fallThroughState = 2;
                        }
                        switchContext.expireNullCheckedFieldInfo();
                    }
                }
            }
            final TypeBinding resolvedTypeBinding = this.expression.resolvedType;
            if (resolvedTypeBinding.isEnum()) {
                final SourceTypeBinding sourceTypeBinding = currentScope.classScope().referenceContext.binding;
                this.synthetic = sourceTypeBinding.addSyntheticMethodForSwitchEnum(resolvedTypeBinding);
            }
            if (this.defaultCase == null) {
                flowInfo.addPotentialInitializationsFrom(caseInits.mergedWith(switchContext.initsOnBreak));
                this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
                return flowInfo;
            }
            final FlowInfo mergedInfo = caseInits.mergedWith(switchContext.initsOnBreak);
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
            return mergedInfo;
        }
        finally {
            if (this.scope != null) {
                this.scope.enclosingCase = null;
            }
        }
    }
    
    public void generateCodeForStringSwitch(final BlockScope currentScope, final CodeStream codeStream) {
        try {
            if ((this.bits & Integer.MIN_VALUE) == 0x0) {
                return;
            }
            final int pc = codeStream.position;
            final boolean hasCases = this.caseCount != 0;
            class StringSwitchCase implements Comparable
            {
                int hashCode = SwitchStatement.this.stringConstants[i].hashCode();
                String string = SwitchStatement.this.stringConstants[i];
                BranchLabel label = sourceCaseLabels[i];
                
                public StringSwitchCase(final String string, final BranchLabel label) {
                }
                
                @Override
                public int compareTo(final Object o) {
                    final StringSwitchCase that = (StringSwitchCase)o;
                    if (this.hashCode == that.hashCode) {
                        return 0;
                    }
                    if (this.hashCode > that.hashCode) {
                        return 1;
                    }
                    return -1;
                }
                
                @Override
                public String toString() {
                    return "StringSwitchCase :\ncase " + this.hashCode + ":(" + this.string + ")\n";
                }
            }
            final StringSwitchCase[] stringCases = new StringSwitchCase[this.caseCount];
            final BranchLabel[] sourceCaseLabels = new BranchLabel[this.caseCount];
            CaseLabel[] hashCodeCaseLabels = new CaseLabel[this.caseCount];
            this.constants = new int[this.caseCount];
            for (int i = 0, max = this.caseCount; i < max; ++i) {
                final CaseStatement caseStatement = this.cases[i];
                final BranchLabel[] array = sourceCaseLabels;
                final int n = i;
                final BranchLabel targetLabel = new BranchLabel(codeStream);
                array[n] = targetLabel;
                caseStatement.targetLabel = targetLabel;
                final BranchLabel branchLabel = sourceCaseLabels[i];
                branchLabel.tagBits |= 0x2;
                stringCases[i] = new StringSwitchCase(SwitchStatement.this.stringConstants[i]);
                hashCodeCaseLabels[i] = new CaseLabel(codeStream);
                final CaseLabel caseLabel = hashCodeCaseLabels[i];
                caseLabel.tagBits |= 0x2;
            }
            Arrays.sort(stringCases);
            int uniqHashCount = 0;
            int lastHashCode = 0;
            for (int j = 0, length = this.caseCount; j < length; ++j) {
                final int hashCode = stringCases[j].hashCode;
                if (j == 0 || hashCode != lastHashCode) {
                    final int[] constants = this.constants;
                    final int n2 = uniqHashCount++;
                    final int n3 = hashCode;
                    constants[n2] = n3;
                    lastHashCode = n3;
                }
            }
            if (uniqHashCount != this.caseCount) {
                System.arraycopy(this.constants, 0, this.constants = new int[uniqHashCount], 0, uniqHashCount);
                System.arraycopy(hashCodeCaseLabels, 0, hashCodeCaseLabels = new CaseLabel[uniqHashCount], 0, uniqHashCount);
            }
            final int[] sortedIndexes = new int[uniqHashCount];
            for (int k = 0; k < uniqHashCount; ++k) {
                sortedIndexes[k] = k;
            }
            final CaseLabel caseLabel2;
            final CaseLabel defaultCaseLabel = caseLabel2 = new CaseLabel(codeStream);
            caseLabel2.tagBits |= 0x2;
            this.breakLabel.initialize(codeStream);
            final BranchLabel defaultBranchLabel = new BranchLabel(codeStream);
            if (hasCases) {
                final BranchLabel branchLabel2 = defaultBranchLabel;
                branchLabel2.tagBits |= 0x2;
            }
            if (this.defaultCase != null) {
                this.defaultCase.targetLabel = defaultBranchLabel;
            }
            this.expression.generateCode(currentScope, codeStream, true);
            codeStream.store(this.dispatchStringCopy, true);
            codeStream.addVariable(this.dispatchStringCopy);
            codeStream.invokeStringHashCode();
            if (hasCases) {
                codeStream.lookupswitch(defaultCaseLabel, this.constants, sortedIndexes, hashCodeCaseLabels);
                int l = 0;
                int m = 0;
                for (int max2 = this.caseCount; l < max2; ++l) {
                    final int hashCode2 = stringCases[l].hashCode;
                    if (l == 0 || hashCode2 != lastHashCode) {
                        lastHashCode = hashCode2;
                        if (l != 0) {
                            codeStream.goto_(defaultBranchLabel);
                        }
                        hashCodeCaseLabels[m++].place();
                    }
                    codeStream.load(this.dispatchStringCopy);
                    codeStream.ldc(stringCases[l].string);
                    codeStream.invokeStringEquals();
                    codeStream.ifne(stringCases[l].label);
                }
                codeStream.goto_(defaultBranchLabel);
            }
            else {
                codeStream.pop();
            }
            int caseIndex = 0;
            if (this.statements != null) {
                for (int i2 = 0, maxCases = this.statements.length; i2 < maxCases; ++i2) {
                    final Statement statement = this.statements[i2];
                    if (caseIndex < this.caseCount && statement == this.cases[caseIndex]) {
                        this.scope.enclosingCase = this.cases[caseIndex];
                        if (this.preSwitchInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
                        }
                        ++caseIndex;
                    }
                    else if (statement == this.defaultCase) {
                        defaultCaseLabel.place();
                        this.scope.enclosingCase = this.defaultCase;
                        if (this.preSwitchInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
                        }
                    }
                    statement.generateCode(this.scope, codeStream);
                }
            }
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            codeStream.removeVariable(this.dispatchStringCopy);
            if (this.scope != currentScope) {
                codeStream.exitUserScope(this.scope);
            }
            this.breakLabel.place();
            if (this.defaultCase == null) {
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd, true);
                defaultCaseLabel.place();
                defaultBranchLabel.place();
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        }
        finally {
            if (this.scope != null) {
                this.scope.enclosingCase = null;
            }
        }
        if (this.scope != null) {
            this.scope.enclosingCase = null;
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if (this.expression.resolvedType.id == 11) {
            this.generateCodeForStringSwitch(currentScope, codeStream);
            return;
        }
        try {
            if ((this.bits & Integer.MIN_VALUE) == 0x0) {
                return;
            }
            final int pc = codeStream.position;
            this.breakLabel.initialize(codeStream);
            final CaseLabel[] caseLabels = new CaseLabel[this.caseCount];
            for (int i = 0, max = this.caseCount; i < max; ++i) {
                final CaseStatement caseStatement = this.cases[i];
                final CaseLabel[] array = caseLabels;
                final int n = i;
                final CaseLabel targetLabel = new CaseLabel(codeStream);
                array[n] = targetLabel;
                caseStatement.targetLabel = targetLabel;
                final CaseLabel caseLabel = caseLabels[i];
                caseLabel.tagBits |= 0x2;
            }
            final CaseLabel defaultLabel = new CaseLabel(codeStream);
            final boolean hasCases = this.caseCount != 0;
            if (hasCases) {
                final CaseLabel caseLabel2 = defaultLabel;
                caseLabel2.tagBits |= 0x2;
            }
            if (this.defaultCase != null) {
                this.defaultCase.targetLabel = defaultLabel;
            }
            final TypeBinding resolvedType = this.expression.resolvedType;
            boolean valueRequired = false;
            if (resolvedType.isEnum()) {
                codeStream.invoke((byte)(-72), this.synthetic, null);
                this.expression.generateCode(currentScope, codeStream, true);
                codeStream.invokeEnumOrdinal(resolvedType.constantPoolName());
                codeStream.iaload();
                if (!hasCases) {
                    codeStream.pop();
                }
            }
            else {
                valueRequired = (this.expression.constant == Constant.NotAConstant || hasCases);
                this.expression.generateCode(currentScope, codeStream, valueRequired);
            }
            if (hasCases) {
                final int[] sortedIndexes = new int[this.caseCount];
                for (int j = 0; j < this.caseCount; ++j) {
                    sortedIndexes[j] = j;
                }
                final int[] localKeysCopy;
                System.arraycopy(this.constants, 0, localKeysCopy = new int[this.caseCount], 0, this.caseCount);
                CodeStream.sort(localKeysCopy, 0, this.caseCount - 1, sortedIndexes);
                final int max2 = localKeysCopy[this.caseCount - 1];
                final int min = localKeysCopy[0];
                if ((long)(this.caseCount * 2.5) > max2 - (long)min) {
                    if (max2 > 2147418112 && currentScope.compilerOptions().complianceLevel < 3145728L) {
                        codeStream.lookupswitch(defaultLabel, this.constants, sortedIndexes, caseLabels);
                    }
                    else {
                        codeStream.tableswitch(defaultLabel, min, max2, this.constants, sortedIndexes, caseLabels);
                    }
                }
                else {
                    codeStream.lookupswitch(defaultLabel, this.constants, sortedIndexes, caseLabels);
                }
                codeStream.recordPositionsFrom(codeStream.position, this.expression.sourceEnd);
            }
            else if (valueRequired) {
                codeStream.pop();
            }
            int caseIndex = 0;
            if (this.statements != null) {
                for (int j = 0, maxCases = this.statements.length; j < maxCases; ++j) {
                    final Statement statement = this.statements[j];
                    if (caseIndex < this.caseCount && statement == this.cases[caseIndex]) {
                        this.scope.enclosingCase = this.cases[caseIndex];
                        if (this.preSwitchInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
                        }
                        ++caseIndex;
                    }
                    else if (statement == this.defaultCase) {
                        this.scope.enclosingCase = this.defaultCase;
                        if (this.preSwitchInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
                        }
                    }
                    statement.generateCode(this.scope, codeStream);
                }
            }
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            if (this.scope != currentScope) {
                codeStream.exitUserScope(this.scope);
            }
            this.breakLabel.place();
            if (this.defaultCase == null) {
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd, true);
                defaultLabel.place();
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        }
        finally {
            if (this.scope != null) {
                this.scope.enclosingCase = null;
            }
        }
        if (this.scope != null) {
            this.scope.enclosingCase = null;
        }
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output).append("switch (");
        this.expression.printExpression(0, output).append(") {");
        if (this.statements != null) {
            for (int i = 0; i < this.statements.length; ++i) {
                output.append('\n');
                if (this.statements[i] instanceof CaseStatement) {
                    this.statements[i].printStatement(indent, output);
                }
                else {
                    this.statements[i].printStatement(indent + 2, output);
                }
            }
        }
        output.append("\n");
        return ASTNode.printIndent(indent, output).append('}');
    }
    
    @Override
    public void resolve(final BlockScope upperScope) {
        try {
            boolean isEnumSwitch = false;
            boolean isStringSwitch = false;
            TypeBinding expressionType = this.expression.resolveType(upperScope);
            final CompilerOptions compilerOptions = upperScope.compilerOptions();
            Label_0200: {
                if (expressionType != null) {
                    this.expression.computeConversion(upperScope, expressionType, expressionType);
                    if (!expressionType.isValidBinding()) {
                        expressionType = null;
                    }
                    else {
                        if (expressionType.isBaseType()) {
                            if (this.expression.isConstantValueOfTypeAssignableToType(expressionType, TypeBinding.INT)) {
                                break Label_0200;
                            }
                            if (expressionType.isCompatibleWith(TypeBinding.INT)) {
                                break Label_0200;
                            }
                        }
                        else if (expressionType.isEnum()) {
                            isEnumSwitch = true;
                            if (compilerOptions.complianceLevel < 3211264L) {
                                upperScope.problemReporter().incorrectSwitchType(this.expression, expressionType);
                            }
                            break Label_0200;
                        }
                        else {
                            if (upperScope.isBoxingCompatibleWith(expressionType, TypeBinding.INT)) {
                                this.expression.computeConversion(upperScope, TypeBinding.INT, expressionType);
                                break Label_0200;
                            }
                            if (compilerOptions.complianceLevel >= 3342336L && expressionType.id == 11) {
                                isStringSwitch = true;
                                break Label_0200;
                            }
                        }
                        upperScope.problemReporter().incorrectSwitchType(this.expression, expressionType);
                        expressionType = null;
                    }
                }
            }
            if (isStringSwitch) {
                upperScope.addLocalVariable(this.dispatchStringCopy = new LocalVariableBinding(SwitchStatement.SecretStringVariableName, upperScope.getJavaLangString(), 0, false));
                this.dispatchStringCopy.setConstant(Constant.NotAConstant);
                this.dispatchStringCopy.useFlag = 1;
            }
            if (this.statements != null) {
                this.scope = new BlockScope(upperScope);
                final int length;
                this.cases = new CaseStatement[length = this.statements.length];
                if (!isStringSwitch) {
                    this.constants = new int[length];
                }
                else {
                    this.stringConstants = new String[length];
                }
                int counter = 0;
                for (int i = 0; i < length; ++i) {
                    final Statement statement = this.statements[i];
                    final Constant constant;
                    if ((constant = statement.resolveCase(this.scope, expressionType, this)) != Constant.NotAConstant) {
                        if (!isStringSwitch) {
                            final int key = constant.intValue();
                            for (int j = 0; j < counter; ++j) {
                                if (this.constants[j] == key) {
                                    this.reportDuplicateCase((CaseStatement)statement, this.cases[j], length);
                                }
                            }
                            this.constants[counter++] = key;
                        }
                        else {
                            final String key2 = constant.stringValue();
                            for (int j = 0; j < counter; ++j) {
                                if (this.stringConstants[j].equals(key2)) {
                                    this.reportDuplicateCase((CaseStatement)statement, this.cases[j], length);
                                }
                            }
                            this.stringConstants[counter++] = key2;
                        }
                    }
                }
                if (length != counter) {
                    if (!isStringSwitch) {
                        System.arraycopy(this.constants, 0, this.constants = new int[counter], 0, counter);
                    }
                    else {
                        System.arraycopy(this.stringConstants, 0, this.stringConstants = new String[counter], 0, counter);
                    }
                }
            }
            else if ((this.bits & 0x8) != 0x0) {
                upperScope.problemReporter().undocumentedEmptyBlock(this.blockStart, this.sourceEnd);
            }
            if (this.defaultCase == null) {
                if (compilerOptions.getSeverity(1073774592) == 256) {
                    if (isEnumSwitch) {
                        upperScope.methodScope().hasMissingSwitchDefault = true;
                    }
                }
                else {
                    upperScope.problemReporter().missingDefaultCase(this, isEnumSwitch, expressionType);
                }
            }
            if (isEnumSwitch && compilerOptions.complianceLevel >= 3211264L && (this.defaultCase == null || compilerOptions.reportMissingEnumCaseDespiteDefault)) {
                final int constantCount = (this.constants == null) ? 0 : this.constants.length;
                if (constantCount == this.caseCount && this.caseCount != ((ReferenceBinding)expressionType).enumConstantCount()) {
                    final FieldBinding[] enumFields = ((ReferenceBinding)expressionType.erasure()).fields();
                Label_0827:
                    for (int i = 0, max = enumFields.length; i < max; ++i) {
                        final FieldBinding enumConstant = enumFields[i];
                        if ((enumConstant.modifiers & 0x4000) != 0x0) {
                            for (int k = 0; k < this.caseCount; ++k) {
                                if (enumConstant.id + 1 == this.constants[k]) {
                                    continue Label_0827;
                                }
                            }
                            final boolean suppress = this.defaultCase != null && (this.defaultCase.bits & 0x40000000) != 0x0;
                            if (!suppress) {
                                upperScope.problemReporter().missingEnumConstantCase(this, enumConstant);
                            }
                        }
                    }
                }
            }
        }
        finally {
            if (this.scope != null) {
                this.scope.enclosingCase = null;
            }
        }
        if (this.scope != null) {
            this.scope.enclosingCase = null;
        }
    }
    
    private void reportDuplicateCase(final CaseStatement duplicate, final CaseStatement original, final int length) {
        if (this.duplicateCaseStatements == null) {
            this.scope.problemReporter().duplicateCase(original);
            this.scope.problemReporter().duplicateCase(duplicate);
            (this.duplicateCaseStatements = new CaseStatement[length])[this.duplicateCaseStatementsCounter++] = original;
            this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = duplicate;
        }
        else {
            boolean found = false;
            for (int k = 2; k < this.duplicateCaseStatementsCounter; ++k) {
                if (this.duplicateCaseStatements[k] == duplicate) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.scope.problemReporter().duplicateCase(duplicate);
                this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = duplicate;
            }
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.expression.traverse(visitor, blockScope);
            if (this.statements != null) {
                for (int statementsLength = this.statements.length, i = 0; i < statementsLength; ++i) {
                    this.statements[i].traverse(visitor, this.scope);
                }
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public void branchChainTo(final BranchLabel label) {
        if (this.breakLabel.forwardReferenceCount() > 0) {
            label.becomeDelegateFor(this.breakLabel);
        }
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        if (this.statements == null || this.statements.length == 0) {
            return false;
        }
        for (int i = 0, length = this.statements.length; i < length; ++i) {
            if (this.statements[i].breaksOut(null)) {
                return false;
            }
        }
        return this.statements[this.statements.length - 1].doesNotCompleteNormally();
    }
    
    @Override
    public boolean completesByContinue() {
        if (this.statements == null || this.statements.length == 0) {
            return false;
        }
        for (int i = 0, length = this.statements.length; i < length; ++i) {
            if (this.statements[i].completesByContinue()) {
                return true;
            }
        }
        return false;
    }
}
