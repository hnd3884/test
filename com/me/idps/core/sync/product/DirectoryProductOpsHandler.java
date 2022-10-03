package com.me.idps.core.sync.product;

import java.lang.reflect.Method;
import com.me.idps.core.util.DirectoryUtil;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.factory.IdpsFactoryConstant;
import com.me.idps.core.util.IdpsUtil;
import java.util.Map;
import org.json.simple.JSONObject;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import com.me.idps.core.sync.events.IdpEventConstants;
import java.util.Properties;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;

public class DirectoryProductOpsHandler
{
    private static DirectoryProductOpsHandler directoryProductOpsHandler;
    
    public static DirectoryProductOpsHandler getInstance() {
        if (DirectoryProductOpsHandler.directoryProductOpsHandler == null) {
            DirectoryProductOpsHandler.directoryProductOpsHandler = new DirectoryProductOpsHandler();
        }
        return DirectoryProductOpsHandler.directoryProductOpsHandler;
    }
    
    private boolean isDirAllowedForEvent(final DirProdImplRequest dirProdImplRequest) {
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final String dmDomainName = dmDomainProps.getProperty("NAME");
        if (dmDomainName.contains("TEST_DIRECTORY;")) {
            IDPSlogger.EVENT.log(Level.INFO, dirProdImplRequest.toString());
            return false;
        }
        return true;
    }
    
    private Object doCoreHandling(final IdpEventConstants eventType, Object response, final DirectoryProductAPI directoryProductImpl, final DirProdImplRequest dirProdImplRequest) throws Exception {
        switch (eventType) {
            case GET_PROD_SPECIFIC_ME_TRACKING_KEYS: {
                if (response == null) {
                    response = new ArrayList();
                }
                final List<String> curProdMEtrackingKeys = directoryProductImpl.getProdSpecificMeTrackingKeys(dirProdImplRequest);
                if (curProdMEtrackingKeys != null) {
                    ((List)response).addAll(curProdMEtrackingKeys);
                    break;
                }
                break;
            }
            case DO_PROD_SPECIFIC_ME_TRACKING: {
                if (response == null) {
                    response = new JSONObject();
                }
                final JSONObject processedProdMEtrackingDetails = new JSONObject();
                final JSONObject inputMEdetails = (JSONObject)((JSONObject)dirProdImplRequest.args[1]).clone();
                final JSONObject prodMEtrackingDetails = directoryProductImpl.doProdSpecificMEtracking((Long)dirProdImplRequest.args[0], inputMEdetails);
                final List<String> prodMEkeys = directoryProductImpl.getProdSpecificMeTrackingKeys(dirProdImplRequest);
                for (int j = 0; j < prodMEkeys.size(); ++j) {
                    final String key = prodMEkeys.get(j);
                    if (prodMEtrackingDetails.containsKey((Object)key)) {
                        processedProdMEtrackingDetails.put((Object)key, prodMEtrackingDetails.get((Object)key));
                    }
                }
                ((JSONObject)response).putAll((Map)processedProdMEtrackingDetails);
                break;
            }
            case PROCESS_USER_IDF_DETAILS: {
                final Long resourceID = (Long)dirProdImplRequest.args[0];
                final JSONObject json = (JSONObject)dirProdImplRequest.args[1];
                final String email = (String)json.getOrDefault((Object)"EMAIL_ADDRESS", (Object)"--");
                final String phoneNumber = (String)json.getOrDefault((Object)"PHONE_NUMBER", (Object)"--");
                if (!IdpsUtil.getInstance().isValidEmail(email) || IdpsUtil.isStringEmpty(phoneNumber)) {
                    final JSONObject prodUserIDFjson = directoryProductImpl.getUserIDFdetails(resourceID);
                    if (prodUserIDFjson != null) {
                        if (!IdpsUtil.getInstance().isValidEmail(email)) {
                            final String managedUserEmail = (String)prodUserIDFjson.get((Object)"EMAIL_ADDRESS");
                            if (IdpsUtil.getInstance().isValidEmail(managedUserEmail)) {
                                json.put((Object)"EMAIL_ADDRESS", (Object)managedUserEmail);
                            }
                        }
                        if (IdpsUtil.isStringEmpty(phoneNumber)) {
                            final String managedPhoneNumber = (String)prodUserIDFjson.get((Object)"PHONE_NUMBER");
                            if (!IdpsUtil.isStringEmpty(managedPhoneNumber)) {
                                json.put((Object)"PHONE_NUMBER", (Object)managedPhoneNumber);
                            }
                        }
                    }
                }
                response = json;
                break;
            }
            case FEATURE_PARAMS: {
                final String featureKey = (String)dirProdImplRequest.args[1];
                final boolean checkFeatureAvailability = ((String)dirProdImplRequest.args[0]).equalsIgnoreCase("CHECK_FEATURE_AVAILABILITY");
                if (checkFeatureAvailability) {
                    Boolean curResponse = directoryProductImpl.isFeatureAvailable(featureKey);
                    if (curResponse == null) {
                        curResponse = false;
                    }
                    if (response == null) {
                        response = false;
                    }
                    response = ((boolean)response | curResponse);
                    break;
                }
                directoryProductImpl.updateFeatureAvailability(featureKey, String.valueOf(dirProdImplRequest.args[2]));
                break;
            }
            case GET_AUTO_VA_DISABLED_TABLES: {
                if (response == null) {
                    response = new ArrayList();
                }
                if (response == null) {
                    break;
                }
                final ArrayList<String> prodAutoVAdisabledTables = directoryProductImpl.getAutoVAdisabledTables(dirProdImplRequest);
                if (prodAutoVAdisabledTables != null && !prodAutoVAdisabledTables.isEmpty()) {
                    ((ArrayList)response).addAll(prodAutoVAdisabledTables);
                    break;
                }
                break;
            }
        }
        return response;
    }
    
    public Object invokeProductImpl(final DirProdImplRequest dirProdImplRequest) throws Exception {
        Object response = null;
        final JSONObject eventDetails = new JSONObject();
        final IdpEventConstants eventType = dirProdImplRequest.eventType;
        final Properties dmDomainProps = dirProdImplRequest.dmDomainProps;
        final Object[] dirProductImpls = IdpsFactoryProvider.getMultiImplClassInstance(IdpsFactoryConstant.PRODUCT_IMPL);
        if (dirProductImpls != null) {
            final String eventHint = eventType.getEventHint(dirProdImplRequest);
            final String eventKey = MessageFormat.format("{0} - {1}", eventType.toString(), eventType);
            for (final Object directoryProductImplObj : dirProductImpls) {
                final DirectoryProductAPI directoryProductImpl = (DirectoryProductAPI)directoryProductImplObj;
                final String curProductImpl = directoryProductImpl.getClass().getSimpleName();
                final long startTime = System.currentTimeMillis();
                if (!eventType.isTestSkippable() || this.isDirAllowedForEvent(dirProdImplRequest)) {
                    if (eventType.isCoreHandlingRequired()) {
                        response = this.doCoreHandling(eventType, response, directoryProductImpl, dirProdImplRequest);
                    }
                    else {
                        final String methodName = eventType.getInitializationMethod();
                        final Method method = directoryProductImpl.getClass().getMethod(methodName, dirProdImplRequest.getClass());
                        try {
                            response = method.invoke(directoryProductImpl, dirProdImplRequest);
                        }
                        catch (final InvocationTargetException e) {
                            if (e.getCause() instanceof RuntimeException) {
                                throw (RuntimeException)e.getCause();
                            }
                            throw e;
                        }
                    }
                }
                final long endTime = System.currentTimeMillis();
                final JSONObject productDetails = (JSONObject)eventDetails.getOrDefault((Object)eventKey, (Object)new JSONObject());
                productDetails.put((Object)curProductImpl, (Object)DirectoryUtil.getInstance().formatDurationMS(endTime - startTime));
                eventDetails.put((Object)eventKey, (Object)productDetails);
            }
            eventDetails.put((Object)"count", (Object)eventHint);
        }
        if (eventType.equals(IdpEventConstants.APPROVE_DOMAIN_DELETION)) {
            DirectoryUtil.getInstance().deleteDomain(dmDomainProps);
        }
        IDPSlogger.EVENT.log(Level.INFO, IdpsUtil.getPrettyJSON(eventDetails));
        return response;
    }
    
    static {
        DirectoryProductOpsHandler.directoryProductOpsHandler = null;
    }
}
