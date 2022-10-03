package com.me.devicemanagement.framework.winaccess;

import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class ADAccessUtil
{
    private static Logger logger;
    private String sourceClass;
    
    public ADAccessUtil() {
        this.sourceClass = "ADAccessUtil";
    }
    
    public String getADSearchFilterWithName(final int nResType, final String strName) throws SyMException {
        final String sourceMethod = "getADSearchFilterWithName";
        if (nResType == 2) {
            return "(&(objectCategory=User)(| (sAMAccountName=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(strName) + ") (userPrincipalName=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(strName) + ") ))";
        }
        if (nResType == 1 || nResType == 3) {
            return "(&(objectCategory=Computer)(name=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(strName) + "))";
        }
        if (nResType == 5) {
            return "(&(objectCategory=domain)(distinguishedName=DC=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(strName.replace(".", ", DC=")) + "))";
        }
        if (nResType == 4) {
            return "(&(objectCategory=site)(name=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(strName) + "))";
        }
        if (nResType == 6) {
            return "(&(objectcategory=organizationalUnit)(name=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(strName) + "))";
        }
        if (nResType == 9) {
            return "(&(objectcategory=container)(name=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(strName) + "))";
        }
        if (nResType == 7) {
            return "(&(objectCategory=Group)(name=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(strName) + "))";
        }
        if (nResType == 12) {
            return "(&(objectCategory=groupPolicyContainer)(displayName=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(strName) + "))";
        }
        SyMLogger.warning(ADAccessUtil.logger, this.sourceClass, sourceMethod, "Filter with Name not present for resource type : " + nResType);
        throw new SyMException(1001, new Exception("Filter with Name not present for given resource type : " + nResType));
    }
    
    public String getEndsWithADSearchFilter(final int nResType, final String startsWithVar) throws SyMException {
        final String sourceMethod = "getEndsWithADSearchFilter";
        if (nResType == 2) {
            return "(&(objectCategory=User)(sAMAccountName=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "))";
        }
        if (nResType == 1 || nResType == 3) {
            return "(&(objectCategory=Computer)(name=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "))";
        }
        if (nResType == 5) {
            return "(&(objectCategory=domain)(distinguishedName=DC=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar.replace(".", ", DC=")) + "))";
        }
        if (nResType == 4) {
            return "(&(objectCategory=site)(name=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "))";
        }
        if (nResType == 6) {
            return "(&(objectcategory=organizationalUnit)(name=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "))";
        }
        if (nResType == 9) {
            return "(&(objectcategory=container)(name=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "))";
        }
        if (nResType == 7) {
            return "(&(objectCategory=Group)(name=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "))";
        }
        if (nResType == 12) {
            return "(&(objectCategory=groupPolicyContainer)(displayName=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "))";
        }
        SyMLogger.warning(ADAccessUtil.logger, this.sourceClass, sourceMethod, "Filter with Name not present for resource type : " + nResType);
        throw new SyMException(1001, new Exception("Filter with Name not present for given resource type : " + nResType));
    }
    
    public String getStartsWithADSearchFilter(final int nResType, final String startsWithVar) throws SyMException {
        final String sourceMethod = "getStartsWithADSearchFilter";
        if (nResType == 2) {
            return "(&(objectCategory=User)(sAMAccountName=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 1 || nResType == 3) {
            return "(&(objectCategory=Computer)(name=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 5) {
            return "(&(objectCategory=domain)(distinguishedName=DC=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar.replace(".", ", DC=")) + "))";
        }
        if (nResType == 4) {
            return "(&(objectCategory=site)(name=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 6) {
            return "(&(objectcategory=organizationalUnit)(name=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 9) {
            return "(&(objectcategory=container)(name=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 7) {
            return "(&(objectCategory=Group)(name=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 12) {
            return "(&(objectCategory=groupPolicyContainer)(displayName=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        SyMLogger.warning(ADAccessUtil.logger, this.sourceClass, sourceMethod, "Filter with Name not present for resource type : " + nResType);
        throw new SyMException(1001, new Exception("Filter with Name not present for given resource type : " + nResType));
    }
    
    public String getContainsADSearchFilter(final int nResType, final String startsWithVar) throws SyMException {
        final String sourceMethod = "getContainsWithADSearchFilter";
        if (nResType == 2) {
            return "(&(objectCategory=User)(sAMAccountName=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 1 || nResType == 3) {
            return "(&(objectCategory=Computer)(name=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 5) {
            return "(&(objectCategory=domain)(distinguishedName=DC=" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar.replace(".", ", DC=")) + "))";
        }
        if (nResType == 4) {
            return "(&(objectCategory=site)(name=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 6) {
            return "(&(objectcategory=organizationalUnit)(name=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 9) {
            return "(&(objectcategory=container)(name=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 7) {
            return "(&(objectCategory=Group)(name=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        if (nResType == 12) {
            return "(&(objectCategory=groupPolicyContainer)(displayName=*" + WinAccessProvider.getInstance().replaceSpcCharForFilter(startsWithVar) + "*))";
        }
        SyMLogger.warning(ADAccessUtil.logger, this.sourceClass, sourceMethod, "Filter with Name not present for resource type : " + nResType);
        throw new SyMException(1001, new Exception("Filter with Name not present for given resource type : " + nResType));
    }
    
    public String getADSearchFilter(final int nResourceType) throws SyMException {
        final String sourceMethod = "getADSearchFilter";
        if (nResourceType == 6) {
            return "(objectCategory=organizationalUnit)";
        }
        if (nResourceType == 12) {
            return "(objectCategory=groupPolicyContainer)";
        }
        if (nResourceType == 7) {
            return "(objectCategory=group)";
        }
        if (nResourceType == 1 || nResourceType == 3) {
            return "(objectCategory=computer)";
        }
        if (nResourceType == 2) {
            return "(&(objectCategory=person)(objectClass=user))";
        }
        if (nResourceType == 8) {
            return "(objectClass=printqueue)";
        }
        if (nResourceType == 4) {
            return "(objectCategory=site)";
        }
        if (nResourceType == 5) {
            return "(&(objectCategory=domain)(!(name=forestdnszones))(!(name=domaindnszones)))";
        }
        if (nResourceType == 9) {
            return "(|(&(objectcategory=container)(|(name=users)(name=computers)))(&(objectcategory=builtindomain)(name=builtin)))";
        }
        if (nResourceType == 11) {
            return "(objectClass=foreignSecurityPrincipal)";
        }
        if (nResourceType == 10) {
            return "(objectClass=subnet)";
        }
        SyMLogger.warning(ADAccessUtil.logger, this.sourceClass, sourceMethod, "Filter not present for resource type : " + nResourceType);
        throw new SyMException(1001, new Exception("Filter not present for given resource type : " + nResourceType));
    }
    
    public String getADSearchFilterWithTime(final int nResType, final long time) throws SyMException {
        final String sourceMethod = "getADSearchFilterWithTime";
        if (nResType == 2) {
            return "(&(objectCategory=person)(objectClass=user)(|(lastlogon>=%s)(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z)))";
        }
        if (nResType == 1 || nResType == 3) {
            return "(&(objectCategory=computer)(|(lastlogon>=%s)(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z)))";
        }
        if (nResType == 5) {
            return "(&(objectCategory=domain)(!(name=forestdnszones))(!(name=domaindnszones))(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z))";
        }
        if (nResType == 4) {
            return "(&(objectClass=site)(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z))";
        }
        if (nResType == 6) {
            return "(&(objectClass=organizationalUnit)(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z))";
        }
        if (nResType == 9) {
            return "(&(|(&(objectcategory=container)(|(name=users)(name=computers)))(&(objectcategory=builtindomain)(name=builtin)))(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z))";
        }
        if (nResType == 7) {
            return "(&(objectCategory=group)(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z))";
        }
        if (nResType == 12) {
            return "(&(objectCategory=groupPolicyContainer)(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z))";
        }
        if (nResType == 8) {
            return "(&(objectClass=printqueue)(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z))";
        }
        if (nResType == 11) {
            return "(&(objectClass=foreignSecurityPrincipal)(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z))";
        }
        if (nResType == 10) {
            return "(&(objectClass=subnet)(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z))";
        }
        SyMLogger.warning(ADAccessUtil.logger, this.sourceClass, sourceMethod, "Filter with Time not present for resource type : " + nResType);
        throw new SyMException(1001, new Exception("Filter with Time not present for given resource type : " + nResType));
    }
    
    public List getAttributes(final int nResType) throws SyMException {
        final String sourceMethod = "getAttributes";
        List lisAttributes = new ArrayList();
        if (nResType == 2) {
            lisAttributes = this.getUserAttributes();
        }
        else if (nResType == 1 || nResType == 3) {
            lisAttributes = this.getComputerAttributes();
        }
        else if (nResType == 7) {
            lisAttributes = this.getGroupAttributes();
        }
        else if (nResType == 9 || nResType == 4 || nResType == 5 || nResType == 6) {
            lisAttributes = this.getContainerAttributes(nResType);
        }
        else if (nResType == 12) {
            lisAttributes = this.getGpoAttributes();
        }
        else if (nResType == 11) {
            lisAttributes = this.getFSPAttributes();
        }
        else if (nResType == 8) {
            lisAttributes = this.getPrinterAttributes();
        }
        else {
            if (nResType != 10) {
                SyMLogger.warning(ADAccessUtil.logger, this.sourceClass, sourceMethod, "Attributes not present for given resource type : " + nResType);
                throw new SyMException(1001, new Exception("Attributes not present for given resource type : " + nResType));
            }
            lisAttributes = this.getSubnetAttributes();
        }
        lisAttributes.add("name");
        return lisAttributes;
    }
    
    public List getUserAttributes() throws SyMException {
        try {
            final ADObjectsConvertor obj = new ADObjectsConvertor();
            final List lisUserAttr = obj.getUserAttrList();
            final List lisResourceAttr = obj.getADResourceAttrList();
            final List lisUserPWDInfoAttr = obj.getUserPWDInfoAttrList();
            final int nCount1 = lisUserAttr.size();
            final int nCount2 = lisUserPWDInfoAttr.size();
            final int nCount3 = lisResourceAttr.size();
            final List lisTotalUserAttr = new ArrayList();
            for (int index1 = 0; index1 < nCount1; ++index1) {
                lisTotalUserAttr.add(lisUserAttr.get(index1));
            }
            for (int index2 = 0; index2 < nCount2; ++index2) {
                final String attribute = lisUserPWDInfoAttr.get(index2);
                if (attribute.compareTo("userAccountControl") != 0) {
                    lisTotalUserAttr.add(attribute);
                }
            }
            for (int index3 = 0; index3 < nCount3; ++index3) {
                final String attribute = lisResourceAttr.get(index3);
                if (attribute.compareTo("domainName") != 0) {
                    lisTotalUserAttr.add(attribute);
                }
            }
            lisTotalUserAttr.add("primaryGroupID");
            lisTotalUserAttr.add("memberOf");
            return lisTotalUserAttr;
        }
        catch (final Exception ex) {
            SyMLogger.info(ADAccessUtil.logger, this.sourceClass, "getUserAttributes", "Exception occured : " + ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public List getFSPAttributes() throws SyMException {
        try {
            final ADObjectsConvertor obj = new ADObjectsConvertor();
            final List lisResourceAttr = obj.getADResourceAttrList();
            final int nCount1 = lisResourceAttr.size();
            final List lisTotalFSPAttr = new ArrayList();
            for (int index1 = 0; index1 < nCount1; ++index1) {
                final String attribute = lisResourceAttr.get(index1);
                if (attribute.compareTo("domainName") != 0) {
                    lisTotalFSPAttr.add(attribute);
                }
            }
            lisTotalFSPAttr.add("memberOf");
            return lisTotalFSPAttr;
        }
        catch (final Exception ex) {
            SyMLogger.info(ADAccessUtil.logger, this.sourceClass, "getFSPAttributes", "Exception occured : " + ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public List getPrinterAttributes() throws SyMException {
        try {
            final ADObjectsConvertor obj = new ADObjectsConvertor();
            final List lisResourceAttr = obj.getADResourceAttrList();
            final List lisPrinterAttr = obj.getADPrinterAttrList();
            final int nCount1 = lisResourceAttr.size();
            final int nCount2 = lisPrinterAttr.size();
            final List lisTotalPrinterAttr = new ArrayList();
            for (int index1 = 0; index1 < nCount1; ++index1) {
                final String attribute = lisResourceAttr.get(index1);
                if (attribute.compareTo("domainName") != 0) {
                    lisTotalPrinterAttr.add(attribute);
                }
            }
            for (int index2 = 0; index2 < nCount2; ++index2) {
                lisTotalPrinterAttr.add(lisPrinterAttr.get(index2));
            }
            return lisTotalPrinterAttr;
        }
        catch (final Exception ex) {
            SyMLogger.info(ADAccessUtil.logger, this.sourceClass, "getPrinterAttributes", "Exception occured : " + ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public List getSubnetAttributes() throws SyMException {
        try {
            final ADObjectsConvertor obj = new ADObjectsConvertor();
            final List lisResourceAttr = obj.getADResourceAttrList();
            final int nCount1 = lisResourceAttr.size();
            final List lisTotalSubnetAttr = new ArrayList();
            for (int index1 = 0; index1 < nCount1; ++index1) {
                final String attribute = lisResourceAttr.get(index1);
                if (attribute.compareTo("domainName") != 0) {
                    lisTotalSubnetAttr.add(attribute);
                }
            }
            return lisTotalSubnetAttr;
        }
        catch (final Exception ex) {
            SyMLogger.info(ADAccessUtil.logger, this.sourceClass, "getSubnetAttributes", "Exception occured : " + ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public List getComputerAttributes() throws SyMException {
        try {
            final ADObjectsConvertor obj = new ADObjectsConvertor();
            final List lisComputerAttr = obj.getComputerAttrList();
            final List lisGeneralComputerAttr = obj.getGeneralComputerAttrList();
            final List lisResourceAttr = obj.getADResourceAttrList();
            final int nCount1 = lisComputerAttr.size();
            final int nCount2 = lisGeneralComputerAttr.size();
            final int nCount3 = lisResourceAttr.size();
            final List lisTotalComputerAttr = new ArrayList();
            for (int index1 = 0; index1 < nCount1; ++index1) {
                lisTotalComputerAttr.add(lisComputerAttr.get(index1));
            }
            for (int index2 = 0; index2 < nCount2; ++index2) {
                lisTotalComputerAttr.add(lisGeneralComputerAttr.get(index2));
            }
            for (int index3 = 0; index3 < nCount3; ++index3) {
                final String attribute = lisResourceAttr.get(index3);
                if (attribute.compareTo("domainName") != 0) {
                    lisTotalComputerAttr.add(attribute);
                }
            }
            lisTotalComputerAttr.add("primaryGroupID");
            lisTotalComputerAttr.add("memberOf");
            return lisTotalComputerAttr;
        }
        catch (final Exception ex) {
            SyMLogger.info(ADAccessUtil.logger, this.sourceClass, "getComputerAttributes", "Exception occured : " + ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public List getGroupAttributes() throws SyMException {
        try {
            final ADObjectsConvertor obj = new ADObjectsConvertor();
            final List lisGroupAttr = obj.getGroupAttrList();
            final List lisResourceAttr = obj.getADResourceAttrList();
            final int nCount1 = lisGroupAttr.size();
            final int nCount2 = lisResourceAttr.size();
            final List lisTotalGroupAttr = new ArrayList();
            for (int index1 = 0; index1 < nCount1; ++index1) {
                final String attribute = lisGroupAttr.get(index1);
                if (attribute.compareTo("location") != 0) {
                    lisTotalGroupAttr.add(attribute);
                }
            }
            for (int index2 = 0; index2 < nCount2; ++index2) {
                final String attribute = lisResourceAttr.get(index2);
                if (attribute.compareTo("domainName") != 0) {
                    lisTotalGroupAttr.add(attribute);
                }
            }
            lisTotalGroupAttr.add("memberOf");
            return lisTotalGroupAttr;
        }
        catch (final Exception ex) {
            SyMLogger.info(ADAccessUtil.logger, this.sourceClass, "getGroupAttributes", "Exception occured : " + ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public List getContainerAttributes(final int nResourceType) throws SyMException {
        try {
            final ADObjectsConvertor obj = new ADObjectsConvertor();
            final List containerAttr = obj.getContainerAttrList();
            final List resourceAttr = obj.getADResourceAttrList();
            final int nCount1 = containerAttr.size();
            final int nCount2 = resourceAttr.size();
            final List totalContainerAttr = new ArrayList();
            for (int index1 = 0; index1 < nCount1; ++index1) {
                final String attribute = containerAttr.get(index1);
                if (attribute.compareTo("location") != 0) {
                    totalContainerAttr.add(attribute);
                }
            }
            for (int index2 = 0; index2 < nCount2; ++index2) {
                final String attribute = resourceAttr.get(index2);
                if (attribute.compareTo("domainName") != 0) {
                    totalContainerAttr.add(attribute);
                }
            }
            if (nResourceType == 6 || nResourceType == 5 || nResourceType == 4) {
                totalContainerAttr.add("gPOptions");
            }
            if (nResourceType == 5) {
                final List pwdAttr = obj.getDomainPWDInfoAttrList();
                for (int nCount3 = pwdAttr.size(), index3 = 0; index3 < nCount3; ++index3) {
                    totalContainerAttr.add(pwdAttr.get(index3));
                }
            }
            return totalContainerAttr;
        }
        catch (final Exception ex) {
            SyMLogger.info(ADAccessUtil.logger, this.sourceClass, "getContainerAttributes", "Exception occured : " + ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public List getGpoAttributes() throws SyMException {
        try {
            final ADObjectsConvertor obj = new ADObjectsConvertor();
            final List gpoAttr = obj.getGpoAttrList();
            final List resourceAttr = obj.getADResourceAttrList();
            final int nCount1 = gpoAttr.size();
            final int nCount2 = resourceAttr.size();
            final List totalGpoAttr = new ArrayList();
            for (int index1 = 0; index1 < nCount1; ++index1) {
                totalGpoAttr.add(gpoAttr.get(index1));
            }
            for (int index2 = 0; index2 < nCount2; ++index2) {
                final String attribute = resourceAttr.get(index2);
                if (attribute.compareTo("domainName") != 0) {
                    totalGpoAttr.add(attribute);
                }
            }
            return totalGpoAttr;
        }
        catch (final Exception ex) {
            SyMLogger.info(ADAccessUtil.logger, this.sourceClass, "getGpoAttributes", "Exception occured : " + ex);
            throw new SyMException(1001, ex);
        }
    }
    
    static {
        ADAccessUtil.logger = Logger.getLogger("SoMLogger");
    }
}
