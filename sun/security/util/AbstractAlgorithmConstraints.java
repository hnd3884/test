package sun.security.util;

import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import java.util.List;
import java.security.AlgorithmConstraints;

public abstract class AbstractAlgorithmConstraints implements AlgorithmConstraints
{
    protected final AlgorithmDecomposer decomposer;
    
    protected AbstractAlgorithmConstraints(final AlgorithmDecomposer decomposer) {
        this.decomposer = decomposer;
    }
    
    static List<String> getAlgorithms(final String s) {
        String substring = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Security.getProperty(s);
            }
        });
        String[] split = null;
        if (substring != null && !substring.isEmpty()) {
            if (substring.length() >= 2 && substring.charAt(0) == '\"' && substring.charAt(substring.length() - 1) == '\"') {
                substring = substring.substring(1, substring.length() - 1);
            }
            split = substring.split(",");
            for (int i = 0; i < split.length; ++i) {
                split[i] = split[i].trim();
            }
        }
        if (split == null) {
            return Collections.emptyList();
        }
        return new ArrayList<String>(Arrays.asList(split));
    }
    
    static boolean checkAlgorithm(final List<String> list, final String s, final AlgorithmDecomposer algorithmDecomposer) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("No algorithm name specified");
        }
        Set<String> decompose = null;
        for (final String s2 : list) {
            if (s2 != null) {
                if (s2.isEmpty()) {
                    continue;
                }
                if (s2.equalsIgnoreCase(s)) {
                    return false;
                }
                if (decompose == null) {
                    decompose = algorithmDecomposer.decompose(s);
                }
                final Iterator<String> iterator2 = decompose.iterator();
                while (iterator2.hasNext()) {
                    if (s2.equalsIgnoreCase(iterator2.next())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
