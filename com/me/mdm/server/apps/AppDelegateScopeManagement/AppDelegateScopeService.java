package com.me.mdm.server.apps.AppDelegateScopeManagement;

import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.List;
import org.json.JSONObject;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.server.device.api.model.apps.AppDelegateScopeModel;
import com.me.mdm.server.apps.config.AppConfigDataHandler;

public class AppDelegateScopeService
{
    private AppConfigDataHandler appConfigDataHandler;
    private AppDelegateScopeModel appDelegateScopeModel;
    private AppDelegateScopeDBHandler appDelegateScopeDBHandler;
    private AppDelegateScopeHandler appDelegateScopeHandler;
    private AppFacade appFacade;
    
    public AppDelegateScopeService() {
        this.appConfigDataHandler = new AppConfigDataHandler();
        this.appDelegateScopeModel = new AppDelegateScopeModel();
        this.appDelegateScopeDBHandler = new AppDelegateScopeDBHandler();
        this.appDelegateScopeHandler = new AppDelegateScopeHandler();
        this.appFacade = new AppFacade();
    }
    
    public void addOrModifyAppDelegateScope(final AppDelegateScopeModel model) throws Exception {
        final JSONObject jsonObject = this.appDelegateScopeHandler.validateAppDelegateScope(model);
        this.appDelegateScopeDBHandler.addOrModifyDelegatedScope(jsonObject, model);
        final List resourceList = this.appDelegateScopeDBHandler.constructResourceList(model.getConfigDataItemId());
        DeviceCommandRepository.getInstance().addAppPermissionPolicyCommand(resourceList);
        NotificationHandler.getInstance().SendNotification(resourceList, 2);
    }
    
    public AppDelegateScopeModel initGetResponseObject(final ContainerRequestContext containerRequestContext, final Long appId, final Long labelId) {
        this.appDelegateScopeModel.setCustomerUserDetails(containerRequestContext);
        this.appDelegateScopeModel.setLabelId(labelId);
        this.appDelegateScopeModel.setAppId(appId);
        return this.appDelegateScopeModel;
    }
    
    public AppDelegateScopeModel getAppDelegateScope(final AppDelegateScopeModel appDelegateScopeModel) throws Exception {
        this.appFacade.validateIfAppFound(appDelegateScopeModel.getAppId(), appDelegateScopeModel.getCustomerId());
        this.appFacade.validateIfReleaseLabelFound(appDelegateScopeModel.getLabelId(), appDelegateScopeModel.getCustomerId());
        final Long configDataItem = this.appConfigDataHandler.getAppConfigDataItemId(appDelegateScopeModel.getLabelId(), appDelegateScopeModel.getAppId());
        if (configDataItem == -1L) {
            throw new Exception("ConfigDataItemId not found for the appId " + appDelegateScopeModel.getAppId());
        }
        final Map resData = this.appDelegateScopeHandler.constructResponseDelegateScope(configDataItem);
        appDelegateScopeModel.setDelegateScope(resData);
        return appDelegateScopeModel;
    }
}
