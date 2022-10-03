package sun.tracing;

import java.util.Iterator;
import java.util.HashSet;
import com.sun.tracing.Provider;
import java.lang.reflect.Method;
import com.sun.tracing.Probe;
import java.util.Set;

class MultiplexProbe extends ProbeSkeleton
{
    private Set<Probe> probes;
    
    MultiplexProbe(final Method method, final Set<Provider> set) {
        super(method.getParameterTypes());
        this.probes = new HashSet<Probe>();
        final Iterator<Provider> iterator = set.iterator();
        while (iterator.hasNext()) {
            final Probe probe = iterator.next().getProbe(method);
            if (probe != null) {
                this.probes.add(probe);
            }
        }
    }
    
    @Override
    public boolean isEnabled() {
        final Iterator<Probe> iterator = this.probes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isEnabled()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void uncheckedTrigger(final Object[] array) {
        for (final Probe probe : this.probes) {
            try {
                ((ProbeSkeleton)probe).uncheckedTrigger(array);
            }
            catch (final ClassCastException ex) {
                try {
                    Probe.class.getMethod("trigger", Class.forName("[java.lang.Object")).invoke(probe, array);
                }
                catch (final Exception ex2) {
                    assert false;
                    continue;
                }
            }
        }
    }
}
