package sun.java2d.cmm.lcms;

import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.CMMServiceProvider;

public final class LcmsServiceProvider extends CMMServiceProvider
{
    @Override
    protected PCMM getModule() {
        return LCMS.getModule();
    }
}
