package com.me.uem.announcement;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.ds.query.DMDataSetWrapper;

public class AnnouncementJSONController
{
    protected JSONArray getArrayFromAnnouncementDS(final DMDataSetWrapper ds) throws Exception {
        final JSONArray announcementArray = new JSONArray();
        while (ds.next()) {
            final JSONObject annJson = this.getAnnouncementJSONFromDataSet(ds);
            announcementArray.put((Object)annJson);
        }
        return announcementArray;
    }
    
    protected JSONObject getAnnouncementJSONFromDataSet(final DMDataSetWrapper ds) throws Exception {
        final JSONObject annJSON = new JSONObject();
        this.setAnnouncementJSON(annJSON, ds);
        this.setAnnouncementDetailsJSON(annJSON, ds);
        this.setAnnouncementProfileJSON(annJSON, ds);
        return annJSON;
    }
    
    protected JSONObject getAnnouncementJSONFromDO(final DataObject annDO, final Row annRow) throws DataAccessException {
        final JSONObject annJSON = new JSONObject();
        return annJSON;
    }
    
    protected void setAnnouncementSpanJSON(final JSONObject annJSON, final Row announcementSpamRow) {
        final JSONObject spanJson = new JSONObject();
        spanJson.put("start_time", (Object)announcementSpamRow.get("START_TIME"));
        spanJson.put("end_time", (Object)announcementSpamRow.get("END_TIME"));
        spanJson.put("repeat_frequency", (int)announcementSpamRow.get("REPEAT_FREQUENCY"));
        spanJson.put("repeat_duration", (int)announcementSpamRow.get("REPEAT_DURATION"));
        annJSON.put("announcement_span", (Object)spanJson);
    }
    
    protected void setAnnouncementDetailsJSON(final JSONObject annJSON, final DMDataSetWrapper announcementDS) throws Exception {
        final JSONObject detailsJson = new JSONObject();
        detailsJson.put("title", (Object)announcementDS.getValue("TITLE"));
        detailsJson.put("title_color", (Object)announcementDS.getValue("TITLE_COLOR"));
        if (announcementDS.getValue("IMAGE_URL") != null) {
            detailsJson.put("ANNOUNCEMENT_IMG_ID", (Object)announcementDS.getValue("ANNOUNCEMENT_IMG_ID"));
            detailsJson.put("nbar_icon", (Object)announcementDS.getValue("IMAGE_URL"));
        }
        if (announcementDS.getValue("NBAR_MESSAGE") != null) {
            detailsJson.put("nbar_message", (Object)announcementDS.getValue("NBAR_MESSAGE"));
        }
        if (announcementDS.getValue("DETAIL_MESSAGE") != null) {
            detailsJson.put("detail_message", (Object)announcementDS.getValue("DETAIL_MESSAGE"));
        }
        if (announcementDS.getValue("ACK_BUTTON") != null) {
            annJSON.put("ack_button", (Object)announcementDS.getValue("ACK_BUTTON"));
        }
        annJSON.put("needs_acknowledgement", (boolean)announcementDS.getValue("NEEDS_ACKNOWLEDGEMENT"));
        annJSON.put("announcement_detail", (Object)detailsJson);
    }
    
    protected void setAnnouncementJSON(final JSONObject annJSON, final DMDataSetWrapper annRow) throws Exception {
        annJSON.put("announcement_id", (Object)annRow.getValue("ANNOUNCEMENT_ID"));
        annJSON.put("announcement_name", (Object)annRow.getValue("ANNOUNCEMENT_NAME"));
        annJSON.put("announcement_format", (int)annRow.getValue("ANNOUNCEMENT_FORMAT"));
    }
    
    protected void setAnnouncementProfileJSON(final JSONObject annJSON, final DMDataSetWrapper annDS) throws Exception {
        annJSON.put("created_by_user", (Object)annDS.getValue("created_by_user"));
        annJSON.put("last_modified_by_user", (Object)annDS.getValue("last_modified_by_user"));
        annJSON.put("CREATION_TIME", (Object)annDS.getValue("CREATION_TIME"));
        annJSON.put("LAST_MODIFIED_TIME", (Object)annDS.getValue("LAST_MODIFIED_TIME"));
        annJSON.put("COLLECTION_ID", (Object)annDS.getValue("COLLECTION_ID"));
        annJSON.put("PROFILE_ID", (Object)annDS.getValue("PROFILE_ID"));
        annJSON.put("IS_MOVED_TO_TRASH", (Object)annDS.getValue("IS_MOVED_TO_TRASH"));
    }
}
