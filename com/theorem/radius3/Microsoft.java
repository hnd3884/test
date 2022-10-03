package com.theorem.radius3;

public final class Microsoft
{
    public static final int VENDORID = 311;
    public static final int MS_CHAP_Response = 1;
    public static final int MS_CHAP_Error = 2;
    public static final int MS_CHAP_CPW_1 = 3;
    public static final int MS_CHAP_CPW_2 = 4;
    public static final int MS_CHAP_LM_Enc_PW = 5;
    public static final int MS_CHAP_NT_Enc_PW = 6;
    public static final int MS_MPPE_Encryption_Policy = 7;
    public static final int MS_MPPE_Encryption_Types = 8;
    public static final int MS_RAS_Vendor = 9;
    public static final int MS_CHAP_Domain = 10;
    public static final int MS_CHAP_Challenge = 11;
    public static final int MS_CHAP_MPPE_Keys = 12;
    public static final int MS_BAP_Usage = 13;
    public static final int MS_Link_Utilization_Threshold = 14;
    public static final int MS_Link_Drop_Time_Limit = 15;
    public static final int MS_MPPE_Send_Key = 16;
    public static final int MS_MPPE_Recv_Key = 17;
    public static final int MS_RAS_Version = 18;
    public static final int MS_Old_ARAP_Password = 19;
    public static final int MS_New_ARAP_Password = 20;
    public static final int MS_ARAP_PW_Change_Reason = 21;
    public static final int MS_Filter = 22;
    public static final int MS_Acct_Auth_Type = 23;
    public static final int MS_Acct_EAP_Type = 24;
    public static final int MS_CHAP2_Response = 25;
    public static final int MS_CHAP2_Success = 26;
    public static final int MS_CHAP2_CPW = 27;
    public static final int MS_Primary_DNS_Server = 28;
    public static final int MS_Secondary_DNS_Server = 29;
    public static final int MS_Primary_NBNS_Server = 30;
    public static final int MS_Secondary_NBNS_Server = 31;
    
    public Microsoft() {
        VendorSpecific.addVendor(this.getClass().getName(), 311);
    }
}
