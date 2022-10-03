package com.zoho.mickey.tools;

import com.adventnet.db.adapter.DBAdapter;

public interface CreateDBUser
{
    void createUser(final DBAdapter p0, final String p1, final String p2, final String p3, final String p4, final String p5, final String p6) throws Exception;
}
