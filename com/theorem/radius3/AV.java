package com.theorem.radius3;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import com.theorem.radius3.dictionary.RADIUSDictionary;

public final class AV
{
    public static String lookup(final int n, final int n2) {
        return lookup(0, n, n2);
    }
    
    public static String lookup(final int n, final int n2, final int n3) {
        final RADIUSDictionary[] dictionaries = Dict.getDictionaries();
        for (int i = 0; i < dictionaries.length; ++i) {
            final String valueName = dictionaries[i].getValueName(n, n2, n3);
            if (valueName != null) {
                return valueName;
            }
        }
        if (n != 0) {
            return "";
        }
        Label_0815: {
            switch (n2) {
                case 6: {
                    switch (n3) {
                        case 6: {}
                        case 4: {}
                        case 11: {}
                        case 2: {}
                        case 7: {}
                        case 10: {}
                        case 9: {}
                        case 8: {}
                        case 1: {}
                        case 3: {}
                    }
                    break;
                }
                case 15: {
                    switch (n3) {
                        case 2: {}
                        case 1: {}
                        case 0: {}
                        case 4: {}
                        case 8: {}
                        case 3: {}
                        case 6: {}
                    }
                    break;
                }
                case 61: {
                    switch (n3) {
                        case 7: {}
                        case 6: {}
                        case 12: {}
                        case 14: {}
                        case 19: {}
                        case 18: {}
                        case 10: {}
                        case 0: {}
                        case 3: {}
                        case 9: {}
                        case 8: {}
                        case 1: {}
                        case 17: {}
                        case 2: {}
                        case 13: {}
                        case 5: {}
                        case 16: {}
                        case 4: {}
                        case 11: {}
                    }
                    break;
                }
                case 65: {
                    switch (n3) {
                        case 8: {}
                        case 15: {}
                        case 12: {}
                        case 7: {}
                        case 1: {}
                        case 6: {}
                        case 11: {}
                        case 9: {}
                        case 14: {}
                        case 4: {}
                        case 2: {}
                        case 13: {}
                        case 3: {}
                        case 10: {
                            break Label_0815;
                        }
                    }
                    break;
                }
                case 64: {
                    switch (n3) {
                        case 9: {}
                        case 4: {}
                        case 5: {}
                        case 11: {}
                        case 12: {}
                        case 7: {}
                        case 10: {}
                        case 6: {}
                        case 1: {}
                        case 8: {}
                        case 2: {}
                    }
                    break;
                }
                case 29: {
                    switch (n3) {
                        case 0: {}
                    }
                    break;
                }
                case 40: {
                    switch (n3) {
                        case 1: {}
                        case 8: {}
                        case 2: {}
                        case 7: {}
                    }
                    break;
                }
                case 10: {
                    switch (n3) {
                        case 0: {}
                        case 2: {}
                        case 3: {}
                    }
                    break;
                }
                case 45: {
                    switch (n3) {
                        case 3: {}
                        case 1: {}
                    }
                    break;
                }
                case 49: {
                    switch (n3) {
                        case 5: {}
                        case 1: {}
                        case 18: {}
                        case 17: {}
                        case 8: {}
                        case 11: {}
                        case 13: {}
                        case 2: {}
                        case 4: {}
                        case 15: {}
                        case 12: {}
                        case 6: {}
                        case 16: {}
                        case 14: {}
                        case 7: {}
                    }
                    break;
                }
                case 72: {
                    switch (n3) {
                        case 2: {}
                        case 3: {}
                    }
                    break;
                }
                case 7: {
                    switch (n3) {
                        case 4: {}
                        case 2: {}
                        case 1: {}
                        case 6: {}
                        case 3: {}
                    }
                    break;
                }
            }
        }
        return "";
    }
    
    public static void addDictionary(final RADIUSDictionary radiusDictionary) {
        Dict.addDictionary(radiusDictionary);
    }
    
    public static int lookup(final int n, final String s) {
        final RADIUSDictionary[] dictionaries = Dict.getDictionaries();
        for (int i = 0; i < dictionaries.length; ++i) {
            final int intValue = dictionaries[i].getIntValue(0, n, s);
            if (intValue != -1) {
                return intValue;
            }
        }
        final String replace = AttributeName.lookup(n).replace('-', '_');
        final Class<? extends AV> class1 = new AV().getClass();
        final int n2 = class1.getName().length() + 1;
        final Class[] classes = class1.getClasses();
        for (int j = 0; j < classes.length; ++j) {
            if (replace.equals(classes[j].getName().substring(n2))) {
                try {
                    return classes[j].getField(s).getInt(null);
                }
                catch (final IllegalAccessException ex) {
                    return -1;
                }
                catch (final NoSuchFieldException ex2) {
                    return -1;
                }
            }
        }
        return -1;
    }
    
    public static int[] getAllValues(final int n) {
        return getAllValues(0, n);
    }
    
    public static int[] getAllValues(final int n, final int n2) {
        final String lookup = AttributeName.lookup(n, n2);
        final RADIUSDictionary[] dictionaries = Dict.getDictionaries();
        for (int i = 0; i < dictionaries.length; ++i) {
            final String[] allValueNames = dictionaries[i].getAllValueNames(n, lookup);
            if (allValueNames.length != 0) {
                final int[] array = new int[allValueNames.length];
                for (int j = 0; j < allValueNames.length; ++j) {
                    array[j] = dictionaries[i].getIntValue(n, n2, allValueNames[j]);
                }
                Arrays.sort(array);
                return array;
            }
        }
        final String replace = lookup.replace('-', '_');
        final Class<? extends AV> class1 = new AV().getClass();
        final int n3 = class1.getName().length() + 1;
        final Class[] classes = class1.getClasses();
        final ArrayList list = new ArrayList<Integer>();
        for (int k = 0; k < classes.length; ++k) {
            if (replace.equals(classes[k].getName().substring(n3))) {
                try {
                    final Field[] fields = classes[k].getFields();
                    for (int l = 0; l < fields.length; ++l) {
                        list.add(new Integer(fields[l].getInt(null)));
                    }
                    break;
                }
                catch (final IllegalAccessException ex) {
                    return new int[0];
                }
            }
        }
        final int[] array2 = new int[list.size()];
        for (int n4 = 0; n4 < array2.length; ++n4) {
            array2[n4] = (int)list.get(n4);
        }
        Arrays.sort(array2);
        return array2;
    }
    
    public static final class ARAP_Zone_Access
    {
        public static final int Default = 1;
        public static final int Inclusively = 2;
        public static final int Exclusively = 3;
    }
    
    public static final class Acct_Authentic
    {
        public static final int RADIUS = 1;
        public static final int Local = 2;
        public static final int Remote = 3;
    }
    
    public static final class Acct_Status_Type
    {
        public static final int Start = 1;
        public static final int Stop = 2;
        public static final int Interim_Update = 3;
        public static final int Accounting_On = 7;
        public static final int Accounting_Off = 8;
    }
    
    public static final class Acct_Terminate_Cause
    {
        public static final int User_Request = 1;
        public static final int Lost_Carrier = 2;
        public static final int Lost_Service = 3;
        public static final int Idle_Timeout = 4;
        public static final int Session_Timeout = 5;
        public static final int Admin_Reset = 6;
        public static final int Admin_Reboot = 7;
        public static final int Port_Error = 8;
        public static final int NAS_Error = 9;
        public static final int NAS_Request = 10;
        public static final int NAS_Reboot = 11;
        public static final int Port_Unneeded = 12;
        public static final int Port_Preempted = 13;
        public static final int Port_Suspended = 14;
        public static final int Service_Unavailable = 15;
        public static final int Callback = 16;
        public static final int User_Error = 17;
        public static final int Host_Request = 18;
    }
    
    public static final class Error_Cause
    {
        public static final int Residual_Session_Context_Removed = 201;
        public static final int Invalid_EAP_Packet = 202;
        public static final int Unsupported_Attribute = 401;
        public static final int Missing_Attribute = 402;
        public static final int NAS_Identification_Mismatch = 403;
        public static final int Invalid_Request = 404;
        public static final int Unsupported_Service = 405;
        public static final int Unsupported_Extension = 406;
        public static final int Administratively_Prohibited = 501;
        public static final int Request_Not_Routable = 502;
        public static final int Session_Context_Not_Found = 503;
        public static final int Session_Context_Not_Removable = 504;
        public static final int Other_Proxy_Processing_Error = 505;
        public static final int Resources_Unavailable = 506;
        public static final int Request_Initiated = 507;
    }
    
    public static final class Framed_Compression
    {
        public static final int None = 0;
        public static final int VJ = 1;
        public static final int IPX = 2;
        public static final int Stac_LZS = 3;
    }
    
    public static final class Framed_Protocol
    {
        public static final int PPP = 1;
        public static final int SLIP = 2;
        public static final int ARAP = 3;
        public static final int Gandalf = 4;
        public static final int Xylogics = 5;
        public static final int X75_Synchronous = 6;
    }
    
    public static final class Framed_Routing
    {
        public static final int No_Framed_Routing = 0;
        public static final int Send_routing_packets = 1;
        public static final int Listen_for_routing_packets = 2;
        public static final int Send_and_Listen = 3;
    }
    
    public static final class Login_Service
    {
        public static final int Telnet = 0;
        public static final int Rlogin = 1;
        public static final int TCP_Clear = 2;
        public static final int PortMaster = 3;
        public static final int LAT = 4;
        public static final int X25_PAD = 5;
        public static final int X25_T3POS = 6;
        public static final int TCP_Clear_Quiet = 8;
    }
    
    public static final class NAS_Port_Type
    {
        public static final int Async = 0;
        public static final int Sync = 1;
        public static final int ISDN_Sync = 2;
        public static final int ISDN_Async_V120 = 3;
        public static final int ISDN_Async_V110 = 4;
        public static final int Virtual = 5;
        public static final int PIAFS = 6;
        public static final int HDLC_Clear_Channel = 7;
        public static final int X25 = 8;
        public static final int X75 = 9;
        public static final int G3_Fax = 10;
        public static final int SDSL = 11;
        public static final int ADSL_CAP = 12;
        public static final int ADSL_DMT = 13;
        public static final int IDSL = 14;
        public static final int Ethernet = 15;
        public static final int xDSL = 16;
        public static final int Cable = 17;
        public static final int Wireless = 18;
        public static final int Wireless_802_11 = 19;
    }
    
    public static final class Service_Type
    {
        public static final int Login = 1;
        public static final int Framed = 2;
        public static final int Callback_Login = 3;
        public static final int Callback_Framed = 4;
        public static final int Outbound = 5;
        public static final int Administrative = 6;
        public static final int NAS_Prompt = 7;
        public static final int Authenticate_Only = 8;
        public static final int Callback_NAS_Prompt = 9;
        public static final int Call_Check = 10;
        public static final int Callback_Administrative = 11;
        public static final int Voice = 12;
        public static final int Fax = 13;
        public static final int Modem_Relay = 14;
        public static final int IAPP_Register = 15;
        public static final int Authorize_Only = 17;
    }
    
    public static final class Termination_Action
    {
        public static final int Default = 0;
        public static final int RADIUS_Request = 1;
    }
    
    public static final class Tunnel_Medium_Type
    {
        public static final int IPv4 = 1;
        public static final int IPv6 = 2;
        public static final int NSAP = 3;
        public static final int HDLC = 4;
        public static final int BBN_1822 = 5;
        public static final int Media_802 = 6;
        public static final int POTS = 7;
        public static final int SMDS = 8;
        public static final int Telex = 9;
        public static final int X_25 = 10;
        public static final int Frame_Relay = 10;
        public static final int IPX = 11;
        public static final int Appletalk = 12;
        public static final int Decnet_IV = 13;
        public static final int Banyan_Vines = 14;
        public static final int E_164 = 15;
    }
    
    public static final class Tunnel_Type
    {
        public static final int PPTP = 1;
        public static final int L2F = 2;
        public static final int L2TP = 3;
        public static final int ATMP = 4;
        public static final int VTP = 5;
        public static final int AH = 6;
        public static final int IP_IP = 7;
        public static final int MIN_IP_IP = 8;
        public static final int ESP = 9;
        public static final int GRE = 10;
        public static final int DVS = 11;
        public static final int IP_in_IP = 12;
    }
}
