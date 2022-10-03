package com.me.devicemanagement.framework.server.dms.exception;

import com.me.ems.framework.common.api.utils.APIException;

public class DMSException extends APIException
{
    public DMSException(final String errorCode) {
        super(errorCode);
    }
}
