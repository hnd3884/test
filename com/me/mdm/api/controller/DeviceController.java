package com.me.mdm.api.controller;

import com.me.mdm.server.device.api.model.DeviceUnlockSettingsModel;
import com.me.mdm.server.device.api.service.DeviceUserService;
import com.me.mdm.server.device.api.model.SearchDeviceUser;
import com.me.mdm.server.device.api.model.DeviceUserListModel;
import com.me.mdm.server.device.api.model.actions.DeviceActionModel;
import com.me.mdm.server.device.api.model.BootstrapTokenInfoModel;
import com.me.mdm.server.device.api.model.LostDeviceCountModel;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.profiles.api.model.ProfileAssociationtoDeviceModel;
import com.me.mdm.server.profiles.api.model.DeviceProfilesModel;
import com.me.mdm.server.apps.api.model.AppAssociateDetailsModel;
import com.me.mdm.server.device.api.model.DeviceFilevaultDetailsModel;
import com.me.mdm.server.device.api.model.actions.FilevaultKeyImportModel;
import com.me.mdm.server.device.api.service.actions.DeviceActionService;
import javax.ws.rs.POST;
import com.me.mdm.server.device.api.model.DeviceCertificateDetailsModel;
import javax.ws.rs.QueryParam;
import com.me.mdm.server.device.api.model.DeviceLocation;
import com.me.mdm.server.device.api.model.DeviceLocationListModel;
import com.me.mdm.server.device.api.model.DeviceDetailsModel;
import javax.ws.rs.DELETE;
import com.me.mdm.api.model.BaseAPIModel;
import com.me.mdm.server.device.api.annotations.ValidDeviceUDID;
import com.me.mdm.server.device.api.annotations.ValidDevice;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import com.me.mdm.server.device.api.model.DeviceUpdate;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import java.util.Iterator;
import org.json.JSONObject;
import com.me.mdm.server.device.api.model.SearchDevice;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.mdm.server.device.api.model.DeviceListModel;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.apps.api.service.AppService;
import com.me.mdm.server.profiles.api.service.ProfileService;
import com.me.mdm.server.device.api.service.DeviceService;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("devices")
public class DeviceController extends BaseController
{
    protected static Logger logger;
    private DeviceService deviceService;
    private ProfileService profileService;
    private AppService appService;
    
    public DeviceController() {
        this.deviceService = new DeviceService();
        this.profileService = new ProfileService();
        this.appService = new AppService();
    }
    
    @GET
    public DeviceListModel getDevices(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        final SearchDevice searchDevice = (SearchDevice)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)SearchDevice.class);
        searchDevice.setCustomerUserDetails(requestContext);
        if (searchDevice.isTreeSource()) {
            return this.deviceService.getDevicesForGroupTree(searchDevice);
        }
        return this.deviceService.getDevices(searchDevice);
    }
    
    @PUT
    @ValidDevice
    @Path("/{device_id}")
    public Response updateDevices(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, final DeviceUpdate deviceUpdate) throws Exception {
        deviceUpdate.setDeviceId(deviceId);
        deviceUpdate.setCustomerUserDetails(requestContext);
        this.deviceService.updateDevice(deviceUpdate);
        return Response.status(202).build();
    }
    
    @PUT
    @ValidDeviceUDID
    @Path("udid/{udid}")
    public Response updateDevicesWithUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, final DeviceUpdate deviceUpdate) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        deviceUpdate.setDeviceId(deviceId);
        deviceUpdate.setCustomerUserDetails(requestContext);
        this.deviceService.updateDevice(deviceUpdate);
        return Response.status(202).build();
    }
    
    @DELETE
    @ValidDevice
    @Path("/{device_id}")
    public Response deleteDevice(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId) throws Exception {
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        this.deviceService.deleteDevice(deviceId, baseAPIModel);
        return Response.status(202).build();
    }
    
    @DELETE
    @ValidDeviceUDID
    @Path("udid/{udid}")
    public Response deleteDeviceWithUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        this.deviceService.deleteDevice(deviceId, baseAPIModel);
        return Response.status(202).build();
    }
    
    @GET
    @ValidDevice
    @Path("/{device_id}")
    public DeviceDetailsModel getDevice(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, @Context final UriInfo uriInfo) throws Exception {
        return this.getDeviceDetails(requestContext, uriInfo, deviceId);
    }
    
    @GET
    @ValidDeviceUDID
    @Path("udid/{udid}")
    public DeviceDetailsModel getDeviceWithUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, @Context final UriInfo uriInfo) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.getDeviceDetails(requestContext, uriInfo, deviceId);
    }
    
    private DeviceDetailsModel getDeviceDetails(final ContainerRequestContext requestContext, final UriInfo uriInfo, final Long deviceId) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        final SearchDevice searchDevice = (SearchDevice)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)SearchDevice.class);
        searchDevice.setDeviceId(deviceId);
        searchDevice.setCustomerUserDetails(requestContext);
        return this.deviceService.getDevice(searchDevice);
    }
    
    @GET
    @ValidDevice
    @Path("/{device_id}/locations")
    public DeviceLocationListModel getDeviceLocation(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, @Context final UriInfo uriInfo) throws Exception {
        return this.getDeviceLocationsDetails(requestContext, uriInfo, deviceId);
    }
    
    @GET
    @ValidDeviceUDID
    @Path("udid/{udid}/locations")
    public DeviceLocationListModel getDeviceLocationUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, @Context final UriInfo uriInfo) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.getDeviceLocationsDetails(requestContext, uriInfo, deviceId);
    }
    
    private DeviceLocationListModel getDeviceLocationsDetails(final ContainerRequestContext requestContext, final UriInfo uriInfo, final Long deviceId) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        final DeviceLocation deviceLocation = (DeviceLocation)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)DeviceLocation.class);
        deviceLocation.setDeviceId(deviceId);
        deviceLocation.setCustomerUserDetails(requestContext);
        return this.deviceService.getDeviceLocations(deviceLocation);
    }
    
    @GET
    @ValidDevice
    @Path("/{device_id}/hardware")
    public DeviceDetailsModel getDeviceHardWare(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, @Context final UriInfo uriInfo) throws Exception {
        return this.deviceService.getDeviceHardwareDetails(deviceId);
    }
    
    @GET
    @ValidDeviceUDID
    @Path("udid/{udid}/hardware")
    public DeviceDetailsModel getDeviceHardwareUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, @Context final UriInfo uriInfo) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.deviceService.getDeviceHardwareDetails(deviceId);
    }
    
    @GET
    @ValidDevice
    @Path("/{device_id}/networks")
    public Map getDeviceNetwork(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, @Context final UriInfo uriInfo) throws Exception {
        return this.deviceService.getDeviceNetworkRelatedDetails(deviceId);
    }
    
    @GET
    @ValidDeviceUDID
    @Path("udid/{udid}/networks")
    public Map getDeviceNetworkUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, @Context final UriInfo uriInfo) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.deviceService.getDeviceNetworkRelatedDetails(deviceId);
    }
    
    @GET
    @ValidDevice
    @Path("/{device_id}/sims")
    public Map getDeviceSim(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, @Context final UriInfo uriInfo) throws Exception {
        return this.deviceService.getDeviceSimDetailsMap(deviceId);
    }
    
    @GET
    @ValidDeviceUDID
    @Path("udid/{udid}/sims")
    public Map getDeviceSimUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, @Context final UriInfo uriInfo) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.deviceService.getDeviceSimDetailsMap(deviceId);
    }
    
    @GET
    @ValidDevice
    @Path("/{device_id}/certificates")
    public DeviceCertificateDetailsModel getDeviceCertificates(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, @Context final UriInfo uriInfo, @QueryParam("expiry") final Long expiry) throws Exception {
        return this.deviceService.getDeviceCertificateDetails(deviceId, expiry);
    }
    
    @GET
    @ValidDeviceUDID
    @Path("udid/{udid}/certificates")
    public DeviceCertificateDetailsModel getDeviceCertificatesUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, @Context final UriInfo uriInfo, @QueryParam("expiry") final Long expiry) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.deviceService.getDeviceCertificateDetails(deviceId, expiry);
    }
    
    @GET
    @ValidDevice
    @Path("/{device_id}/apps")
    public Map getDeviceAppDetails(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, @Context final UriInfo uriInfo) throws Exception {
        return this.getDeviceAppDetails(requestContext, uriInfo, deviceId);
    }
    
    @GET
    @ValidDeviceUDID
    @Path("udid/{udid}/apps")
    public Map getDeviceAppDetailsUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, @Context final UriInfo uriInfo) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.getDeviceAppDetails(requestContext, uriInfo, deviceId);
    }
    
    private Map getDeviceAppDetails(final ContainerRequestContext requestContext, final UriInfo uriInfo, final Long deviceId) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        final SearchDevice searchDevice = (SearchDevice)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)SearchDevice.class);
        searchDevice.setDeviceId(deviceId);
        searchDevice.setCustomerUserDetails(requestContext);
        return this.deviceService.getDeviceAppDetails(searchDevice);
    }
    
    @POST
    @ValidDeviceUDID
    @Path("udid/{udid}/actions/rotate_filevault_personal_key")
    public Response rotateMacFilevaultPersonalRecoveryKeyUDID(@Context final ContainerRequestContext requestContext) {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.rotateMacFilevaultPersonalRecoveryKeyResID(requestContext, deviceId);
    }
    
    @POST
    @ValidDevice
    @Path("/{device_id}/actions/rotate_filevault_personal_key")
    public Response rotateMacFilevaultPersonalRecoveryKeyResID(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId) {
        final BaseAPIModel model = new BaseAPIModel();
        model.setCustomerUserDetails(requestContext);
        final DeviceActionService service = new DeviceActionService();
        service.rotateFilevaultPersonalRecoveryKeyForInventryAction(deviceId, model.getUserId());
        return Response.status(202).build();
    }
    
    @POST
    @ValidDeviceUDID
    @Path("udid/{udid}/actions/import_filevault_personal_key")
    public Response importMacFilevaultPersonalRecoveryKeyUDID(@Context final ContainerRequestContext requestContext, final FilevaultKeyImportModel keyImport) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.importMacFilevaultPersonalRecoveryKeyResID(requestContext, deviceId, keyImport);
    }
    
    @POST
    @ValidDevice
    @Path("/{device_id}/actions/import_filevault_personal_key")
    public Response importMacFilevaultPersonalRecoveryKeyResID(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, final FilevaultKeyImportModel keyImport) {
        keyImport.setCustomerUserDetails(requestContext);
        keyImport.setResourceID(deviceId);
        final DeviceActionService service = new DeviceActionService();
        if (service.importFilevaultPersonalRotateKey(keyImport)) {
            return Response.accepted().build();
        }
        return Response.notModified().build();
    }
    
    @POST
    @ValidDeviceUDID
    @Path("udid/{udid}/filevault")
    public DeviceFilevaultDetailsModel getDeviceSecurityDetailsKeyUDID(@Context final ContainerRequestContext requestContext) {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.getDeviceSecurityDetails(requestContext, deviceId);
    }
    
    @GET
    @ValidDevice
    @Path("/{device_id}/filevault")
    public DeviceFilevaultDetailsModel getDeviceSecurityDetails(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId) {
        final DeviceFilevaultDetailsModel details = this.deviceService.getDeviceEncryptionSecurityDetails(deviceId);
        return details;
    }
    
    @POST
    @ValidDevice
    @Path("{device_id}/apps")
    public Response updateApps(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, final AppAssociateDetailsModel appAssociateDetailsModel) throws Exception {
        return this.updateAppsToDevice(deviceId, requestContext, appAssociateDetailsModel);
    }
    
    @POST
    @ValidDeviceUDID
    @Path("udid/{udid}/apps")
    public Response updateAppsUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, final AppAssociateDetailsModel appAssociateDetailsModel) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.updateAppsToDevice(deviceId, requestContext, appAssociateDetailsModel);
    }
    
    private Response updateAppsToDevice(final Long deviceId, final ContainerRequestContext requestContext, final AppAssociateDetailsModel appAssociateDetailsModel) throws Exception {
        appAssociateDetailsModel.setCustomerUserDetails(requestContext);
        appAssociateDetailsModel.setDeviceId(deviceId);
        this.appService.associateAppsToDevices(appAssociateDetailsModel);
        return Response.status(202).build();
    }
    
    @DELETE
    @ValidDevice
    @Path("{device_id}/apps")
    public Response disassociateAppsfromDevice(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, final AppAssociateDetailsModel appAssociateDetailsModel) throws Exception {
        return this.disassociateAppsfromDevice(deviceId, requestContext, appAssociateDetailsModel);
    }
    
    @DELETE
    @ValidDeviceUDID
    @Path("udid/{udid}/apps")
    public Response disassociateAppsfromDeviceUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, final AppAssociateDetailsModel appAssociateDetailsModel) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.disassociateAppsfromDevice(deviceId, requestContext, appAssociateDetailsModel);
    }
    
    private Response disassociateAppsfromDevice(final Long deviceId, final ContainerRequestContext requestContext, final AppAssociateDetailsModel appAssociateDetailsModel) throws Exception {
        appAssociateDetailsModel.setDeviceId(deviceId);
        appAssociateDetailsModel.setCustomerUserDetails(requestContext);
        this.appService.disassociateAppsfromDevices(appAssociateDetailsModel);
        return Response.status(202).build();
    }
    
    @GET
    @ValidDevice
    @Path("{device_id}/profiles")
    public DeviceProfilesModel getDeviceProfileDetails(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, @Context final UriInfo uriInfo) throws Exception {
        return this.getDeviceProfileDetails(deviceId, requestContext, uriInfo);
    }
    
    @GET
    @ValidDeviceUDID
    @Path("udid/{udid}/profiles")
    public DeviceProfilesModel getDeviceProfileDetailsUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, @Context final UriInfo uriInfo) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.getDeviceProfileDetails(deviceId, requestContext, uriInfo);
    }
    
    private DeviceProfilesModel getDeviceProfileDetails(final Long deviceId, final ContainerRequestContext requestContext, final UriInfo uriInfo) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        final SearchDevice searchDevice = (SearchDevice)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)SearchDevice.class);
        searchDevice.setDeviceId(deviceId);
        searchDevice.setCustomerUserDetails(requestContext);
        return this.deviceService.getDeviceProfiles(searchDevice);
    }
    
    @POST
    @ValidDevice
    @Path("{device_id}/profiles")
    public Response updateProfile(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, final ProfileAssociationtoDeviceModel profileAssociationtoDeviceModel) {
        return this.updateProfilestoDevice(deviceId, profileAssociationtoDeviceModel, requestContext);
    }
    
    @POST
    @ValidDeviceUDID
    @Path("udid/{udid}/profiles")
    public Response updateProfileUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, final ProfileAssociationtoDeviceModel profileAssociationtoDeviceModel) {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.updateProfilestoDevice(deviceId, profileAssociationtoDeviceModel, requestContext);
    }
    
    private Response updateProfilestoDevice(final Long deviceId, final ProfileAssociationtoDeviceModel profileAssociationtoDeviceModel, final ContainerRequestContext requestContext) {
        profileAssociationtoDeviceModel.setCustomerUserDetails(requestContext);
        profileAssociationtoDeviceModel.setDeviceId(deviceId);
        this.profileService.associateProfilesToDevices(profileAssociationtoDeviceModel);
        return Response.status(202).build();
    }
    
    @DELETE
    @ValidDevice
    @Path("{device_id}/profiles")
    public Response disassociateProfilestoDevice(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, final ProfileAssociationtoDeviceModel profileAssociationtoDeviceModel) throws DataAccessException {
        return this.disassociateProfilestoDevice(deviceId, requestContext, profileAssociationtoDeviceModel);
    }
    
    @DELETE
    @ValidDeviceUDID
    @Path("udid/{udid}/profiles")
    public Response disassociateProfilestoDeviceUDID(@Context final ContainerRequestContext requestContext, @PathParam("udid") final String udid, final ProfileAssociationtoDeviceModel profileAssociationtoDeviceModel) throws DataAccessException {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.disassociateProfilestoDevice(deviceId, requestContext, profileAssociationtoDeviceModel);
    }
    
    private Response disassociateProfilestoDevice(final Long deviceId, final ContainerRequestContext requestContext, final ProfileAssociationtoDeviceModel profileAssociationtoDeviceModel) throws DataAccessException {
        profileAssociationtoDeviceModel.setCustomerUserDetails(requestContext);
        profileAssociationtoDeviceModel.setDeviceId(deviceId);
        this.profileService.disassociateProfilesToDevices(profileAssociationtoDeviceModel);
        return Response.status(202).build();
    }
    
    @POST
    @ValidDevice
    @Path("{device_id}/apps/refreshstatus")
    public Response refreshAppsStatusForDevice(@Context final ContainerRequestContext requestContext, final AppAssociateDetailsModel appAssociateDetailsModel, @PathParam("device_id") final Long deviceId, @QueryParam("status") final Integer status) throws Exception {
        return this.refreshAppsStatusForDevice(deviceId, status, requestContext, appAssociateDetailsModel);
    }
    
    @POST
    @ValidDeviceUDID
    @Path("udid/{udid}/apps/refreshstatus")
    public Response refreshAppsStatusForDeviceUDID(@Context final ContainerRequestContext requestContext, final AppAssociateDetailsModel appAssociateDetailsModel, @PathParam("udid") final String udid, @QueryParam("status") final Integer status) throws Exception {
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        return this.refreshAppsStatusForDevice(deviceId, status, requestContext, appAssociateDetailsModel);
    }
    
    @GET
    @Path("lost_devices_count")
    public LostDeviceCountModel getLostDeviceCount(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo) throws Exception {
        final String customerIdStr = (String)requestContext.getProperty("X-Customer");
        long customerId = -1L;
        if (customerIdStr != null && !customerIdStr.equalsIgnoreCase("All")) {
            customerId = Long.parseLong(customerIdStr);
        }
        return this.deviceService.getLostDeviceCount(customerId);
    }
    
    private Response refreshAppsStatusForDevice(final Long deviceId, final Integer status, final ContainerRequestContext requestContext, final AppAssociateDetailsModel appAssociateDetailsModel) throws Exception {
        appAssociateDetailsModel.setCustomerUserDetails(requestContext);
        appAssociateDetailsModel.setDeviceId(deviceId);
        appAssociateDetailsModel.setStatus(status);
        this.appService.refreshAppStatusForDevice(appAssociateDetailsModel);
        return Response.status(204).build();
    }
    
    @GET
    @ValidDevice
    @Path("/{device_id}/bootstrapTokenInfo")
    public BootstrapTokenInfoModel bootstrapTokenInfo(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long resourceId) throws Exception {
        return this.deviceService.getIndividualMacBootstrapTokenInfo(resourceId);
    }
    
    @POST
    @ValidDevice
    @Path("/{device_id}/actions/logout_user")
    public Response logoutUser(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId) throws Exception {
        final DeviceActionModel model = new DeviceActionModel();
        model.setCustomerUserDetails(requestContext);
        model.setCommandName("LogOutUser");
        model.setDeviceId(deviceId);
        final SearchDevice searchModel = new SearchDevice();
        searchModel.setDeviceId(deviceId);
        searchModel.setCustomerUserDetails(requestContext);
        new DeviceActionService().logoutUser(model, searchModel);
        return Response.status(202).build();
    }
    
    @POST
    @ValidDeviceUDID
    @Path("udid/{udid}/actions/logout_user")
    public Response logoutUser(@Context final ContainerRequestContext requestContext) throws Exception {
        final DeviceActionModel model = new DeviceActionModel();
        model.setCustomerUserDetails(requestContext);
        model.setCommandName("LogOutUser");
        final Long deviceId = (Long)requestContext.getProperty("MANAGED_DEVICE_ID");
        model.setDeviceId(deviceId);
        final SearchDevice searchModel = new SearchDevice();
        searchModel.setDeviceId(deviceId);
        searchModel.setCustomerUserDetails(requestContext);
        new DeviceActionService().logoutUser(model, searchModel);
        return Response.status(202).build();
    }
    
    @GET
    @ValidDeviceUDID
    @Path("/{device_id}/users")
    public DeviceUserListModel getDeviceUsers(@Context final ContainerRequestContext requestContext, @Context final UriInfo uriInfo, @PathParam("device_id") final Long deviceId) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        for (final Map.Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            final String value = entry.getValue().get(0);
            queryParams.put(entry.getKey(), value);
        }
        final SearchDeviceUser deviceUser = (SearchDeviceUser)mapper.readValue(new JSONObject((Map)queryParams).toString(), (Class)SearchDeviceUser.class);
        deviceUser.setDeviceId(deviceId);
        deviceUser.setCustomerUserDetails(requestContext);
        return new DeviceUserService().getDeviceUser(deviceUser);
    }
    
    @GET
    @ValidDevice
    @Path("/{device_id}/unlockpin")
    public DeviceUnlockSettingsModel getDeviceLockPin(@Context final ContainerRequestContext requestContext, @PathParam("device_id") final Long deviceId, @Context final UriInfo uriInfo) throws Exception {
        final DeviceUnlockSettingsModel deviceUnlockSettingsModel = new DeviceUnlockSettingsModel();
        deviceUnlockSettingsModel.setCustomerUserDetails(requestContext);
        deviceUnlockSettingsModel.setResourceID(deviceId);
        this.deviceService.setDeviceUnlockPinDetails(deviceUnlockSettingsModel);
        return deviceUnlockSettingsModel;
    }
    
    static {
        DeviceController.logger = Logger.getLogger("MDMApiLogger");
    }
}
