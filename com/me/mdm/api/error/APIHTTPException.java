package com.me.mdm.api.error;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import javax.xml.ws.http.HTTPException;

public class APIHTTPException extends HTTPException
{
    APIError message;
    
    public APIHTTPException(final int code, final String errorCode, final Object... args) {
        super(APIErrorUtil.getInstance().getHttpStatusCode(errorCode));
        this.message = null;
        this.message = new APIError(errorCode, args);
    }
    
    public APIHTTPException(final String errorCode, final Object... args) {
        super(APIErrorUtil.getInstance().getHttpStatusCode(errorCode));
        this.message = null;
        this.message = new APIError(errorCode, args);
    }
    
    public APIHTTPException(final JSONObject customErrorParams, final String errorCode, final Object... args) {
        super(APIErrorUtil.getInstance().getHttpStatusCode(errorCode));
        this.message = null;
        this.message = new APIError(customErrorParams, errorCode, args);
    }
    
    public APIHTTPException(final APIError error) {
        super(error.getHttpStatus());
        this.message = null;
        this.message = error;
    }
    
    public void setErrorResponse(final HttpServletResponse response) throws IOException {
        response.setStatus(this.getStatusCode());
        if (this.message != null) {
            try {
                response.setHeader("Content-Type", "application/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                final PrintWriter pout = response.getWriter();
                pout.print(this.message.toJSONObject().toString());
            }
            catch (final Exception ex) {
                Logger.getLogger(APIHTTPException.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public String toString() {
        return this.message.toJSONObject().toString();
    }
    
    public JSONObject toJSONObject() {
        return this.message.toJSONObject();
    }
}
