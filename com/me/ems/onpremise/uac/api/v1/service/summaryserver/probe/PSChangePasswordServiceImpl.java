package com.me.ems.onpremise.uac.api.v1.service.summaryserver.probe;

import java.util.logging.Level;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.onpremise.uac.api.v1.service.factory.ChangePasswordService;
import com.me.ems.onpremise.uac.api.v1.service.ChangePasswordServiceImpl;

public class PSChangePasswordServiceImpl extends ChangePasswordServiceImpl implements ChangePasswordService
{
    @Override
    public Map<String, Object> changePasswordAndCloseSession(final User user, final HttpServletRequest request, final Map<String, String> passwordDetails) throws APIException {
        try {
            ApiFactoryProvider.getPersonalizationAPIForRest().closeAllSessions(user);
            String oldEncodedPassword = passwordDetails.get("oldPassword");
            String newEncodedPassword = passwordDetails.get("newPassword");
            oldEncodedPassword = ((oldEncodedPassword != null && !oldEncodedPassword.trim().isEmpty()) ? SyMUtil.decodeAsUTF16LE(oldEncodedPassword) : null);
            newEncodedPassword = ((newEncodedPassword != null && !newEncodedPassword.trim().isEmpty()) ? SyMUtil.decodeAsUTF16LE(newEncodedPassword) : null);
            return this.changePassword(user.getUserID(), user.getLoginID(), user.getName(), oldEncodedPassword, newEncodedPassword, request);
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception ex) {
            PSChangePasswordServiceImpl.LOGGER.log(Level.WARNING, "Exception while deleting active session of user : " + user.getName(), ex);
            throw new APIException("GENERIC0005");
        }
    }
}
