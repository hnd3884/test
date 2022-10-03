package com.sun.org.apache.xerces.internal.impl.xpath.regex;

public class CaseInsensitiveMap
{
    private static int CHUNK_SHIFT;
    private static int CHUNK_SIZE;
    private static int CHUNK_MASK;
    private static int INITIAL_CHUNK_COUNT;
    private static int[][][] caseInsensitiveMap;
    private static Boolean mapBuilt;
    private static int LOWER_CASE_MATCH;
    private static int UPPER_CASE_MATCH;
    
    public static int[] get(final int codePoint) {
        if (CaseInsensitiveMap.mapBuilt == Boolean.FALSE) {
            synchronized (CaseInsensitiveMap.mapBuilt) {
                if (CaseInsensitiveMap.mapBuilt == Boolean.FALSE) {
                    buildCaseInsensitiveMap();
                }
            }
        }
        return (int[])((codePoint < 65536) ? getMapping(codePoint) : null);
    }
    
    private static int[] getMapping(final int codePoint) {
        final int chunk = codePoint >>> CaseInsensitiveMap.CHUNK_SHIFT;
        final int offset = codePoint & CaseInsensitiveMap.CHUNK_MASK;
        return CaseInsensitiveMap.caseInsensitiveMap[chunk][offset];
    }
    
    private static void buildCaseInsensitiveMap() {
        CaseInsensitiveMap.caseInsensitiveMap = new int[CaseInsensitiveMap.INITIAL_CHUNK_COUNT][][];
        for (int i = 0; i < CaseInsensitiveMap.INITIAL_CHUNK_COUNT; ++i) {
            CaseInsensitiveMap.caseInsensitiveMap[i] = new int[CaseInsensitiveMap.CHUNK_SIZE][];
        }
        for (int j = 0; j < 65536; ++j) {
            final int lc = Character.toLowerCase(j);
            final int uc = Character.toUpperCase(j);
            if (lc != uc || lc != j) {
                int[] map = new int[2];
                int index = 0;
                if (lc != j) {
                    map[index++] = lc;
                    map[index++] = CaseInsensitiveMap.LOWER_CASE_MATCH;
                    final int[] lcMap = getMapping(lc);
                    if (lcMap != null) {
                        map = updateMap(j, map, lc, lcMap, CaseInsensitiveMap.LOWER_CASE_MATCH);
                    }
                }
                if (uc != j) {
                    if (index == map.length) {
                        map = expandMap(map, 2);
                    }
                    map[index++] = uc;
                    map[index++] = CaseInsensitiveMap.UPPER_CASE_MATCH;
                    final int[] ucMap = getMapping(uc);
                    if (ucMap != null) {
                        map = updateMap(j, map, uc, ucMap, CaseInsensitiveMap.UPPER_CASE_MATCH);
                    }
                }
                set(j, map);
            }
        }
        CaseInsensitiveMap.mapBuilt = Boolean.TRUE;
    }
    
    private static int[] expandMap(final int[] srcMap, final int expandBy) {
        final int oldLen = srcMap.length;
        final int[] newMap = new int[oldLen + expandBy];
        System.arraycopy(srcMap, 0, newMap, 0, oldLen);
        return newMap;
    }
    
    private static void set(final int codePoint, final int[] map) {
        final int chunk = codePoint >>> CaseInsensitiveMap.CHUNK_SHIFT;
        final int offset = codePoint & CaseInsensitiveMap.CHUNK_MASK;
        CaseInsensitiveMap.caseInsensitiveMap[chunk][offset] = map;
    }
    
    private static int[] updateMap(final int codePoint, int[] codePointMap, final int ciCodePoint, int[] ciCodePointMap, final int matchType) {
        for (int i = 0; i < ciCodePointMap.length; i += 2) {
            final int c = ciCodePointMap[i];
            int[] cMap = getMapping(c);
            if (cMap != null && contains(cMap, ciCodePoint, matchType)) {
                if (!contains(cMap, codePoint)) {
                    cMap = expandAndAdd(cMap, codePoint, matchType);
                    set(c, cMap);
                }
                if (!contains(codePointMap, c)) {
                    codePointMap = expandAndAdd(codePointMap, c, matchType);
                }
            }
        }
        if (!contains(ciCodePointMap, codePoint)) {
            ciCodePointMap = expandAndAdd(ciCodePointMap, codePoint, matchType);
            set(ciCodePoint, ciCodePointMap);
        }
        return codePointMap;
    }
    
    private static boolean contains(final int[] map, final int codePoint) {
        for (int i = 0; i < map.length; i += 2) {
            if (map[i] == codePoint) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean contains(final int[] map, final int codePoint, final int matchType) {
        for (int i = 0; i < map.length; i += 2) {
            if (map[i] == codePoint && map[i + 1] == matchType) {
                return true;
            }
        }
        return false;
    }
    
    private static int[] expandAndAdd(final int[] srcMap, final int codePoint, final int matchType) {
        final int oldLen = srcMap.length;
        final int[] newMap = new int[oldLen + 2];
        System.arraycopy(srcMap, 0, newMap, 0, oldLen);
        newMap[oldLen] = codePoint;
        newMap[oldLen + 1] = matchType;
        return newMap;
    }
    
    static {
        CaseInsensitiveMap.CHUNK_SHIFT = 10;
        CaseInsensitiveMap.CHUNK_SIZE = 1 << CaseInsensitiveMap.CHUNK_SHIFT;
        CaseInsensitiveMap.CHUNK_MASK = CaseInsensitiveMap.CHUNK_SIZE - 1;
        CaseInsensitiveMap.INITIAL_CHUNK_COUNT = 64;
        CaseInsensitiveMap.mapBuilt = Boolean.FALSE;
        CaseInsensitiveMap.LOWER_CASE_MATCH = 1;
        CaseInsensitiveMap.UPPER_CASE_MATCH = 2;
    }
}
