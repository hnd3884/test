package org.bouncycastle.est.jcajce;

import java.io.IOException;
import javax.net.ssl.SSLSession;

public interface JsseHostnameAuthorizer
{
    boolean verified(final String p0, final SSLSession p1) throws IOException;
}
