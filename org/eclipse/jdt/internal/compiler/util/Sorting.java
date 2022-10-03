package org.eclipse.jdt.internal.compiler.util;

import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class Sorting
{
    public static ReferenceBinding[] sortTypes(final ReferenceBinding[] types) {
        final int len = types.length;
        final ReferenceBinding[] unsorted = new ReferenceBinding[len];
        final ReferenceBinding[] sorted = new ReferenceBinding[len];
        System.arraycopy(types, 0, unsorted, 0, len);
        int o = 0;
        for (int i = 0; i < len; ++i) {
            o = sort(unsorted, i, sorted, o);
        }
        return sorted;
    }
    
    private static int sort(final ReferenceBinding[] input, final int i, final ReferenceBinding[] output, int o) {
        if (input[i] == null) {
            return o;
        }
        final ReferenceBinding superclass = input[i].superclass();
        o = sortSuper(superclass, input, output, o);
        final ReferenceBinding[] superInterfaces = input[i].superInterfaces();
        for (int j = 0; j < superInterfaces.length; ++j) {
            o = sortSuper(superInterfaces[j], input, output, o);
        }
        output[o++] = input[i];
        input[i] = null;
        return o;
    }
    
    private static int sortSuper(final ReferenceBinding superclass, final ReferenceBinding[] input, final ReferenceBinding[] output, int o) {
        if (superclass.id != 1) {
            int j;
            for (j = 0, j = 0; j < input.length && !TypeBinding.equalsEquals(input[j], superclass); ++j) {}
            if (j < input.length) {
                o = sort(input, j, output, o);
            }
        }
        return o;
    }
    
    public static MethodBinding[] concreteFirst(final MethodBinding[] methods, final int length) {
        if (length == 0 || (length > 0 && !methods[0].isAbstract())) {
            return methods;
        }
        final MethodBinding[] copy = new MethodBinding[length];
        int idx = 0;
        for (int i = 0; i < length; ++i) {
            if (!methods[i].isAbstract()) {
                copy[idx++] = methods[i];
            }
        }
        for (int i = 0; i < length; ++i) {
            if (methods[i].isAbstract()) {
                copy[idx++] = methods[i];
            }
        }
        return copy;
    }
    
    public static MethodBinding[] abstractFirst(final MethodBinding[] methods, final int length) {
        if (length == 0 || (length > 0 && methods[0].isAbstract())) {
            return methods;
        }
        final MethodBinding[] copy = new MethodBinding[length];
        int idx = 0;
        for (int i = 0; i < length; ++i) {
            if (methods[i].isAbstract()) {
                copy[idx++] = methods[i];
            }
        }
        for (int i = 0; i < length; ++i) {
            if (!methods[i].isAbstract()) {
                copy[idx++] = methods[i];
            }
        }
        return copy;
    }
    
    public static void sortInferenceVariables(final InferenceVariable[] variables) {
        Arrays.sort(variables, new Comparator<InferenceVariable>() {
            @Override
            public int compare(final InferenceVariable iv1, final InferenceVariable iv2) {
                return iv1.rank - iv2.rank;
            }
        });
    }
}
