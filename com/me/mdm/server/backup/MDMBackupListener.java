package com.me.mdm.server.backup;

import org.json.JSONArray;

public interface MDMBackupListener
{
    JSONArray getFileBackupDetails() throws Exception;
}
