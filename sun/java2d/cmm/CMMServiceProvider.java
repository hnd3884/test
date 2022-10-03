package sun.java2d.cmm;

public abstract class CMMServiceProvider
{
    public final PCMM getColorManagementModule() {
        if (CMSManager.canCreateModule()) {
            return this.getModule();
        }
        return null;
    }
    
    protected abstract PCMM getModule();
}
