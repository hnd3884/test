package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.DELETE;
import java.util.Map;
import javax.ws.rs.PUT;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.ems.framework.common.api.response.APIResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import com.me.ems.framework.common.api.v1.service.MessageService;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.ArrayList;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.v1.model.Message;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;

@Path("messages")
public class MessageController
{
    @GET
    @Path("{pageNumber}/")
    @Produces({ "application/fetchMessages.v1+json" })
    public Message fetchMessages(@PathParam("pageNumber") final String pageNumberString, @Context final SecurityContext securityContext, @Context final ContainerRequestContext containerRequestContext, @Context final UriInfo uriInfo) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        final MultivaluedMap<String, String> userParams = (MultivaluedMap<String, String>)uriInfo.getQueryParameters();
        final Long pageNumber = Long.valueOf(pageNumberString);
        final Long userID = user.getUserID();
        final String customerIDStr = (String)containerRequestContext.getProperty("X-Customer");
        final ArrayList<Long> customerIDList = new ArrayList<Long>();
        if (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) {
            final Long customerID = Long.parseLong(customerIDStr);
            customerIDList.add(customerID);
        }
        else if (customerIDStr != null) {
            try {
                final ArrayList customersInfoList = CustomerInfoUtil.getInstance().getCustomersForUser(userID);
                customersInfoList.stream().forEach(item -> {
                    if (item instanceof HashMap) {
                        final HashMap customerMap = (HashMap)item;
                        final Long customerID2 = customerMap.get("CUSTOMER_ID");
                        list.add(customerID2);
                    }
                    return;
                });
            }
            catch (final Exception e) {
                throw new APIException("CUSTOMER0001");
            }
        }
        final Boolean isAdmin = user.isAdminUser();
        final List<String> userRoleList = user.getAllRoles();
        final Message messagesBean = MessageService.getMessages(pageNumber, userID, isAdmin, userRoleList, customerIDList, userParams);
        if (messagesBean == null) {
            throw APIException.noDataAvailable();
        }
        return messagesBean;
    }
    
    @PUT
    @Path("{pageNumber}/{messageID}")
    @Produces({ "application/closeMessage.v1+json" })
    public Response closeMessage(@PathParam("pageNumber") final String pageNumberString, @PathParam("messageID") final String messageIDString, @Context final SecurityContext securityContext, @Context final ContainerRequestContext containerRequestContext, @Context final UriInfo uriInfo) {
        final User user = (User)securityContext.getUserPrincipal();
        final Long pageNumber = Long.valueOf(pageNumberString);
        final Long userID = user.getUserID();
        final Long customerID = Long.parseLong((String)containerRequestContext.getProperty("X-Customer"));
        final Long messageID = Long.parseLong(messageIDString);
        try {
            if (!MessageService.isMessageClosableByUser(messageID)) {
                return APIResponse.errorResponse("MESSAGE0001");
            }
            if (MessageProvider.getInstance().closeMsgForUser(userID, messageID, pageNumber, customerID)) {
                return Response.status(Response.Status.OK).build();
            }
            return APIResponse.errorResponse("GENERIC0002", "ems.rest.api_message_error", new String[0]);
        }
        catch (final Exception ex) {
            return APIResponse.errorResponse("GENERIC0002", "ems.rest.api_message_error", new String[0]);
        }
    }
    
    @GET
    @Path("license/{message_box_name}")
    @Produces({ "application/licenseMessageBoxStatus.v1+json" })
    public Map<String, Object> licenseMessageBoxStatus(@Context final ContainerRequestContext requestContext, @PathParam("message_box_name") final String messageBoxName) throws APIException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User user = (User)securityContext.getUserPrincipal();
        return MessageService.getMessageBoxStatus(messageBoxName, user.getUserID());
    }
    
    @DELETE
    @Path("users")
    @Produces({ "application/closemessage.v1+json" })
    public Response closeMessage(@Context final ContainerRequestContext requestContext) throws APIException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User user = (User)securityContext.getUserPrincipal();
        MessageService.closeUserMessage(user);
        return Response.status(Response.Status.ACCEPTED).build();
    }
    
    @DELETE
    @Path("license/{message_box_name}")
    @Produces({ "application/closeLicenseMessageBox.v1+json" })
    public Response closeLicenseMessageBox(@Context final ContainerRequestContext requestContext, @PathParam("message_box_name") final String messageBoxName) throws APIException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        final User user = (User)securityContext.getUserPrincipal();
        return MessageService.closeMessageBox(messageBoxName, user.getUserID());
    }
}
