package com.theorem.radius3;

public final class WISPr
{
    public static final int VENDORID = 14122;
    public static final int WISPr_Location_ID = 1;
    public static final int WISPr_Location_Name = 2;
    public static final int WISPr_Logoff_URL = 3;
    public static final int WISPr_Redirection_URL = 4;
    public static final int WISPr_Bandwidth_Min_Up = 5;
    public static final int WISPr_Bandwidth_Min_Down = 6;
    public static final int WISPr_Bandwidth_Max_Up = 7;
    public static final int WISPr_Bandwidth_Max_Down = 8;
    public static final int WISPr_Session_Terminate_Time = 9;
    public static final int WISPr_Session_Terminate_End_Of_Day = 10;
    public static final int WISPr_Billing_Class_Of_Service = 11;
    
    public WISPr() {
        VendorSpecific.addVendor(this.getClass().getName(), 14122);
    }
}
