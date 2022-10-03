package com.me.webclient.admin;

import java.util.Hashtable;
import com.adventnet.sym.webclient.admin.fos.FosTrialLicense;
import com.adventnet.sym.server.fos.FailoverServerUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;

public class FosMsgHandler implements MsgHandler
{
    public Properties modifyMsgProperty(final Properties msgProperties, final Properties userDefined, final HttpServletRequest request) throws Exception {
        final String msgName = ((Hashtable<K, String>)msgProperties).get("MSG_NAME");
        final Boolean isDBInSameNetwork = FailoverServerUtil.isDBInSameNetwork();
        final Boolean isRemoteDB = FailoverServerUtil.hasRemoteDB();
        if (msgName.equalsIgnoreCase("REMOTE_DB_NOT_CONFIGURED") && !isDBInSameNetwork && isRemoteDB) {
            final String msgContent = "mdm.admin.remoteDB_not_in_same_network";
            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
        }
        if (msgName.equalsIgnoreCase("FOS_NOT_PURCHASED") && !FosTrialLicense.canOptTrial()) {
            final String msgContent = "mdm.fos.need_to_purchase_license";
            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
        }
        return msgProperties;
    }
}
