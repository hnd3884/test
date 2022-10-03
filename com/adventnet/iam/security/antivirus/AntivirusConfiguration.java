package com.adventnet.iam.security.antivirus;

import com.adventnet.iam.security.antivirus.restapi.RestApiVendorAV;
import com.adventnet.iam.security.antivirus.icap.ICAPVendorAV;
import com.adventnet.iam.security.antivirus.clamav.CLAMVendorAV;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import com.adventnet.iam.security.antivirus.restapi.RestApiConfiguration;
import com.adventnet.iam.security.antivirus.icap.IcapAvConfiguration;
import com.adventnet.iam.security.antivirus.clamav.CLAMAVConfiguration;

public class AntivirusConfiguration
{
    private final CLAMAVConfiguration clamAvConfig;
    private final IcapAvConfiguration icapAvConfig;
    private final RestApiConfiguration restApiAvConfig;
    
    private AntivirusConfiguration(final CLAMAVConfiguration clamAvConfig, final IcapAvConfiguration icapAvConfig, final RestApiConfiguration restApiAvConfig) {
        this.clamAvConfig = clamAvConfig;
        this.icapAvConfig = icapAvConfig;
        this.restApiAvConfig = restApiAvConfig;
    }
    
    public static AntivirusConfiguration init(final CLAMAVConfiguration clamAvConfig, final String... configClasses) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        IcapAvConfiguration icapConfiguration = null;
        RestApiConfiguration restApiConfiguration = null;
        if (configClasses != null) {
            for (final String configClass : configClasses) {
                if (configClass != null) {
                    final Object obj = cl.loadClass(configClass).newInstance();
                    if (obj instanceof IcapAvConfiguration) {
                        icapConfiguration = (IcapAvConfiguration)obj;
                    }
                    else if (obj instanceof RestApiConfiguration) {
                        restApiConfiguration = (RestApiConfiguration)obj;
                    }
                }
            }
        }
        return new AntivirusConfiguration(clamAvConfig, icapConfiguration, restApiConfiguration);
    }
    
    public VendorAVProvider newVendorAVProvider() {
        return new VendorAVProvider() {
            @Override
            public List<VendorAV<File>> getVendorAVs() {
                final List<VendorAV<File>> avVendors = new ArrayList<VendorAV<File>>();
                if (AntivirusConfiguration.this.clamAvConfig != null) {
                    avVendors.add(CLAMVendorAV.getInstance(AntivirusConfiguration.this.clamAvConfig));
                }
                if (AntivirusConfiguration.this.icapAvConfig != null) {
                    avVendors.add(ICAPVendorAV.newInstance(AntivirusConfiguration.this.icapAvConfig));
                }
                if (AntivirusConfiguration.this.restApiAvConfig != null) {
                    avVendors.add(RestApiVendorAV.newInstance(AntivirusConfiguration.this.restApiAvConfig));
                }
                return avVendors.isEmpty() ? null : avVendors;
            }
        };
    }
    
    public CLAMAVConfiguration getClamAvConfig() {
        return this.clamAvConfig;
    }
    
    public IcapAvConfiguration getIcapAvConfig() {
        return this.icapAvConfig;
    }
    
    public RestApiConfiguration getRestApiAvConfig() {
        return this.restApiAvConfig;
    }
}
