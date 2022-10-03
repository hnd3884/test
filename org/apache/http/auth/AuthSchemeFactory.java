package org.apache.http.auth;

import org.apache.http.params.HttpParams;

@Deprecated
public interface AuthSchemeFactory
{
    AuthScheme newInstance(final HttpParams p0);
}
