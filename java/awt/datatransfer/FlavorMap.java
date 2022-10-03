package java.awt.datatransfer;

import java.util.Map;

public interface FlavorMap
{
    Map<DataFlavor, String> getNativesForFlavors(final DataFlavor[] p0);
    
    Map<String, DataFlavor> getFlavorsForNatives(final String[] p0);
}
