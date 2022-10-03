package com.me.idps.core.sync.product;

import com.me.idps.core.util.IdpsUtil;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.JSONObject;
import com.me.idps.core.sync.events.IdpEventConstants;
import java.util.Properties;

public class DirProdImplRequest
{
    public Object[] args;
    public Long aaaUserID;
    public String userName;
    public Properties dmDomainProps;
    public IdpEventConstants eventType;
    
    public DirProdImplRequest() {
        this.args = null;
        this.eventType = null;
        this.aaaUserID = null;
        this.userName = null;
        this.dmDomainProps = null;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final JSONObject jsonObject = new JSONObject();
        for (int i = 0; this.args != null && i < this.args.length; ++i) {
            if (i > 0) {
                sb.append(" ,");
            }
            else {
                sb.append(this.args.length);
                sb.append(" -> ");
            }
            sb.append(String.valueOf(this.args[i]));
        }
        try {
            jsonObject.put("args", (Object)sb.toString());
            jsonObject.put("userName", (Object)String.valueOf(this.userName));
            jsonObject.put("aaaUserID", (Object)String.valueOf(this.aaaUserID));
            jsonObject.put("eventType", (Object)String.valueOf(this.eventType));
            if (this.dmDomainProps != null) {
                jsonObject.put("dmDomainProps", (Object)this.dmDomainProps.toString());
            }
        }
        catch (final JSONException ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, (Throwable)ex);
        }
        return IdpsUtil.getPrettyJSON(jsonObject);
    }
}
