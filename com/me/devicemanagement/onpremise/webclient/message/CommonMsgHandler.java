package com.me.devicemanagement.onpremise.webclient.message;

import java.util.Hashtable;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.mailmanager.MailProcessor;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.adventnet.devicemanagement.silentmigration.SilentUpdation;
import java.io.File;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;

public class CommonMsgHandler implements MsgHandler
{
    private static final Logger LOGGER;
    
    public Properties modifyMsgProperty(final Properties msgProperties, final Properties userDefined, final HttpServletRequest request) throws Exception {
        final String msgName = ((Hashtable<K, String>)msgProperties).get("MSG_NAME");
        if (msgName.equalsIgnoreCase("PORT_BLOCKED_WRITE")) {
            final String ports = SYMClientUtil.getBlockedPorts();
            String msgContent = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
            msgContent = msgContent.replace("{0}", ports);
            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
        }
        if (msgName.equalsIgnoreCase("NAT_FIREWALL_INFO_MSG")) {
            final HashMap ports2 = NATHandler.getInstance().getPortsMap();
            String msgContent = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
            if (CustomerInfoUtil.isPMPProduct() || CustomerInfoUtil.isVMPProduct()) {
                msgContent = msgContent.replace(I18N.getMsg("ems.agents.settings.NAT.firewall.message.content", new Object[0]), I18N.getMsg("desktopcentral.settings.NAT.firewall.message", new Object[0]));
            }
            final Iterator it = ports2.keySet().iterator();
            String natPorts = "";
            while (it.hasNext()) {
                if ("".equals(natPorts)) {
                    natPorts = String.valueOf(ports2.get(it.next()));
                }
                else {
                    natPorts = natPorts + ", " + String.valueOf(ports2.get(it.next()));
                }
            }
            msgContent = msgContent.replace("{0}", natPorts);
            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
        }
        if (msgName.equalsIgnoreCase("QPM_INSTALL_FAILED")) {
            String msgContent2 = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
            try {
                final String serverHome = System.getProperty("server.home");
                final String propertyFile = serverHome + File.separator + SilentUpdation.QUICK_FIX_HISTORY;
                if (new File(propertyFile).exists()) {
                    final Properties fixHistory = FileAccessUtil.readProperties(propertyFile);
                    if (fixHistory.getProperty("LastQuickFixerStatus") != null && fixHistory.getProperty("LastQuickFixerStatus").equals("failed")) {
                        msgContent2 = msgContent2.replaceFirst("\\{0\\}", fixHistory.getProperty("LastQuickFixerName"));
                        msgContent2 = msgContent2.replaceFirst("\\{1\\}", fixHistory.getProperty("LastQuickFixerInstallTime"));
                    }
                }
            }
            catch (final Exception ex) {}
            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent2);
        }
        if (msgName.equalsIgnoreCase("QUICKFIXER_INCOMPATIBLE_FILE")) {
            final String msgContent2 = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
            final String msgTitle = ((Hashtable<K, String>)msgProperties).get("MSG_TITLE");
            String serverHome2 = new File(System.getProperty("server.home")).getCanonicalPath();
            serverHome2 = serverHome2.replace("\\", "\\\\");
            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent2.replaceFirst("\\{0\\}", serverHome2));
            ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle.replaceFirst("\\{0\\}", serverHome2));
        }
        if (msgName.equalsIgnoreCase("MAIL_SERVER_CONFIGURED_INCORRECTLY_READ") || msgName.equalsIgnoreCase("MAIL_SERVER_CONFIGURED_INCORRECTLY_WRITE")) {
            final DataObject dobj = SyMUtil.getPersistence().get("SmtpConfiguration", (Criteria)null);
            Integer errorCode = null;
            if (!dobj.isEmpty()) {
                errorCode = (Integer)dobj.getRow("SmtpConfiguration").get("PREVIOUS_ERROR_CODE");
            }
            String msgContent3 = "";
            if (errorCode != null && errorCode != -1) {
                msgContent3 = I18N.getMsg(MailProcessor.getInstance().getErrorKeyForErrorCode(errorCode), new Object[0]);
            }
            else {
                msgContent3 = I18N.getMsg("desktopcentral.admin.mail.server.unknown_cause", new Object[0]);
            }
            if (msgName.equalsIgnoreCase("MAIL_SERVER_CONFIGURED_INCORRECTLY_WRITE")) {
                msgContent3 = msgContent3 + " <a href=\"javascript:showURLInDialog('smtpConfig.do?actionToCall=showSmtpConfigAsPopUp', 'position=absmiddle,modal=yes,width=575,top=100,left=200,scrollbars=yes,title=Mail Server Setting')\">" + I18N.getMsg("desktopcentral.common.CONFIGURE_NOW", new Object[0]) + "</a>";
            }
            else {
                msgContent3 = msgContent3 + " " + I18N.getMsg("dc.rep.adreport.contact_admin_to_config", new Object[0]);
            }
            if (errorCode != null && errorCode == 40008) {
                msgContent3 = msgContent3 + " <a href=\"https://www.manageengine.com/mobile-device-management/how-to/configure-mail-server-using-gmail.html?" + ProductUrlLoader.getInstance().getGeneralProperites().getProperty("trackingcode") + "\">" + I18N.getMsg("dc.common.READ_KB", new Object[0]) + "</a>";
            }
            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent3);
        }
        if (msgName.equalsIgnoreCase("REQUIRED_SERVICE_RESTART_READ") || msgName.equalsIgnoreCase("REQUIRED_SERVICE_RESTART_POSTGRES_DB")) {
            final String msgContent2 = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
            final String msgTitle = ((Hashtable<K, String>)msgProperties).get("MSG_TITLE");
            final String productDisplayName = ProductUrlLoader.getInstance().getValue("displayname");
            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent2.replaceFirst("\\{0\\}", productDisplayName));
            ((Hashtable<String, String>)msgProperties).put("MSG_TITLE", msgTitle.replaceFirst("\\{0\\}", productDisplayName));
        }
        return msgProperties;
    }
    
    public Map modifyMessageContent(final Row msgContentRow, final Long userID, final ArrayList<Long> customerID, final MultivaluedMap<String, String> userDefinedAttributes) throws Exception {
        final Map messageMap = MessageProvider.getInstance().getI18NMessageMap(msgContentRow);
        final String messageName = (String)msgContentRow.get("MSG_NAME");
        String messageTitle = (String)msgContentRow.get("MSG_TITLE");
        messageTitle = I18N.getMsg(messageTitle, new Object[0]);
        String messageContent = messageMap.get("content");
        Map messageAttributes = messageMap.get("messageAttributes");
        if (messageAttributes == null) {
            messageAttributes = new HashMap();
        }
        if (messageName.equalsIgnoreCase("PORT_BLOCKED_WRITE") || messageName.equalsIgnoreCase("PORT_BLOCKED_READ")) {
            final String blockedPorts = SYMClientUtil.getBlockedPorts();
            messageContent = messageContent.replace("{0}", blockedPorts);
            final Map componentAttributes = messageAttributes.get("LINK_0");
            final String componentContent = componentAttributes.get("linkText");
            messageAttributes.remove("LINK_0");
            messageAttributes = MessageProvider.getInstance().getMessageComponentAttributeMap(messageAttributes, componentContent, "home/message-components/open-port-block", (Map)new HashMap(1));
        }
        if (messageName.equalsIgnoreCase("HD_MAIL_SERVER_NOT_CONFIGURED_WRITE")) {
            messageContent = messageContent + " #{LINK_" + messageAttributes.size() + "}";
            messageAttributes = MessageProvider.getInstance().getMessageComponentAttributeMap(messageAttributes, I18N.getMsg("desktopcentral.common.CONFIGURE_NOW", new Object[0]), "common/message-components/mail-server-configure", (Map)new HashMap(1));
        }
        if (messageName.equalsIgnoreCase("MAIL_SERVER_CONFIGURED_INCORRECTLY_READ") || messageName.equalsIgnoreCase("MAIL_SERVER_CONFIGURED_INCORRECTLY_WRITE")) {
            final DataObject smtpObj = SyMUtil.getPersistence().get("SmtpConfiguration", (Criteria)null);
            Integer errorCode = null;
            if (!smtpObj.isEmpty()) {
                errorCode = (Integer)smtpObj.getRow("SmtpConfiguration").get("PREVIOUS_ERROR_CODE");
            }
            if (errorCode != null && errorCode != -1) {
                messageContent = I18N.getMsg(MailProcessor.getInstance().getErrorKeyForErrorCode(errorCode), new Object[0]);
            }
            else {
                messageContent = I18N.getMsg("desktopcentral.admin.mail.server.unknown_cause", new Object[0]);
            }
            if (messageName.equalsIgnoreCase("MAIL_SERVER_CONFIGURED_INCORRECTLY_WRITE")) {
                messageContent = messageContent + " #{LINK_" + messageAttributes.size() + "}";
                messageAttributes = MessageProvider.getInstance().getMessageComponentAttributeMap(messageAttributes, I18N.getMsg("desktopcentral.common.CONFIGURE_NOW", new Object[0]), "common/message-components/mail-server-configure", (Map)new HashMap(1));
            }
            else {
                messageContent = messageContent + " " + I18N.getMsg("dc.rep.adreport.contact_admin_to_config", new Object[0]);
            }
            if (errorCode != null && errorCode == 40008) {
                messageContent = messageContent + " #{LINK_" + messageAttributes.size() + "}";
                final String mailAuthErrorUrl = ProductUrlLoader.getInstance().getGeneralProperites().getProperty("mailAuthErrorUrl") + ProductUrlLoader.getInstance().getGeneralProperites().getProperty("trackingcode");
                messageAttributes = MessageProvider.getInstance().getMessageAttributeMap(messageAttributes, I18N.getMsg("dc.common.READ_KB", new Object[0]), "link", mailAuthErrorUrl, "_blank");
            }
        }
        if (messageName.equalsIgnoreCase("QPM_INSTALL_FAILED")) {
            try {
                final String serverHome = System.getProperty("server.home");
                final String propertyFile = serverHome + File.separator + SilentUpdation.QUICK_FIX_HISTORY;
                if (new File(propertyFile).exists()) {
                    final Properties fixHistory = FileAccessUtil.readProperties(propertyFile);
                    if (fixHistory.getProperty("LastQuickFixerStatus") != null && fixHistory.getProperty("LastQuickFixerStatus").equalsIgnoreCase("failed")) {
                        messageContent = messageContent.replace("{0}", fixHistory.getProperty("LastQuickFixerName")).replace("{1}", fixHistory.getProperty("LastQuickFixerInstallTime"));
                        final String linkTagID = "LINK_0";
                        final Map messageAttributeDetails = messageAttributes.get(linkTagID);
                        messageAttributeDetails.put("url", "/webclient#/uems/support/create?disableServer=true");
                    }
                }
            }
            catch (final Exception ex) {}
        }
        if (messageName.equalsIgnoreCase("QUICKFIXER_INCOMPATIBLE_FILE")) {
            String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            serverHome = serverHome.replace("\\", "\\\\");
            messageContent = messageContent.replace("{0}", serverHome);
            messageTitle = messageTitle.replace("{0}", serverHome);
        }
        if (messageName.equalsIgnoreCase("NAT_FIREWALL_INFO_MSG")) {
            if (CustomerInfoUtil.isPMPProduct() || CustomerInfoUtil.isVMPProduct()) {
                messageContent = messageContent.replace(I18N.getMsg("ems.agents.settings.NAT.firewall.message.content", new Object[0]), I18N.getMsg("desktopcentral.settings.NAT.firewall.message", new Object[0]));
            }
            final HashMap<String, Object> ports = NATHandler.getInstance().getPortsMap();
            final String natPorts = ports.values().stream().map((Function<? super Object, ?>)Object::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(","));
            messageContent = messageContent.replace("{0}", natPorts);
        }
        if (messageName.equalsIgnoreCase("REQUIRED_SERVICE_RESTART_READ") || messageName.equalsIgnoreCase("REQUIRED_SERVICE_RESTART_POSTGRES_DB")) {
            final String productDisplayName = ProductUrlLoader.getInstance().getValue("displayname");
            messageContent = messageContent.replace("{0}", productDisplayName);
            messageTitle = messageTitle.replace("{0}", productDisplayName);
        }
        messageMap.put("title", messageTitle);
        messageMap.put("content", messageContent);
        messageMap.put("messageAttributes", messageAttributes);
        return messageMap;
    }
    
    static {
        LOGGER = Logger.getLogger(CommonMsgHandler.class.getName());
    }
}
