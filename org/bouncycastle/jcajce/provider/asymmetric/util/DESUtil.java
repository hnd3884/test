package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.util.HashSet;
import org.bouncycastle.util.Strings;
import java.util.Set;

public class DESUtil
{
    private static final Set<String> des;
    
    public static boolean isDES(final String s) {
        return DESUtil.des.contains(Strings.toUpperCase(s));
    }
    
    public static void setOddParity(final byte[] array) {
        for (int i = 0; i < array.length; ++i) {
            final byte b = array[i];
            array[i] = (byte)((b & 0xFE) | ((b >> 1 ^ b >> 2 ^ b >> 3 ^ b >> 4 ^ b >> 5 ^ b >> 6 ^ b >> 7 ^ 0x1) & 0x1));
        }
    }
    
    static {
        (des = new HashSet<String>()).add("DES");
        DESUtil.des.add("DESEDE");
        DESUtil.des.add(OIWObjectIdentifiers.desCBC.getId());
        DESUtil.des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
        DESUtil.des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
        DESUtil.des.add(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId());
    }
}
