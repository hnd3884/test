package com.adventnet.tools.update.installer;

import java.util.List;
import java.nio.file.Path;

public interface PatchesCompatibilityVerifier
{
    void verifyPatch(final Path p0);
    
    boolean isHotSwappablePatch(final Path p0);
    
    List<String> getPatchesOrdered(final Path p0, final List<String> p1);
}
