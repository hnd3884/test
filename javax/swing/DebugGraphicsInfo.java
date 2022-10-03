package javax.swing;

import java.io.PrintStream;
import java.util.Hashtable;
import java.awt.Color;

class DebugGraphicsInfo
{
    Color flashColor;
    int flashTime;
    int flashCount;
    Hashtable<JComponent, Integer> componentToDebug;
    JFrame debugFrame;
    PrintStream stream;
    
    DebugGraphicsInfo() {
        this.flashColor = Color.red;
        this.flashTime = 100;
        this.flashCount = 2;
        this.debugFrame = null;
        this.stream = System.out;
    }
    
    void setDebugOptions(final JComponent component, final int n) {
        if (n == 0) {
            return;
        }
        if (this.componentToDebug == null) {
            this.componentToDebug = new Hashtable<JComponent, Integer>();
        }
        if (n > 0) {
            this.componentToDebug.put(component, n);
        }
        else {
            this.componentToDebug.remove(component);
        }
    }
    
    int getDebugOptions(final JComponent component) {
        if (this.componentToDebug == null) {
            return 0;
        }
        final Integer n = this.componentToDebug.get(component);
        return (n == null) ? 0 : n;
    }
    
    void log(final String s) {
        this.stream.println(s);
    }
}
