package org.bouncycastle.jcajce.provider.config;

import java.security.Permission;
import java.util.StringTokenizer;
import org.bouncycastle.util.Strings;
import java.security.BasicPermission;

public class ProviderConfigurationPermission extends BasicPermission
{
    private static final int THREAD_LOCAL_EC_IMPLICITLY_CA = 1;
    private static final int EC_IMPLICITLY_CA = 2;
    private static final int THREAD_LOCAL_DH_DEFAULT_PARAMS = 4;
    private static final int DH_DEFAULT_PARAMS = 8;
    private static final int ACCEPTABLE_EC_CURVES = 16;
    private static final int ADDITIONAL_EC_PARAMETERS = 32;
    private static final int ALL = 63;
    private static final String THREAD_LOCAL_EC_IMPLICITLY_CA_STR = "threadlocalecimplicitlyca";
    private static final String EC_IMPLICITLY_CA_STR = "ecimplicitlyca";
    private static final String THREAD_LOCAL_DH_DEFAULT_PARAMS_STR = "threadlocaldhdefaultparams";
    private static final String DH_DEFAULT_PARAMS_STR = "dhdefaultparams";
    private static final String ACCEPTABLE_EC_CURVES_STR = "acceptableeccurves";
    private static final String ADDITIONAL_EC_PARAMETERS_STR = "additionalecparameters";
    private static final String ALL_STR = "all";
    private final String actions;
    private final int permissionMask;
    
    public ProviderConfigurationPermission(final String s) {
        super(s);
        this.actions = "all";
        this.permissionMask = 63;
    }
    
    public ProviderConfigurationPermission(final String s, final String actions) {
        super(s, actions);
        this.actions = actions;
        this.permissionMask = this.calculateMask(actions);
    }
    
    private int calculateMask(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(Strings.toLowerCase(s), " ,");
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            if (nextToken.equals("threadlocalecimplicitlyca")) {
                n |= 0x1;
            }
            else if (nextToken.equals("ecimplicitlyca")) {
                n |= 0x2;
            }
            else if (nextToken.equals("threadlocaldhdefaultparams")) {
                n |= 0x4;
            }
            else if (nextToken.equals("dhdefaultparams")) {
                n |= 0x8;
            }
            else if (nextToken.equals("acceptableeccurves")) {
                n |= 0x10;
            }
            else if (nextToken.equals("additionalecparameters")) {
                n |= 0x20;
            }
            else {
                if (!nextToken.equals("all")) {
                    continue;
                }
                n |= 0x3F;
            }
        }
        if (n == 0) {
            throw new IllegalArgumentException("unknown permissions passed to mask");
        }
        return n;
    }
    
    @Override
    public String getActions() {
        return this.actions;
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof ProviderConfigurationPermission)) {
            return false;
        }
        if (!this.getName().equals(permission.getName())) {
            return false;
        }
        final ProviderConfigurationPermission providerConfigurationPermission = (ProviderConfigurationPermission)permission;
        return (this.permissionMask & providerConfigurationPermission.permissionMask) == providerConfigurationPermission.permissionMask;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ProviderConfigurationPermission) {
            final ProviderConfigurationPermission providerConfigurationPermission = (ProviderConfigurationPermission)o;
            return this.permissionMask == providerConfigurationPermission.permissionMask && this.getName().equals(providerConfigurationPermission.getName());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode() + this.permissionMask;
    }
}
