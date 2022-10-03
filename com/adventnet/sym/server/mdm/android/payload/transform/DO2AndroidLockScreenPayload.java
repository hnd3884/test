package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidLockScreenPayload;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidLockScreenPayload implements DO2AndroidPayload
{
    public Logger logger;
    
    public DO2AndroidLockScreenPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidLockScreenPayload payload = null;
        final JSONArray messages = new JSONArray();
        final JSONArray messagesWithOrder = new JSONArray();
        try {
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("LockScreenConfiguration");
                while (iterator.hasNext()) {
                    payload = new AndroidLockScreenPayload("1.0", "com.mdm.mobiledevice.lockscreen", "Lock Screen");
                    final Row lockScreenRow = iterator.next();
                    final SortColumn sortColumn = new SortColumn(new Column("LockScreenMessages", "ORDER"), true);
                    dataObject.sortRows("LockScreenMessages", new SortColumn[] { sortColumn });
                    String textColour = null;
                    final Iterator messageIterator = dataObject.getRows("LockScreenMessages", new Criteria(new Column("LockScreenToMsgInfo", "LOCK_SCREEN_CONFIGURATION_ID"), lockScreenRow.get("LOCK_SCREEN_CONFIGURATION_ID"), 0));
                    while (messageIterator.hasNext()) {
                        final JSONObject msgWithOrder = new JSONObject();
                        final Row messageRow = messageIterator.next();
                        final String message = (String)messageRow.get("MESSAGE");
                        textColour = (String)messageRow.get("TEXT_COLOUR");
                        final int order = (int)messageRow.get("ORDER");
                        msgWithOrder.put("message", (Object)message);
                        msgWithOrder.put("order", order);
                        messages.put((Object)message);
                        messagesWithOrder.put((Object)msgWithOrder);
                    }
                    String wallpaperPath = (String)lockScreenRow.get("WALLPAPER_PATH");
                    wallpaperPath = wallpaperPath + "/" + "defaultLockscreen.ioswallpaper";
                    final Integer wallpaperType = (Integer)lockScreenRow.get("WALLPAPER_TYPE");
                    final String backGroundColour = (String)lockScreenRow.get("BG_COLOUR");
                    final Integer orientation = (Integer)lockScreenRow.get("ORIENTATION");
                    payload.setTextColour(textColour);
                    payload.setWallpaperType(wallpaperType);
                    payload.setWallpaperPath(wallpaperPath);
                    payload.setMessages(messages);
                    payload.setMessagesWithOrder(messagesWithOrder);
                    payload.setBackgroundColour(backGroundColour);
                    payload.setOrientation(orientation);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in saving androidlockscreen payload data", ex);
        }
        return payload;
    }
}
