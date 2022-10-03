package com.me.mdm.server.announcement.listener;

import java.util.Collections;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;
import com.me.mdm.server.announcement.handler.AnnouncementAssociationHandler;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class AnnouncementGroupMemberListner implements MDMGroupMemberListener
{
    private final Logger logger;
    
    public AnnouncementGroupMemberListner() {
        this.logger = Logger.getLogger("MDMAnnouncementLogger");
    }
    
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        try {
            final Long groupResourceId = groupEvent.groupID;
            final Long customerId = groupEvent.customerId;
            final List resourceList = new ArrayList(Arrays.asList(groupEvent.memberIds));
            this.logger.log(Level.INFO, "Inside groupMemberAdded of AnnouncementGroupMemberListner {0}", resourceList.toString());
            final AnnouncementAssociationHandler annHandler = AnnouncementAssociationHandler.getNewInstance();
            final JSONArray resourceJSONArray = new JSONArray();
            for (final Object resource : resourceList) {
                resourceJSONArray.put((Object)resource);
            }
            final JSONArray announcementProfileListJSON = annHandler.getApplicableAnnouncementForGroupAssocation(groupResourceId);
            final JSONArray profileJSONArray = new JSONArray();
            final JSONArray collectionJSONArray = new JSONArray();
            final JSONArray announcementJSONArray = new JSONArray();
            for (int i = 0; i < announcementProfileListJSON.length(); ++i) {
                final JSONObject annJSON = announcementProfileListJSON.getJSONObject(i);
                announcementJSONArray.put((Object)annJSON.get("ANNOUNCEMENT_ID"));
                collectionJSONArray.put((Object)annJSON.get("COLLECTION_ID"));
                profileJSONArray.put((Object)annJSON.get("PROFILE_ID"));
            }
            if (groupEvent.groupType == 7) {
                for (int i = 0; i < announcementProfileListJSON.length(); ++i) {
                    final JSONObject annJSON = new JSONObject();
                    annJSON.put("profile_list", (Object)profileJSONArray);
                    annJSON.put("profile_announcement_map", (Object)announcementProfileListJSON);
                    annJSON.put("collection_list", (Object)collectionJSONArray);
                    annJSON.put("announcement_list", (Object)announcementJSONArray);
                    annJSON.put("resource_list", (Object)resourceJSONArray);
                    annJSON.put("resource_type", 2);
                    annJSON.put("customer_id", (Object)customerId);
                    annJSON.put("marked_for_delete", (Object)Boolean.FALSE);
                    annJSON.put("user_id", (Object)groupEvent.userId);
                    annJSON.put("notify_user", false);
                    AnnouncementAssociationHandler.getNewInstance().associateProfileToManagedUser(annJSON);
                }
            }
            else if (groupEvent.groupType == 6) {
                final JSONObject annJSON2 = new JSONObject();
                annJSON2.put("profile_list", (Object)profileJSONArray);
                annJSON2.put("profile_announcement_map", (Object)announcementProfileListJSON);
                annJSON2.put("collection_list", (Object)collectionJSONArray);
                annJSON2.put("announcement_list", (Object)announcementJSONArray);
                annJSON2.put("resource_list", (Object)resourceJSONArray);
                annJSON2.put("customer_id", (Object)customerId);
                annJSON2.put("user_id", (Object)groupEvent.userId);
                annJSON2.put("notify_user", false);
                annJSON2.put("marked_for_delete", (Object)Boolean.FALSE);
                AnnouncementAssociationHandler.getNewInstance().associateProfileForDevice(annJSON2);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in group listener", e);
        }
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        try {
            final Long groupResourceId = groupEvent.groupID;
            final Long customerId = groupEvent.customerId;
            final List resourceList = new ArrayList(Arrays.asList(groupEvent.memberIds));
            final JSONArray resourceJSONArray = new JSONArray();
            for (final Object resource : resourceList) {
                resourceJSONArray.put((Object)resource);
            }
            final JSONArray announcementProfileListJSON = AnnouncementAssociationHandler.getNewInstance().getApplicableAnnouncementForGroupDisassociation(groupResourceId);
            final JSONArray profileJSONArray = new JSONArray();
            final JSONArray collectionJSONArray = new JSONArray();
            final JSONArray announcementJSONArray = new JSONArray();
            for (int i = 0; i < announcementProfileListJSON.length(); ++i) {
                final JSONObject annJSON = announcementProfileListJSON.getJSONObject(i);
                announcementJSONArray.put((Object)annJSON.get("ANNOUNCEMENT_ID"));
                collectionJSONArray.put((Object)annJSON.get("COLLECTION_ID"));
                profileJSONArray.put((Object)annJSON.get("PROFILE_ID"));
            }
            JSONObject json = new JSONObject();
            json.put("profile_announcement_map", (Object)announcementProfileListJSON);
            json.put("profile_list", (Object)profileJSONArray);
            json.put("collection_list", (Object)collectionJSONArray);
            json.put("announcement_list", (Object)announcementJSONArray);
            if (groupEvent.groupType == 7) {
                for (int j = 0; j < announcementProfileListJSON.length(); ++j) {
                    final JSONObject annJSON2 = announcementProfileListJSON.getJSONObject(j);
                    final Long userId = groupEvent.userId;
                    final Long profileId = annJSON2.getLong("profile_id");
                    final Long collectionId = annJSON2.getLong("collection_id");
                    profileJSONArray.put((Object)profileId);
                    final HashMap profileCollnMap = new HashMap();
                    profileCollnMap.put(profileId, collectionId);
                    final HashMap excludeMap = ProfileDistributionListHandler.getDistributionProfileListHandler(0).getGroupDeviceExcludeProfileMap(resourceList, profileCollnMap, new ArrayList(Collections.singleton(groupResourceId)));
                    final List excludeList = excludeMap.get(profileId);
                    final List tempList = new ArrayList(resourceList);
                    if (excludeList != null) {
                        tempList.removeAll(excludeList);
                    }
                    final JSONArray tempResourceJSONArray = new JSONArray();
                    for (final Object value : tempList) {
                        tempResourceJSONArray.put((Object)value);
                    }
                    annJSON2.put("profile_list", (Object)profileJSONArray);
                    annJSON2.put("announcement_id", annJSON2.getLong("ANNOUNCEMENT_ID"));
                    annJSON2.put("resource_list", (Object)tempResourceJSONArray);
                    annJSON2.put("resource_type", 2);
                    annJSON2.put("user_id", (Object)userId);
                    annJSON2.put("customer_id", (Object)customerId);
                    if (tempResourceJSONArray.length() != 0) {
                        AnnouncementAssociationHandler.getNewInstance().disassociateProfileToManagedUser(annJSON2);
                    }
                }
            }
            else if (groupEvent.groupType == 6) {
                final Long userId2 = groupEvent.userId;
                final ArrayList groupResList = new ArrayList();
                groupResList.add(groupResourceId);
                json = AnnouncementAssociationHandler.getNewInstance().getExclusionMap(json, resourceList, groupResList, false);
                json.put("customer_id", (Object)customerId);
                json.put("user_id", (Object)groupEvent.userId);
                AnnouncementAssociationHandler.getNewInstance().disassociateProfileForDevice(json);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- groupMemberRemoved()    >   Error removing compliance    ", e);
        }
    }
}
