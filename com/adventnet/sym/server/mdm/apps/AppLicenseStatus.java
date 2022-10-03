package com.adventnet.sym.server.mdm.apps;

public class AppLicenseStatus
{
    public String licenseCode;
    public boolean isAlreadyRedeemed;
    public String redemptionLink;
    public static final int YET_TO_REDEEMED = 0;
    public static final int REDEEMED = 1;
    public static final int REVERTED = 2;
    public Long vpplicenseId;
    public Long adamId;
    public boolean isRevokable;
    public int status;
}
