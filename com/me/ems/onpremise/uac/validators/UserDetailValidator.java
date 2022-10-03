package com.me.ems.onpremise.uac.validators;

import java.lang.annotation.Annotation;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;
import java.util.logging.Logger;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;
import com.me.ems.onpremise.uac.api.annotations.Valid;
import javax.validation.ConstraintValidator;

public class UserDetailValidator implements ConstraintValidator<Valid, UserDetails>
{
    private static Logger logger;
    List<Integer> allowedComputerScopes;
    List<Integer> allowedDeviceScopes;
    List<String> allowedAuthTypes;
    
    public UserDetailValidator() {
        this.allowedComputerScopes = Stream.of(new Integer[] { 0, 1, 2 }).collect((Collector<? super Integer, ?, List<Integer>>)Collectors.toList());
        this.allowedDeviceScopes = Stream.of(new Integer[] { 0, 1 }).collect((Collector<? super Integer, ?, List<Integer>>)Collectors.toList());
        this.allowedAuthTypes = Stream.of(new String[] { "localAuthentication", "adAuthentication" }).collect((Collector<? super String, ?, List<String>>)Collectors.toList());
    }
    
    public void initialize(final Valid constraintAnnotation) {
    }
    
    public boolean isValid(final UserDetails userDetails, final ConstraintValidatorContext constraintValidatorContext) {
        return this.allowedComputerScopes.contains(userDetails.getComputerScopeType()) && this.allowedDeviceScopes.contains(userDetails.getDeviceScopeType()) && this.allowedAuthTypes.contains(userDetails.getAuthType()) && (userDetails.getLanguage().equalsIgnoreCase("en_US") || LicenseProvider.getInstance().isLanguagePackEnabled());
    }
    
    static {
        UserDetailValidator.logger = Logger.getLogger("UserManagementLogger");
    }
}
