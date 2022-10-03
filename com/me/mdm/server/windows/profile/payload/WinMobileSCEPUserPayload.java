package com.me.mdm.server.windows.profile.payload;

public class WinMobileSCEPUserPayload extends WinMobileSCEPPayload
{
    public WinMobileSCEPUserPayload(final String scepConfigName) {
        super(scepConfigName);
        this.baseLocURI = "./User/Vendor/MSFT/ClientCertificateInstall/SCEP/" + scepConfigName;
        this.baseInstallLocURI = this.baseLocURI + "/Install/";
    }
}
