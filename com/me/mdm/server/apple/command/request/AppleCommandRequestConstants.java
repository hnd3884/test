package com.me.mdm.server.apple.command.request;

public class AppleCommandRequestConstants
{
    public static final class UserList
    {
        public static final String COMMAND_NAME = "UserList";
    }
    
    public static final class Settings
    {
        public static final String COMMAND_NAME = "Settings";
        
        public static final class SharedDeviceConfiguration
        {
            public static final String ITEM = "SharedDeviceConfiguration";
            public static final String QUOTA_SIZE = "QuotaSize";
            public static final String RESIDENT_USERS = "ResidentUsers";
            public static final String TEMPORARY_SESSION_ONLY = "TemporarySessionOnly";
            public static final String TEMPORARY_SESSION_TIMEOUT = "TemporarySessionTimeout";
            public static final String USER_SESSION_TIMEOUT = "UserSessionTimeout";
        }
        
        public static final class PasscodeGracePeriod
        {
            public static final String ITEM = "PasscodeLockGracePeriod";
        }
        
        public static final class AppAnalytics
        {
            public static final String ITEM = "AppAnalytics";
            public static final String ENABLED = "Enabled";
        }
        
        public static final class DiagnosticSubmission
        {
            public static final String ITEM = "DiagnosticSubmission";
            public static final String ENABLED = "Enabled";
        }
        
        public static final class AccessibiblitySettings
        {
            public static final String ITEM = "AccessibilitySettings";
            public static final String BOLD_TEXT = "BoldTextEnabled";
            public static final String INCREASE_CONTRAST_ENABLED = "IncreaseContrastEnabled";
            public static final String REDUCE_MOTION_ENABLED = "ReduceMotionEnabled";
            public static final String REDUCE_TRANSPARENCY_ENABLED = "ReduceTransparencyEnabled";
            public static final String TEXT_SIZE = "TextSize";
            public static final String TOUCH_ACCOMMODATIONS_ENABLED = "TouchAccommodationsEnabled";
            public static final String VOICE_OVER_ENABLED = "VoiceOverEnabled";
            public static final String ZOOM_ENABLED = "ZoomEnabled";
        }
    }
    
    public static final class LogoutUser
    {
        public static final String COMMAND_NAME = "LogOutUser";
    }
}
