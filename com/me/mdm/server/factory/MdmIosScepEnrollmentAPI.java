package com.me.mdm.server.factory;

import java.io.InputStream;

public interface MdmIosScepEnrollmentAPI
{
    InputStream makeHttpRequest(final String p0, final String p1, final String p2, final byte[] p3);
}
