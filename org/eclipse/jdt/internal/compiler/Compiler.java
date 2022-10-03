package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeCollisionException;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import java.io.OutputStream;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.impl.CompilerStats;
import java.io.PrintWriter;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;

public class Compiler implements ITypeRequestor, ProblemSeverities
{
    public Parser parser;
    public ICompilerRequestor requestor;
    public CompilerOptions options;
    public ProblemReporter problemReporter;
    protected PrintWriter out;
    public CompilerStats stats;
    public CompilationProgress progress;
    public int remainingIterations;
    public CompilationUnitDeclaration[] unitsToProcess;
    public int totalUnits;
    private Map<String, APTProblem[]> aptProblems;
    public LookupEnvironment lookupEnvironment;
    public static boolean DEBUG;
    public int parseThreshold;
    public AbstractAnnotationProcessorManager annotationProcessorManager;
    public int annotationProcessorStartIndex;
    public ReferenceBinding[] referenceBindings;
    public boolean useSingleThread;
    public static IDebugRequestor DebugRequestor;
    
    static {
        Compiler.DEBUG = false;
        Compiler.DebugRequestor = null;
    }
    
    @Deprecated
    public Compiler(final INameEnvironment environment, final IErrorHandlingPolicy policy, final Map<String, String> settings, final ICompilerRequestor requestor, final IProblemFactory problemFactory) {
        this(environment, policy, new CompilerOptions(settings), requestor, problemFactory, null, null);
    }
    
    @Deprecated
    public Compiler(final INameEnvironment environment, final IErrorHandlingPolicy policy, final Map settings, final ICompilerRequestor requestor, final IProblemFactory problemFactory, final boolean parseLiteralExpressionsAsConstants) {
        this(environment, policy, new CompilerOptions(settings, parseLiteralExpressionsAsConstants), requestor, problemFactory, null, null);
    }
    
    public Compiler(final INameEnvironment environment, final IErrorHandlingPolicy policy, final CompilerOptions options, final ICompilerRequestor requestor, final IProblemFactory problemFactory) {
        this(environment, policy, options, requestor, problemFactory, null, null);
    }
    
    @Deprecated
    public Compiler(final INameEnvironment environment, final IErrorHandlingPolicy policy, final CompilerOptions options, final ICompilerRequestor requestor, final IProblemFactory problemFactory, final PrintWriter out) {
        this(environment, policy, options, requestor, problemFactory, out, null);
    }
    
    public Compiler(final INameEnvironment environment, final IErrorHandlingPolicy policy, final CompilerOptions options, final ICompilerRequestor requestor, final IProblemFactory problemFactory, final PrintWriter out, final CompilationProgress progress) {
        this.remainingIterations = 1;
        this.parseThreshold = -1;
        this.annotationProcessorStartIndex = 0;
        this.useSingleThread = true;
        this.options = options;
        this.progress = progress;
        if (Compiler.DebugRequestor == null) {
            this.requestor = requestor;
        }
        else {
            this.requestor = new ICompilerRequestor() {
                @Override
                public void acceptResult(final CompilationResult result) {
                    if (Compiler.DebugRequestor.isActive()) {
                        Compiler.DebugRequestor.acceptDebugResult(result);
                    }
                    requestor.acceptResult(result);
                }
            };
        }
        this.problemReporter = new ProblemReporter(policy, this.options, problemFactory);
        this.lookupEnvironment = new LookupEnvironment(this, this.options, this.problemReporter, environment);
        this.out = ((out == null) ? new PrintWriter(System.out, true) : out);
        this.stats = new CompilerStats();
        this.initializeParser();
    }
    
    @Override
    public void accept(final IBinaryType binaryType, final PackageBinding packageBinding, final AccessRestriction accessRestriction) {
        if (this.options.verbose) {
            this.out.println(Messages.bind(Messages.compilation_loadBinary, new String(binaryType.getName())));
        }
        this.lookupEnvironment.createBinaryTypeFrom(binaryType, packageBinding, accessRestriction);
    }
    
    @Override
    public void accept(final ICompilationUnit sourceUnit, final AccessRestriction accessRestriction) {
        final CompilationResult unitResult = new CompilationResult(sourceUnit, this.totalUnits, this.totalUnits, this.options.maxProblemsPerUnit);
        unitResult.checkSecondaryTypes = true;
        try {
            if (this.options.verbose) {
                final String count = String.valueOf(this.totalUnits + 1);
                this.out.println(Messages.bind(Messages.compilation_request, new String[] { count, count, new String(sourceUnit.getFileName()) }));
            }
            CompilationUnitDeclaration parsedUnit;
            if (this.totalUnits < this.parseThreshold) {
                parsedUnit = this.parser.parse(sourceUnit, unitResult);
            }
            else {
                parsedUnit = this.parser.dietParse(sourceUnit, unitResult);
            }
            this.lookupEnvironment.buildTypeBindings(parsedUnit, accessRestriction);
            this.addCompilationUnit(sourceUnit, parsedUnit);
            this.lookupEnvironment.completeTypeBindings(parsedUnit);
        }
        catch (final AbortCompilationUnit e) {
            if (unitResult.compilationUnit != sourceUnit) {
                throw e;
            }
            this.requestor.acceptResult(unitResult.tagAsAccepted());
        }
    }
    
    @Override
    public void accept(final ISourceType[] sourceTypes, final PackageBinding packageBinding, final AccessRestriction accessRestriction) {
        this.problemReporter.abortDueToInternalError(Messages.bind(Messages.abort_againstSourceModel, new String[] { String.valueOf(sourceTypes[0].getName()), String.valueOf(sourceTypes[0].getFileName()) }));
    }
    
    protected synchronized void addCompilationUnit(final ICompilationUnit sourceUnit, final CompilationUnitDeclaration parsedUnit) {
        if (this.unitsToProcess == null) {
            return;
        }
        final int size = this.unitsToProcess.length;
        if (this.totalUnits == size) {
            System.arraycopy(this.unitsToProcess, 0, this.unitsToProcess = new CompilationUnitDeclaration[size * 2], 0, this.totalUnits);
        }
        this.unitsToProcess[this.totalUnits++] = parsedUnit;
    }
    
    protected void beginToCompile(final ICompilationUnit[] sourceUnits) {
        final int maxUnits = sourceUnits.length;
        this.totalUnits = 0;
        this.unitsToProcess = new CompilationUnitDeclaration[maxUnits];
        this.internalBeginToCompile(sourceUnits, maxUnits);
    }
    
    protected void reportProgress(final String taskDecription) {
        if (this.progress != null) {
            if (this.progress.isCanceled()) {
                throw new AbortCompilation(true, null);
            }
            this.progress.setTaskName(taskDecription);
        }
    }
    
    protected void reportWorked(final int workIncrement, final int currentUnitIndex) {
        if (this.progress != null) {
            if (this.progress.isCanceled()) {
                throw new AbortCompilation(true, null);
            }
            this.progress.worked(workIncrement, this.totalUnits * this.remainingIterations - currentUnitIndex - 1);
        }
    }
    
    public void compile(final ICompilationUnit[] sourceUnits) {
        this.compile(sourceUnits, false);
    }
    
    private void compile(final ICompilationUnit[] sourceUnits, final boolean lastRound) {
        this.stats.startTime = System.currentTimeMillis();
        try {
            this.reportProgress(Messages.compilation_beginningToCompile);
            if (this.annotationProcessorManager == null) {
                this.beginToCompile(sourceUnits);
            }
            else {
                final ICompilationUnit[] originalUnits = sourceUnits.clone();
                try {
                    this.beginToCompile(sourceUnits);
                    if (!lastRound) {
                        this.processAnnotations();
                    }
                    if (!this.options.generateClassFiles) {
                        return;
                    }
                }
                catch (final SourceTypeCollisionException e) {
                    this.backupAptProblems();
                    this.reset();
                    final int originalLength = originalUnits.length;
                    final int newProcessedLength = e.newAnnotationProcessorUnits.length;
                    final ICompilationUnit[] combinedUnits = new ICompilationUnit[originalLength + newProcessedLength];
                    System.arraycopy(originalUnits, 0, combinedUnits, 0, originalLength);
                    System.arraycopy(e.newAnnotationProcessorUnits, 0, combinedUnits, originalLength, newProcessedLength);
                    this.annotationProcessorStartIndex = originalLength;
                    this.compile(combinedUnits, e.isLastRound);
                    return;
                }
            }
            this.restoreAptProblems();
            this.processCompiledUnits(0, lastRound);
        }
        catch (final AbortCompilation e2) {
            this.handleInternalException(e2, null);
        }
        if (this.options.verbose) {
            if (this.totalUnits > 1) {
                this.out.println(Messages.bind(Messages.compilation_units, String.valueOf(this.totalUnits)));
            }
            else {
                this.out.println(Messages.bind(Messages.compilation_unit, String.valueOf(this.totalUnits)));
            }
        }
    }
    
    protected void backupAptProblems() {
        if (this.unitsToProcess == null) {
            return;
        }
        for (int i = 0; i < this.totalUnits; ++i) {
            final CompilationUnitDeclaration unitDecl = this.unitsToProcess[i];
            final CompilationResult result = unitDecl.compilationResult;
            if (result != null && result.hasErrors()) {
                final CategorizedProblem[] errors = result.getErrors();
                CategorizedProblem[] array;
                for (int length = (array = errors).length, j = 0; j < length; ++j) {
                    final CategorizedProblem problem = array[j];
                    if (problem.getCategoryID() == 0) {
                        if (this.aptProblems == null) {
                            this.aptProblems = new HashMap<String, APTProblem[]>();
                        }
                        final APTProblem[] problems = this.aptProblems.get(new String(unitDecl.getFileName()));
                        if (problems == null) {
                            this.aptProblems.put(new String(unitDecl.getFileName()), new APTProblem[] { new APTProblem(problem, result.getContext(problem)) });
                        }
                        else {
                            final APTProblem[] temp = new APTProblem[problems.length + 1];
                            System.arraycopy(problems, 0, temp, 0, problems.length);
                            temp[problems.length] = new APTProblem(problem, result.getContext(problem));
                            this.aptProblems.put(new String(unitDecl.getFileName()), temp);
                        }
                    }
                }
            }
        }
    }
    
    protected void restoreAptProblems() {
        if (this.unitsToProcess != null && this.aptProblems != null) {
            for (int i = 0; i < this.totalUnits; ++i) {
                final CompilationUnitDeclaration unitDecl = this.unitsToProcess[i];
                final APTProblem[] problems = this.aptProblems.get(new String(unitDecl.getFileName()));
                if (problems != null) {
                    APTProblem[] array;
                    for (int length = (array = problems).length, j = 0; j < length; ++j) {
                        final APTProblem problem = array[j];
                        unitDecl.compilationResult.record(problem.problem, problem.context);
                    }
                }
            }
        }
        this.aptProblems = null;
    }
    
    protected void processCompiledUnits(final int startingIndex, final boolean lastRound) throws Error {
        CompilationUnitDeclaration unit = null;
        ProcessTaskManager processingTask = null;
        try {
            if (this.useSingleThread) {
                for (int i = startingIndex; i < this.totalUnits; ++i) {
                    unit = this.unitsToProcess[i];
                    if (unit.compilationResult == null || !unit.compilationResult.hasBeenAccepted) {
                        this.reportProgress(Messages.bind(Messages.compilation_processing, new String(unit.getFileName())));
                        try {
                            if (this.options.verbose) {
                                this.out.println(Messages.bind(Messages.compilation_process, new String[] { String.valueOf(i + 1), String.valueOf(this.totalUnits), new String(this.unitsToProcess[i].getFileName()) }));
                            }
                            this.process(unit, i);
                        }
                        finally {
                            if (this.annotationProcessorManager == null || this.shouldCleanup(i)) {
                                unit.cleanUp();
                            }
                        }
                        if (this.annotationProcessorManager == null || this.shouldCleanup(i)) {
                            unit.cleanUp();
                        }
                        if (this.annotationProcessorManager == null) {
                            this.unitsToProcess[i] = null;
                        }
                        this.reportWorked(1, i);
                        final CompilerStats stats = this.stats;
                        stats.lineCount += unit.compilationResult.lineSeparatorPositions.length;
                        final long acceptStart = System.currentTimeMillis();
                        this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
                        final CompilerStats stats2 = this.stats;
                        stats2.generateTime += System.currentTimeMillis() - acceptStart;
                        if (this.options.verbose) {
                            this.out.println(Messages.bind(Messages.compilation_done, new String[] { String.valueOf(i + 1), String.valueOf(this.totalUnits), new String(unit.getFileName()) }));
                        }
                    }
                }
            }
            else {
                processingTask = new ProcessTaskManager(this, startingIndex);
                int acceptedCount = 0;
                while (true) {
                    try {
                        unit = processingTask.removeNextUnit();
                    }
                    catch (final Error e) {
                        unit = processingTask.unitToProcess;
                        throw e;
                    }
                    catch (final RuntimeException e2) {
                        unit = processingTask.unitToProcess;
                        throw e2;
                    }
                    if (unit == null) {
                        break;
                    }
                    this.reportWorked(1, acceptedCount++);
                    final CompilerStats stats3 = this.stats;
                    stats3.lineCount += unit.compilationResult.lineSeparatorPositions.length;
                    this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
                    if (!this.options.verbose) {
                        continue;
                    }
                    this.out.println(Messages.bind(Messages.compilation_done, new String[] { String.valueOf(acceptedCount), String.valueOf(this.totalUnits), new String(unit.getFileName()) }));
                }
            }
            if (!lastRound && this.annotationProcessorManager != null && this.totalUnits > this.annotationProcessorStartIndex) {
                final int backup = this.annotationProcessorStartIndex;
                final int prevUnits = this.totalUnits;
                this.processAnnotations();
                for (int j = backup; j < prevUnits; ++j) {
                    this.unitsToProcess[j].cleanUp();
                }
                this.processCompiledUnits(backup, lastRound);
            }
        }
        catch (final AbortCompilation e3) {
            this.handleInternalException(e3, unit);
        }
        catch (final Error e4) {
            this.handleInternalException(e4, unit, null);
            throw e4;
        }
        catch (final RuntimeException e5) {
            this.handleInternalException(e5, unit, null);
            throw e5;
        }
        finally {
            if (processingTask != null) {
                processingTask.shutdown();
                processingTask = null;
            }
            this.reset();
            this.annotationProcessorStartIndex = 0;
            this.stats.endTime = System.currentTimeMillis();
        }
        if (processingTask != null) {
            processingTask.shutdown();
            processingTask = null;
        }
        this.reset();
        this.annotationProcessorStartIndex = 0;
        this.stats.endTime = System.currentTimeMillis();
    }
    
    public synchronized CompilationUnitDeclaration getUnitToProcess(final int next) {
        if (next < this.totalUnits) {
            final CompilationUnitDeclaration unit = this.unitsToProcess[next];
            if (this.annotationProcessorManager == null || next < this.annotationProcessorStartIndex) {
                this.unitsToProcess[next] = null;
            }
            return unit;
        }
        return null;
    }
    
    public boolean shouldCleanup(final int index) {
        return index < this.annotationProcessorStartIndex;
    }
    
    public void setBinaryTypes(final ReferenceBinding[] binaryTypes) {
        this.referenceBindings = binaryTypes;
    }
    
    protected void handleInternalException(final Throwable internalException, final CompilationUnitDeclaration unit, CompilationResult result) {
        if (result == null && unit != null) {
            result = unit.compilationResult;
        }
        if (result == null && this.lookupEnvironment.unitBeingCompleted != null) {
            result = this.lookupEnvironment.unitBeingCompleted.compilationResult;
        }
        if (result == null) {
            synchronized (this) {
                if (this.unitsToProcess != null && this.totalUnits > 0) {
                    result = this.unitsToProcess[this.totalUnits - 1].compilationResult;
                }
            }
        }
        boolean needToPrint = true;
        if (result != null) {
            final String[] pbArguments = { Messages.bind(Messages.compilation_internalError, Util.getExceptionSummary(internalException)) };
            result.record(this.problemReporter.createProblem(result.getFileName(), 0, pbArguments, pbArguments, 1, 0, 0, 0, 0), unit, true);
            if (!result.hasBeenAccepted) {
                this.requestor.acceptResult(result.tagAsAccepted());
                needToPrint = false;
            }
        }
        if (needToPrint) {
            internalException.printStackTrace();
        }
    }
    
    protected void handleInternalException(final AbortCompilation abortException, final CompilationUnitDeclaration unit) {
        if (!abortException.isSilent) {
            CompilationResult result = abortException.compilationResult;
            if (result == null && unit != null) {
                result = unit.compilationResult;
            }
            if (result == null && this.lookupEnvironment.unitBeingCompleted != null) {
                result = this.lookupEnvironment.unitBeingCompleted.compilationResult;
            }
            if (result == null) {
                synchronized (this) {
                    if (this.unitsToProcess != null && this.totalUnits > 0) {
                        result = this.unitsToProcess[this.totalUnits - 1].compilationResult;
                    }
                }
            }
            if (result != null && !result.hasBeenAccepted) {
                Label_0221: {
                    if (abortException.problem != null) {
                        final CategorizedProblem distantProblem = abortException.problem;
                        final CategorizedProblem[] knownProblems = result.problems;
                        for (int i = 0; i < result.problemCount; ++i) {
                            if (knownProblems[i] == distantProblem) {
                                break Label_0221;
                            }
                        }
                        if (distantProblem instanceof DefaultProblem) {
                            ((DefaultProblem)distantProblem).setOriginatingFileName(result.getFileName());
                        }
                        result.record(distantProblem, unit, true);
                    }
                    else if (abortException.exception != null) {
                        this.handleInternalException(abortException.exception, null, result);
                        return;
                    }
                }
                if (!result.hasBeenAccepted) {
                    this.requestor.acceptResult(result.tagAsAccepted());
                }
            }
            else {
                abortException.printStackTrace();
            }
            return;
        }
        if (abortException.silentException == null) {
            return;
        }
        throw abortException.silentException;
    }
    
    public void initializeParser() {
        this.parser = new Parser(this.problemReporter, this.options.parseLiteralExpressionsAsConstants);
    }
    
    protected void internalBeginToCompile(final ICompilationUnit[] sourceUnits, final int maxUnits) {
        if (!this.useSingleThread && maxUnits >= 10) {
            this.parser.readManager = new ReadManager(sourceUnits, maxUnits);
        }
        for (int i = 0; i < maxUnits; ++i) {
            CompilationResult unitResult = null;
            try {
                if (this.options.verbose) {
                    this.out.println(Messages.bind(Messages.compilation_request, new String[] { String.valueOf(i + 1), String.valueOf(maxUnits), new String(sourceUnits[i].getFileName()) }));
                }
                unitResult = new CompilationResult(sourceUnits[i], i, maxUnits, this.options.maxProblemsPerUnit);
                final long parseStart = System.currentTimeMillis();
                CompilationUnitDeclaration parsedUnit;
                if (this.totalUnits < this.parseThreshold) {
                    parsedUnit = this.parser.parse(sourceUnits[i], unitResult);
                }
                else {
                    parsedUnit = this.parser.dietParse(sourceUnits[i], unitResult);
                }
                final long resolveStart = System.currentTimeMillis();
                final CompilerStats stats = this.stats;
                stats.parseTime += resolveStart - parseStart;
                this.lookupEnvironment.buildTypeBindings(parsedUnit, null);
                final CompilerStats stats2 = this.stats;
                stats2.resolveTime += System.currentTimeMillis() - resolveStart;
                this.addCompilationUnit(sourceUnits[i], parsedUnit);
                final ImportReference currentPackage = parsedUnit.currentPackage;
                if (currentPackage != null) {
                    unitResult.recordPackageName(currentPackage.tokens);
                }
            }
            catch (final AbortCompilation a) {
                if (a.compilationResult == null) {
                    a.compilationResult = unitResult;
                }
                throw a;
            }
            finally {
                sourceUnits[i] = null;
            }
            sourceUnits[i] = null;
        }
        if (this.parser.readManager != null) {
            this.parser.readManager.shutdown();
            this.parser.readManager = null;
        }
        this.lookupEnvironment.completeTypeBindings();
    }
    
    public void process(final CompilationUnitDeclaration unit, final int i) {
        this.lookupEnvironment.unitBeingCompleted = unit;
        final long parseStart = System.currentTimeMillis();
        this.parser.getMethodBodies(unit);
        final long resolveStart = System.currentTimeMillis();
        final CompilerStats stats = this.stats;
        stats.parseTime += resolveStart - parseStart;
        if (unit.scope != null) {
            unit.scope.faultInTypes();
        }
        if (unit.scope != null) {
            unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
        }
        unit.resolve();
        final long analyzeStart = System.currentTimeMillis();
        final CompilerStats stats2 = this.stats;
        stats2.resolveTime += analyzeStart - resolveStart;
        if (!this.options.ignoreMethodBodies) {
            unit.analyseCode();
        }
        final long generateStart = System.currentTimeMillis();
        final CompilerStats stats3 = this.stats;
        stats3.analyzeTime += generateStart - analyzeStart;
        if (!this.options.ignoreMethodBodies) {
            unit.generateCode();
        }
        if (this.options.produceReferenceInfo && unit.scope != null) {
            unit.scope.storeDependencyInfo();
        }
        unit.finalizeProblems();
        final CompilerStats stats4 = this.stats;
        stats4.generateTime += System.currentTimeMillis() - generateStart;
        unit.compilationResult.totalUnitsKnown = this.totalUnits;
        this.lookupEnvironment.unitBeingCompleted = null;
    }
    
    protected void processAnnotations() {
        int newUnitSize = 0;
        int newClassFilesSize = 0;
        int bottom = this.annotationProcessorStartIndex;
        int top = this.totalUnits;
        ReferenceBinding[] binaryTypeBindingsTemp = this.referenceBindings;
        if (top == 0 && binaryTypeBindingsTemp == null) {
            return;
        }
        this.referenceBindings = null;
        do {
            int length = top - bottom;
            CompilationUnitDeclaration[] currentUnits = new CompilationUnitDeclaration[length];
            int index = 0;
            for (int i = bottom; i < top; ++i) {
                final CompilationUnitDeclaration currentUnit = this.unitsToProcess[i];
                currentUnits[index++] = currentUnit;
            }
            if (index != length) {
                System.arraycopy(currentUnits, 0, currentUnits = new CompilationUnitDeclaration[index], 0, index);
            }
            this.annotationProcessorManager.processAnnotations(currentUnits, binaryTypeBindingsTemp, false);
            if (top < this.totalUnits) {
                length = this.totalUnits - top;
                final CompilationUnitDeclaration[] addedUnits = new CompilationUnitDeclaration[length];
                System.arraycopy(this.unitsToProcess, top, addedUnits, 0, length);
                this.annotationProcessorManager.processAnnotations(addedUnits, binaryTypeBindingsTemp, false);
            }
            this.annotationProcessorStartIndex = top;
            final ICompilationUnit[] newUnits = this.annotationProcessorManager.getNewUnits();
            newUnitSize = newUnits.length;
            final ReferenceBinding[] newClassFiles = binaryTypeBindingsTemp = this.annotationProcessorManager.getNewClassFiles();
            newClassFilesSize = newClassFiles.length;
            if (newUnitSize != 0) {
                final ICompilationUnit[] newProcessedUnits = newUnits.clone();
                try {
                    this.lookupEnvironment.isProcessingAnnotations = true;
                    this.internalBeginToCompile(newUnits, newUnitSize);
                }
                catch (final SourceTypeCollisionException e) {
                    e.newAnnotationProcessorUnits = newProcessedUnits;
                    throw e;
                }
                finally {
                    this.lookupEnvironment.isProcessingAnnotations = false;
                    this.annotationProcessorManager.reset();
                }
                this.lookupEnvironment.isProcessingAnnotations = false;
                this.annotationProcessorManager.reset();
                bottom = top;
                top = this.totalUnits;
                this.annotationProcessorStartIndex = top;
            }
            else {
                bottom = top;
                this.annotationProcessorManager.reset();
            }
        } while (newUnitSize != 0 || newClassFilesSize != 0);
        this.annotationProcessorManager.processAnnotations(null, null, true);
        final ICompilationUnit[] newUnits2 = this.annotationProcessorManager.getNewUnits();
        newUnitSize = newUnits2.length;
        if (newUnitSize != 0) {
            final ICompilationUnit[] newProcessedUnits2 = newUnits2.clone();
            try {
                this.lookupEnvironment.isProcessingAnnotations = true;
                this.internalBeginToCompile(newUnits2, newUnitSize);
            }
            catch (final SourceTypeCollisionException e2) {
                e2.isLastRound = true;
                e2.newAnnotationProcessorUnits = newProcessedUnits2;
                throw e2;
            }
            finally {
                this.lookupEnvironment.isProcessingAnnotations = false;
                this.annotationProcessorManager.reset();
            }
            this.lookupEnvironment.isProcessingAnnotations = false;
            this.annotationProcessorManager.reset();
        }
        else {
            this.annotationProcessorManager.reset();
        }
        this.annotationProcessorStartIndex = this.totalUnits;
    }
    
    public void reset() {
        this.lookupEnvironment.reset();
        this.parser.scanner.source = null;
        this.unitsToProcess = null;
        if (Compiler.DebugRequestor != null) {
            Compiler.DebugRequestor.reset();
        }
        this.problemReporter.reset();
    }
    
    public CompilationUnitDeclaration resolve(CompilationUnitDeclaration unit, final ICompilationUnit sourceUnit, final boolean verifyMethods, final boolean analyzeCode, final boolean generateCode) {
        try {
            if (unit == null) {
                this.parseThreshold = 0;
                this.beginToCompile(new ICompilationUnit[] { sourceUnit });
                for (int i = 0; i < this.totalUnits; ++i) {
                    if (this.unitsToProcess[i] != null && this.unitsToProcess[i].compilationResult.compilationUnit == sourceUnit) {
                        unit = this.unitsToProcess[i];
                        break;
                    }
                }
                if (unit == null) {
                    unit = this.unitsToProcess[0];
                }
            }
            else {
                this.lookupEnvironment.buildTypeBindings(unit, null);
                this.lookupEnvironment.completeTypeBindings();
            }
            this.lookupEnvironment.unitBeingCompleted = unit;
            this.parser.getMethodBodies(unit);
            if (unit.scope != null) {
                unit.scope.faultInTypes();
                if (unit.scope != null && verifyMethods) {
                    unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
                }
                unit.resolve();
                if (analyzeCode) {
                    unit.analyseCode();
                }
                if (generateCode) {
                    unit.generateCode();
                }
                unit.finalizeProblems();
            }
            if (this.unitsToProcess != null) {
                this.unitsToProcess[0] = null;
            }
            this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
            return unit;
        }
        catch (final AbortCompilation e) {
            this.handleInternalException(e, unit);
            return (unit == null) ? this.unitsToProcess[0] : unit;
        }
        catch (final Error e2) {
            this.handleInternalException(e2, unit, null);
            throw e2;
        }
        catch (final RuntimeException e3) {
            this.handleInternalException(e3, unit, null);
            throw e3;
        }
    }
    
    public CompilationUnitDeclaration resolve(final ICompilationUnit sourceUnit, final boolean verifyMethods, final boolean analyzeCode, final boolean generateCode) {
        return this.resolve(null, sourceUnit, verifyMethods, analyzeCode, generateCode);
    }
    
    class APTProblem
    {
        CategorizedProblem problem;
        ReferenceContext context;
        
        APTProblem(final CategorizedProblem problem, final ReferenceContext context) {
            this.problem = problem;
            this.context = context;
        }
    }
}
