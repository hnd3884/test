package com.adventnet.tools.update.installer;

public enum PatchInstallationState
{
    PATCH_VALIDATION_STARTED, 
    PATCH_VALIDATION_COMPLETED, 
    PATCH_VALIDATION_FAILED, 
    PATCH_INSTALLATION_STARTED, 
    PATCH_INSTALLATION_FAILED, 
    PATCH_INSTALLATION_COMPLETED, 
    PRE_PROCESSOR_EXECUTION_STARTED, 
    PRE_PROCESSOR_EXECUTION_COMPLETED, 
    PRE_PROCESSOR_EXECUTION_FAILED, 
    FILE_BACKUP_STARTED, 
    FILE_BACKUP_COMPLETED, 
    FILE_BACKUP_FAILED, 
    FILE_UPDATE_STARTED, 
    FILE_UPDATE_COMPLETED, 
    FILE_UPDATE_FAILED, 
    POST_PROCESSOR_EXECUTION_STARTED, 
    POST_PROCESSOR_EXECUTION_COMPLETED, 
    POST_PROCESSOR_EXECUTION_FAILED, 
    PATCH_REVERT_STARTED, 
    PATCH_REVERT_FAILED, 
    PATCH_REVERT_COMPLETED, 
    POST_PROCESSOR_REVERT_EXECUTION_STARTED, 
    POST_PROCESSOR_REVERT_EXECUTION_COMPLETED, 
    POST_PROCESSOR_REVERT_EXECUTION_FAILED, 
    FILE_REVERT_STARTED, 
    FILE_REVERT_COMPLETED, 
    FILE_REVERT_FAILED, 
    FILE_RESTORE_STARTED, 
    FILE_RESTORE_COMPLETED, 
    FILE_RESTORE_FAILED, 
    PRE_PROCESSOR_REVERT_EXECUTION_STARTED, 
    PRE_PROCESSOR_REVERT_EXECUTION_COMPLETED, 
    PRE_PROCESSOR_REVERT_EXECUTION_FAILED, 
    CONSENT_ACCEPTED_FOR_SELF_SIGNED_PPM, 
    SIGNED_WITH_BLACKLISTED_CERTIFICATE;
}