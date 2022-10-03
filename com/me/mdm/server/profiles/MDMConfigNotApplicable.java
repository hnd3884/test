package com.me.mdm.server.profiles;

import java.util.ArrayList;
import java.util.List;

public class MDMConfigNotApplicable
{
    public Long collectionId;
    public List<Long> resourceList;
    public List<Long> directRemovalList;
    public Long profileId;
    public Long customerId;
    
    public MDMConfigNotApplicable(final Long collectionId, final Long profileId, final List<Long> resourceList, final Long customerId) {
        this.collectionId = collectionId;
        this.resourceList = resourceList;
        this.profileId = profileId;
        this.directRemovalList = new ArrayList<Long>();
        this.customerId = customerId;
    }
    
    public MDMConfigNotApplicable(final Long collectionId, final Long profileId, final List<Long> resourceList, final List<Long> directRemovalList) {
        this.collectionId = collectionId;
        this.profileId = profileId;
        this.resourceList = resourceList;
        this.directRemovalList = directRemovalList;
    }
}
