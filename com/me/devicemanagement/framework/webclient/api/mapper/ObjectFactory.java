package com.me.devicemanagement.framework.webclient.api.mapper;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory
{
    public RequestMapper createRequestMapper() {
        return new RequestMapper();
    }
    
    public RequestMapper.Entity createRequestMapperEntity() {
        return new RequestMapper.Entity();
    }
    
    public RequestMapper.Entity.Request createRequestMapperEntityRequest() {
        return new RequestMapper.Entity.Request();
    }
    
    public RequestMapper.Entity.Request.ViewConfiguration createRequestMapperEntityRequestViewConfiguration() {
        return new RequestMapper.Entity.Request.ViewConfiguration();
    }
}
