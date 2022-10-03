package org.apache.tomcat.jdbc.pool;

import java.sql.Connection;

public interface Validator
{
    boolean validate(final Connection p0, final int p1);
}
