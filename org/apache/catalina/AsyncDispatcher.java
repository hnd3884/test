package org.apache.catalina;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;

public interface AsyncDispatcher
{
    void dispatch(final ServletRequest p0, final ServletResponse p1) throws ServletException, IOException;
}
