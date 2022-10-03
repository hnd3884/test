package com.adventnet.tools.update.installer;

import com.adventnet.tools.update.XmlData;

public interface PatchValidator
{
    boolean allowPatchUpgrade(final XmlData p0);
}
