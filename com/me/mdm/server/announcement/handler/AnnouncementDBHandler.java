package com.me.mdm.server.announcement.handler;

import com.me.uem.announcement.AnnouncementDBController;
import org.json.JSONArray;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import org.json.JSONObject;
import com.me.uem.announcement.AnnouncementHandler;

public class AnnouncementDBHandler extends AnnouncementHandler
{
    public static AnnouncementDBHandler newInstance() {
        return new AnnouncementDBHandler();
    }
    
    public JSONObject getCollectionIdForAnnouncement(final long announcementId) throws DataAccessException {
        final JSONObject colJson = this.getAnnouncementCollectionInfo(announcementId);
        final long collectionid = colJson.getLong("COLLECTION_ID");
        final long profileId = new ProfileHandler().getProfileIDFromCollectionID(collectionid);
        colJson.put("PROFILE_ID", profileId);
        colJson.put("COLLECTION_ID", collectionid);
        return colJson;
    }
    
    public JSONArray getCollectionIdsForAnnouncement(final List announcementIdList) throws DataAccessException {
        return new AnnouncementDBController().getCollectionProfileMapForAnnouncemnetList(announcementIdList);
    }
}
