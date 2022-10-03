package com.me.mdm.webclient.formbean;

import org.json.JSONException;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.CMSException;
import org.xml.sax.SAXParseException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.dd.plist.PropertyListParser;
import org.bouncycastle.cms.CMSSignedDataParser;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import com.me.mdm.server.profiles.CustomProfileHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Level;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.payload.PayloadException;
import com.dd.plist.NSArray;
import com.me.mdm.server.profiles.AppleCustomProfileHandler;
import java.util.HashSet;
import java.util.List;
import org.json.JSONObject;

public class MDMAppleCustomProfileFormBean extends MDMCustomProfileFormBean
{
    @Override
    protected List<String> getPayloadList(final JSONObject dynaForm) throws PayloadException {
        final HashSet<String> payloadSet = new HashSet<String>();
        try {
            final String filePath = dynaForm.optString("CUSTOM_PROFILE_PATH");
            final Integer customProfileType = dynaForm.optInt("CUSTOM_PROFILE_TYPE");
            final NSDictionary rootDict = this.getDictionaryFromStream(filePath);
            if (customProfileType.equals(AppleCustomProfileHandler.CUSTOM_CONFIGURATION)) {
                final NSArray payloadArray = (NSArray)rootDict.get((Object)"PayloadContent");
                if (payloadArray == null) {
                    throw new PayloadException("PAY0010");
                }
                for (int i = 0; i < payloadArray.count(); ++i) {
                    final NSDictionary payloadDictionary = (NSDictionary)payloadArray.objectAtIndex(i);
                    final String payloadType = payloadDictionary.get((Object)"PayloadType").toString();
                    payloadSet.add(payloadType);
                }
            }
            else if (customProfileType.equals(AppleCustomProfileHandler.CUSTOM_COMMAND)) {
                final NSDictionary requestDict = (NSDictionary)rootDict.get((Object)"Command");
                final String requestType = requestDict.get((Object)"RequestType").toString();
                payloadSet.add(requestType);
            }
        }
        catch (final PayloadException e) {
            throw e;
        }
        catch (final Exception e2) {
            MDMAppleCustomProfileFormBean.logger.log(Level.SEVERE, "Exception in parsing profile", e2);
            throw new PayloadException("PAY0010");
        }
        return new ArrayList<String>(payloadSet);
    }
    
    @Override
    protected CustomProfileHandler getCustomProfileHandler() {
        return new AppleCustomProfileHandler();
    }
    
    public NSDictionary getDictionaryFromStream(final String customProfilePath) throws Exception {
        NSDictionary rootDict = null;
        try {
            final CMSSignedDataParser parser = new CMSSignedDataParser(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build(), ApiFactoryProvider.getFileAccessAPI().readFile(customProfilePath));
            final CMSTypedStream cmsTStream = parser.getSignedContent();
            rootDict = (NSDictionary)PropertyListParser.parse(cmsTStream.getContentStream());
        }
        catch (final CMSException ex) {
            try {
                rootDict = (NSDictionary)DMSecurityUtil.parsePropertyList(ApiFactoryProvider.getFileAccessAPI().readFile(customProfilePath));
            }
            catch (final SAXParseException e) {
                throw new APIHTTPException("PAY0010", new Object[0]);
            }
        }
        catch (final Exception e2) {
            throw new APIHTTPException("PAY0010", new Object[0]);
        }
        return rootDict;
    }
    
    @Override
    protected JSONObject getCustomProfileJSON(final JSONObject dynaForm) throws JSONException {
        final JSONObject customProfileJSON = super.getCustomProfileJSON(dynaForm);
        customProfileJSON.put("CUSTOM_PROFILE_TYPE", dynaForm.get("CUSTOM_PROFILE_TYPE"));
        return customProfileJSON;
    }
}
