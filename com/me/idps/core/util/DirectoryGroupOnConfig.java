package com.me.idps.core.util;

import java.util.List;

public class DirectoryGroupOnConfig
{
    public Long groupOnAttr;
    public Integer groupResType;
    public List<Integer> memberResTypes;
    
    private DirectoryGroupOnConfig() {
    }
    
    public DirectoryGroupOnConfig(final Long groupOnAttr, final Integer groupResType, final List<Integer> memberResTypes) {
        this.groupOnAttr = groupOnAttr;
        this.groupResType = groupResType;
        this.memberResTypes = memberResTypes;
    }
    
    public Integer[] getMemberTypesInArray() {
        return this.memberResTypes.toArray(new Integer[this.memberResTypes.size()]);
    }
}
