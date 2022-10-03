package com.me.devicemanagement.onpremise.tools.backuprestore.handler;

import java.util.List;

public interface BackUpHandler
{
    List<String> backUpFile(final String p0) throws Exception;
}
