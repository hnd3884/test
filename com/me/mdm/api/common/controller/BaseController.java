package com.me.mdm.api.common.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

@Provider
@Produces({ "application/mdm.v1+json" })
@Consumes({ "application/json" })
public class BaseController
{
}
