package com.me.idps.core.factory;

import java.util.Hashtable;
import java.util.Properties;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.idps.core.service.okta.OktaHandler;
import com.me.idps.core.service.gsuite.GSuiteDirectoryServiceAccessProvider;
import com.me.idps.core.service.azure.AzureADAccessProvider;
import com.me.idps.core.oauth.OauthServiceAPI;
import java.util.Iterator;
import java.util.HashSet;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import java.util.ArrayList;

public class IdpsFactoryProvider
{
    private static IdpsProdEnvAPI idpsProdEnvAPI;
    private static IdpsAccessAPI gSuiteAccessAPI;
    private static IdpsAccessAPI azureADAccessAPI;
    private static IdpsAccessAPI onpremiseADAccessAPI;
    private static IdpsAccessAPI zohoDirectoryAccessAPI;
    private static IdpsAccessAPI oktaDirectoryAccessAPI;
    private static IdpsAccessAPI testDirectoryAccessAPI;
    
    public static Object[] getMultiImplClassInstance(final IdpsFactoryConstant key) {
        final ArrayList objs = new ArrayList();
        final HashSet<Class> classnames = IdpsImplRegistrar.getInstance().getImplClassName(key);
        if (classnames != null && !classnames.isEmpty()) {
            final Iterator<Class> itr = (Iterator<Class>)classnames.iterator();
            while (itr != null && itr.hasNext()) {
                Object implObj = null;
                final Class clazz = itr.next();
                try {
                    if (clazz != null) {
                        implObj = clazz.newInstance();
                    }
                    else {
                        IDPSlogger.ERR.log(Level.SEVERE, "class is null -> {0} for {1}", new Object[] { clazz, key });
                    }
                }
                catch (final InstantiationException ie) {
                    IDPSlogger.ERR.log(Level.SEVERE, "InstantiationException During Instantiation  for" + clazz, ie);
                }
                catch (final IllegalAccessException ie2) {
                    IDPSlogger.ERR.log(Level.SEVERE, "IllegalAccessException During Instantiation  for" + clazz, ie2);
                }
                catch (final Exception ex) {
                    IDPSlogger.ERR.log(Level.SEVERE, "Exception During Instantiation  for" + clazz, ex);
                }
                objs.add(implObj);
            }
        }
        return objs.toArray(new Object[objs.size()]);
    }
    
    public static Object getSingleImplClassInstance(final IdpsFactoryConstant key) {
        final Object[] impls = getMultiImplClassInstance(key);
        if (impls != null && impls.length == 1) {
            return impls[0];
        }
        return null;
    }
    
    public static IdpsProdEnvAPI getIdpsProdEnvAPI() {
        if (IdpsFactoryProvider.idpsProdEnvAPI == null) {
            IdpsFactoryProvider.idpsProdEnvAPI = (IdpsProdEnvAPI)getSingleImplClassInstance(IdpsFactoryConstant.DIRECTORY_DB_API);
        }
        return IdpsFactoryProvider.idpsProdEnvAPI;
    }
    
    public static OauthServiceAPI getOauthImpl(final int oauthType) {
        switch (oauthType) {
            case 201: {
                return (OauthServiceAPI)getSingleImplClassInstance(IdpsFactoryConstant.AZURE_OAUTH_IMPL);
            }
            case 202: {
                return (OauthServiceAPI)getSingleImplClassInstance(IdpsFactoryConstant.ASSIST_OAUTH_IMPL);
            }
            case 301: {
                return (OauthServiceAPI)getSingleImplClassInstance(IdpsFactoryConstant.CSEZ_ZD_OAUTH_IMPL);
            }
            case 203: {
                return (OauthServiceAPI)getSingleImplClassInstance(IdpsFactoryConstant.AZURE_MAM_OAUTH_IMPL);
            }
            case 204: {
                return (OauthServiceAPI)getSingleImplClassInstance(IdpsFactoryConstant.MIGRATION_TOOL_OAUTH_CHINA_TYPE);
            }
            case 205: {
                return (OauthServiceAPI)getSingleImplClassInstance(IdpsFactoryConstant.MIGRATION_TOOL_OAUTH_OTHER_TYPE);
            }
            default: {
                return null;
            }
        }
    }
    
    public static IdpsAccessAPI getIdpsAccessAPI(final int domainNetworkType) {
        switch (domainNetworkType) {
            case 2: {
                if (IdpsFactoryProvider.onpremiseADAccessAPI == null) {
                    IdpsFactoryProvider.onpremiseADAccessAPI = (IdpsAccessAPI)getSingleImplClassInstance(IdpsFactoryConstant.NATIVE_OP_IMPL);
                }
                return IdpsFactoryProvider.onpremiseADAccessAPI;
            }
            case 3: {
                if (IdpsFactoryProvider.azureADAccessAPI == null) {
                    IdpsFactoryProvider.azureADAccessAPI = AzureADAccessProvider.getInstance();
                }
                return IdpsFactoryProvider.azureADAccessAPI;
            }
            case 101: {
                if (IdpsFactoryProvider.gSuiteAccessAPI == null) {
                    IdpsFactoryProvider.gSuiteAccessAPI = GSuiteDirectoryServiceAccessProvider.getInstance();
                }
                return IdpsFactoryProvider.gSuiteAccessAPI;
            }
            case 201: {
                if (IdpsFactoryProvider.zohoDirectoryAccessAPI == null) {
                    try {
                        IdpsFactoryProvider.zohoDirectoryAccessAPI = (IdpsAccessAPI)getSingleImplClassInstance(IdpsFactoryConstant.ZD_SYNC_IMPL);
                    }
                    catch (final Exception ex) {
                        IDPSlogger.ERR.log(Level.SEVERE, "exception creating instance of zoho directory impl", ex);
                    }
                }
                return IdpsFactoryProvider.zohoDirectoryAccessAPI;
            }
            case 301: {
                if (IdpsFactoryProvider.oktaDirectoryAccessAPI == null) {
                    IdpsFactoryProvider.oktaDirectoryAccessAPI = OktaHandler.getInstance();
                }
                return IdpsFactoryProvider.oktaDirectoryAccessAPI;
            }
            case 10001:
            case 10002: {
                if (IdpsFactoryProvider.testDirectoryAccessAPI == null) {
                    try {
                        IdpsFactoryProvider.testDirectoryAccessAPI = (IdpsAccessAPI)Class.forName("com.me.idps.test.dataimpl.DirectorySyncTesterAccessImpl").newInstance();
                    }
                    catch (final Exception ex) {
                        IDPSlogger.ERR.log(Level.SEVERE, "exception creating instance of tester impl", ex);
                    }
                }
                return IdpsFactoryProvider.testDirectoryAccessAPI;
            }
            default: {
                return null;
            }
        }
    }
    
    public static IdpsAccessAPI getIdpsAccessAPI(final String domainName, final Long customerID) {
        final Properties domainProps = DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID);
        return getIdpsAccessAPI(((Hashtable<K, Integer>)domainProps).get("CLIENT_ID"));
    }
    
    static {
        IdpsFactoryProvider.idpsProdEnvAPI = null;
        IdpsFactoryProvider.gSuiteAccessAPI = null;
        IdpsFactoryProvider.azureADAccessAPI = null;
        IdpsFactoryProvider.onpremiseADAccessAPI = null;
        IdpsFactoryProvider.zohoDirectoryAccessAPI = null;
        IdpsFactoryProvider.oktaDirectoryAccessAPI = null;
        IdpsFactoryProvider.testDirectoryAccessAPI = null;
    }
}
