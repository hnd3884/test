package com.me.mdm.server.customgroup.resource;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Group
{
    @SerializedName(value = "group_id", alternate = { "resource_id" })
    public Long groupID;
    @SerializedName("name")
    public String name;
    @SerializedName("group_type")
    public Integer groupType;
    @SerializedName("description")
    public String description;
    @SerializedName("domain")
    public String domain;
    @SerializedName("members")
    public List<Long> members;
}
