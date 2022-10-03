package com.me.devicemanagement.framework.server.api;

import java.util.TreeMap;
import java.util.Properties;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;

public interface ADAccessAPI
{
    int validateADsBindingwitherrcode(final String p0, final String p1, final String p2, final String p3, final String p4, final boolean p5, final int p6) throws SyMException;
    
    boolean nativeIsValidADObjectName(final String p0, final String p1, final String p2, final String p3, final String p4, final boolean p5, final int p6) throws SyMException;
    
    List nativeGetADObjects(final String p0, final String p1, final String p2, final String p3, final List p4, final String p5, final int p6, final boolean p7, final int p8) throws SyMException;
    
    Properties nativeGetThisDCInfo(final String p0) throws SyMException;
    
    List nativeGetModifiedADObjects(final String p0, final String p1, final String p2, final String p3, final List p4, final String p5, final int p6, final long p7, final boolean p8, final int p9) throws SyMException;
    
    boolean validateADsBinding(final String p0, final String p1, final String p2, final String p3, final boolean p4, final int p5) throws SyMException;
    
    String getDomainGUID(final String p0, final String p1, final String p2, final String p3, final boolean p4, final int p5) throws SyMException;
    
    TreeMap getADDomainNamesForLoginPage();
    
    List getGeneralComputerAttrList() throws Exception;
    
    int fetchBulkADdata(final String p0, final String p1, final String p2, final String p3, final String p4, final int p5, final List p6, final String p7, final int p8, final long p9, final int p10, final boolean p11, final String p12, final boolean p13, final int p14) throws Exception;
    
    String getNetBIOSName(final String p0, final String p1, final String p2, final String p3, final boolean p4, final int p5) throws SyMException;
}
