package com.me.ems.onpremise.productbanner.api.v1.service;

import java.util.concurrent.TimeUnit;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.onpremise.productbanner.core.ProductBannerUtil;
import java.util.logging.Logger;

public class ProductBannerService
{
    private static final Logger LOGGER;
    private final ProductBannerUtil util;
    
    public ProductBannerService() {
        this.util = ProductBannerUtil.getInstance();
    }
    
    public Map<String, Object> getProductBannerData(final User user) throws APIException {
        try {
            final Long loginID = user.getLoginID();
            final Map<String, Object> response = new HashMap<String, Object>(8);
            final boolean isBannerDisabled = Boolean.parseBoolean(SyMUtil.getSyMParameter("DISABLE_PRODUCT_BANNER"));
            final boolean isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
            if (isBannerDisabled || isDemoMode) {
                response.put("showBanner", Boolean.FALSE);
                ProductBannerService.LOGGER.log(Level.INFO, "Banner disabled");
            }
            else {
                final Map<String, Object> bannerData;
                if ((bannerData = this.util.processBannerJSON(user.isAdminUser())).isEmpty()) {
                    response.put("showBanner", Boolean.FALSE);
                    ProductBannerService.LOGGER.log(Level.INFO, "No Banner Data");
                }
                else {
                    final String bannerName = bannerData.get("bannerID");
                    final boolean isBannerClosed = this.util.isBannerClosed(bannerName, loginID);
                    if (isBannerClosed) {
                        response.put("showBanner", Boolean.FALSE);
                    }
                    else {
                        final String bannerMessage = bannerData.get("message");
                        final String urlText = bannerData.get("urlText");
                        final String url = bannerData.get("url");
                        response.put("showBanner", Boolean.TRUE);
                        response.put("message", bannerMessage);
                        response.put("urlText", urlText);
                        response.put("url", url);
                        response.put("bannerID", bannerName);
                        response.put("showOptions", this.util.showOptions(bannerName, loginID));
                        SyMUtil.updateUserParameter(user.getUserID(), "REVIEW_BANNER_SHOWN".concat(".").concat(bannerName), Boolean.TRUE.toString());
                    }
                }
            }
            return response;
        }
        catch (final Exception ex) {
            ProductBannerService.LOGGER.log(Level.SEVERE, "Exception occurred while fetching banner data", ex);
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
    }
    
    public void updateBannerStatus(final String bannerID, final String action, final Long loginID) throws APIException {
        try {
            ProductBannerService.LOGGER.log(Level.INFO, "Updating status of banner : " + bannerID + " to " + action + " for loginID : " + loginID);
            switch (action) {
                case "doNotShow": {
                    this.util.doNotShowAgainForUser(bannerID, loginID, System.currentTimeMillis());
                    METrackerUtil.incrementMETrackParams("DO_NOT_SHOW".concat(".").concat(bannerID));
                    break;
                }
                case "remindLater": {
                    this.util.remindMeLater(bannerID, loginID, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30L));
                    METrackerUtil.incrementMETrackParams("REMIND_LATER".concat(".").concat(bannerID));
                    break;
                }
                case "close": {
                    this.util.remindMeLater(bannerID, loginID, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30L));
                    METrackerUtil.incrementMETrackParams("FIRST_CLOSE".concat(".").concat(bannerID));
                    break;
                }
                case "reviewed": {
                    this.util.reviewed(bannerID, loginID, System.currentTimeMillis());
                    METrackerUtil.incrementMETrackParams("REVIEWED".concat(".").concat(bannerID));
                    break;
                }
                default: {
                    throw new APIException("IAM0003");
                }
            }
        }
        catch (final Exception ex) {
            ProductBannerService.LOGGER.log(Level.SEVERE, "Exception occurred while updating banner status", ex);
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
    }
    
    public void addOrIncrementClickCountForReviewPage(final String bannerID) {
        try {
            METrackerUtil.incrementMETrackParams("BANNER_CLICK_COUNT".concat(".").concat(bannerID));
        }
        catch (final Exception ex) {
            ProductBannerService.LOGGER.log(Level.SEVERE, "Exception occurred while incrementing click count", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ProductBannerService.class.getName());
    }
}
