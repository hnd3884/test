package org.apache.catalina;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Request;

public interface Authenticator
{
    boolean authenticate(final Request p0, final HttpServletResponse p1) throws IOException;
    
    void login(final String p0, final String p1, final Request p2) throws ServletException;
    
    void logout(final Request p0);
}
