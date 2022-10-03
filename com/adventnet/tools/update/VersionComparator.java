package com.adventnet.tools.update;

import java.util.Comparator;
import com.adventnet.tools.update.installer.VersionChecker;
import java.io.File;
import com.adventnet.tools.update.installer.VersionProfile;
import java.util.ArrayList;

public class VersionComparator
{
    private static String confProductName;
    private static String confProductVersion;
    private static XmlData xmlData;
    
    public static VersionDiffType compareVersion(final String oldVersion, final String newVersion) {
        final int compare = new ComparatorInstance().compare(oldVersion, newVersion);
        switch (compare) {
            case -1: {
                return VersionDiffType.GREATER;
            }
            case 1: {
                return VersionDiffType.LOWER;
            }
            default: {
                return VersionDiffType.EQUAL;
            }
        }
    }
    
    public static boolean isProductsCompatible(final String existingPrdName, final String existingPrdVersion, final XmlData xmlData) {
        VersionComparator.confProductName = existingPrdName;
        VersionComparator.confProductVersion = existingPrdVersion;
        VersionComparator.xmlData = xmlData;
        final String toUpgrade_PrdName = VersionComparator.xmlData.getProductName();
        final String toUpgrade_PrdVersion = VersionComparator.xmlData.getProductVersion();
        final ArrayList fcomp = VersionComparator.xmlData.getFeatureCompatibility();
        final boolean pncheck = featureCheck(fcomp);
        if (pncheck) {
            return true;
        }
        if (VersionComparator.confProductName.equals(toUpgrade_PrdName) && VersionComparator.confProductVersion.equals(toUpgrade_PrdVersion)) {
            final boolean bool = checkProduct(fcomp, toUpgrade_PrdName, toUpgrade_PrdVersion);
            return !bool;
        }
        return false;
    }
    
    private static boolean checkProduct(final ArrayList al, final String prdName, final String prdVersion) {
        final ArrayList list = al;
        if (list == null || list.isEmpty()) {
            return false;
        }
        for (int size = list.size(), i = 0; i < size; ++i) {
            final FeatureCompInfo fpc = list.get(i);
            final String pName = fpc.getProductName();
            if (pName.equals(prdName)) {
                final Object[] obj = fpc.getPrdVersionInfo();
                if (obj == null) {
                    return false;
                }
                for (int s = obj.length, j = 0; j < s; ++j) {
                    final FeaturePrdVersionInfo fc = (FeaturePrdVersionInfo)obj[j];
                    final String pVersion = fc.getProductVersion();
                    if (pVersion.equals(prdVersion)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean featureCheck(final ArrayList al) {
        final ArrayList list = al;
        if (list == null || list.isEmpty()) {
            return true;
        }
        for (int size = list.size(), i = 0; i < size; ++i) {
            final FeatureCompInfo fpc = list.get(i);
            final String pName = fpc.getProductName();
            if (pName.equals(VersionComparator.confProductName)) {
                return productVersionCheck(fpc);
            }
        }
        return false;
    }
    
    private static boolean productVersionCheck(final FeatureCompInfo fcomp) {
        final FeatureCompInfo fpc = fcomp;
        final Object[] obj = fpc.getPrdVersionInfo();
        if (obj == null) {
            return true;
        }
        final int s = obj.length;
        if (s == 0) {
            return true;
        }
        for (int j = 0; j < s; ++j) {
            final FeaturePrdVersionInfo fc = (FeaturePrdVersionInfo)obj[j];
            final String pVersion = fc.getProductVersion();
            if (pVersion.equals(VersionComparator.confProductVersion)) {
                return productFeatureCheck(fc);
            }
        }
        return false;
    }
    
    private static boolean productFeatureCheck(final FeaturePrdVersionInfo fcheck) {
        final FeaturePrdVersionInfo fc = fcheck;
        final FeatureVersionComp fvc = fc.getFeatureVersionComp();
        if (fvc == null) {
            return true;
        }
        final String patchVersion = fvc.getCompPatchVersion();
        final String patchOption = fvc.getCompPatchOption();
        if (patchVersion != null && patchOption != null) {
            final boolean patchComp = checkForPatchComp(patchVersion, patchOption);
            if (!patchComp) {
                return false;
            }
        }
        final String type = VersionComparator.xmlData.getPatchType();
        if (type != null && type.equals("FP")) {
            final String[] verr = fvc.getVersions();
            for (int i = 0; i < verr.length; i += 3) {
                final String featureName = verr[i];
                final String featureOption = verr[i + 1];
                final String featureValue = verr[i + 2];
                final boolean featureComp = checkForFeatureComp(featureName, featureOption, featureValue);
                if (!featureComp) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean checkForPatchComp(final String patchVersion, final String patchOption) {
        final VersionProfile vprofile = VersionProfile.getInstance();
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        String[] versionArray = null;
        if (new File(specsPath).exists()) {
            vprofile.readDocument(specsPath, false, false);
            versionArray = vprofile.getTheVersions();
        }
        if (versionArray == null) {
            return false;
        }
        final int trunlen = versionArray.length;
        String[] trunVersionArray = new String[0];
        String prdVerCheck1 = VersionComparator.confProductName + "-" + VersionComparator.confProductVersion + "-SP-";
        prdVerCheck1 = prdVerCheck1.replace(' ', '_');
        String prdVerCheck2 = VersionComparator.confProductName + "-" + VersionComparator.confProductVersion + "-ServicePack-";
        prdVerCheck2 = prdVerCheck2.replace(' ', '_');
        for (final String installedVersion : versionArray) {
            if (installedVersion.startsWith(prdVerCheck1) || installedVersion.startsWith(prdVerCheck2)) {
                final String patch = installedVersion.substring(installedVersion.lastIndexOf("-") + 1);
                final int leng = trunVersionArray.length;
                final String[] tmp = new String[leng + 1];
                System.arraycopy(trunVersionArray, 0, tmp, 0, leng);
                tmp[leng] = patch;
                trunVersionArray = tmp;
            }
        }
        final VersionChecker vChecker = new VersionChecker();
        final int opt = CommonUtil.parseOption(patchOption);
        final boolean bool = vChecker.checkVersionCompatible(patchVersion, trunVersionArray, opt);
        return bool;
    }
    
    private static boolean checkForFeatureComp(final String featureName, final String featureOption, final String featureValue) {
        final VersionProfile vprofile = VersionProfile.getInstance();
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        String[] versionArray = null;
        if (new File(specsPath).exists()) {
            vprofile.readDocument(specsPath, false, false);
            versionArray = vprofile.getTheFPVersions();
        }
        if (versionArray == null) {
            return false;
        }
        final int trunlen = versionArray.length;
        String[] trunVersionArray = new String[0];
        String prdVerCheck1 = VersionComparator.confProductName + "-" + VersionComparator.confProductVersion + "-" + featureName + "-FP-";
        prdVerCheck1 = prdVerCheck1.replace(' ', '_');
        String prdVerCheck2 = VersionComparator.confProductName + "-" + VersionComparator.confProductVersion + "-" + featureName + "-FeaturePack-";
        prdVerCheck2 = prdVerCheck2.replace(' ', '_');
        for (final String installedVersion : versionArray) {
            if (installedVersion.startsWith(prdVerCheck1) || installedVersion.startsWith(prdVerCheck2)) {
                final String patch = installedVersion.substring(installedVersion.lastIndexOf("-") + 1);
                final int leng = trunVersionArray.length;
                final String[] tmp = new String[leng + 1];
                System.arraycopy(trunVersionArray, 0, tmp, 0, leng);
                tmp[leng] = patch;
                trunVersionArray = tmp;
            }
        }
        final VersionChecker vChecker = new VersionChecker();
        final int opt = CommonUtil.parseOption(featureOption);
        final boolean bool = vChecker.checkVersionCompatible(featureValue, trunVersionArray, opt);
        return bool;
    }
    
    public enum VersionDiffType
    {
        GREATER, 
        LOWER, 
        EQUAL;
    }
    
    public static class ComparatorInstance implements Comparator<String>
    {
        @Override
        public int compare(final String oldVersion, final String newVersion) {
            final String[] oldVer = oldVersion.split("\\.");
            final String[] newVer = newVersion.split("\\.");
            for (int length = Math.max(oldVer.length, newVer.length), i = 0; i < length; ++i) {
                final int leftValue = (i < oldVer.length) ? Integer.valueOf(oldVer[i]) : 0;
                final int rightValue = (i < newVer.length) ? Integer.valueOf(newVer[i]) : 0;
                if (leftValue < rightValue) {
                    return -1;
                }
                if (leftValue > rightValue) {
                    return 1;
                }
            }
            return 0;
        }
    }
}
