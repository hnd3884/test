package com.me.devicemanagement.framework.webclient.message;

import com.adventnet.i18n.I18N;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public class DefaultMsgHandler implements MsgHandler
{
    @Override
    public Properties modifyMsgProperty(final Properties msgProperties, final Properties userDefined, final HttpServletRequest request) throws Exception {
        return msgProperties;
    }
    
    @Override
    public Map modifyMessageContent(final Row msgContentRow, final Long userID, final ArrayList<Long> customerID, final MultivaluedMap<String, String> userDefinedAttributes) throws Exception {
        final Map messageMap = MessageProvider.getInstance().getI18NMessageMap(msgContentRow);
        final String messageName = (String)msgContentRow.get("MSG_NAME");
        Map messageAttributes = messageMap.get("messageAttributes");
        if (messageAttributes == null) {
            messageAttributes = new HashMap();
        }
        if (messageName.equalsIgnoreCase("CUSTOMER_NOT_ADDED_WRITE")) {
            messageAttributes.remove("LINK_0");
            String addCustomerMessage = I18N.getMsg("dc.common.msg.add_customers", new Object[0]);
            addCustomerMessage = addCustomerMessage.replace("<a>", "");
            addCustomerMessage = addCustomerMessage.replace("</a>", "");
            messageAttributes = MessageProvider.getInstance().getMessageComponentAttributeMap(messageAttributes, addCustomerMessage, "home/message-components/add-customer", new HashMap(1));
        }
        messageMap.put("messageAttributes", messageAttributes);
        return messageMap;
    }
}
