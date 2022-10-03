package sun.awt;

import java.nio.charset.Charset;
import sun.nio.cs.ext.MS950_HKSCS_XP;

public class HKSCS extends MS950_HKSCS_XP
{
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof HKSCS;
    }
}
