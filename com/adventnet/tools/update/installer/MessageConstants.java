package com.adventnet.tools.update.installer;

public enum MessageConstants
{
    SELF_SIGNED_WARNING_MESSAGE("self.signed.warning.message", "System detected a potential security threat that the given patch file is not signed using a trusted certificate. \n\nIt is recommended to report this to support immediately. \n\nVisit https://www.manageengine.com for more details."), 
    IMPORT_SELF_SIGNED_CERTIFICATE("self.signed.certificate.import.message", "The certificate imported to verify the patch integrity is a self-signed or a non-trusted certificate. \n\nIt is recommended to report this to support immediately. \n\nVisit https://www.manageengine.com for more details."), 
    ACCEPT_RISKS_MESSAGE("self.signed.warning.accept.message", "I hereby understand and accept the risks and confirm that I will be responsible for any issue arising out of this consent. Click \"Proceed\" to continue."), 
    SELF_SIGNED_WARNING_HEADER("self.signed.warning.header", "Alert: Potential Security Risk Ahead"), 
    CANNOT_DISPLAY_CONTEXT_HELP("cannot.display.context.help", "Unable to show context sensitive help. Please contact support."), 
    VALIDATING_HELP_FILE_URL("invalid.help.url", "Issue occurred while validating help file url."), 
    IMPORT_CERTIFICATE_DESCRIPTION("import.certificate.description", "Click \"Import\" button to import certificate required to verify patch integrity. This will lead to a guided import process. Valid certificate can be downloaded from ${cert.url[here]}"), 
    CMD_IMPORT_CERTIFICATE_DESCRIPTION("cmd.import.certificate.description", "In the absence of a Valid certificate for the Patch Installation, it can be downloaded from ${cert.url}."), 
    INSTALL_DESCRIPTION("install.description", "To install a Software Upgrade or Service Pack, click 'Install' button. This will open the Installation Wizard which will guide you through the upgrade process. "), 
    SERVICE_PACK_LIST_DESCRIPTION("service.pack.list.description", "Service Pack(s) installed is(are) listed below. To view the details of a particular Service Pack, either double-click on it or click 'Details' button.To uninstall a particular Service Pack, select and click 'Uninstall' button. "), 
    INVALID_CERTIFICATE_FILE_EXTENSION("invalid.certificate.file.extension", "The certificate should be a file with extension .crt or .cer. Please provide a valid certificate file."), 
    FILE_NOT_EXISTS("file.not.exist", "The specified file does not exist."), 
    CERTIFICATE_EXISTS("certificate.exist", "The selected certificate already exists in keystore."), 
    CERTIFICATE_NOT_EXIST("certificate.not.exist", "The selected certificate does not exist."), 
    IMPORT_CERTIFICATE_SUCCESS("import.certificate.success", "The selected certificate is imported successfully."), 
    IMPORT_CERTIFICATE_FAILED("import.certificate.failed", "Import failed for selected certificate. Please contact support to get help."), 
    IMPORT_CERTIFICATE_URL_COPIED_TO_CLIPBOARD("import.certificate.url.copy.clipboard", "Failed to open browser. Hence, the link to download the Certificate (${cert.url}) has been copied to clipboard."), 
    SELECT_FILE("select.file", "Select a File"), 
    GET_CERTIFICATE_INPUT("get.certificate", "Enter the certificate file to import : "), 
    GET_CONSENT1_INPUT("get.consent1.input", "Enter \"a\" for Advanced or any other key to Exit(Recommended)"), 
    GET_CONSENT2_INPUT("get.consent2.input", "Enter \"p\" to Proceed or any other key to Exit(Recommended)"), 
    IMPORT_CERTIFICATE("import.certificate", "Import Certificate"), 
    INSTALL("install", "Install"), 
    UNINSTALL("uninstall", "Uninstall"), 
    BROWSE("browse", "Browse"), 
    README("readme", "Readme"), 
    SERVICE_PACK_LIST("service.pack.list", "List of Installed Service Packs"), 
    DETAILS("details", "Details"), 
    IMPORT("import", "Import"), 
    EXIT("exit", "Exit"), 
    HELP("help", "Help"), 
    BACK("back", "Back"), 
    HERE("here", "here"), 
    CLICK_TO_COPY("click.to.copy", "Click to Copy"), 
    ERROR("error", "Error"), 
    WARNING("warning", "Warning"), 
    INFO("info", "Information"), 
    RECOMMENDED("recommended", "Recommended"), 
    ADVANCED("advanced", "Advanced"), 
    PROCEED("proceed", "Proceed"), 
    SELF_SIGNED_CERTIFICATE_WARNING_TITLE("self.signed.certificate.warning.title", "Obtaining consent for using self signed certificate"), 
    SELF_SIGNED_PATCH_WARNING_TITLE("self.signed.patch.warning.title", "Obtaining consent for using self signed patch"), 
    PATCH_INTEGRITY_PASSWORD_NOT_SUPPLIED("integrity.state.password.not.supplied", "Keystore password not provided."), 
    PATCH_INTEGRITY_STATE_NO_PATCH_FILE("integrity.state.no.patch.file", "Patch file is not specified."), 
    PATCH_INTEGRITY_STATE_NO_KEYSTORE_FILE("integrity.state.no.keystore.file", "Keystore file not found. Kindly Import Certificate before applying patch."), 
    PATCH_INTEGRITY_STATE_PATCH_FILE_DOES_NOT_EXIST("integrity.state.patch.file.not.exist", "Patch file does not exist."), 
    PATCH_INTEGRITY_STATE_NO_CERTIFICATES("integrity.state.no.certificates", "Certificates are not specified. Please contact support."), 
    PATCH_INTEGRITY_STATE_UNABLE_TO_PARSE_SIGNATURE_FILE("integrity.state.unable.to.parse", "Unable to parse Signature file. Please contact support."), 
    PATCH_INTEGRITY_STATE_PATCH_NOT_SIGNED("integrity.state.patch.not.signed", "Given patch is not signed. It is not recommended to install this patch as it may have been tampered. Please contact support."), 
    PATCH_INTEGRITY_STATE_CONTENTS_NOT_SIGNED("integrity.state.contents.not.signed", "Contents of the given patch are not signed. It is not recommended to install this patch as it may have been tampered. Please contact support."), 
    PATCH_INTEGRITY_STATE_CONTENTS_MODIFIED("integrity.state.contents.modified", "Contents of the given patch are modified. It is not recommended to install this patch. Please contact support."), 
    PATCH_INTEGRITY_STATE_SIGNATURE_DOES_NOT_MATCH("integrity.state.signature.not.match", "Signature does not match with any available certificates. Please import valid certificate or contact support."), 
    PATCH_INTEGRITY_STATE_CONTENTS_SIGNED_WITH_DIFFERENT_CERTIFICATE("integrity.state.signed.with.different.certificate", "Contents of the given patch are signed with a different certificate."), 
    PATCH_INTEGRITY_STATE_DIGEST_NOT_FOUND("integrity.state.digest.not.found", "Checksum file not found in the patch. It is not recommended to install this patch. Please contact support."), 
    PATCH_INTEGRITY_STATE_EMPTY_MANIFEST("integrity.state.empty.manifest", "Manifest file is empty in the given patch. Please contact support."), 
    PATCH_INTEGRITY_SIGNED_WITH_BLACKLISTED_CERTIFICATE("integrity.state.signed.with.blacklisted.certificate", "The given patch is signed with a blacklisted certificate. It is not safe to install this patch. Please download the patch and import the certificate again."), 
    PATCH_INTEGRITY_STATE_SUCCESS("integrity.state.success", "Verification of Patch Integrity is successful"), 
    PATCH_INTEGRITY_STATE_FAILURE("integrity.state.failure", "Signature verification for the patch file has failed. The patch may have been tampered. Please contact support.");
    
    String key;
    String message;
    
    private MessageConstants(final String key, final String message) {
        this.key = key;
        this.message = message;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getMessage() {
        return this.message;
    }
}
