package sun.security.krb5.internal;

import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.KrbTgsReq;
import java.util.LinkedList;
import java.io.IOException;
import sun.security.krb5.Realm;
import sun.security.krb5.Config;
import sun.security.krb5.KrbException;
import sun.security.krb5.Credentials;
import sun.security.krb5.PrincipalName;

public class CredentialsUtil
{
    private static boolean DEBUG;
    
    public static Credentials acquireS4U2selfCreds(final PrincipalName principalName, final Credentials credentials) throws KrbException, IOException {
        if (!credentials.isForwardable()) {
            throw new KrbException("S4U2self needs a FORWARDABLE ticket");
        }
        PrincipalName principalName2 = credentials.getClient();
        final String realmString = principalName.getRealmString();
        if (!realmString.equals(credentials.getClient().getRealmString())) {
            if (Config.DISABLE_REFERRALS) {
                throw new KrbException("Cross-realm S4U2Self request not possible when referrals are disabled.");
            }
            if (credentials.getClientAlias() != null) {
                principalName2 = credentials.getClientAlias();
            }
            principalName2 = new PrincipalName(principalName2.getNameType(), principalName2.getNameStrings(), new Realm(realmString));
        }
        final Credentials serviceCreds = serviceCreds(KDCOptions.with(1), credentials, credentials.getClient(), principalName2, null, new PAData[] { new PAData(129, new PAForUserEnc(principalName, credentials.getSessionKey()).asn1Encode()), new PAData(167, new PaPacOptions().setResourceBasedConstrainedDelegation(true).setClaims(true).asn1Encode()) }, S4U2Type.SELF);
        if (!serviceCreds.getClient().equals(principalName)) {
            throw new KrbException("S4U2self request not honored by KDC");
        }
        if (!serviceCreds.isForwardable()) {
            throw new KrbException("S4U2self ticket must be FORWARDABLE");
        }
        return serviceCreds;
    }
    
    public static Credentials acquireS4U2proxyCreds(final String s, final Ticket ticket, final PrincipalName principalName, final Credentials credentials) throws KrbException, IOException {
        PrincipalName principalName2 = new PrincipalName(s);
        final String realmString = principalName2.getRealmString();
        final String realmString2 = credentials.getClient().getRealmString();
        if (!realmString.equals(realmString2)) {
            if (Config.DISABLE_REFERRALS) {
                throw new KrbException("Cross-realm S4U2Proxy request not possible when referrals are disabled.");
            }
            principalName2 = new PrincipalName(principalName2.getNameType(), principalName2.getNameStrings(), new Realm(realmString2));
        }
        final Credentials serviceCreds = serviceCreds(KDCOptions.with(14, 1), credentials, credentials.getClient(), principalName2, new Ticket[] { ticket }, new PAData[] { new PAData(167, new PaPacOptions().setResourceBasedConstrainedDelegation(true).setClaims(true).asn1Encode()) }, S4U2Type.PROXY);
        if (!serviceCreds.getClient().equals(principalName)) {
            throw new KrbException("S4U2proxy request not honored by KDC");
        }
        return serviceCreds;
    }
    
    public static Credentials acquireServiceCreds(final String s, final Credentials credentials) throws KrbException, IOException {
        return serviceCreds(new PrincipalName(s, 0), credentials);
    }
    
    private static Credentials getTGTforRealm(final String s, final String s2, final Credentials credentials, final boolean[] array) throws KrbException {
        final String[] realmsList = Realm.getRealmsList(s, s2);
        Credentials credentials2 = null;
        array[0] = true;
        Credentials credentials3 = credentials;
        int i = 0;
        while (i < realmsList.length) {
            final PrincipalName tgsService = PrincipalName.tgsService(s2, realmsList[i]);
            if (CredentialsUtil.DEBUG) {
                System.out.println(">>> Credentials acquireServiceCreds: main loop: [" + i + "] tempService=" + tgsService);
            }
            Credentials credentials4;
            try {
                credentials4 = serviceCreds(tgsService, credentials3);
            }
            catch (final Exception ex) {
                credentials4 = null;
            }
            if (credentials4 == null) {
                if (CredentialsUtil.DEBUG) {
                    System.out.println(">>> Credentials acquireServiceCreds: no tgt; searching thru capath");
                }
                credentials4 = null;
                for (int n = i + 1; credentials4 == null && n < realmsList.length; ++n) {
                    final PrincipalName tgsService2 = PrincipalName.tgsService(realmsList[n], realmsList[i]);
                    if (CredentialsUtil.DEBUG) {
                        System.out.println(">>> Credentials acquireServiceCreds: inner loop: [" + n + "] tempService=" + tgsService2);
                    }
                    try {
                        credentials4 = serviceCreds(tgsService2, credentials3);
                    }
                    catch (final Exception ex2) {
                        credentials4 = null;
                    }
                }
            }
            if (credentials4 == null) {
                if (CredentialsUtil.DEBUG) {
                    System.out.println(">>> Credentials acquireServiceCreds: no tgt; cannot get creds");
                    break;
                }
                break;
            }
            else {
                final String instanceComponent = credentials4.getServer().getInstanceComponent();
                if (array[0] && !credentials4.checkDelegate()) {
                    if (CredentialsUtil.DEBUG) {
                        System.out.println(">>> Credentials acquireServiceCreds: global OK-AS-DELEGATE turned off at " + credentials4.getServer());
                    }
                    array[0] = false;
                }
                if (CredentialsUtil.DEBUG) {
                    System.out.println(">>> Credentials acquireServiceCreds: got tgt");
                }
                if (instanceComponent.equals(s2)) {
                    credentials2 = credentials4;
                    break;
                }
                int n2;
                for (n2 = i + 1; n2 < realmsList.length && !instanceComponent.equals(realmsList[n2]); ++n2) {}
                if (n2 >= realmsList.length) {
                    break;
                }
                i = n2;
                credentials3 = credentials4;
                if (!CredentialsUtil.DEBUG) {
                    continue;
                }
                System.out.println(">>> Credentials acquireServiceCreds: continuing with main loop counter reset to " + i);
            }
        }
        return credentials2;
    }
    
    private static Credentials serviceCreds(final PrincipalName principalName, final Credentials credentials) throws KrbException, IOException {
        return serviceCreds(new KDCOptions(), credentials, credentials.getClient(), principalName, null, null, S4U2Type.NONE);
    }
    
    private static Credentials serviceCreds(final KDCOptions kdcOptions, final Credentials credentials, final PrincipalName principalName, final PrincipalName principalName2, final Ticket[] array, final PAData[] array2, final S4U2Type s4U2Type) throws KrbException, IOException {
        if (!Config.DISABLE_REFERRALS) {
            try {
                return serviceCredsReferrals(kdcOptions, credentials, principalName, principalName2, s4U2Type, array, array2);
            }
            catch (final KrbException ex) {}
        }
        return serviceCredsSingle(kdcOptions, credentials, principalName, credentials.getClientAlias(), principalName2, principalName2, s4U2Type, array, array2);
    }
    
    private static Credentials serviceCredsReferrals(KDCOptions kdcOptions, Credentials credentials, final PrincipalName principalName, final PrincipalName principalName2, final S4U2Type s4U2Type, final Ticket[] array, final PAData[] array2) throws KrbException, IOException {
        kdcOptions = new KDCOptions(kdcOptions.toBooleanArray());
        kdcOptions.set(15, true);
        PrincipalName principalName3 = principalName2;
        Credentials credentials2 = null;
        int n = 0;
        final LinkedList list = new LinkedList();
        final PrincipalName clientAlias = credentials.getClientAlias();
        while (list.size() <= Config.MAX_REFERRALS) {
            final ReferralsCache.ReferralCacheEntry value = ReferralsCache.get(principalName, principalName2, principalName3.getRealmString());
            String s = null;
            if (value == null) {
                credentials2 = serviceCredsSingle(kdcOptions, credentials, principalName, clientAlias, principalName3, principalName2, s4U2Type, array, array2);
                final PrincipalName server = credentials2.getServer();
                if (!principalName3.equals(server)) {
                    final String[] nameStrings = server.getNameStrings();
                    if (nameStrings.length == 2 && nameStrings[0].equals("krbtgt") && !principalName3.getRealmAsString().equals(nameStrings[1])) {
                        if (s4U2Type == S4U2Type.NONE) {
                            ReferralsCache.put(principalName, principalName2, server.getRealmString(), nameStrings[1], credentials2);
                        }
                        s = nameStrings[1];
                        n = 1;
                    }
                }
            }
            else {
                credentials2 = value.getCreds();
                s = value.getToRealm();
                n = 1;
            }
            if (n == 0) {
                break;
            }
            if (s4U2Type == S4U2Type.PROXY) {
                final Credentials[] array3 = { credentials2, null };
                s = handleS4U2ProxyReferral(credentials, array3, principalName2);
                credentials2 = array3[0];
                if (array == null || array.length == 0 || array3[1] == null) {
                    throw new KrbException("Additional tickets expected for S4U2Proxy.");
                }
                array[0] = array3[1].getTicket();
            }
            else if (s4U2Type == S4U2Type.SELF) {
                handleS4U2SelfReferral(array2, credentials, credentials2);
            }
            if (list.contains(s)) {
                return null;
            }
            credentials = credentials2;
            principalName3 = new PrincipalName(principalName3.getNameString(), principalName3.getNameType(), s);
            list.add(s);
            n = 0;
        }
        return credentials2;
    }
    
    private static Credentials serviceCredsSingle(final KDCOptions kdcOptions, Credentials credentials, PrincipalName client, final PrincipalName principalName, final PrincipalName principalName2, final PrincipalName principalName3, final S4U2Type s4U2Type, final Ticket[] array, final PAData[] array2) throws KrbException, IOException {
        final boolean[] array3 = { true };
        final String s = credentials.getServer().getNameStrings()[1];
        final String realmString = principalName2.getRealmString();
        if (!realmString.equals(s)) {
            if (CredentialsUtil.DEBUG) {
                System.out.println(">>> serviceCredsSingle: cross-realm authentication");
                System.out.println(">>> serviceCredsSingle: obtaining credentials from " + s + " to " + realmString);
            }
            final Credentials tgTforRealm = getTGTforRealm(s, realmString, credentials, array3);
            if (tgTforRealm == null) {
                throw new KrbApErrException(63, "No service creds");
            }
            if (CredentialsUtil.DEBUG) {
                System.out.println(">>> Cross-realm TGT Credentials serviceCredsSingle: ");
                Credentials.printDebug(tgTforRealm);
            }
            if (s4U2Type == S4U2Type.SELF) {
                handleS4U2SelfReferral(array2, credentials, tgTforRealm);
            }
            credentials = tgTforRealm;
            client = credentials.getClient();
        }
        else if (CredentialsUtil.DEBUG) {
            System.out.println(">>> Credentials serviceCredsSingle: same realm");
        }
        final Credentials sendAndGetCreds = new KrbTgsReq(kdcOptions, credentials, client, principalName, principalName2, principalName3, array, array2).sendAndGetCreds();
        if (sendAndGetCreds != null) {
            if (CredentialsUtil.DEBUG) {
                System.out.println(">>> TGS credentials serviceCredsSingle:");
                Credentials.printDebug(sendAndGetCreds);
            }
            if (!array3[0]) {
                sendAndGetCreds.resetDelegate();
            }
        }
        return sendAndGetCreds;
    }
    
    private static void handleS4U2SelfReferral(final PAData[] array, final Credentials credentials, final Credentials credentials2) throws Asn1Exception, KrbException, IOException {
        if (CredentialsUtil.DEBUG) {
            System.out.println(">>> Handling S4U2Self referral");
        }
        for (int i = 0; i < array.length; ++i) {
            final PAData paData = array[i];
            if (paData.getType() == 129) {
                array[i] = new PAData(129, new PAForUserEnc(new PAForUserEnc(new DerValue(paData.getValue()), credentials.getSessionKey()).getName(), credentials2.getSessionKey()).asn1Encode());
                break;
            }
        }
    }
    
    private static String handleS4U2ProxyReferral(final Credentials credentials, final Credentials[] array, final PrincipalName principalName) throws KrbException, IOException {
        if (CredentialsUtil.DEBUG) {
            System.out.println(">>> Handling S4U2Proxy referral");
        }
        final String realmString = serviceCreds(principalName, credentials).getServer().getRealmString();
        final String s = array[0].getServer().getNameStrings()[1];
        Credentials tgTforRealm;
        if (!s.equals(realmString)) {
            tgTforRealm = getTGTforRealm(s, realmString, array[0], new boolean[1]);
        }
        else {
            tgTforRealm = array[0];
        }
        array[0] = getTGTforRealm(credentials.getClient().getRealmString(), realmString, credentials, new boolean[1]);
        array[1] = tgTforRealm;
        return realmString;
    }
    
    static {
        CredentialsUtil.DEBUG = Krb5.DEBUG;
    }
    
    private enum S4U2Type
    {
        NONE, 
        SELF, 
        PROXY;
    }
}
