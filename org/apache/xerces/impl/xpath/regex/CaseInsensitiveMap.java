package org.apache.xerces.impl.xpath.regex;

final class CaseInsensitiveMap
{
    private static int CHUNK_SHIFT;
    private static int CHUNK_SIZE;
    private static int CHUNK_MASK;
    private static int INITIAL_CHUNK_COUNT;
    private static int[][][] caseInsensitiveMap;
    private static int LOWER_CASE_MATCH;
    private static int UPPER_CASE_MATCH;
    
    public static int[] get(final int n) {
        return (int[])((n < 65536) ? getMapping(n) : null);
    }
    
    private static int[] getMapping(final int n) {
        return CaseInsensitiveMap.caseInsensitiveMap[n >>> CaseInsensitiveMap.CHUNK_SHIFT][n & CaseInsensitiveMap.CHUNK_MASK];
    }
    
    private static void buildCaseInsensitiveMap() {
        CaseInsensitiveMap.caseInsensitiveMap = new int[CaseInsensitiveMap.INITIAL_CHUNK_COUNT][CaseInsensitiveMap.CHUNK_SIZE][];
        for (int i = '\0'; i < 65536; ++i) {
            final char lowerCase = Character.toLowerCase((char)i);
            final char upperCase = Character.toUpperCase((char)i);
            if (lowerCase != upperCase || lowerCase != i) {
                int[] array = new int[2];
                int n = 0;
                if (lowerCase != i) {
                    array[n++] = lowerCase;
                    array[n++] = CaseInsensitiveMap.LOWER_CASE_MATCH;
                    final int[] mapping = getMapping(lowerCase);
                    if (mapping != null) {
                        array = updateMap(i, array, lowerCase, mapping, CaseInsensitiveMap.LOWER_CASE_MATCH);
                    }
                }
                if (upperCase != i) {
                    if (n == array.length) {
                        array = expandMap(array, 2);
                    }
                    array[n++] = upperCase;
                    array[n++] = CaseInsensitiveMap.UPPER_CASE_MATCH;
                    final int[] mapping2 = getMapping(upperCase);
                    if (mapping2 != null) {
                        array = updateMap(i, array, upperCase, mapping2, CaseInsensitiveMap.UPPER_CASE_MATCH);
                    }
                }
                set(i, array);
            }
        }
    }
    
    private static int[] expandMap(final int[] array, final int n) {
        final int length = array.length;
        final int[] array2 = new int[length + n];
        System.arraycopy(array, 0, array2, 0, length);
        return array2;
    }
    
    private static void set(final int n, final int[] array) {
        CaseInsensitiveMap.caseInsensitiveMap[n >>> CaseInsensitiveMap.CHUNK_SHIFT][n & CaseInsensitiveMap.CHUNK_MASK] = array;
    }
    
    private static int[] updateMap(final int n, int[] expandAndAdd, final int n2, int[] expandAndAdd2, final int n3) {
        for (int i = 0; i < expandAndAdd2.length; i += 2) {
            final int n4 = expandAndAdd2[i];
            final int[] mapping = getMapping(n4);
            if (mapping != null && contains(mapping, n2, n3)) {
                if (!contains(mapping, n)) {
                    set(n4, expandAndAdd(mapping, n, n3));
                }
                if (!contains(expandAndAdd, n4)) {
                    expandAndAdd = expandAndAdd(expandAndAdd, n4, n3);
                }
            }
        }
        if (!contains(expandAndAdd2, n)) {
            expandAndAdd2 = expandAndAdd(expandAndAdd2, n, n3);
            set(n2, expandAndAdd2);
        }
        return expandAndAdd;
    }
    
    private static boolean contains(final int[] array, final int n) {
        for (int i = 0; i < array.length; i += 2) {
            if (array[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean contains(final int[] array, final int n, final int n2) {
        for (int i = 0; i < array.length; i += 2) {
            if (array[i] == n && array[i + 1] == n2) {
                return true;
            }
        }
        return false;
    }
    
    private static int[] expandAndAdd(final int[] array, final int n, final int n2) {
        final int length = array.length;
        final int[] array2 = new int[length + 2];
        System.arraycopy(array, 0, array2, 0, length);
        array2[length] = n;
        array2[length + 1] = n2;
        return array2;
    }
    
    static {
        CaseInsensitiveMap.CHUNK_SHIFT = 10;
        CaseInsensitiveMap.CHUNK_SIZE = 1 << CaseInsensitiveMap.CHUNK_SHIFT;
        CaseInsensitiveMap.CHUNK_MASK = CaseInsensitiveMap.CHUNK_SIZE - 1;
        CaseInsensitiveMap.INITIAL_CHUNK_COUNT = 64;
        CaseInsensitiveMap.LOWER_CASE_MATCH = 1;
        CaseInsensitiveMap.UPPER_CASE_MATCH = 2;
        buildCaseInsensitiveMap();
    }
}
