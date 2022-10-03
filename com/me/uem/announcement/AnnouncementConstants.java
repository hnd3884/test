package com.me.uem.announcement;

import java.util.ArrayList;

public class AnnouncementConstants
{
    public static final String ANNOUNCEMENT = "announcement";
    public static final String ANNOUNCEMENTS = "announcements";
    public static final String ANNOUNCEMENT_ID = "announcement_id";
    public static final String ANNOUNCEMENT_NAME = "announcement_name";
    public static final String ANNOUNCEMENT_FORMAT = "announcement_format";
    public static final String NEEDS_ACKNOWLEDGEMENT = "needs_acknowledgement";
    public static final String ACK_BUTTON = "ack_button";
    public static final String ANNOUNCEMENT_DETAIL = "announcement_detail";
    public static final String TITLE = "title";
    public static final String TITLE_COLOR = "title_color";
    public static final String NBAR_ICON_ID = "nbar_icon_id";
    public static final String NBAR_ICON = "nbar_icon";
    public static final String NBAR_MESSAGE = "nbar_message";
    public static final String DETAIL_MESSAGE = "detail_message";
    public static final String ANNOUNCEMENT_SPAN = "announcement_span";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String REPEAT_FREQUENCY = "repeat_frequency";
    public static final String REPEAT_DURATION = "repeat_duration";
    public static final String ANNOUNCEMENT_ACK_TIME = "acknowledged_time";
    public static final String ANNOUNCEMENT_READ_TIME = "read_time";
    public static final String ANNOUNCEMENT_DELETED = "announcement_deleted";
    public static final String ANNOUNCEMENT_READ_ALREADY = "announcement_read";
    public static final String ANNOUNCEMENT_ACK_ALREADY = "announcement_acknowledged";
    public static final String ANNOUNCEMENT_DETAILS = "announcementdetails";
    public static final String ANNOUNCEMENT_NEED_DELIVERY = "announcement_need_delivery";
    public static final String ANNOUNCEMENT_DISTRIBUTED_TIME = "announcement_distributed_time";
    public static final String ANNOUNCEMENT_ADDED_LIST = "added_announcement";
    public static final String ANNOUNCEMENT_MODIFIED_LIST = "modified_announcement";
    public static final String ANNOUNCEMENT_DELETED_LIST = "deleted_announcement";
    public static final String ANNOUNCEMENT_DELIVERED_TIME = "delivered_time";
    public static final int AF_N_BAR = 1;
    public static final int AF_DETAILED_MSG = 2;
    public static final int AF_COMBINED = 3;
    public static final int AF_UNOBTRUSIVE = 4;
    public static final int ANNOUNCEMENT_READ = 2;
    public static final int ANNOUNCEMENT_ACKNOWLEDGE = 3;
    public static final ArrayList ANNOUNCEMENT_YET_TO_DELIVER;
    public static final ArrayList ANNOUNCEMENT_DELIVERED;
    public static final ArrayList ANNOUNCEMENT_FAILED;
    
    static {
        ANNOUNCEMENT_YET_TO_DELIVER = new ArrayList() {
            {
                this.add(18);
                this.add(200);
                this.add(300);
                this.add(1);
                this.add(3);
                this.add(12);
                this.add(16);
                this.add(18);
                this.add(13);
            }
        };
        ANNOUNCEMENT_DELIVERED = new ArrayList() {
            {
                this.add(2);
                this.add(6);
                this.add(4);
            }
        };
        ANNOUNCEMENT_FAILED = new ArrayList() {
            {
                this.add(7);
                this.add(9);
                this.add(10);
                this.add(11);
            }
        };
    }
}
