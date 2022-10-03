package com.adventnet.tools.update.installer;

import java.io.File;

public final class DiskSpace
{
    private static DiskSpace space;
    
    public static DiskSpace getInstance() {
        if (DiskSpace.space == null) {
            DiskSpace.space = new DiskSpace();
        }
        return DiskSpace.space;
    }
    
    public long getFreeSpace(final String drive) {
        try {
            final File fl = new File(drive);
            return fl.getUsableSpace();
        }
        catch (final Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    static {
        DiskSpace.space = null;
    }
}
