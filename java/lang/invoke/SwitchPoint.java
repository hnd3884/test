package java.lang.invoke;

public class SwitchPoint
{
    private static final MethodHandle K_true;
    private static final MethodHandle K_false;
    private final MutableCallSite mcs;
    private final MethodHandle mcsInvoker;
    
    public SwitchPoint() {
        this.mcs = new MutableCallSite(SwitchPoint.K_true);
        this.mcsInvoker = this.mcs.dynamicInvoker();
    }
    
    public boolean hasBeenInvalidated() {
        return this.mcs.getTarget() != SwitchPoint.K_true;
    }
    
    public MethodHandle guardWithTest(final MethodHandle methodHandle, final MethodHandle methodHandle2) {
        if (this.mcs.getTarget() == SwitchPoint.K_false) {
            return methodHandle2;
        }
        return MethodHandles.guardWithTest(this.mcsInvoker, methodHandle, methodHandle2);
    }
    
    public static void invalidateAll(final SwitchPoint[] array) {
        if (array.length == 0) {
            return;
        }
        final MutableCallSite[] array2 = new MutableCallSite[array.length];
        for (int i = 0; i < array.length; ++i) {
            final SwitchPoint switchPoint = array[i];
            if (switchPoint == null) {
                break;
            }
            array2[i] = switchPoint.mcs;
            switchPoint.mcs.setTarget(SwitchPoint.K_false);
        }
        MutableCallSite.syncAll(array2);
    }
    
    static {
        K_true = MethodHandles.constant(Boolean.TYPE, true);
        K_false = MethodHandles.constant(Boolean.TYPE, false);
    }
}
