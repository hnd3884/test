package com.me.ems.summaryserver.common.probeadministration;

import com.adventnet.ds.query.Criteria;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public interface ProbeDetailsAPI
{
    HashMap getCurrentProbeServerDetail();
    
    Long getCurrentProbeID();
    
    HashMap getProbeLiveStatusDetails(final Long p0);
    
    Integer getProbeLiveStatus(final Long p0);
    
    void updateProbeDetailsInProbeProperties();
    
    List<Map> getAllProbeDetails();
    
    Boolean isValidProbeAuthKey(final String p0);
    
    Boolean isValidProbeAuthKey(final ContainerRequestContext p0);
    
    String getProbeName(final Long p0);
    
    String getSummaryServerBaseURL();
    
    HashMap getSummaryServerDetails();
    
    Map getSummaryServerAPIKeyDetails();
    
    boolean isValidSummaryServerAuthKey(final String p0, final Long p1);
    
    boolean isValidSummaryServerAuthKey(final ContainerRequestContext p0);
    
    HashMap getSummaryServerLiveStatusDetails();
    
    Integer getSummaryServerLiveStatus();
    
    HashMap checkAndUpdateSummaryServerLiveStatus();
    
    void updateIpAddr(final Long p0, final String p1);
    
    List<HashMap> getProbeDetails(final Criteria p0);
}
