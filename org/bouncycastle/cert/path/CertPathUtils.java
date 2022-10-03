package org.bouncycastle.cert.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.cert.X509CertificateHolder;

class CertPathUtils
{
    static Set getCriticalExtensionsOIDs(final X509CertificateHolder[] array) {
        final HashSet set = new HashSet();
        for (int i = 0; i != array.length; ++i) {
            set.addAll(array[i].getCriticalExtensionOIDs());
        }
        return set;
    }
}
