package sun.swing;

import javax.swing.SwingUtilities;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public abstract class AccumulativeRunnable<T> implements Runnable
{
    private List<T> arguments;
    
    public AccumulativeRunnable() {
        this.arguments = null;
    }
    
    protected abstract void run(final List<T> p0);
    
    @Override
    public final void run() {
        this.run(this.flush());
    }
    
    @SafeVarargs
    public final synchronized void add(final T... array) {
        boolean b = true;
        if (this.arguments == null) {
            b = false;
            this.arguments = new ArrayList<T>();
        }
        Collections.addAll(this.arguments, array);
        if (!b) {
            this.submit();
        }
    }
    
    protected void submit() {
        SwingUtilities.invokeLater(this);
    }
    
    private final synchronized List<T> flush() {
        final List<T> arguments = this.arguments;
        this.arguments = null;
        return arguments;
    }
}
