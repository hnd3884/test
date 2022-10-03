package com.me.devicemanagement.framework.webclient.message;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public interface MsgHandler
{
    public static final String TYPE = "type";
    public static final String URL = "url";
    public static final String BLANK = "_blank";
    public static final String SELF = "_self";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String MESSAGE_ATTRIBUTES = "messageAttributes";
    public static final String TARGET = "target";
    public static final String LINK = "link";
    public static final String LINK_TEXT = "linkText";
    public static final String PLACEHOLDER = "placeHolder";
    public static final String ACTION = "action";
    public static final String COMPONENT_NAME = "componentName";
    public static final String COMPONENT_PAYLOAD = "componentPayload";
    public static final String NAME = "name";
    
    Properties modifyMsgProperty(final Properties p0, final Properties p1, final HttpServletRequest p2) throws Exception;
    
    default Map modifyMessageContent(final Row msgContentRow, final Long userID, final ArrayList<Long> customerID, final MultivaluedMap<String, String> userDefinedAttributes) throws Exception {
        return new HashMap();
    }
}
