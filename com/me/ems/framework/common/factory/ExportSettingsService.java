package com.me.ems.framework.common.factory;

import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;

public interface ExportSettingsService
{
    Map getExportSettings(final User p0) throws APIException;
    
    Map currentExportRedactType(final User p0) throws APIException;
    
    void userChosenRedactLevel(final User p0, final Map p1, final HttpServletRequest p2) throws APIException;
    
    void validateExportSettings(final Map p0, final User p1) throws APIException;
    
    boolean saveExportSettings(final Map p0, final Long p1, final HttpServletRequest p2) throws APIException;
    
    Long validateCustomer(final String p0) throws APIException;
}
