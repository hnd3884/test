package com.me.ems.framework.common.api.v1.service;

import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;
import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.ems.framework.common.api.v1.model.MessageContent;
import java.util.Iterator;
import com.adventnet.i18n.I18N;
import com.me.ems.framework.common.api.v1.model.MessageGroup;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.ems.framework.common.api.v1.model.Message;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MessageService
{
    private static Logger logger;
    
    public static Message getMessages(final Long pageNumber, final Long userID, final Boolean isAdmin, final List<String> userRoleList, final ArrayList<Long> customerIDList, final MultivaluedMap<String, String> userParams) {
        Message message = null;
        try {
            final Row msgPageRow = DBUtil.getRowFromDB("MsgPage", "PAGE_ID", pageNumber);
            if (msgPageRow != null) {
                final Boolean considerCount = (Boolean)msgPageRow.get("DISPLAY_TYPE");
                final Integer messageCount = (Integer)msgPageRow.get("MSG_COUNT");
                final DataObject messageDO = MessageProvider.getInstance().getMessageDataObject(pageNumber, userID, isAdmin, customerIDList, userRoleList);
                message = getDCMessage(customerIDList, userID, messageDO, userParams);
                if (message != null && message.getMessages() != null && !message.getMessages().isEmpty()) {
                    message.setPageNumber(pageNumber);
                    message.setConsiderCount(considerCount);
                    message.setMessageCount(messageCount);
                }
            }
        }
        catch (final Exception e) {
            MessageService.logger.log(Level.WARNING, "Exception occured while getting messages for pageNumber: " + pageNumber + " userID: " + userID);
        }
        return message;
    }
    
    private static Message getDCMessage(final ArrayList<Long> customerIDList, final Long userID, final DataObject messageDO, final MultivaluedMap<String, String> userParams) {
        Message dcMessage = null;
        try {
            final MessageGroup alertMessageGroup = new MessageGroup();
            final MessageGroup informationMessageGroup = new MessageGroup();
            final MessageGroup videoMessageGroup = new MessageGroup();
            final MessageGroup inProgressMessageGroup = new MessageGroup();
            alertMessageGroup.setMessageType("alerts");
            informationMessageGroup.setMessageType("informations");
            videoMessageGroup.setMessageType("videos");
            inProgressMessageGroup.setMessageType("inprogress");
            alertMessageGroup.setMessageTypeLabel(I18N.getMsg("dc.common.ALERTS", new Object[0]));
            informationMessageGroup.setMessageTypeLabel(I18N.getMsg("dc.common.INFORMATION", new Object[0]));
            videoMessageGroup.setMessageTypeLabel(I18N.getMsg("dm.common.messages.training_videos", new Object[0]));
            inProgressMessageGroup.setMessageTypeLabel(I18N.getMsg("dc.common.status.in_progress", new Object[0]));
            if (messageDO != null) {
                dcMessage = new Message();
                final Iterator messageIterator = messageDO.getRows("MsgContent");
                while (messageIterator.hasNext()) {
                    final Row messageContentRow = messageIterator.next();
                    final Integer msgType = (Integer)messageContentRow.get("MSG_TYPE");
                    final MessageContent currentMessageContent = getMessageContent(messageContentRow, userID, customerIDList, userParams);
                    if (currentMessageContent.getMessageContent() != null) {
                        switch (msgType) {
                            case 1: {
                                alertMessageGroup.addMessageContent(currentMessageContent);
                                alertMessageGroup.incrementMessageCount();
                                continue;
                            }
                            case 2: {
                                informationMessageGroup.addMessageContent(currentMessageContent);
                                informationMessageGroup.incrementMessageCount();
                                continue;
                            }
                            case 3: {
                                inProgressMessageGroup.addMessageContent(currentMessageContent);
                                inProgressMessageGroup.incrementMessageCount();
                                continue;
                            }
                            case 5: {
                                videoMessageGroup.addMessageContent(currentMessageContent);
                                videoMessageGroup.incrementMessageCount();
                                continue;
                            }
                        }
                    }
                }
                if (alertMessageGroup.getMessageLinks() != null) {
                    dcMessage.addMessageGroup(alertMessageGroup);
                }
                if (informationMessageGroup.getMessageLinks() != null) {
                    dcMessage.addMessageGroup(informationMessageGroup);
                }
                if (videoMessageGroup.getMessageLinks() != null) {
                    dcMessage.addMessageGroup(videoMessageGroup);
                }
                if (inProgressMessageGroup.getMessageLinks() != null) {
                    dcMessage.addMessageGroup(inProgressMessageGroup);
                }
            }
        }
        catch (final Exception e) {
            MessageService.logger.log(Level.WARNING, "Exception while getting DC message Group for messages {0}", e);
        }
        return dcMessage;
    }
    
    public static boolean isMessageClosableByUser(final Long msgID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgContent"));
        selectQuery.addSelectColumn(Column.getColumn("MsgContent", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MsgContent", "MSG_CONTENT_ID"), (Object)msgID, 0);
        selectQuery.setCriteria(criteria);
        return Boolean.parseBoolean(String.valueOf(SyMUtil.getPersistence().get(selectQuery).getFirstRow("MsgContent").get("ENABLE_USER_CLOSE")));
    }
    
    private static MessageContent getMessageContent(final Row msgContentRow, final Long userID, final ArrayList<Long> customerIDList, final MultivaluedMap<String, String> userParams) {
        final MessageContent messageContent = new MessageContent();
        try {
            final String msgHandlerClass = (String)msgContentRow.get("MSG_HANDLER_CLASS");
            final Integer msgType = (Integer)msgContentRow.get("MSG_TYPE");
            final Boolean enableUserClose = (Boolean)msgContentRow.get("ENABLE_USER_CLOSE");
            final Long msgContentID = (Long)msgContentRow.get("MSG_CONTENT_ID");
            final Long msgGroupID = (Long)msgContentRow.get("MSG_GROUP_ID");
            Map messageContentDetails = new HashMap();
            if (msgHandlerClass != null) {
                final MsgHandler msgHandler = (MsgHandler)Class.forName(msgHandlerClass).newInstance();
                messageContentDetails = msgHandler.modifyMessageContent(msgContentRow, userID, customerIDList, userParams);
            }
            else {
                messageContentDetails = MessageProvider.getInstance().getI18NMessageMap(msgContentRow);
            }
            messageContent.setEnableUserClose(enableUserClose);
            messageContent.setMsgContentID(String.valueOf(msgContentID));
            messageContent.setMsgGroupID(String.valueOf(msgGroupID));
            messageContent.setMsgType(String.valueOf(msgType));
            messageContent.setMessageContent(messageContentDetails);
            final List<String> autoClosableList = (List<String>)userParams.get((Object)"isAutoClosableFromBackend");
            if (autoClosableList != null) {
                final String autoClosableString = autoClosableList.get(0);
                final Boolean isAutoClosableFromBackend = Boolean.valueOf(autoClosableString);
                if (msgType == 2 && isAutoClosableFromBackend) {
                    MessageProvider.getInstance().updateMsgStatusForInfoMessage(userID, msgContentID);
                }
            }
        }
        catch (final Exception e) {
            MessageService.logger.log(Level.WARNING, "Exception while getting DC Message Content for messages {0}", e);
        }
        return messageContent;
    }
    
    public static void closeUserMessage(final User dcUser) throws APIException {
        try {
            final String isExistingAdmin = SyMUtil.getUserParameter(dcUser.getUserID(), "isExistingAdmin");
            if (isExistingAdmin == null) {
                throw new APIException(Response.Status.NOT_FOUND, "UAC018", "ems.user.uac.user_message_closed");
            }
            SyMUtil.deleteUserParameter(dcUser.getUserID(), "isExistingAdmin");
        }
        catch (final Exception ex) {
            MessageService.logger.log(Level.WARNING, "Exception while get token details", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public static Map<String, Object> getMessageBoxStatus(final String messageBoxName, final Long userID) throws APIException {
        final Map<String, Object> messageBoxStatusMap = new HashMap<String, Object>();
        try {
            final String messageBoxStatus = SyMUtil.getUserParameter(userID, messageBoxName);
            if (messageBoxStatus == null || !CustomerInfoUtil.isDC()) {
                messageBoxStatusMap.put(messageBoxName, false);
                return messageBoxStatusMap;
            }
            messageBoxStatusMap.put(messageBoxName, Boolean.parseBoolean(messageBoxStatus));
            return messageBoxStatusMap;
        }
        catch (final Exception e) {
            MessageService.logger.log(Level.SEVERE, "LicenseException: Exception while fetching message status for", messageBoxName);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public static Response closeMessageBox(final String messageBoxName, final Long userID) throws APIException {
        try {
            SyMUtil.deleteUserParameter(userID, messageBoxName);
            return Response.status(204).build();
        }
        catch (final Exception e) {
            MessageService.logger.log(Level.SEVERE, "LicenseException: Exception while fetching message status for", messageBoxName);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    static {
        MessageService.logger = Logger.getLogger(MessageService.class.getName());
    }
}
