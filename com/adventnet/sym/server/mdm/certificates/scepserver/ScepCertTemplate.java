package com.adventnet.sym.server.mdm.certificates.scepserver;

import java.util.Collections;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.Collection;
import org.json.JSONArray;
import java.util.List;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfilePayloadOperator;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScepCertTemplate
{
    private static final Logger LOGGER;
    
    private ScepCertTemplate() {
    }
    
    public static boolean modifyCertificateTemplates(final ScepServer scepServer, final Boolean isProfileRedistributionOpted, final long userID, final long loginId) throws Exception {
        ScepCertTemplate.LOGGER.log(Level.INFO, "ScepServerHandler: Modifying certificate templates belonging to server: {0}, Is profile redistribution opted: {1}, User id: {2}", new Object[] { scepServer.getScepServerId(), isProfileRedistributionOpted, userID });
        final List<Long> templates = ScepCertTemplateDB.getTemplatesMappedToServer(scepServer.getScepServerId(), scepServer.getCustomerId());
        boolean isProfileRedistributionDone = false;
        if (!templates.isEmpty()) {
            ScepCertTemplate.LOGGER.log(Level.INFO, "ScepServerHandler: Certificate templates belonging to server {0}: Templates: {1}", new Object[] { scepServer.getScepServerId(), templates });
            ScepCertTemplateDB.updateTemplatesInDB(templates, scepServer);
            final ProfilePayloadOperator payloadOperator = new ProfilePayloadOperator(ProfileCertificateUtil.getInstance().certificatesMapptedTableList, ProfileCertificateUtil.getInstance().unConfigureMap);
            payloadOperator.rePublishPayloadProfiles(templates, scepServer.getCustomerId(), userID, isProfileRedistributionOpted, Boolean.TRUE);
            final String templateNames = ProfileCertificateUtil.getInstance().getTemplateNames(new JSONArray((Collection)templates), scepServer.getCustomerId());
            ScepCertTemplate.LOGGER.log(Level.INFO, "ScepServerHandler: Profile having these templates are successfully redistributed {0}, Templates: {1}", new Object[] { scepServer.getScepServerId(), templates });
            MDMEventLogHandler.getInstance().addEvent(72428, DMUserHandler.getDCUser(Long.valueOf(loginId)), "mdm.mgmt.certrepo.template_modified", (List<Object>)Collections.singletonList(templateNames), scepServer.getCustomerId(), System.currentTimeMillis());
            isProfileRedistributionDone = true;
        }
        return isProfileRedistributionDone;
    }
    
    public static boolean trashCertificateTemplates(final ScepServer scepServer, final boolean isProfileRedistributionOpted, final long userId, final long loginId) throws Exception {
        ScepCertTemplate.LOGGER.log(Level.INFO, "ScepServerHandler: Trashing certificate templates belonging to server {0}, Is profile redistribution opted: {1}, User id: {2}", new Object[] { scepServer.getScepServerId(), isProfileRedistributionOpted, loginId });
        final List<Long> templates = ScepCertTemplateDB.getTemplatesMappedToServer(scepServer.getScepServerId(), scepServer.getCustomerId());
        boolean isProfileRedistributionDone = false;
        if (!templates.isEmpty()) {
            ScepCertTemplate.LOGGER.log(Level.INFO, "ScepServerHandler: Trashing certificate templates belonging to server {0}: {1}", new Object[] { scepServer.getScepServerId(), templates });
            ProfileCertificateUtil.getInstance().moveCertificatesToTrash(templates, scepServer.getCustomerId());
            final ProfilePayloadOperator payloadOperator = new ProfilePayloadOperator(ProfileCertificateUtil.getInstance().certificatesMapptedTableList, ProfileCertificateUtil.getInstance().unConfigureMap);
            payloadOperator.performPayloadOperation(templates, scepServer.getCustomerId(), userId, -1L, Boolean.TRUE, isProfileRedistributionOpted);
            final String templateNames = ProfileCertificateUtil.getInstance().getTemplateNames(new JSONArray((Collection)templates), scepServer.getCustomerId());
            ScepCertTemplate.LOGGER.log(Level.INFO, "ScepServerHandler: Profile having these templates are successfully redistributed {0}, Templates: {1}", new Object[] { scepServer.getScepServerId(), templates });
            MDMEventLogHandler.getInstance().addEvent(72429, DMUserHandler.getDCUser(Long.valueOf(loginId)), "mdm.mgmt.certrepo.template_deleted", (List<Object>)Collections.singletonList(templateNames), scepServer.getCustomerId(), System.currentTimeMillis());
            isProfileRedistributionDone = true;
        }
        return isProfileRedistributionDone;
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
