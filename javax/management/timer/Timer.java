package javax.management.timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import javax.management.InstanceNotFoundException;
import java.util.TimerTask;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import javax.management.MBeanNotificationInfo;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanRegistration;
import javax.management.NotificationBroadcasterSupport;

public class Timer extends NotificationBroadcasterSupport implements TimerMBean, MBeanRegistration
{
    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60000L;
    public static final long ONE_HOUR = 3600000L;
    public static final long ONE_DAY = 86400000L;
    public static final long ONE_WEEK = 604800000L;
    private final Map<Integer, Object[]> timerTable;
    private boolean sendPastNotifications;
    private transient boolean isActive;
    private transient long sequenceNumber;
    private static final int TIMER_NOTIF_INDEX = 0;
    private static final int TIMER_DATE_INDEX = 1;
    private static final int TIMER_PERIOD_INDEX = 2;
    private static final int TIMER_NB_OCCUR_INDEX = 3;
    private static final int ALARM_CLOCK_INDEX = 4;
    private static final int FIXED_RATE_INDEX = 5;
    private volatile int counterID;
    private java.util.Timer timer;
    
    public Timer() {
        this.timerTable = new HashMap<Integer, Object[]>();
        this.sendPastNotifications = false;
        this.isActive = false;
        this.sequenceNumber = 0L;
        this.counterID = 0;
    }
    
    @Override
    public ObjectName preRegister(final MBeanServer mBeanServer, final ObjectName objectName) throws Exception {
        return objectName;
    }
    
    @Override
    public void postRegister(final Boolean b) {
    }
    
    @Override
    public void preDeregister() throws Exception {
        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "preDeregister", "stop the timer");
        this.stop();
    }
    
    @Override
    public void postDeregister() {
    }
    
    @Override
    public synchronized MBeanNotificationInfo[] getNotificationInfo() {
        final TreeSet set = new TreeSet();
        final Iterator<Object[]> iterator = this.timerTable.values().iterator();
        while (iterator.hasNext()) {
            set.add(((TimerNotification)iterator.next()[0]).getType());
        }
        return new MBeanNotificationInfo[] { new MBeanNotificationInfo((String[])set.toArray(new String[0]), TimerNotification.class.getName(), "Notification sent by Timer MBean") };
    }
    
    @Override
    public synchronized void start() {
        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "starting the timer");
        if (!this.isActive) {
            this.timer = new java.util.Timer();
            final Date date = new Date();
            this.sendPastNotifications(date, this.sendPastNotifications);
            for (final Object[] array : this.timerTable.values()) {
                final Date date2 = (Date)array[1];
                if (array[5]) {
                    final TimerAlarmClock timerAlarmClock = new TimerAlarmClock(this, date2);
                    array[4] = timerAlarmClock;
                    this.timer.schedule(timerAlarmClock, timerAlarmClock.next);
                }
                else {
                    final TimerAlarmClock timerAlarmClock2 = new TimerAlarmClock(this, date2.getTime() - date.getTime());
                    array[4] = timerAlarmClock2;
                    this.timer.schedule(timerAlarmClock2, timerAlarmClock2.timeout);
                }
            }
            this.isActive = true;
            JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "timer started");
        }
        else {
            JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "the timer is already activated");
        }
    }
    
    @Override
    public synchronized void stop() {
        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "stopping the timer");
        if (this.isActive) {
            final Iterator<Object[]> iterator = this.timerTable.values().iterator();
            while (iterator.hasNext()) {
                final TimerAlarmClock timerAlarmClock = (TimerAlarmClock)iterator.next()[4];
                if (timerAlarmClock != null) {
                    timerAlarmClock.cancel();
                }
            }
            this.timer.cancel();
            this.isActive = false;
            JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "timer stopped");
        }
        else {
            JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "the timer is already deactivated");
        }
    }
    
    @Override
    public synchronized Integer addNotification(final String s, final String s2, final Object userData, final Date date, final long n, final long n2, final boolean b) throws IllegalArgumentException {
        if (date == null) {
            throw new IllegalArgumentException("Timer notification date cannot be null.");
        }
        if (n < 0L || n2 < 0L) {
            throw new IllegalArgumentException("Negative values for the periodicity");
        }
        final Date date2 = new Date();
        if (date2.after(date)) {
            date.setTime(date2.getTime());
            if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", "update timer notification to add with:\n\tNotification date = " + date);
            }
        }
        final Integer value = ++this.counterID;
        final TimerNotification timerNotification = new TimerNotification(s, this, 0L, 0L, s2, value);
        timerNotification.setUserData(userData);
        final Object[] array = new Object[6];
        TimerAlarmClock timerAlarmClock;
        if (b) {
            timerAlarmClock = new TimerAlarmClock(this, date);
        }
        else {
            timerAlarmClock = new TimerAlarmClock(this, date.getTime() - date2.getTime());
        }
        final Date date3 = new Date(date.getTime());
        array[0] = timerNotification;
        array[1] = date3;
        array[2] = n;
        array[3] = n2;
        array[4] = timerAlarmClock;
        array[5] = b;
        if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", "adding timer notification:\n\t" + "Notification source = " + timerNotification.getSource() + "\n\tNotification type = " + timerNotification.getType() + "\n\tNotification ID = " + value + "\n\tNotification date = " + date3 + "\n\tNotification period = " + n + "\n\tNotification nb of occurrences = " + n2 + "\n\tNotification executes at fixed rate = " + b);
        }
        this.timerTable.put(value, array);
        if (this.isActive) {
            if (b) {
                this.timer.schedule(timerAlarmClock, timerAlarmClock.next);
            }
            else {
                this.timer.schedule(timerAlarmClock, timerAlarmClock.timeout);
            }
        }
        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", "timer notification added");
        return value;
    }
    
    @Override
    public synchronized Integer addNotification(final String s, final String s2, final Object o, final Date date, final long n, final long n2) throws IllegalArgumentException {
        return this.addNotification(s, s2, o, date, n, n2, false);
    }
    
    @Override
    public synchronized Integer addNotification(final String s, final String s2, final Object o, final Date date, final long n) throws IllegalArgumentException {
        return this.addNotification(s, s2, o, date, n, 0L);
    }
    
    @Override
    public synchronized Integer addNotification(final String s, final String s2, final Object o, final Date date) throws IllegalArgumentException {
        return this.addNotification(s, s2, o, date, 0L, 0L);
    }
    
    @Override
    public synchronized void removeNotification(final Integer n) throws InstanceNotFoundException {
        if (!this.timerTable.containsKey(n)) {
            throw new InstanceNotFoundException("Timer notification to remove not in the list of notifications");
        }
        final Object[] array = this.timerTable.get(n);
        final TimerAlarmClock timerAlarmClock = (TimerAlarmClock)array[4];
        if (timerAlarmClock != null) {
            timerAlarmClock.cancel();
        }
        if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeNotification", "removing timer notification:" + "\n\tNotification source = " + ((TimerNotification)array[0]).getSource() + "\n\tNotification type = " + ((TimerNotification)array[0]).getType() + "\n\tNotification ID = " + ((TimerNotification)array[0]).getNotificationID() + "\n\tNotification date = " + array[1] + "\n\tNotification period = " + array[2] + "\n\tNotification nb of occurrences = " + array[3] + "\n\tNotification executes at fixed rate = " + array[5]);
        }
        this.timerTable.remove(n);
        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeNotification", "timer notification removed");
    }
    
    @Override
    public synchronized void removeNotifications(final String s) throws InstanceNotFoundException {
        final Vector<Integer> notificationIDs = this.getNotificationIDs(s);
        if (notificationIDs.isEmpty()) {
            throw new InstanceNotFoundException("Timer notifications to remove not in the list of notifications");
        }
        final Iterator<Integer> iterator = notificationIDs.iterator();
        while (iterator.hasNext()) {
            this.removeNotification(iterator.next());
        }
    }
    
    @Override
    public synchronized void removeAllNotifications() {
        final Iterator<Object[]> iterator = this.timerTable.values().iterator();
        while (iterator.hasNext()) {
            ((TimerAlarmClock)iterator.next()[4]).cancel();
        }
        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "removing all timer notifications");
        this.timerTable.clear();
        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "all timer notifications removed");
        this.counterID = 0;
        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "timer notification counter ID reset");
    }
    
    @Override
    public synchronized int getNbNotifications() {
        return this.timerTable.size();
    }
    
    @Override
    public synchronized Vector<Integer> getAllNotificationIDs() {
        return new Vector<Integer>(this.timerTable.keySet());
    }
    
    @Override
    public synchronized Vector<Integer> getNotificationIDs(final String s) {
        final Vector vector = new Vector();
        for (final Map.Entry entry : this.timerTable.entrySet()) {
            final String type = ((TimerNotification)((Object[])entry.getValue())[0]).getType();
            if (s == null) {
                if (type != null) {
                    continue;
                }
            }
            else if (!s.equals(type)) {
                continue;
            }
            vector.addElement(entry.getKey());
        }
        return vector;
    }
    
    @Override
    public synchronized String getNotificationType(final Integer n) {
        final Object[] array = this.timerTable.get(n);
        if (array != null) {
            return ((TimerNotification)array[0]).getType();
        }
        return null;
    }
    
    @Override
    public synchronized String getNotificationMessage(final Integer n) {
        final Object[] array = this.timerTable.get(n);
        if (array != null) {
            return ((TimerNotification)array[0]).getMessage();
        }
        return null;
    }
    
    @Override
    public synchronized Object getNotificationUserData(final Integer n) {
        final Object[] array = this.timerTable.get(n);
        if (array != null) {
            return ((TimerNotification)array[0]).getUserData();
        }
        return null;
    }
    
    @Override
    public synchronized Date getDate(final Integer n) {
        final Object[] array = this.timerTable.get(n);
        if (array != null) {
            return new Date(((Date)array[1]).getTime());
        }
        return null;
    }
    
    @Override
    public synchronized Long getPeriod(final Integer n) {
        final Object[] array = this.timerTable.get(n);
        if (array != null) {
            return (Long)array[2];
        }
        return null;
    }
    
    @Override
    public synchronized Long getNbOccurences(final Integer n) {
        final Object[] array = this.timerTable.get(n);
        if (array != null) {
            return (Long)array[3];
        }
        return null;
    }
    
    @Override
    public synchronized Boolean getFixedRate(final Integer n) {
        final Object[] array = this.timerTable.get(n);
        if (array != null) {
            return (boolean)array[5];
        }
        return null;
    }
    
    @Override
    public boolean getSendPastNotifications() {
        return this.sendPastNotifications;
    }
    
    @Override
    public void setSendPastNotifications(final boolean sendPastNotifications) {
        this.sendPastNotifications = sendPastNotifications;
    }
    
    @Override
    public boolean isActive() {
        return this.isActive;
    }
    
    @Override
    public synchronized boolean isEmpty() {
        return this.timerTable.isEmpty();
    }
    
    private synchronized void sendPastNotifications(final Date date, final boolean b) {
        for (final Object[] array : new ArrayList(this.timerTable.values())) {
            final TimerNotification timerNotification = (TimerNotification)array[0];
            final Integer notificationID = timerNotification.getNotificationID();
            final Date date2 = (Date)array[1];
            while (date.after(date2) && this.timerTable.containsKey(notificationID)) {
                if (b) {
                    if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
                        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendPastNotifications", "sending past timer notification:" + "\n\tNotification source = " + timerNotification.getSource() + "\n\tNotification type = " + timerNotification.getType() + "\n\tNotification ID = " + timerNotification.getNotificationID() + "\n\tNotification date = " + date2 + "\n\tNotification period = " + array[2] + "\n\tNotification nb of occurrences = " + array[3] + "\n\tNotification executes at fixed rate = " + array[5]);
                    }
                    this.sendNotification(date2, timerNotification);
                    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendPastNotifications", "past timer notification sent");
                }
                this.updateTimerTable(timerNotification.getNotificationID());
            }
        }
    }
    
    private synchronized void updateTimerTable(final Integer n) {
        final Object[] array = this.timerTable.get(n);
        final Date date = (Date)array[1];
        final Long n2 = (Long)array[2];
        final Long n3 = (Long)array[3];
        final Boolean b = (Boolean)array[5];
        final TimerAlarmClock timerAlarmClock = (TimerAlarmClock)array[4];
        if (n2 != 0L) {
            if (n3 == 0L || n3 > 1L) {
                date.setTime(date.getTime() + n2);
                array[3] = Math.max(0L, n3 - 1L);
                final Long n4 = (Long)array[3];
                if (this.isActive) {
                    if (b) {
                        final TimerAlarmClock timerAlarmClock2 = new TimerAlarmClock(this, date);
                        array[4] = timerAlarmClock2;
                        this.timer.schedule(timerAlarmClock2, timerAlarmClock2.next);
                    }
                    else {
                        final TimerAlarmClock timerAlarmClock3 = new TimerAlarmClock(this, n2);
                        array[4] = timerAlarmClock3;
                        this.timer.schedule(timerAlarmClock3, timerAlarmClock3.timeout);
                    }
                }
                if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
                    final TimerNotification timerNotification = (TimerNotification)array[0];
                    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "updateTimerTable", "update timer notification with:" + "\n\tNotification source = " + timerNotification.getSource() + "\n\tNotification type = " + timerNotification.getType() + "\n\tNotification ID = " + n + "\n\tNotification date = " + date + "\n\tNotification period = " + n2 + "\n\tNotification nb of occurrences = " + n4 + "\n\tNotification executes at fixed rate = " + b);
                }
            }
            else {
                if (timerAlarmClock != null) {
                    timerAlarmClock.cancel();
                }
                this.timerTable.remove(n);
            }
        }
        else {
            if (timerAlarmClock != null) {
                timerAlarmClock.cancel();
            }
            this.timerTable.remove(n);
        }
    }
    
    void notifyAlarmClock(final TimerAlarmClockNotification timerAlarmClockNotification) {
        TimerNotification timerNotification = null;
        Date date = null;
        final TimerAlarmClock timerAlarmClock = (TimerAlarmClock)timerAlarmClockNotification.getSource();
        synchronized (this) {
            for (final Object[] array : this.timerTable.values()) {
                if (array[4] == timerAlarmClock) {
                    timerNotification = (TimerNotification)array[0];
                    date = (Date)array[1];
                    break;
                }
            }
        }
        this.sendNotification(date, timerNotification);
        this.updateTimerTable(timerNotification.getNotificationID());
    }
    
    void sendNotification(final Date date, final TimerNotification timerNotification) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     6: invokevirtual   java/util/logging/Logger.isLoggable:(Ljava/util/logging/Level;)Z
        //     9: ifeq            90
        //    12: new             Ljava/lang/StringBuilder;
        //    15: dup            
        //    16: invokespecial   java/lang/StringBuilder.<init>:()V
        //    19: ldc             "sending timer notification:"
        //    21: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    24: ldc             "\n\tNotification source = "
        //    26: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    29: aload_2        
        //    30: invokevirtual   javax/management/timer/TimerNotification.getSource:()Ljava/lang/Object;
        //    33: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    36: ldc             "\n\tNotification type = "
        //    38: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    41: aload_2        
        //    42: invokevirtual   javax/management/timer/TimerNotification.getType:()Ljava/lang/String;
        //    45: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    48: ldc             "\n\tNotification ID = "
        //    50: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    53: aload_2        
        //    54: invokevirtual   javax/management/timer/TimerNotification.getNotificationID:()Ljava/lang/Integer;
        //    57: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    60: ldc             "\n\tNotification date = "
        //    62: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    65: aload_1        
        //    66: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    69: astore_3       
        //    70: getstatic       com/sun/jmx/defaults/JmxProperties.TIMER_LOGGER:Ljava/util/logging/Logger;
        //    73: getstatic       java/util/logging/Level.FINER:Ljava/util/logging/Level;
        //    76: ldc             Ljavax/management/timer/Timer;.class
        //    78: invokevirtual   java/lang/Class.getName:()Ljava/lang/String;
        //    81: ldc             "sendNotification"
        //    83: aload_3        
        //    84: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    87: invokevirtual   java/util/logging/Logger.logp:(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
        //    90: aload_0        
        //    91: dup            
        //    92: astore          5
        //    94: monitorenter   
        //    95: aload_0        
        //    96: aload_0        
        //    97: getfield        javax/management/timer/Timer.sequenceNumber:J
        //   100: lconst_1       
        //   101: ladd           
        //   102: putfield        javax/management/timer/Timer.sequenceNumber:J
        //   105: aload_0        
        //   106: getfield        javax/management/timer/Timer.sequenceNumber:J
        //   109: lstore_3       
        //   110: aload           5
        //   112: monitorexit    
        //   113: goto            124
        //   116: astore          6
        //   118: aload           5
        //   120: monitorexit    
        //   121: aload           6
        //   123: athrow         
        //   124: aload_2        
        //   125: dup            
        //   126: astore          5
        //   128: monitorenter   
        //   129: aload_2        
        //   130: aload_1        
        //   131: invokevirtual   java/util/Date.getTime:()J
        //   134: invokevirtual   javax/management/timer/TimerNotification.setTimeStamp:(J)V
        //   137: aload_2        
        //   138: lload_3        
        //   139: invokevirtual   javax/management/timer/TimerNotification.setSequenceNumber:(J)V
        //   142: aload_0        
        //   143: aload_2        
        //   144: invokevirtual   javax/management/timer/TimerNotification.cloneTimerNotification:()Ljava/lang/Object;
        //   147: checkcast       Ljavax/management/timer/TimerNotification;
        //   150: invokevirtual   javax/management/timer/Timer.sendNotification:(Ljavax/management/Notification;)V
        //   153: aload           5
        //   155: monitorexit    
        //   156: goto            167
        //   159: astore          7
        //   161: aload           5
        //   163: monitorexit    
        //   164: aload           7
        //   166: athrow         
        //   167: getstatic       com/sun/jmx/defaults/JmxProperties.TIMER_LOGGER:Ljava/util/logging/Logger;
        //   170: getstatic       java/util/logging/Level.FINER:Ljava/util/logging/Level;
        //   173: ldc             Ljavax/management/timer/Timer;.class
        //   175: invokevirtual   java/lang/Class.getName:()Ljava/lang/String;
        //   178: ldc             "sendNotification"
        //   180: ldc             "timer notification sent"
        //   182: invokevirtual   java/util/logging/Logger.logp:(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
        //   185: return         
        //    StackMapTable: 00 05 FB 00 5A FF 00 19 00 06 07 00 D7 07 00 D8 07 00 DE 00 00 07 01 0B 00 01 07 01 0C FF 00 07 00 04 07 00 D7 07 00 D8 07 00 DE 04 00 00 FF 00 22 00 05 07 00 D7 07 00 D8 07 00 DE 04 07 01 0B 00 01 07 01 0C FA 00 07
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  95     113    116    124    Any
        //  116    121    116    124    Any
        //  129    156    159    167    Any
        //  159    164    159    167    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index: 1, Size: 1
        //     at java.util.ArrayList.rangeCheck(Unknown Source)
        //     at java.util.ArrayList.get(Unknown Source)
        //     at com.strobel.assembler.Collection.get(Collection.java:43)
        //     at java.util.Collections$UnmodifiableList.get(Unknown Source)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.adjustArgumentsForMethodCallCore(AstMethodBodyBuilder.java:1300)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.adjustArgumentsForMethodCall(AstMethodBodyBuilder.java:1273)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1184)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:716)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformSynchronized(AstMethodBodyBuilder.java:523)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:360)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
