package com.microsoft.sqlserver.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

final class ByteArrayOutputStreamToInputStream extends ByteArrayOutputStream
{
    ByteArrayInputStream getInputStream() throws SQLServerException {
        final ByteArrayInputStream is = new ByteArrayInputStream(this.buf, 0, this.count);
        return is;
    }
}
