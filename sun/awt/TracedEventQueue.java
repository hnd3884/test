package sun.awt;

import java.util.StringTokenizer;
import java.awt.Toolkit;
import java.awt.AWTEvent;
import java.awt.EventQueue;

public class TracedEventQueue extends EventQueue
{
    static boolean trace;
    static int[] suppressedIDs;
    
    @Override
    public void postEvent(final AWTEvent awtEvent) {
        boolean b = true;
        final int id = awtEvent.getID();
        for (int i = 0; i < TracedEventQueue.suppressedIDs.length; ++i) {
            if (id == TracedEventQueue.suppressedIDs[i]) {
                b = false;
                break;
            }
        }
        if (b) {
            System.out.println(Thread.currentThread().getName() + ": " + awtEvent);
        }
        super.postEvent(awtEvent);
    }
    
    static {
        TracedEventQueue.trace = false;
        TracedEventQueue.suppressedIDs = null;
        final String property = Toolkit.getProperty("AWT.IgnoreEventIDs", "");
        if (property.length() > 0) {
            final StringTokenizer stringTokenizer = new StringTokenizer(property, ",");
            final int countTokens = stringTokenizer.countTokens();
            TracedEventQueue.suppressedIDs = new int[countTokens];
            for (int i = 0; i < countTokens; ++i) {
                final String nextToken = stringTokenizer.nextToken();
                try {
                    TracedEventQueue.suppressedIDs[i] = Integer.parseInt(nextToken);
                }
                catch (final NumberFormatException ex) {
                    System.err.println("Bad ID listed in AWT.IgnoreEventIDs in awt.properties: \"" + nextToken + "\" -- skipped");
                    TracedEventQueue.suppressedIDs[i] = 0;
                }
            }
        }
        else {
            TracedEventQueue.suppressedIDs = new int[0];
        }
    }
}
