package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Location;
import java.io.InputStream;
import org.bouncycastle.util.encoders.Base64;
import java.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.webclient.mdm.config.CredentialsMgmtAction;
import com.me.mdm.framework.syncml.requestcmds.AddRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WpEnterpriseAppManagementCommand
{
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        final int type = jsonObject.optInt("Type");
        if (type == 1) {
            this.processAETRequest(responseSyncML, jsonObject);
        }
        else if (type == 2) {
            this.processCertRequest(responseSyncML, jsonObject);
        }
    }
    
    private void processAETRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final String enterpriseID = String.valueOf(jsonObject.get("ENTERPRISE_ID"));
            final String enrollmentToken = String.valueOf(jsonObject.get("APP_ENROLLMENT_TOKEN"));
            final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
            atomicCommand.setRequestCmdId("AppEnrollmentToken");
            final ReplaceRequestCommand command = new ReplaceRequestCommand();
            command.setRequestCmdId("AppEnrollmentToken");
            final ArrayList items = new ArrayList();
            final String entepriseIDUri = "./Vendor/MSFT/EnterpriseAppManagement/" + enterpriseID + "/EnrollmentToken";
            final Item entepriseIDItem = this.createTargetItemTagElement(entepriseIDUri, enrollmentToken, "chr");
            items.add(entepriseIDItem);
            final String sslClientSearchCriteriaValue = "CN=MDMClientAuthentication";
            final String certificateUri = "./Vendor/MSFT/EnterpriseAppManagement/" + enterpriseID + "/CertificateSearchCriteria";
            final Item sslClientSearchItem = this.createTargetItemTagElement(certificateUri, sslClientSearchCriteriaValue, "chr");
            items.add(sslClientSearchItem);
            final String crlCheckUri = "./Vendor/MSFT/EnterpriseAppManagement/" + enterpriseID + "/CRLCheck";
            final Item crlCheckItem = this.createTargetItemTagElement(crlCheckUri, "0", "chr");
            items.add(crlCheckItem);
            command.setRequestItems(items);
            atomicCommand.addRequestCmd(command);
            responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    private void processCertRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final String certificatePath = String.valueOf(jsonObject.get("CERT_FILE_PATH"));
            final int modeltype = jsonObject.optInt("ModelType");
            final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
            atomicCommand.setRequestCmdId("AppEnrollmentToken");
            final AddRequestCommand command = new AddRequestCommand();
            command.setRequestCmdId("AppEnrollmentToken");
            final ArrayList items = new ArrayList();
            final JSONObject certDetails = CredentialsMgmtAction.extractCertificateDetails(certificatePath, null);
            final String thumbprint = (String)certDetails.get("CERTIFICATE_THUMB_PRINT");
            final byte[] certificateContentBytes = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(certificatePath);
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            final InputStream in = new ByteArrayInputStream(certificateContentBytes);
            final X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);
            final String contents = new String(Base64.encode(cert.getEncoded())).replaceAll("\\s+", "");
            String entepriseIDUri = null;
            if (modeltype == 1) {
                entepriseIDUri = "./Vendor/MSFT/RootCATrustedCertificates/Root/" + thumbprint + "/EncodedCertificate";
            }
            else {
                entepriseIDUri = "./Device/Vendor/MSFT/RootCATrustedCertificates/Root/" + thumbprint + "/EncodedCertificate";
            }
            final Item entepriseIDItem = this.createTargetItemTagElement(entepriseIDUri, contents, "b64");
            items.add(entepriseIDItem);
            command.setRequestItems(items);
            atomicCommand.addRequestCmd(command);
            responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    private Item createTargetItemTagElement(final String locationUri, final String data, final String format) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        final Meta meta = new Meta();
        meta.setFormat(format);
        item.setData(data);
        return item;
    }
}
