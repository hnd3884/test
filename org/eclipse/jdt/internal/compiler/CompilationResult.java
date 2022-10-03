package org.eclipse.jdt.internal.compiler;

import java.util.Iterator;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import java.util.HashSet;
import java.util.HashMap;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import java.util.Hashtable;
import org.eclipse.jdt.internal.compiler.util.Util;
import java.util.Comparator;
import org.eclipse.jdt.internal.compiler.parser.RecoveryScannerData;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

public class CompilationResult
{
    public CategorizedProblem[] problems;
    public CategorizedProblem[] tasks;
    public int problemCount;
    public int taskCount;
    public ICompilationUnit compilationUnit;
    private Map<CategorizedProblem, ReferenceContext> problemsMap;
    private Set firstErrors;
    private int maxProblemPerUnit;
    public char[][][] qualifiedReferences;
    public char[][] simpleNameReferences;
    public char[][] rootReferences;
    public boolean hasAnnotations;
    public boolean hasFunctionalTypes;
    public int[] lineSeparatorPositions;
    public RecoveryScannerData recoveryScannerData;
    public Map compiledTypes;
    public int unitIndex;
    public int totalUnitsKnown;
    public boolean hasBeenAccepted;
    public char[] fileName;
    public boolean hasInconsistentToplevelHierarchies;
    public boolean hasSyntaxError;
    public char[][] packageName;
    public boolean checkSecondaryTypes;
    private int numberOfErrors;
    private boolean hasMandatoryErrors;
    private static final int[] EMPTY_LINE_ENDS;
    private static final Comparator PROBLEM_COMPARATOR;
    
    static {
        EMPTY_LINE_ENDS = Util.EMPTY_INT_ARRAY;
        PROBLEM_COMPARATOR = new Comparator() {
            @Override
            public int compare(final Object o1, final Object o2) {
                return ((CategorizedProblem)o1).getSourceStart() - ((CategorizedProblem)o2).getSourceStart();
            }
        };
    }
    
    public CompilationResult(final char[] fileName, final int unitIndex, final int totalUnitsKnown, final int maxProblemPerUnit) {
        this.hasAnnotations = false;
        this.hasFunctionalTypes = false;
        this.compiledTypes = new Hashtable(11);
        this.hasBeenAccepted = false;
        this.hasInconsistentToplevelHierarchies = false;
        this.hasSyntaxError = false;
        this.checkSecondaryTypes = false;
        this.fileName = fileName;
        this.unitIndex = unitIndex;
        this.totalUnitsKnown = totalUnitsKnown;
        this.maxProblemPerUnit = maxProblemPerUnit;
    }
    
    public CompilationResult(final ICompilationUnit compilationUnit, final int unitIndex, final int totalUnitsKnown, final int maxProblemPerUnit) {
        this.hasAnnotations = false;
        this.hasFunctionalTypes = false;
        this.compiledTypes = new Hashtable(11);
        this.hasBeenAccepted = false;
        this.hasInconsistentToplevelHierarchies = false;
        this.hasSyntaxError = false;
        this.checkSecondaryTypes = false;
        this.fileName = compilationUnit.getFileName();
        this.compilationUnit = compilationUnit;
        this.unitIndex = unitIndex;
        this.totalUnitsKnown = totalUnitsKnown;
        this.maxProblemPerUnit = maxProblemPerUnit;
    }
    
    private int computePriority(final CategorizedProblem problem) {
        int priority = 10000 - problem.getSourceLineNumber();
        if (priority < 0) {
            priority = 0;
        }
        if (problem.isError()) {
            priority += 100000;
        }
        final ReferenceContext context = (this.problemsMap == null) ? null : this.problemsMap.get(problem);
        if (context != null) {
            if (context instanceof AbstractMethodDeclaration) {
                final AbstractMethodDeclaration method = (AbstractMethodDeclaration)context;
                if (method.isStatic()) {
                    priority += 10000;
                }
            }
            else {
                priority += 40000;
            }
            if (this.firstErrors.contains(problem)) {
                priority += 20000;
            }
        }
        else {
            priority += 40000;
        }
        return priority;
    }
    
    public CategorizedProblem[] getAllProblems() {
        final CategorizedProblem[] onlyProblems = this.getProblems();
        final int onlyProblemCount = (onlyProblems != null) ? onlyProblems.length : 0;
        final CategorizedProblem[] onlyTasks = this.getTasks();
        final int onlyTaskCount = (onlyTasks != null) ? onlyTasks.length : 0;
        if (onlyTaskCount == 0) {
            return onlyProblems;
        }
        if (onlyProblemCount == 0) {
            return onlyTasks;
        }
        final int totalNumberOfProblem = onlyProblemCount + onlyTaskCount;
        final CategorizedProblem[] allProblems = new CategorizedProblem[totalNumberOfProblem];
        int allProblemIndex = 0;
        int taskIndex = 0;
        int problemIndex = 0;
        while (taskIndex + problemIndex < totalNumberOfProblem) {
            CategorizedProblem nextTask = null;
            CategorizedProblem nextProblem = null;
            if (taskIndex < onlyTaskCount) {
                nextTask = onlyTasks[taskIndex];
            }
            if (problemIndex < onlyProblemCount) {
                nextProblem = onlyProblems[problemIndex];
            }
            CategorizedProblem currentProblem = null;
            if (nextProblem != null) {
                if (nextTask != null) {
                    if (nextProblem.getSourceStart() < nextTask.getSourceStart()) {
                        currentProblem = nextProblem;
                        ++problemIndex;
                    }
                    else {
                        currentProblem = nextTask;
                        ++taskIndex;
                    }
                }
                else {
                    currentProblem = nextProblem;
                    ++problemIndex;
                }
            }
            else if (nextTask != null) {
                currentProblem = nextTask;
                ++taskIndex;
            }
            allProblems[allProblemIndex++] = currentProblem;
        }
        return allProblems;
    }
    
    public ClassFile[] getClassFiles() {
        final ClassFile[] classFiles = new ClassFile[this.compiledTypes.size()];
        this.compiledTypes.values().toArray(classFiles);
        return classFiles;
    }
    
    public ICompilationUnit getCompilationUnit() {
        return this.compilationUnit;
    }
    
    public CategorizedProblem[] getErrors() {
        final CategorizedProblem[] reportedProblems = this.getProblems();
        int errorCount = 0;
        for (int i = 0; i < this.problemCount; ++i) {
            if (reportedProblems[i].isError()) {
                ++errorCount;
            }
        }
        if (errorCount == this.problemCount) {
            return reportedProblems;
        }
        final CategorizedProblem[] errors = new CategorizedProblem[errorCount];
        int index = 0;
        for (int j = 0; j < this.problemCount; ++j) {
            if (reportedProblems[j].isError()) {
                errors[index++] = reportedProblems[j];
            }
        }
        return errors;
    }
    
    public char[] getFileName() {
        return this.fileName;
    }
    
    public int[] getLineSeparatorPositions() {
        return (this.lineSeparatorPositions == null) ? CompilationResult.EMPTY_LINE_ENDS : this.lineSeparatorPositions;
    }
    
    public CategorizedProblem[] getProblems() {
        if (this.problems != null) {
            if (this.problemCount != this.problems.length) {
                System.arraycopy(this.problems, 0, this.problems = new CategorizedProblem[this.problemCount], 0, this.problemCount);
            }
            if (this.maxProblemPerUnit > 0 && this.problemCount > this.maxProblemPerUnit) {
                this.quickPrioritize(this.problems, 0, this.problemCount - 1);
                this.problemCount = this.maxProblemPerUnit;
                System.arraycopy(this.problems, 0, this.problems = new CategorizedProblem[this.problemCount], 0, this.problemCount);
            }
            Arrays.sort(this.problems, 0, this.problems.length, CompilationResult.PROBLEM_COMPARATOR);
        }
        return this.problems;
    }
    
    public CategorizedProblem[] getCUProblems() {
        if (this.problems != null) {
            CategorizedProblem[] filteredProblems = new CategorizedProblem[this.problemCount];
            int keep = 0;
            for (int i = 0; i < this.problemCount; ++i) {
                final CategorizedProblem problem = this.problems[i];
                if (problem.getID() != 536871825) {
                    filteredProblems[keep++] = problem;
                }
                else if (this.compilationUnit != null && CharOperation.equals(this.compilationUnit.getMainTypeName(), TypeConstants.PACKAGE_INFO_NAME)) {
                    filteredProblems[keep++] = problem;
                }
            }
            if (keep < this.problemCount) {
                System.arraycopy(filteredProblems, 0, filteredProblems = new CategorizedProblem[keep], 0, keep);
                this.problemCount = keep;
            }
            this.problems = filteredProblems;
            if (this.maxProblemPerUnit > 0 && this.problemCount > this.maxProblemPerUnit) {
                this.quickPrioritize(this.problems, 0, this.problemCount - 1);
                this.problemCount = this.maxProblemPerUnit;
                System.arraycopy(this.problems, 0, this.problems = new CategorizedProblem[this.problemCount], 0, this.problemCount);
            }
            Arrays.sort(this.problems, 0, this.problems.length, CompilationResult.PROBLEM_COMPARATOR);
        }
        return this.problems;
    }
    
    public CategorizedProblem[] getTasks() {
        if (this.tasks != null) {
            if (this.taskCount != this.tasks.length) {
                System.arraycopy(this.tasks, 0, this.tasks = new CategorizedProblem[this.taskCount], 0, this.taskCount);
            }
            Arrays.sort(this.tasks, 0, this.tasks.length, CompilationResult.PROBLEM_COMPARATOR);
        }
        return this.tasks;
    }
    
    public boolean hasErrors() {
        return this.numberOfErrors != 0;
    }
    
    public boolean hasMandatoryErrors() {
        return this.hasMandatoryErrors;
    }
    
    public boolean hasProblems() {
        return this.problemCount != 0;
    }
    
    public boolean hasTasks() {
        return this.taskCount != 0;
    }
    
    public boolean hasWarnings() {
        if (this.problems != null) {
            for (int i = 0; i < this.problemCount; ++i) {
                if (this.problems[i].isWarning()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void quickPrioritize(final CategorizedProblem[] problemList, int left, int right) {
        if (left >= right) {
            return;
        }
        final int original_left = left;
        final int original_right = right;
        final int mid = this.computePriority(problemList[left + (right - left) / 2]);
        while (true) {
            if (this.computePriority(problemList[right]) >= mid) {
                while (mid < this.computePriority(problemList[left])) {
                    ++left;
                }
                if (left <= right) {
                    final CategorizedProblem tmp = problemList[left];
                    problemList[left] = problemList[right];
                    problemList[right] = tmp;
                    ++left;
                    --right;
                }
                if (left > right) {
                    break;
                }
                continue;
            }
            else {
                --right;
            }
        }
        if (original_left < right) {
            this.quickPrioritize(problemList, original_left, right);
        }
        if (left < original_right) {
            this.quickPrioritize(problemList, left, original_right);
        }
    }
    
    public void recordPackageName(final char[][] packName) {
        this.packageName = packName;
    }
    
    public void record(final CategorizedProblem newProblem, final ReferenceContext referenceContext) {
        this.record(newProblem, referenceContext, true);
    }
    
    public void record(final CategorizedProblem newProblem, final ReferenceContext referenceContext, final boolean mandatoryError) {
        if (newProblem.getID() == 536871362) {
            this.recordTask(newProblem);
            return;
        }
        if (this.problemCount == 0) {
            this.problems = new CategorizedProblem[5];
        }
        else if (this.problemCount == this.problems.length) {
            System.arraycopy(this.problems, 0, this.problems = new CategorizedProblem[this.problemCount * 2], 0, this.problemCount);
        }
        this.problems[this.problemCount++] = newProblem;
        if (referenceContext != null) {
            if (this.problemsMap == null) {
                this.problemsMap = new HashMap<CategorizedProblem, ReferenceContext>(5);
            }
            if (this.firstErrors == null) {
                this.firstErrors = new HashSet(5);
            }
            if (newProblem.isError() && !referenceContext.hasErrors()) {
                this.firstErrors.add(newProblem);
            }
            this.problemsMap.put(newProblem, referenceContext);
        }
        if (newProblem.isError()) {
            ++this.numberOfErrors;
            if (mandatoryError) {
                this.hasMandatoryErrors = true;
            }
            if ((newProblem.getID() & 0x40000000) != 0x0) {
                this.hasSyntaxError = true;
            }
        }
    }
    
    ReferenceContext getContext(final CategorizedProblem problem) {
        if (problem != null) {
            return this.problemsMap.get(problem);
        }
        return null;
    }
    
    public void record(final char[] typeName, final ClassFile classFile) {
        final SourceTypeBinding sourceType = classFile.referenceBinding;
        if (!sourceType.isLocalType() && sourceType.isHierarchyInconsistent()) {
            this.hasInconsistentToplevelHierarchies = true;
        }
        this.compiledTypes.put(typeName, classFile);
    }
    
    private void recordTask(final CategorizedProblem newProblem) {
        if (this.taskCount == 0) {
            this.tasks = new CategorizedProblem[5];
        }
        else if (this.taskCount == this.tasks.length) {
            System.arraycopy(this.tasks, 0, this.tasks = new CategorizedProblem[this.taskCount * 2], 0, this.taskCount);
        }
        this.tasks[this.taskCount++] = newProblem;
    }
    
    public void removeProblem(final CategorizedProblem problem) {
        if (this.problemsMap != null) {
            this.problemsMap.remove(problem);
        }
        if (this.firstErrors != null) {
            this.firstErrors.remove(problem);
        }
        if (problem.isError()) {
            --this.numberOfErrors;
        }
        --this.problemCount;
    }
    
    public CompilationResult tagAsAccepted() {
        this.hasBeenAccepted = true;
        this.problemsMap = null;
        this.firstErrors = null;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        if (this.fileName != null) {
            buffer.append("Filename : ").append(this.fileName).append('\n');
        }
        if (this.compiledTypes != null) {
            buffer.append("COMPILED type(s)\t\n");
            for (final char[] typeName : this.compiledTypes.keySet()) {
                buffer.append("\t - ").append(typeName).append('\n');
            }
        }
        else {
            buffer.append("No COMPILED type\n");
        }
        if (this.problems != null) {
            buffer.append(this.problemCount).append(" PROBLEM(s) detected \n");
            for (int i = 0; i < this.problemCount; ++i) {
                buffer.append("\t - ").append(this.problems[i]).append('\n');
            }
        }
        else {
            buffer.append("No PROBLEM\n");
        }
        return buffer.toString();
    }
}
