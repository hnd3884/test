package org.eclipse.jdt.internal.compiler.parser.diagnose;

import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;

public class RangeUtil
{
    public static final int NO_FLAG = 0;
    public static final int LBRACE_MISSING = 1;
    public static final int IGNORE = 2;
    
    public static boolean containsErrorInSignature(final AbstractMethodDeclaration method) {
        return method.sourceEnd + 1 == method.bodyStart || method.bodyEnd == method.declarationSourceEnd;
    }
    
    public static int[][] computeDietRange(final TypeDeclaration[] types) {
        if (types == null || types.length == 0) {
            return new int[3][0];
        }
        final RangeResult result = new RangeResult();
        computeDietRange0(types, result);
        return result.getRanges();
    }
    
    private static void computeDietRange0(final TypeDeclaration[] types, final RangeResult result) {
        for (int j = 0; j < types.length; ++j) {
            final TypeDeclaration[] memberTypeDeclarations = types[j].memberTypes;
            if (memberTypeDeclarations != null && memberTypeDeclarations.length > 0) {
                computeDietRange0(types[j].memberTypes, result);
            }
            final AbstractMethodDeclaration[] methods = types[j].methods;
            if (methods != null) {
                for (final AbstractMethodDeclaration method : methods) {
                    if (containsIgnoredBody(method)) {
                        if (containsErrorInSignature(method)) {
                            final AbstractMethodDeclaration abstractMethodDeclaration = method;
                            abstractMethodDeclaration.bits |= 0x20;
                            result.addInterval(method.declarationSourceStart, method.declarationSourceEnd, 2);
                        }
                        else {
                            final int flags = (method.sourceEnd + 1 == method.bodyStart) ? 1 : 0;
                            result.addInterval(method.bodyStart, method.bodyEnd, flags);
                        }
                    }
                }
            }
            final FieldDeclaration[] fields = types[j].fields;
            if (fields != null) {
                for (int length2 = fields.length, k = 0; k < length2; ++k) {
                    if (fields[k] instanceof Initializer) {
                        final Initializer initializer = (Initializer)fields[k];
                        if (initializer.declarationSourceEnd == initializer.bodyEnd && initializer.declarationSourceStart != initializer.declarationSourceEnd) {
                            final Initializer initializer2 = initializer;
                            initializer2.bits |= 0x20;
                            result.addInterval(initializer.declarationSourceStart, initializer.declarationSourceEnd, 2);
                        }
                        else {
                            result.addInterval(initializer.bodyStart, initializer.bodyEnd);
                        }
                    }
                }
            }
        }
    }
    
    public static boolean containsIgnoredBody(final AbstractMethodDeclaration method) {
        return !method.isDefaultConstructor() && !method.isClinit() && (method.modifiers & 0x1000000) == 0x0;
    }
    
    static class RangeResult
    {
        private static final int INITIAL_SIZE = 10;
        int pos;
        int[] intervalStarts;
        int[] intervalEnds;
        int[] intervalFlags;
        
        RangeResult() {
            this.pos = 0;
            this.intervalStarts = new int[10];
            this.intervalEnds = new int[10];
            this.intervalFlags = new int[10];
        }
        
        void addInterval(final int start, final int end) {
            this.addInterval(start, end, 0);
        }
        
        void addInterval(final int start, final int end, final int flags) {
            if (this.pos >= this.intervalStarts.length) {
                System.arraycopy(this.intervalStarts, 0, this.intervalStarts = new int[this.pos * 2], 0, this.pos);
                System.arraycopy(this.intervalEnds, 0, this.intervalEnds = new int[this.pos * 2], 0, this.pos);
                System.arraycopy(this.intervalFlags, 0, this.intervalFlags = new int[this.pos * 2], 0, this.pos);
            }
            this.intervalStarts[this.pos] = start;
            this.intervalEnds[this.pos] = end;
            this.intervalFlags[this.pos] = flags;
            ++this.pos;
        }
        
        int[][] getRanges() {
            final int[] resultStarts = new int[this.pos];
            final int[] resultEnds = new int[this.pos];
            final int[] resultFlags = new int[this.pos];
            System.arraycopy(this.intervalStarts, 0, resultStarts, 0, this.pos);
            System.arraycopy(this.intervalEnds, 0, resultEnds, 0, this.pos);
            System.arraycopy(this.intervalFlags, 0, resultFlags, 0, this.pos);
            if (resultStarts.length > 1) {
                this.quickSort(resultStarts, resultEnds, resultFlags, 0, resultStarts.length - 1);
            }
            return new int[][] { resultStarts, resultEnds, resultFlags };
        }
        
        private void quickSort(final int[] list, final int[] list2, final int[] list3, int left, int right) {
            final int original_left = left;
            final int original_right = right;
            final int mid = list[left + (right - left) / 2];
            while (true) {
                if (this.compare(list[left], mid) >= 0) {
                    while (this.compare(mid, list[right]) < 0) {
                        --right;
                    }
                    if (left <= right) {
                        int tmp = list[left];
                        list[left] = list[right];
                        list[right] = tmp;
                        tmp = list2[left];
                        list2[left] = list2[right];
                        list2[right] = tmp;
                        tmp = list3[left];
                        list3[left] = list3[right];
                        list3[right] = tmp;
                        ++left;
                        --right;
                    }
                    if (left > right) {
                        break;
                    }
                    continue;
                }
                else {
                    ++left;
                }
            }
            if (original_left < right) {
                this.quickSort(list, list2, list3, original_left, right);
            }
            if (left < original_right) {
                this.quickSort(list, list2, list3, left, original_right);
            }
        }
        
        private int compare(final int i1, final int i2) {
            return i1 - i2;
        }
    }
}
