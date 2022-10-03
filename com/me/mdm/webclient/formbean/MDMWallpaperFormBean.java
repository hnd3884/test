package com.me.mdm.webclient.formbean;

import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.io.File;
import com.adventnet.sym.server.mdm.ios.payload.transform.DO2iOSWallpaperPayload;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.mdm.server.config.PayloadProperty;
import org.json.JSONObject;

public class MDMWallpaperFormBean extends MDMDefaultFormBean
{
    @Override
    protected boolean getTransformedFormPropertyValue(final JSONObject multipleConfigForm, final JSONObject dynaFormData, final PayloadProperty payloadProperty) throws Exception {
        if (!payloadProperty.name.equals("BELOW_HDPI_WALLPAPER_PATH") && !payloadProperty.name.equals("ABOVE_HDPI_WALLPAPER_PATH")) {
            return true;
        }
        final boolean isModified = dynaFormData.optBoolean(payloadProperty.name.equals("BELOW_HDPI_WALLPAPER_PATH") ? "IS_BELOW_HDPI_WALL_MODIFIED" : "IS_ABOVE_HDPI_WALL_MODIFIED");
        String source = dynaFormData.optString(payloadProperty.name.equals("BELOW_HDPI_WALLPAPER_PATH") ? "BELOW_HDPI_WALLPAPER" : "ABOVE_HDPI_WALLPAPER");
        final boolean isLockModified = dynaFormData.optBoolean(payloadProperty.name.equals("BELOW_HDPI_WALLPAPER_PATH") ? "IS_BELOW_HDPI_LOCK_WALL_MODIFIED" : null);
        String lockSource = dynaFormData.optString(payloadProperty.name.equals("BELOW_HDPI_WALLPAPER_PATH") ? "BELOW_HDPI_LOCK_WALLPAPER" : null);
        final Integer wallpaperPosition = dynaFormData.optInt("SET_WALLPAPER_POSITION");
        final Long collectionId = multipleConfigForm.optLong("COLLECTION_ID");
        final String currentConfig = multipleConfigForm.optString("CURRENT_CONFIG");
        final String wallpaperPath = dynaFormData.optString(payloadProperty.name.equals("BELOW_HDPI_WALLPAPER_PATH") ? "BELOW_HDPI_WALLPAPER_PATH" : null);
        final String lockWallpaperPath = dynaFormData.optString(payloadProperty.name.equals("BELOW_HDPI_WALLPAPER_PATH") ? "BELOW_HDPI_LOCK_WALLPAPER_PATH" : null);
        final String webdir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        if ((isModified || !MDMStringUtils.isEmpty(wallpaperPath)) && wallpaperPosition == 3) {
            final JSONObject param = new JSONObject();
            param.put("COLLECTION_ID", (Object)collectionId);
            if (!isModified && MDMStringUtils.isEmpty(source)) {
                source = webdir + wallpaperPath;
            }
            param.put("SOURCE", (Object)source);
            if (currentConfig.equalsIgnoreCase("IOS_WALLPAPER_POLICY")) {
                param.put("FILENAME", (Object)DO2iOSWallpaperPayload.wallpaperName);
            }
            return this.addSourceFileToCollection(param, payloadProperty);
        }
        if (wallpaperPosition == 1 && (isLockModified || !MDMStringUtils.isEmpty(lockWallpaperPath))) {
            final JSONObject param = new JSONObject();
            param.put("COLLECTION_ID", (Object)collectionId);
            if (!isLockModified && MDMStringUtils.isEmpty(lockSource)) {
                lockSource = webdir + lockWallpaperPath;
            }
            param.put("SOURCE", (Object)lockSource);
            if (currentConfig.equalsIgnoreCase("IOS_WALLPAPER_POLICY")) {
                param.put("FILENAME", (Object)DO2iOSWallpaperPayload.wallpaperName);
            }
            return this.addSourceFileToCollection(param, payloadProperty);
        }
        if (wallpaperPosition == 4 && (isLockModified || isModified)) {
            boolean isLockChanged = false;
            boolean isWallChanged = false;
            if (isLockModified || !MDMStringUtils.isEmpty(lockWallpaperPath)) {
                final JSONObject param2 = new JSONObject();
                param2.put("COLLECTION_ID", (Object)collectionId);
                if (!isLockModified && MDMStringUtils.isEmpty(lockSource)) {
                    lockSource = webdir + lockWallpaperPath;
                }
                param2.put("SOURCE", (Object)lockSource);
                param2.put("FILENAME", (Object)DO2iOSWallpaperPayload.lockscreenWallpaperName);
                isLockChanged = this.addSourceFileToCollection(param2, payloadProperty);
            }
            if (isModified || !MDMStringUtils.isEmpty(wallpaperPath)) {
                final JSONObject param2 = new JSONObject();
                param2.put("COLLECTION_ID", (Object)collectionId);
                if (!isModified && MDMStringUtils.isEmpty(source)) {
                    source = webdir + wallpaperPath;
                }
                param2.put("SOURCE", (Object)source);
                param2.put("FILENAME", (Object)DO2iOSWallpaperPayload.homeScreenWallpaperName);
                isWallChanged = this.addSourceFileToCollection(param2, payloadProperty);
            }
            return isLockChanged || isWallChanged;
        }
        if (wallpaperPosition == 2 && (isModified || !MDMStringUtils.isEmpty(wallpaperPath))) {
            final JSONObject param = new JSONObject();
            param.put("COLLECTION_ID", (Object)collectionId);
            if (MDMStringUtils.isEmpty(source)) {
                source = webdir + wallpaperPath;
            }
            param.put("SOURCE", (Object)source);
            if (currentConfig.equalsIgnoreCase("IOS_WALLPAPER_POLICY")) {
                param.put("FILENAME", (Object)DO2iOSWallpaperPayload.wallpaperName);
            }
            return this.addSourceFileToCollection(param, payloadProperty);
        }
        return false;
    }
    
    private Object getFilePathForConfig(Object belowPath, final String currentConfig, final boolean isBelow) {
        final String fileName = isBelow ? DO2iOSWallpaperPayload.lockscreenWallpaperName : null;
        belowPath = belowPath.toString().replace("/", File.separator);
        belowPath = belowPath + File.separator + fileName;
        return belowPath;
    }
    
    private boolean addSourceFileToCollection(final JSONObject params, final PayloadProperty payloadProperty) throws Exception {
        final String source = params.optString("SOURCE");
        final Long collectionID = params.optLong("COLLECTION_ID");
        final String fileNameAvailable = params.optString("FILENAME");
        if (source == null || source.equals("")) {
            payloadProperty.value = null;
            return true;
        }
        final File file = new File(source);
        final String fileName = MDMStringUtils.isEmpty(fileNameAvailable) ? file.getName() : fileNameAvailable;
        final String folderpath = ProfileUtil.getAndroidWallpaperFolderPath(collectionID);
        final String targetFile = folderpath + File.separator + fileName;
        String path = ProfileUtil.getAndroidWallpaperDBPath(collectionID);
        if (source.equals(targetFile)) {
            payloadProperty.value = path.replaceAll("\\\\", "/");
            return true;
        }
        final boolean upload = ProfileUtil.getInstance().uploadProfileImageFile(source, folderpath, fileName);
        if (upload) {
            if (MDMStringUtils.isEmpty(fileNameAvailable)) {
                path = path + File.separator + fileName;
            }
            payloadProperty.value = path.replaceAll("\\\\", "/");
            return true;
        }
        throw new Exception("Unable to upload Account Icon file - ");
    }
}
