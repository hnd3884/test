package com.me.ems.framework.common.api.v1.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.me.ems.framework.common.api.v1.model.helpermodel.DashCardBean;

@JsonTypeName("card")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class DashCardAPIBean extends DashCardBean
{
}
