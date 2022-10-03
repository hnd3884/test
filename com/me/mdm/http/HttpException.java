package com.me.mdm.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

public class HttpException extends HTTPException
{
    String message;
    
    public HttpException(final int httpErrorCode, final String errorMessage) {
        super(httpErrorCode);
        this.message = errorMessage;
    }
    
    public void setErrorResponse(final HttpServletResponse response) throws IOException {
        response.setStatus(this.getStatusCode());
        if (!MDMStringUtils.isEmpty(this.message)) {
            final JSONObject json = new JSONObject();
            final JSONObject errorDetails = new JSONObject();
            try {
                errorDetails.put("error_description", (Object)this.message);
                json.put("error", (Object)this.message);
                response.setHeader("Content-Type", "application/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                final PrintWriter pout = response.getWriter();
                pout.print(json.toString());
            }
            catch (final Exception ex) {
                Logger.getLogger(HttpException.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
