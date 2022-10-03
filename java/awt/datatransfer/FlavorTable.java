package java.awt.datatransfer;

import java.util.List;

public interface FlavorTable extends FlavorMap
{
    List<String> getNativesForFlavor(final DataFlavor p0);
    
    List<DataFlavor> getFlavorsForNative(final String p0);
}
