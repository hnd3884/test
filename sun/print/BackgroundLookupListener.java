package sun.print;

import javax.print.PrintService;

public interface BackgroundLookupListener
{
    void notifyServices(final PrintService[] p0);
}
