package com.adventnet.tools.update;

import java.util.HashMap;
import java.util.Map;

public class PatchesInfoHolder
{
    private static Map<String, Boolean> patchInfoHolder;
    
    public static void addPatchInfo(final String patchName, final boolean isHotSwapped) {
        if (!PatchesInfoHolder.patchInfoHolder.containsKey(patchName)) {
            PatchesInfoHolder.patchInfoHolder.put(patchName, isHotSwapped);
        }
    }
    
    public static boolean isHotSwappablePatch(final String patchName) {
        return PatchesInfoHolder.patchInfoHolder.containsKey(patchName) && PatchesInfoHolder.patchInfoHolder.get(patchName);
    }
    
    static {
        PatchesInfoHolder.patchInfoHolder = new HashMap<String, Boolean>();
    }
}
