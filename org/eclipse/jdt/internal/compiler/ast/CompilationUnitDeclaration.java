package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.AbortType;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.impl.IrritantSet;
import org.eclipse.jdt.internal.compiler.util.HashSetOfInt;
import org.eclipse.jdt.internal.compiler.parser.NLSTag;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import java.util.Comparator;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

public class CompilationUnitDeclaration extends ASTNode implements ProblemSeverities, ReferenceContext
{
    private static final Comparator STRING_LITERAL_COMPARATOR;
    private static final int STRING_LITERALS_INCREMENT = 10;
    public ImportReference currentPackage;
    public ImportReference[] imports;
    public TypeDeclaration[] types;
    public int[][] comments;
    public boolean ignoreFurtherInvestigation;
    public boolean ignoreMethodBodies;
    public CompilationUnitScope scope;
    public ProblemReporter problemReporter;
    public CompilationResult compilationResult;
    public LocalTypeBinding[] localTypes;
    public int localTypeCount;
    public boolean isPropagatingInnerClassEmulation;
    public Javadoc javadoc;
    public NLSTag[] nlsTags;
    private StringLiteral[] stringLiterals;
    private int stringLiteralsPtr;
    private HashSetOfInt stringLiteralsStart;
    public boolean[] validIdentityComparisonLines;
    IrritantSet[] suppressWarningIrritants;
    Annotation[] suppressWarningAnnotations;
    long[] suppressWarningScopePositions;
    int suppressWarningsCount;
    public int functionalExpressionsCount;
    public FunctionalExpression[] functionalExpressions;
    
    static {
        STRING_LITERAL_COMPARATOR = new Comparator() {
            @Override
            public int compare(final Object o1, final Object o2) {
                final StringLiteral literal1 = (StringLiteral)o1;
                final StringLiteral literal2 = (StringLiteral)o2;
                return literal1.sourceStart - literal2.sourceStart;
            }
        };
    }
    
    public CompilationUnitDeclaration(final ProblemReporter problemReporter, final CompilationResult compilationResult, final int sourceLength) {
        this.ignoreFurtherInvestigation = false;
        this.ignoreMethodBodies = false;
        this.localTypeCount = 0;
        this.problemReporter = problemReporter;
        this.compilationResult = compilationResult;
        this.sourceStart = 0;
        this.sourceEnd = sourceLength - 1;
    }
    
    @Override
    public void abort(final int abortLevel, final CategorizedProblem problem) {
        switch (abortLevel) {
            case 8: {
                throw new AbortType(this.compilationResult, problem);
            }
            case 16: {
                throw new AbortMethod(this.compilationResult, problem);
            }
            default: {
                throw new AbortCompilationUnit(this.compilationResult, problem);
            }
        }
    }
    
    public void analyseCode() {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            if (this.types != null) {
                for (int i = 0, count = this.types.length; i < count; ++i) {
                    this.types[i].analyseCode(this.scope);
                }
            }
            this.propagateInnerEmulationForAllLocalTypes();
        }
        catch (final AbortCompilationUnit abortCompilationUnit) {
            this.ignoreFurtherInvestigation = true;
        }
    }
    
    public void cleanUp() {
        if (this.types != null) {
            for (int i = 0, max = this.types.length; i < max; ++i) {
                this.cleanUp(this.types[i]);
            }
            for (int i = 0, max = this.localTypeCount; i < max; ++i) {
                final LocalTypeBinding localType = this.localTypes[i];
                localType.scope = null;
                localType.enclosingCase = null;
            }
        }
        this.compilationResult.recoveryScannerData = null;
        final ClassFile[] classFiles = this.compilationResult.getClassFiles();
        for (int j = 0, max2 = classFiles.length; j < max2; ++j) {
            final ClassFile classFile = classFiles[j];
            classFile.referenceBinding = null;
            classFile.innerClassesBindings = null;
            classFile.bootstrapMethods = null;
            classFile.missingTypes = null;
            classFile.visitedTypes = null;
        }
        this.suppressWarningAnnotations = null;
        if (this.scope != null) {
            this.scope.cleanUpInferenceContexts();
        }
    }
    
    private void cleanUp(final TypeDeclaration type) {
        if (type.memberTypes != null) {
            for (int i = 0, max = type.memberTypes.length; i < max; ++i) {
                this.cleanUp(type.memberTypes[i]);
            }
        }
        if (type.binding != null && type.binding.isAnnotationType()) {
            this.compilationResult.hasAnnotations = true;
        }
        if (type.binding != null) {
            type.binding.scope = null;
        }
    }
    
    public void checkUnusedImports() {
        if (this.scope.imports != null) {
            for (int i = 0, max = this.scope.imports.length; i < max; ++i) {
                final ImportBinding importBinding = this.scope.imports[i];
                final ImportReference importReference = importBinding.reference;
                if (importReference != null && (importReference.bits & 0x2) == 0x0) {
                    this.scope.problemReporter().unusedImport(importReference);
                }
            }
        }
    }
    
    @Override
    public CompilationResult compilationResult() {
        return this.compilationResult;
    }
    
    public void createPackageInfoType() {
        final TypeDeclaration declaration = new TypeDeclaration(this.compilationResult);
        declaration.name = TypeConstants.PACKAGE_INFO_NAME;
        declaration.modifiers = 512;
        declaration.javadoc = this.javadoc;
        this.types[0] = declaration;
    }
    
    public TypeDeclaration declarationOfType(final char[][] typeName) {
        for (int i = 0; i < this.types.length; ++i) {
            final TypeDeclaration typeDecl = this.types[i].declarationOfType(typeName);
            if (typeDecl != null) {
                return typeDecl;
            }
        }
        return null;
    }
    
    public void finalizeProblems() {
        if (this.suppressWarningsCount == 0) {
            return;
        }
        int removed = 0;
        final CategorizedProblem[] problems = this.compilationResult.problems;
        final int problemCount = this.compilationResult.problemCount;
        final IrritantSet[] foundIrritants = new IrritantSet[this.suppressWarningsCount];
        final CompilerOptions options = this.scope.compilerOptions();
        boolean hasMandatoryErrors = false;
        for (int iProblem = 0, length = problemCount; iProblem < length; ++iProblem) {
            final CategorizedProblem problem = problems[iProblem];
            final int problemID = problem.getID();
            final int irritant = ProblemReporter.getIrritant(problemID);
            final boolean isError = problem.isError();
            if (isError) {
                if (irritant == 0) {
                    hasMandatoryErrors = true;
                    continue;
                }
                if (!options.suppressOptionalErrors) {
                    continue;
                }
            }
            final int start = problem.getSourceStart();
            final int end = problem.getSourceEnd();
            for (int iSuppress = 0, suppressCount = this.suppressWarningsCount; iSuppress < suppressCount; ++iSuppress) {
                final long position = this.suppressWarningScopePositions[iSuppress];
                final int startSuppress = (int)(position >>> 32);
                final int endSuppress = (int)position;
                if (start >= startSuppress) {
                    if (end <= endSuppress) {
                        if (this.suppressWarningIrritants[iSuppress].isSet(irritant)) {
                            ++removed;
                            problems[iProblem] = null;
                            this.compilationResult.removeProblem(problem);
                            if (foundIrritants[iSuppress] == null) {
                                foundIrritants[iSuppress] = new IrritantSet(irritant);
                                break;
                            }
                            foundIrritants[iSuppress].set(irritant);
                            break;
                        }
                    }
                }
            }
        }
        if (removed > 0) {
            int i = 0;
            int index = 0;
            while (i < problemCount) {
                final CategorizedProblem problem;
                if ((problem = problems[i]) != null) {
                    if (i > index) {
                        problems[index++] = problem;
                    }
                    else {
                        ++index;
                    }
                }
                ++i;
            }
        }
        if (!hasMandatoryErrors) {
            final int severity = options.getSeverity(570425344);
            if (severity != 256) {
                final boolean unusedWarningTokenIsWarning = (severity & 0x1) == 0x0;
            Label_0913:
                for (int iSuppress2 = 0, suppressCount2 = this.suppressWarningsCount; iSuppress2 < suppressCount2; ++iSuppress2) {
                    final Annotation annotation = this.suppressWarningAnnotations[iSuppress2];
                    if (annotation != null) {
                        final IrritantSet irritants = this.suppressWarningIrritants[iSuppress2];
                        if (!unusedWarningTokenIsWarning || !irritants.areAllSet()) {
                            if (irritants != foundIrritants[iSuppress2]) {
                                final MemberValuePair[] pairs = annotation.memberValuePairs();
                                int iPair = 0;
                                final int pairCount = pairs.length;
                                while (iPair < pairCount) {
                                    final MemberValuePair pair = pairs[iPair];
                                    if (CharOperation.equals(pair.name, TypeConstants.VALUE)) {
                                        final Expression value = pair.value;
                                        if (value instanceof ArrayInitializer) {
                                            final ArrayInitializer initializer = (ArrayInitializer)value;
                                            final Expression[] inits = initializer.expressions;
                                            if (inits != null) {
                                                for (int iToken = 0, tokenCount = inits.length; iToken < tokenCount; ++iToken) {
                                                    final Constant cst = inits[iToken].constant;
                                                    if (cst != Constant.NotAConstant && cst.typeID() == 11) {
                                                        final IrritantSet tokenIrritants = CompilerOptions.warningTokenToIrritants(cst.stringValue());
                                                        if (tokenIrritants != null && !tokenIrritants.areAllSet() && options.isAnyEnabled(tokenIrritants) && (foundIrritants[iSuppress2] == null || !foundIrritants[iSuppress2].isAnySet(tokenIrritants))) {
                                                            if (unusedWarningTokenIsWarning) {
                                                                final int start2 = value.sourceStart;
                                                                final int end2 = value.sourceEnd;
                                                                for (int jSuppress = iSuppress2 - 1; jSuppress >= 0; --jSuppress) {
                                                                    final long position2 = this.suppressWarningScopePositions[jSuppress];
                                                                    final int startSuppress2 = (int)(position2 >>> 32);
                                                                    final int endSuppress2 = (int)position2;
                                                                    if (start2 >= startSuppress2) {
                                                                        if (end2 <= endSuppress2) {
                                                                            if (this.suppressWarningIrritants[jSuppress].areAllSet()) {
                                                                                continue Label_0913;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            this.scope.problemReporter().unusedWarningToken(inits[iToken]);
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                            break;
                                        }
                                        else {
                                            final Constant cst2 = value.constant;
                                            if (cst2 == Constant.NotAConstant || cst2.typeID() != 11) {
                                                break;
                                            }
                                            final IrritantSet tokenIrritants2 = CompilerOptions.warningTokenToIrritants(cst2.stringValue());
                                            if (tokenIrritants2 != null && !tokenIrritants2.areAllSet() && options.isAnyEnabled(tokenIrritants2) && (foundIrritants[iSuppress2] == null || !foundIrritants[iSuppress2].isAnySet(tokenIrritants2))) {
                                                if (unusedWarningTokenIsWarning) {
                                                    final int start3 = value.sourceStart;
                                                    final int end3 = value.sourceEnd;
                                                    for (int jSuppress2 = iSuppress2 - 1; jSuppress2 >= 0; --jSuppress2) {
                                                        final long position3 = this.suppressWarningScopePositions[jSuppress2];
                                                        final int startSuppress3 = (int)(position3 >>> 32);
                                                        final int endSuppress3 = (int)position3;
                                                        if (start3 >= startSuppress3) {
                                                            if (end3 <= endSuppress3) {
                                                                if (this.suppressWarningIrritants[jSuppress2].areAllSet()) {
                                                                    continue Label_0913;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                this.scope.problemReporter().unusedWarningToken(value);
                                                break;
                                            }
                                            break;
                                        }
                                    }
                                    else {
                                        ++iPair;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void generateCode() {
        if (this.ignoreFurtherInvestigation) {
            if (this.types != null) {
                for (int i = 0, count = this.types.length; i < count; ++i) {
                    this.types[i].ignoreFurtherInvestigation = true;
                    this.types[i].generateCode(this.scope);
                }
            }
            return;
        }
        try {
            if (this.types != null) {
                for (int i = 0, count = this.types.length; i < count; ++i) {
                    this.types[i].generateCode(this.scope);
                }
            }
        }
        catch (final AbortCompilationUnit abortCompilationUnit) {}
    }
    
    @Override
    public CompilationUnitDeclaration getCompilationUnitDeclaration() {
        return this;
    }
    
    public char[] getFileName() {
        return this.compilationResult.getFileName();
    }
    
    public char[] getMainTypeName() {
        if (this.compilationResult.compilationUnit == null) {
            final char[] fileName = this.compilationResult.getFileName();
            int start = CharOperation.lastIndexOf('/', fileName) + 1;
            if (start == 0 || start < CharOperation.lastIndexOf('\\', fileName)) {
                start = CharOperation.lastIndexOf('\\', fileName) + 1;
            }
            int end = CharOperation.lastIndexOf('.', fileName);
            if (end == -1) {
                end = fileName.length;
            }
            return CharOperation.subarray(fileName, start, end);
        }
        return this.compilationResult.compilationUnit.getMainTypeName();
    }
    
    public boolean isEmpty() {
        return this.currentPackage == null && this.imports == null && this.types == null;
    }
    
    public boolean isPackageInfo() {
        return CharOperation.equals(this.getMainTypeName(), TypeConstants.PACKAGE_INFO_NAME);
    }
    
    public boolean isSuppressed(final CategorizedProblem problem) {
        if (this.suppressWarningsCount == 0) {
            return false;
        }
        final int irritant = ProblemReporter.getIrritant(problem.getID());
        if (irritant == 0) {
            return false;
        }
        final int start = problem.getSourceStart();
        final int end = problem.getSourceEnd();
        for (int iSuppress = 0, suppressCount = this.suppressWarningsCount; iSuppress < suppressCount; ++iSuppress) {
            final long position = this.suppressWarningScopePositions[iSuppress];
            final int startSuppress = (int)(position >>> 32);
            final int endSuppress = (int)position;
            if (start >= startSuppress) {
                if (end <= endSuppress) {
                    if (this.suppressWarningIrritants[iSuppress].isSet(irritant)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean hasFunctionalTypes() {
        return this.compilationResult.hasFunctionalTypes;
    }
    
    @Override
    public boolean hasErrors() {
        return this.ignoreFurtherInvestigation;
    }
    
    @Override
    public StringBuffer print(final int indent, final StringBuffer output) {
        if (this.currentPackage != null) {
            ASTNode.printIndent(indent, output).append("package ");
            this.currentPackage.print(0, output, false).append(";\n");
        }
        if (this.imports != null) {
            for (int i = 0; i < this.imports.length; ++i) {
                ASTNode.printIndent(indent, output).append("import ");
                final ImportReference currentImport = this.imports[i];
                if (currentImport.isStatic()) {
                    output.append("static ");
                }
                currentImport.print(0, output).append(";\n");
            }
        }
        if (this.types != null) {
            for (int i = 0; i < this.types.length; ++i) {
                this.types[i].print(indent, output).append("\n");
            }
        }
        return output;
    }
    
    public void propagateInnerEmulationForAllLocalTypes() {
        this.isPropagatingInnerClassEmulation = true;
        for (int i = 0, max = this.localTypeCount; i < max; ++i) {
            final LocalTypeBinding localType = this.localTypes[i];
            if ((localType.scope.referenceType().bits & Integer.MIN_VALUE) != 0x0) {
                localType.updateInnerEmulationDependents();
            }
        }
    }
    
    public void recordStringLiteral(final StringLiteral literal, final boolean fromRecovery) {
        if (this.stringLiteralsStart != null) {
            if (this.stringLiteralsStart.contains(literal.sourceStart)) {
                return;
            }
            this.stringLiteralsStart.add(literal.sourceStart);
        }
        else if (fromRecovery) {
            this.stringLiteralsStart = new HashSetOfInt(this.stringLiteralsPtr + 10);
            for (int i = 0; i < this.stringLiteralsPtr; ++i) {
                this.stringLiteralsStart.add(this.stringLiterals[i].sourceStart);
            }
            if (this.stringLiteralsStart.contains(literal.sourceStart)) {
                return;
            }
            this.stringLiteralsStart.add(literal.sourceStart);
        }
        if (this.stringLiterals == null) {
            this.stringLiterals = new StringLiteral[10];
            this.stringLiteralsPtr = 0;
        }
        else {
            final int stackLength = this.stringLiterals.length;
            if (this.stringLiteralsPtr == stackLength) {
                System.arraycopy(this.stringLiterals, 0, this.stringLiterals = new StringLiteral[stackLength + 10], 0, stackLength);
            }
        }
        this.stringLiterals[this.stringLiteralsPtr++] = literal;
    }
    
    public void recordSuppressWarnings(final IrritantSet irritants, final Annotation annotation, final int scopeStart, final int scopeEnd, final ReferenceContext context) {
        if (context instanceof LambdaExpression && context != ((LambdaExpression)context).original()) {
            return;
        }
        if (this.suppressWarningIrritants == null) {
            this.suppressWarningIrritants = new IrritantSet[3];
            this.suppressWarningAnnotations = new Annotation[3];
            this.suppressWarningScopePositions = new long[3];
        }
        else if (this.suppressWarningIrritants.length == this.suppressWarningsCount) {
            System.arraycopy(this.suppressWarningIrritants, 0, this.suppressWarningIrritants = new IrritantSet[2 * this.suppressWarningsCount], 0, this.suppressWarningsCount);
            System.arraycopy(this.suppressWarningAnnotations, 0, this.suppressWarningAnnotations = new Annotation[2 * this.suppressWarningsCount], 0, this.suppressWarningsCount);
            System.arraycopy(this.suppressWarningScopePositions, 0, this.suppressWarningScopePositions = new long[2 * this.suppressWarningsCount], 0, this.suppressWarningsCount);
        }
        final long scopePositions = ((long)scopeStart << 32) + scopeEnd;
        for (int i = 0, max = this.suppressWarningsCount; i < max; ++i) {
            if (this.suppressWarningAnnotations[i] == annotation && this.suppressWarningScopePositions[i] == scopePositions && this.suppressWarningIrritants[i].hasSameIrritants(irritants)) {
                return;
            }
        }
        this.suppressWarningIrritants[this.suppressWarningsCount] = irritants;
        this.suppressWarningAnnotations[this.suppressWarningsCount] = annotation;
        this.suppressWarningScopePositions[this.suppressWarningsCount++] = scopePositions;
    }
    
    public void record(final LocalTypeBinding localType) {
        if (this.localTypeCount == 0) {
            this.localTypes = new LocalTypeBinding[5];
        }
        else if (this.localTypeCount == this.localTypes.length) {
            System.arraycopy(this.localTypes, 0, this.localTypes = new LocalTypeBinding[this.localTypeCount * 2], 0, this.localTypeCount);
        }
        this.localTypes[this.localTypeCount++] = localType;
    }
    
    public int record(final FunctionalExpression expression) {
        if (this.functionalExpressionsCount == 0) {
            this.functionalExpressions = new FunctionalExpression[5];
        }
        else if (this.functionalExpressionsCount == this.functionalExpressions.length) {
            System.arraycopy(this.functionalExpressions, 0, this.functionalExpressions = new FunctionalExpression[this.functionalExpressionsCount * 2], 0, this.functionalExpressionsCount);
        }
        this.functionalExpressions[this.functionalExpressionsCount++] = expression;
        return expression.enclosingScope.classScope().referenceContext.record(expression);
    }
    
    public void resolve() {
        int startingTypeIndex = 0;
        final boolean isPackageInfo = this.isPackageInfo();
        if (this.types != null && isPackageInfo) {
            final TypeDeclaration syntheticTypeDeclaration = this.types[0];
            if (syntheticTypeDeclaration.javadoc == null) {
                syntheticTypeDeclaration.javadoc = new Javadoc(syntheticTypeDeclaration.declarationSourceStart, syntheticTypeDeclaration.declarationSourceStart);
            }
            syntheticTypeDeclaration.resolve(this.scope);
            if (this.javadoc != null && syntheticTypeDeclaration.staticInitializerScope != null) {
                this.javadoc.resolve(syntheticTypeDeclaration.staticInitializerScope);
            }
            startingTypeIndex = 1;
        }
        else if (this.javadoc != null) {
            this.javadoc.resolve(this.scope);
        }
        if (this.currentPackage != null && this.currentPackage.annotations != null && !isPackageInfo) {
            this.scope.problemReporter().invalidFileNameForPackageAnnotations(this.currentPackage.annotations[0]);
        }
        try {
            if (this.types != null) {
                for (int i = startingTypeIndex, count = this.types.length; i < count; ++i) {
                    this.types[i].resolve(this.scope);
                }
            }
            if (!this.compilationResult.hasMandatoryErrors()) {
                this.checkUnusedImports();
            }
            this.reportNLSProblems();
        }
        catch (final AbortCompilationUnit abortCompilationUnit) {
            this.ignoreFurtherInvestigation = true;
        }
    }
    
    private void reportNLSProblems() {
        if (this.nlsTags != null || this.stringLiterals != null) {
            final int stringLiteralsLength = this.stringLiteralsPtr;
            final int nlsTagsLength = (this.nlsTags == null) ? 0 : this.nlsTags.length;
            if (stringLiteralsLength == 0) {
                if (nlsTagsLength != 0) {
                    for (int i = 0; i < nlsTagsLength; ++i) {
                        final NLSTag tag = this.nlsTags[i];
                        if (tag != null) {
                            this.scope.problemReporter().unnecessaryNLSTags(tag.start, tag.end);
                        }
                    }
                }
            }
            else {
                if (nlsTagsLength != 0) {
                    if (this.stringLiterals.length != stringLiteralsLength) {
                        System.arraycopy(this.stringLiterals, 0, this.stringLiterals = new StringLiteral[stringLiteralsLength], 0, stringLiteralsLength);
                    }
                    Arrays.sort(this.stringLiterals, CompilationUnitDeclaration.STRING_LITERAL_COMPARATOR);
                    int indexInLine = 1;
                    int lastLineNumber = -1;
                    StringLiteral literal = null;
                    int index = 0;
                    int j = 0;
                Label_0476:
                    while (true) {
                        while (j < stringLiteralsLength) {
                            literal = this.stringLiterals[j];
                            final int literalLineNumber = literal.lineNumber;
                            if (lastLineNumber != literalLineNumber) {
                                indexInLine = 1;
                                lastLineNumber = literalLineNumber;
                            }
                            else {
                                ++indexInLine;
                            }
                            if (index < nlsTagsLength) {
                                while (index < nlsTagsLength) {
                                    final NLSTag tag2 = this.nlsTags[index];
                                    Label_0461: {
                                        if (tag2 != null) {
                                            final int tagLineNumber = tag2.lineNumber;
                                            Label_0473: {
                                                if (literalLineNumber < tagLineNumber) {
                                                    this.scope.problemReporter().nonExternalizedStringLiteral(literal);
                                                }
                                                else {
                                                    if (literalLineNumber != tagLineNumber) {
                                                        this.scope.problemReporter().unnecessaryNLSTags(tag2.start, tag2.end);
                                                        break Label_0461;
                                                    }
                                                    if (tag2.index == indexInLine) {
                                                        this.nlsTags[index] = null;
                                                        ++index;
                                                    }
                                                    else {
                                                        for (int index2 = index + 1; index2 < nlsTagsLength; ++index2) {
                                                            final NLSTag tag3 = this.nlsTags[index2];
                                                            if (tag3 != null) {
                                                                final int tagLineNumber2 = tag3.lineNumber;
                                                                if (literalLineNumber != tagLineNumber2) {
                                                                    this.scope.problemReporter().nonExternalizedStringLiteral(literal);
                                                                    break Label_0473;
                                                                }
                                                                if (tag3.index == indexInLine) {
                                                                    this.nlsTags[index2] = null;
                                                                    break Label_0473;
                                                                }
                                                            }
                                                        }
                                                        this.scope.problemReporter().nonExternalizedStringLiteral(literal);
                                                    }
                                                }
                                            }
                                            ++j;
                                            continue Label_0476;
                                        }
                                    }
                                    ++index;
                                }
                            }
                            while (j < stringLiteralsLength) {
                                this.scope.problemReporter().nonExternalizedStringLiteral(this.stringLiterals[j]);
                                ++j;
                            }
                            if (index < nlsTagsLength) {
                                while (index < nlsTagsLength) {
                                    final NLSTag tag4 = this.nlsTags[index];
                                    if (tag4 != null) {
                                        this.scope.problemReporter().unnecessaryNLSTags(tag4.start, tag4.end);
                                    }
                                    ++index;
                                }
                            }
                            return;
                        }
                        continue;
                    }
                }
                if (this.stringLiterals.length != stringLiteralsLength) {
                    System.arraycopy(this.stringLiterals, 0, this.stringLiterals = new StringLiteral[stringLiteralsLength], 0, stringLiteralsLength);
                }
                Arrays.sort(this.stringLiterals, CompilationUnitDeclaration.STRING_LITERAL_COMPARATOR);
                for (int i = 0; i < stringLiteralsLength; ++i) {
                    this.scope.problemReporter().nonExternalizedStringLiteral(this.stringLiterals[i]);
                }
            }
        }
    }
    
    @Override
    public void tagAsHavingErrors() {
        this.ignoreFurtherInvestigation = true;
    }
    
    @Override
    public void tagAsHavingIgnoredMandatoryErrors(final int problemId) {
    }
    
    public void traverse(final ASTVisitor visitor, final CompilationUnitScope unitScope) {
        this.traverse(visitor, unitScope, true);
    }
    
    public void traverse(final ASTVisitor visitor, final CompilationUnitScope unitScope, final boolean skipOnError) {
        if (skipOnError && this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            if (visitor.visit(this, this.scope)) {
                if (this.types != null && this.isPackageInfo()) {
                    final TypeDeclaration syntheticTypeDeclaration = this.types[0];
                    final MethodScope methodScope = syntheticTypeDeclaration.staticInitializerScope;
                    if (this.javadoc != null && methodScope != null) {
                        this.javadoc.traverse(visitor, methodScope);
                    }
                    if (this.currentPackage != null && methodScope != null) {
                        final Annotation[] annotations = this.currentPackage.annotations;
                        if (annotations != null) {
                            for (int annotationsLength = annotations.length, i = 0; i < annotationsLength; ++i) {
                                annotations[i].traverse(visitor, methodScope);
                            }
                        }
                    }
                }
                if (this.currentPackage != null) {
                    this.currentPackage.traverse(visitor, this.scope);
                }
                if (this.imports != null) {
                    for (int importLength = this.imports.length, j = 0; j < importLength; ++j) {
                        this.imports[j].traverse(visitor, this.scope);
                    }
                }
                if (this.types != null) {
                    for (int typesLength = this.types.length, j = 0; j < typesLength; ++j) {
                        this.types[j].traverse(visitor, this.scope);
                    }
                }
            }
            visitor.endVisit(this, this.scope);
        }
        catch (final AbortCompilationUnit abortCompilationUnit) {}
    }
}
