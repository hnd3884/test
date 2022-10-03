package org.bouncycastle.jce.interfaces;

import org.bouncycastle.math.ec.ECPoint;
import java.security.PublicKey;

public interface ECPublicKey extends ECKey, PublicKey
{
    ECPoint getQ();
}
