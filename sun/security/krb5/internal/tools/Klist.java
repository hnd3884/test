package sun.security.krb5.internal.tools;

import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KerberosTime;
import java.util.Iterator;
import java.util.List;
import java.net.InetAddress;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.ccache.Credentials;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.ktab.KeyTabEntry;
import sun.security.krb5.internal.ktab.KeyTab;
import sun.security.krb5.internal.ccache.CredentialsCache;

public class Klist
{
    Object target;
    char[] options;
    String name;
    char action;
    private static boolean DEBUG;
    
    public Klist() {
        this.options = new char[4];
    }
    
    public static void main(final String[] array) {
        final Klist klist = new Klist();
        if (array == null || array.length == 0) {
            klist.action = 'c';
        }
        else {
            klist.processArgs(array);
        }
        switch (klist.action) {
            case 'c': {
                if (klist.name == null) {
                    klist.target = CredentialsCache.getInstance();
                    klist.name = CredentialsCache.cacheName();
                }
                else {
                    klist.target = CredentialsCache.getInstance(klist.name);
                }
                if (klist.target != null) {
                    klist.displayCache();
                    break;
                }
                klist.displayMessage("Credentials cache");
                System.exit(-1);
                break;
            }
            case 'k': {
                final KeyTab instance = KeyTab.getInstance(klist.name);
                if (instance.isMissing()) {
                    System.out.println("KeyTab " + klist.name + " not found.");
                    System.exit(-1);
                }
                else if (!instance.isValid()) {
                    System.out.println("KeyTab " + klist.name + " format not supported.");
                    System.exit(-1);
                }
                klist.target = instance;
                klist.name = instance.tabName();
                klist.displayTab();
                break;
            }
            default: {
                if (klist.name != null) {
                    klist.printHelp();
                    System.exit(-1);
                    break;
                }
                klist.target = CredentialsCache.getInstance();
                klist.name = CredentialsCache.cacheName();
                if (klist.target != null) {
                    klist.displayCache();
                    break;
                }
                klist.displayMessage("Credentials cache");
                System.exit(-1);
                break;
            }
        }
    }
    
    void processArgs(final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].length() >= 2 && array[i].startsWith("-")) {
                switch ((char)new Character(array[i].charAt(1))) {
                    case 'c': {
                        this.action = 'c';
                        break;
                    }
                    case 'k': {
                        this.action = 'k';
                        break;
                    }
                    case 'a': {
                        this.options[2] = 'a';
                        break;
                    }
                    case 'n': {
                        this.options[3] = 'n';
                        break;
                    }
                    case 'f': {
                        this.options[1] = 'f';
                        break;
                    }
                    case 'e': {
                        this.options[0] = 'e';
                        break;
                    }
                    case 'K': {
                        this.options[1] = 'K';
                        break;
                    }
                    case 't': {
                        this.options[2] = 't';
                        break;
                    }
                    default: {
                        this.printHelp();
                        System.exit(-1);
                        break;
                    }
                }
            }
            else if (!array[i].startsWith("-") && i == array.length - 1) {
                this.name = array[i];
            }
            else {
                this.printHelp();
                System.exit(-1);
            }
        }
    }
    
    void displayTab() {
        final KeyTabEntry[] entries = ((KeyTab)this.target).getEntries();
        if (entries.length == 0) {
            System.out.println("\nKey tab: " + this.name + ",  0 entries found.\n");
        }
        else {
            if (entries.length == 1) {
                System.out.println("\nKey tab: " + this.name + ", " + entries.length + " entry found.\n");
            }
            else {
                System.out.println("\nKey tab: " + this.name + ", " + entries.length + " entries found.\n");
            }
            for (int i = 0; i < entries.length; ++i) {
                System.out.println("[" + (i + 1) + "] Service principal: " + entries[i].getService().toString());
                System.out.println("\t KVNO: " + entries[i].getKey().getKeyVersionNumber());
                if (this.options[0] == 'e') {
                    System.out.println("\t Key type: " + entries[i].getKey().getEType());
                }
                if (this.options[1] == 'K') {
                    entries[i].getKey();
                    System.out.println("\t Key: " + entries[i].getKeyString());
                }
                if (this.options[2] == 't') {
                    System.out.println("\t Time stamp: " + this.format(entries[i].getTimeStamp()));
                }
            }
        }
    }
    
    void displayCache() {
        final CredentialsCache credentialsCache = (CredentialsCache)this.target;
        final Credentials[] credsList = credentialsCache.getCredsList();
        if (credsList == null) {
            System.out.println("No credentials available in the cache " + this.name);
            System.exit(-1);
        }
        System.out.println("\nCredentials cache: " + this.name);
        final String string = credentialsCache.getPrimaryPrincipal().toString();
        if (credsList.length == 1) {
            System.out.println("\nDefault principal: " + string + ", " + credsList.length + " entry found.\n");
        }
        else {
            System.out.println("\nDefault principal: " + string + ", " + credsList.length + " entries found.\n");
        }
        if (credsList != null) {
            for (int i = 0; i < credsList.length; ++i) {
                try {
                    String s;
                    if (credsList[i].getStartTime() != null) {
                        s = this.format(credsList[i].getStartTime());
                    }
                    else {
                        s = this.format(credsList[i].getAuthTime());
                    }
                    final String format = this.format(credsList[i].getEndTime());
                    System.out.println("[" + (i + 1) + "]  Service Principal:  " + credsList[i].getServicePrincipal().toString());
                    final PrincipalName servicePrincipal2 = credsList[i].getServicePrincipal2();
                    if (servicePrincipal2 != null) {
                        System.out.println("     Second Service:     " + servicePrincipal2);
                    }
                    final String string2 = credsList[i].getClientPrincipal().toString();
                    if (!string2.equals(string)) {
                        System.out.println("     Client Principal:   " + string2);
                    }
                    System.out.println("     Valid starting:     " + s);
                    System.out.println("     Expires:            " + format);
                    if (credsList[i].getRenewTill() != null) {
                        System.out.println("     Renew until:        " + this.format(credsList[i].getRenewTill()));
                    }
                    if (this.options[0] == 'e') {
                        final String string3 = EType.toString(credsList[i].getEType());
                        final String string4 = EType.toString(credsList[i].getTktEType());
                        if (credsList[i].getTktEType2() == 0) {
                            System.out.println("     EType (skey, tkt):  " + string3 + ", " + string4);
                        }
                        else {
                            System.out.println("     EType (skey, tkts): " + string3 + ", " + string4 + ", " + EType.toString(credsList[i].getTktEType2()));
                        }
                    }
                    if (this.options[1] == 'f') {
                        System.out.println("     Flags:              " + credsList[i].getTicketFlags().toString());
                    }
                    if (this.options[2] == 'a') {
                        int n = 1;
                        final InetAddress[] clientAddresses = credsList[i].setKrbCreds().getClientAddresses();
                        if (clientAddresses != null) {
                            for (final InetAddress inetAddress : clientAddresses) {
                                String s2;
                                if (this.options[3] == 'n') {
                                    s2 = inetAddress.getHostAddress();
                                }
                                else {
                                    s2 = inetAddress.getCanonicalHostName();
                                }
                                System.out.println("     " + ((n != 0) ? "Addresses:" : "          ") + "       " + s2);
                                n = 0;
                            }
                        }
                        else {
                            System.out.println("     [No host addresses info]");
                        }
                    }
                }
                catch (final RealmException ex) {
                    System.out.println("Error reading principal from the entry.");
                    if (Klist.DEBUG) {
                        ex.printStackTrace();
                    }
                    System.exit(-1);
                }
            }
        }
        else {
            System.out.println("\nNo entries found.");
        }
        final List<CredentialsCache.ConfigEntry> configEntries = credentialsCache.getConfigEntries();
        if (configEntries != null && !configEntries.isEmpty()) {
            System.out.println("\nConfig entries:");
            final Iterator iterator = configEntries.iterator();
            while (iterator.hasNext()) {
                System.out.println("     " + iterator.next());
            }
        }
    }
    
    void displayMessage(final String s) {
        if (this.name == null) {
            System.out.println("Default " + s + " not found.");
        }
        else {
            System.out.println(s + " " + this.name + " not found.");
        }
    }
    
    private String format(final KerberosTime kerberosTime) {
        final String string = kerberosTime.toDate().toString();
        return string.substring(4, 7) + " " + string.substring(8, 10) + ", " + string.substring(24) + " " + string.substring(11, 19);
    }
    
    void printHelp() {
        System.out.println("\nUsage: klist [[-c] [-f] [-e] [-a [-n]]] [-k [-t] [-K]] [name]");
        System.out.println("   name\t name of credentials cache or  keytab with the prefix. File-based cache or keytab's prefix is FILE:.");
        System.out.println("   -c specifies that credential cache is to be listed");
        System.out.println("   -k specifies that key tab is to be listed");
        System.out.println("   options for credentials caches:");
        System.out.println("\t-f \t shows credentials flags");
        System.out.println("\t-e \t shows the encryption type");
        System.out.println("\t-a \t shows addresses");
        System.out.println("\t  -n \t   do not reverse-resolve addresses");
        System.out.println("   options for keytabs:");
        System.out.println("\t-t \t shows keytab entry timestamps");
        System.out.println("\t-K \t shows keytab entry key value");
        System.out.println("\t-e \t shows keytab entry key type");
        System.out.println("\nUsage: java sun.security.krb5.tools.Klist -help for help.");
    }
    
    static {
        Klist.DEBUG = Krb5.DEBUG;
    }
}
