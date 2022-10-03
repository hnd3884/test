package com.adventnet.taskengine.util;

import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.logging.Logger;

public class ScheduleUtil
{
    private static String className;
    private static Logger out;
    private static int intersectionIter;
    private static List<String> allowedRepFreqs;
    private static List<String> allowedUnitTime;
    private static boolean usePrevExec;
    
    public static Long calculateNextScheduleTime(final DataObject sch, final Long previousExecTime, final boolean isReschedule) {
        return calculateNextScheduleTime(sch, previousExecTime, isReschedule, 5);
    }
    
    public static Long calculateNextScheduleTime(final DataObject sch, final Long previousExecTime, final boolean isReschedule, final int taskScheduleType) {
        long retVal = -1L;
        final long previousExecutionTime = previousExecTime;
        try {
            if (sch.containsTable("Periodic")) {
                final Row perSchRow = sch.getFirstRow("Periodic");
                retVal = calculateNextPeriodicSchedule(perSchRow, previousExecutionTime, isReschedule, taskScheduleType);
            }
            else if (sch.containsTable("Calendar_Periodicity") && sch.containsTable("Calendar")) {
                final Row calRow = sch.getFirstRow("Calendar");
                final Row calPeriodicityRow = sch.getFirstRow("Calendar_Periodicity");
                retVal = calculateNextCalendarPeriodicitySchedule(calRow, calPeriodicityRow, previousExecutionTime);
            }
            else if (sch.containsTable("Calendar") && !sch.containsTable("Calendar_Periodicity")) {
                retVal = calculateNextCalendarSchedule(new CalendarRowConfig(sch.getFirstRow("Calendar")), previousExecutionTime);
            }
            else if (sch.containsTable("Composite")) {
                ScheduleUtil.intersectionIter = 0;
                final Row compSchRow = sch.getFirstRow("Composite");
                retVal = calculateNextCompositeSchedule(compSchRow, previousExecutionTime, isReschedule);
            }
        }
        catch (final DataAccessException dae) {
            ScheduleUtil.out.log(Level.FINE, "Exception when Calculating Schedule ", (Throwable)dae);
        }
        return new Long(retVal);
    }
    
    private static long calculateNextPeriodicSchedule(final Row perRow, final long previousExecutionTime, final boolean isReschedule) {
        return calculateNextPeriodicSchedule(perRow, previousExecutionTime, isReschedule, 5);
    }
    
    private static long calculateNextPeriodicSchedule(final Row perRow, long previousExecutionTime, final boolean isReschedule, final int taskScheduleType) {
        long startDate = -1L;
        long endDate = -1L;
        long timePeriod = -1L;
        String unitOfTime = null;
        boolean executeNow = false;
        if (perRow != null) {
            if (perRow.get("START_DATE") != null) {
                startDate = ((Timestamp)perRow.get("START_DATE")).getTime();
            }
            if (perRow.get("END_DATE") != null) {
                endDate = ((Timestamp)perRow.get("END_DATE")).getTime();
            }
            timePeriod = (long)perRow.get("TIME_PERIOD");
            unitOfTime = (String)perRow.get("UNIT_OF_TIME");
            executeNow = (boolean)perRow.get("EXECUTE_IMMEDIATELY");
        }
        if (!ScheduleUtil.allowedUnitTime.contains(unitOfTime.toUpperCase())) {
            ScheduleUtil.out.log(Level.SEVERE, "A wrong value has been specified for UNIT_OF_TIME :: [{0}] since the allowed values for this column is {1}, hence stopping the schedule", new Object[] { unitOfTime, ScheduleUtil.allowedUnitTime });
            return -1L;
        }
        final long currTime = System.currentTimeMillis();
        if (startDate == -1L) {
            startDate = System.currentTimeMillis();
        }
        if (isReschedule) {
            ScheduleUtil.out.log(Level.FINE, "Within Reschedule");
            if (previousExecutionTime == -1L) {
                previousExecutionTime = startDate;
            }
        }
        else {
            if (executeNow) {
                return currTime;
            }
            previousExecutionTime = startDate;
            ScheduleUtil.out.log(Level.FINE, "UT:9.Previous exec. time set as current time {0}:", previousExecutionTime);
        }
        if (taskScheduleType != 6 && previousExecutionTime > System.currentTimeMillis()) {
            return previousExecutionTime;
        }
        if (endDate != -1L && currTime > endDate) {
            return -1L;
        }
        long currentExecutionTime = 0L;
        long addTime = 0L;
        if (unitOfTime.equalsIgnoreCase("Seconds")) {
            addTime = timePeriod * 1000L;
        }
        else if (unitOfTime.equalsIgnoreCase("Minutes")) {
            addTime = timePeriod * 60L * 1000L;
        }
        else if (unitOfTime.equalsIgnoreCase("Hours")) {
            addTime = timePeriod * 60L * 60L * 1000L;
        }
        ScheduleUtil.out.log(Level.FINE, "UT:10.Add Time : {0}", addTime);
        if (addTime > 0L && previousExecutionTime + addTime < currTime) {
            final long missedCount = (System.currentTimeMillis() - previousExecutionTime) / addTime;
            ScheduleUtil.out.log(Level.FINE, "Missed Count {0}:", missedCount);
            if (missedCount <= 0L) {
                previousExecutionTime = System.currentTimeMillis() - previousExecutionTime;
            }
            else {
                final long addSleepTime = missedCount * addTime;
                previousExecutionTime += addSleepTime;
            }
        }
        currentExecutionTime = previousExecutionTime + addTime;
        ScheduleUtil.out.log(Level.FINE, "The Execution Time in Periodic Schedule is {0}  in millis {1}.   The Previous Execution Time is {2}. The Additional Time is  {3}", new Object[] { new Date(currentExecutionTime), new Long(currentExecutionTime), new Long(previousExecutionTime), new Long(addTime) });
        return currentExecutionTime;
    }
    
    private static int getAdjustedExecTime(final int curTime, final int workStart, final int workEnd, final int period, final int timeOfDay) {
        int timeToSet = (timeOfDay == 0) ? workStart : timeOfDay;
        if (curTime > workEnd) {
            return -1;
        }
        for (int maxTime = (curTime > workStart) ? curTime : workStart; timeToSet <= maxTime; timeToSet += period) {}
        if (timeToSet < workEnd) {
            return timeToSet;
        }
        return -1;
    }
    
    private static long calculateNextCalendarPeriodicitySchedule(final Row calRow, final Row calPeriodicityRow, final long previousExecTime) {
        Row workingHoursRow = null;
        try {
            workingHoursRow = PersistenceUtil.getWorkingHoursByID((Long)calPeriodicityRow.get(2));
        }
        catch (final Exception exp) {
            throw new RuntimeException(exp);
        }
        final int startTime = getTimeOfDayInSecs((long)workingHoursRow.get(3), (String)workingHoursRow.get(4));
        final int endTime = getTimeOfDayInSecs((long)workingHoursRow.get(5), (String)workingHoursRow.get(6));
        final int period = getTimeOfDayInSecs((long)calPeriodicityRow.get(3), (String)calPeriodicityRow.get(4));
        final CalendarRowConfig calRowConf = new CalendarRowConfig(calRow);
        final CalendarRowConfig.Time1 tod = calRowConf.getExecutionTime();
        final int timeOfDay = tod.hours() * 3600 + tod.minutes() * 60 + tod.seconds();
        if (timeOfDay != 0 && (timeOfDay < startTime || timeOfDay > endTime)) {
            throw new RuntimeException("Calendar TIME_OF_DAY entry should be within WORKING_HOURS boundary");
        }
        final Calendar curr = calRowConf.createCalendarObj();
        curr.setMinimalDaysInFirstWeek(1);
        curr.setFirstDayOfWeek(1);
        final Calendar next = calRowConf.createCalendarObj();
        next.setMinimalDaysInFirstWeek(1);
        next.setFirstDayOfWeek(1);
        final int curTime = getTimeOfDayInSecs(curr);
        final Calendar prev = null;
        int execTime = -1;
        if (previousExecTime <= 0L) {
            calRowConf.setExecutionTime(23, 59, 59);
            next.setTimeInMillis(calculateNextCalendarSchedule(calRowConf, previousExecTime));
        }
        else {
            next.setTimeInMillis(previousExecTime);
        }
        if (isSameDay(curr, next)) {
            if (previousExecTime > 0L) {
                final int prevTime = getTimeOfDayInSecs(next);
                execTime = getAdjustedExecTime(curTime, prevTime, endTime, period, prevTime);
            }
            else {
                execTime = getAdjustedExecTime(curTime, startTime, endTime, period, timeOfDay);
            }
            if (execTime > 1) {
                setTime(next, new CalendarRowConfig.Time1(execTime, "seconds"));
                return next.getTimeInMillis();
            }
        }
        execTime = ((timeOfDay == 0) ? startTime : timeOfDay);
        final CalendarRowConfig.Time1 t = new CalendarRowConfig.Time1(execTime, "seconds");
        calRowConf.setExecutionTime(t.hours(), t.minutes(), t.seconds());
        next.setTimeInMillis(calculateNextCalendarSchedule(calRowConf, previousExecTime));
        return next.getTimeInMillis();
    }
    
    private static long calculateNextCompositeSchedule(final Row compRow, final long previousExecutionTime, final boolean isReschedule) throws DataAccessException {
        try {
            Long scheduleid1 = null;
            Long scheduleid2 = null;
            String operator = null;
            scheduleid1 = (Long)compRow.get("SUB_SCHEDULE_ID1");
            scheduleid2 = (Long)compRow.get("SUB_SCHEDULE_ID2");
            operator = (String)compRow.get("OPERATOR");
            long scheduleTime1 = -1L;
            long scheduleTime2 = -1L;
            DataObject sch1 = null;
            DataObject sch2 = null;
            try {
                sch1 = PersistenceUtil.getSchedule(scheduleid1);
                sch2 = PersistenceUtil.getSchedule(scheduleid2);
            }
            catch (final DataAccessException io) {
                return -1L;
            }
            Row schedule1 = null;
            Row schedule2 = null;
            schedule1 = sch1.getFirstRow("Schedule");
            schedule2 = sch2.getFirstRow("Schedule");
            final List<String> per = new ArrayList<String>();
            per.add("Periodic");
            final List<String> cal = new ArrayList<String>();
            cal.add("Calendar");
            final List<String> comp = new ArrayList<String>();
            comp.add("Composite");
            final DataObject subPeriodicSchedule1 = PersistenceUtil.getCachedPersistence().get((List)per, schedule1);
            if (subPeriodicSchedule1 != null && subPeriodicSchedule1.containsTable("Periodic")) {
                final Row subPeriodicSchedule1Row = subPeriodicSchedule1.getFirstRow("Periodic");
                scheduleTime1 = calculateNextPeriodicSchedule(subPeriodicSchedule1Row, previousExecutionTime, isReschedule);
            }
            final DataObject subCalendarSchedule1 = PersistenceUtil.getCachedPersistence().get((List)cal, schedule1);
            if (subCalendarSchedule1 != null && subCalendarSchedule1.containsTable("Calendar")) {
                ScheduleUtil.out.log(Level.FINE, "The Calendar Schedule 1 is {0}", subCalendarSchedule1);
                final Row calRow = subCalendarSchedule1.getFirstRow("Calendar");
                final CalendarRowConfig calRowConf = new CalendarRowConfig(calRow);
                if (calRowConf.getSkipFrequency() > 0) {
                    calRowConf.setSkipFrequency(0);
                    ScheduleUtil.out.log(Level.WARNING, "SkipFrequency reset to 0. SkipFrequency NOT supported for Calendar Schedule [{0}] - which is part of a Composite schedule.", calRow.get(1));
                }
                scheduleTime1 = calculateNextCalendarSchedule(calRowConf, previousExecutionTime);
                ScheduleUtil.out.log(Level.FINE, "The Calendar Schedule 1 time is {0}", new Date(scheduleTime1));
            }
            final DataObject subCompositeSchedule1 = PersistenceUtil.getCachedPersistence().get((List)comp, schedule1);
            if (subCompositeSchedule1 != null && subCompositeSchedule1.containsTable("Composite")) {
                final Row subCompositeSchedule1Row = subCompositeSchedule1.getFirstRow("Composite");
                scheduleTime1 = calculateNextCompositeSchedule(subCompositeSchedule1Row, previousExecutionTime, isReschedule);
            }
            final DataObject subPeriodicSchedule2 = PersistenceUtil.getCachedPersistence().get((List)per, schedule2);
            if (subPeriodicSchedule2 != null && subPeriodicSchedule2.containsTable("Periodic")) {
                final Row subPeriodicSchedule2Row = subPeriodicSchedule2.getFirstRow("Periodic");
                scheduleTime2 = calculateNextPeriodicSchedule(subPeriodicSchedule2Row, previousExecutionTime, isReschedule);
            }
            final DataObject subCalendarSchedule2 = PersistenceUtil.getCachedPersistence().get((List)cal, schedule2);
            if (subCalendarSchedule2 != null && subCalendarSchedule2.containsTable("Calendar")) {
                ScheduleUtil.out.log(Level.FINE, "The Calendar Schedule 2 is {0}", subCalendarSchedule2);
                final Row calRow2 = subCalendarSchedule2.getFirstRow("Calendar");
                final CalendarRowConfig calRowConf2 = new CalendarRowConfig(calRow2);
                if (calRowConf2.getSkipFrequency() > 0) {
                    calRowConf2.setSkipFrequency(0);
                    ScheduleUtil.out.log(Level.WARNING, "SkipFrequency reset to 0. SkipFrequency NOT supported for Calendar Schedule [{0}] - which is part of a Composite schedule.", calRow2.get(1));
                }
                scheduleTime2 = calculateNextCalendarSchedule(calRowConf2, previousExecutionTime);
                ScheduleUtil.out.log(Level.FINE, "The Calendar Schedule 2 time is {0}", new Date(scheduleTime2));
            }
            final DataObject subCompositeSchedule2 = PersistenceUtil.getCachedPersistence().get((List)comp, schedule2);
            if (subCompositeSchedule2 != null && subCompositeSchedule2.containsTable("Composite")) {
                final Row subCompositeSchedule2Row = subCompositeSchedule2.getFirstRow("Composite");
                scheduleTime2 = calculateNextCompositeSchedule(subCompositeSchedule2Row, previousExecutionTime, isReschedule);
            }
            ScheduleUtil.out.log(Level.FINE, "The Composite Schedule values are {0}   {1}   {2}", new Object[] { new Date(scheduleTime1), new Date(scheduleTime2), new Date(System.currentTimeMillis()) });
            if (operator.equalsIgnoreCase("Union")) {
                if (scheduleTime1 < scheduleTime2) {
                    if (scheduleTime1 < System.currentTimeMillis()) {
                        if (scheduleTime2 < System.currentTimeMillis()) {
                            return -1L;
                        }
                        return scheduleTime2;
                    }
                    else {
                        if (scheduleTime1 > System.currentTimeMillis()) {
                            return scheduleTime1;
                        }
                        return -1L;
                    }
                }
                else {
                    if (scheduleTime2 > System.currentTimeMillis()) {
                        return scheduleTime2;
                    }
                    return -1L;
                }
            }
            else if (operator.equalsIgnoreCase("Difference")) {
                ScheduleUtil.out.log(Level.FINE, "The Composite Schedule values for Difference are {0}   {1}   {2}", new Object[] { new Date(scheduleTime1), new Date(scheduleTime2), new Date(System.currentTimeMillis()) });
                if (scheduleTime2 > System.currentTimeMillis() && scheduleTime1 / 1000L == scheduleTime2 / 1000L) {
                    return calculateNextCompositeSchedule(compRow, scheduleTime1, isReschedule);
                }
                if (scheduleTime1 > System.currentTimeMillis()) {
                    return scheduleTime1;
                }
                return -1L;
            }
            else {
                if (!operator.equalsIgnoreCase("Intersection")) {
                    return -1L;
                }
                ScheduleUtil.out.log(Level.FINE, "The Composite Schedule values for Intersection are {0}   {1}   {2}", new Object[] { new Date(scheduleTime1), new Date(scheduleTime2), new Date(System.currentTimeMillis()) });
                if (ScheduleUtil.intersectionIter > 100) {
                    throw new DataAccessException("schedules doesn't get intersected in 100 iterations");
                }
                if (scheduleTime1 / 1000L == scheduleTime2 / 1000L) {
                    return scheduleTime1;
                }
                if (scheduleTime1 > scheduleTime2) {
                    ++ScheduleUtil.intersectionIter;
                    return calculateNextCompositeSchedule(compRow, scheduleTime2, isReschedule);
                }
                if (scheduleTime2 > scheduleTime1) {
                    ++ScheduleUtil.intersectionIter;
                    return calculateNextCompositeSchedule(compRow, scheduleTime1, isReschedule);
                }
                return -1L;
            }
        }
        catch (final DataAccessException dae) {
            ScheduleUtil.out.log(Level.FINE, "", (Throwable)dae);
            return -1L;
        }
    }
    
    public static long calculateNextCalendarSchedule(final CalendarRowConfig calRowConf, final long previousExecTime) {
        ScheduleUtil.out.log(Level.FINE, "Entered calculateNextCalendarSchedule :: CalendarRowConfig :: [{0}], previousExecTime :: [{1}]", new Object[] { calRowConf, new Date(previousExecTime) });
        Calendar prev = null;
        if (previousExecTime != -1L) {
            prev = calRowConf.createCalendarObj();
            prev.setMinimalDaysInFirstWeek(1);
            prev.setFirstDayOfWeek(1);
            prev.setTimeInMillis(previousExecTime);
        }
        final Calendar curr = calRowConf.createCalendarObj();
        curr.setMinimalDaysInFirstWeek(1);
        curr.setFirstDayOfWeek(1);
        Calendar next = null;
        if (calRowConf.getScheduleType() == CalendarRowConfig.ScheduleType.NONE) {
            next = ((prev == null) ? findNextScheduleForNone(curr, calRowConf) : null);
        }
        else {
            next = findNextCalendarScheduleInternal(curr, prev, calRowConf.getExecutionTime(), calRowConf);
        }
        return (next != null) ? next.getTimeInMillis() : -1L;
    }
    
    public static Calendar findNextScheduleForNone(final Calendar curr, final CalendarRowConfig calRowConf) {
        ScheduleUtil.out.log(Level.FINE, "findNextScheduleForNone :: curr :: [{0}] calRowConf :: [{1}]", new Object[] { curr, calRowConf });
        final Calendar start = calRowConf.createCalendarObjForStartDate();
        final Calendar next = (Calendar)((start != null) ? start : curr.clone());
        next.setMinimalDaysInFirstWeek(1);
        next.setFirstDayOfWeek(1);
        ScheduleUtil.out.log(Level.FINE, "findNextScheduleForNone :: start :: [{0}] next :: [{1}]", new Object[] { start, next });
        setTime(next, calRowConf.getExecutionTime());
        return next.after(curr) ? next : null;
    }
    
    public static Calendar findNextCalendarScheduleInternal(Calendar curr, final Calendar prev, final CalendarRowConfig.Time1 execTime, final CalendarRowConfig calRowConf) {
        if (execTime == null) {
            throw new IllegalArgumentException("Execution Time not set for Calendar Schedule {" + calRowConf + "}");
        }
        if (prev == null) {
            final Calendar start = calRowConf.createCalendarObjForStartDate();
            if (start != null && start.after(curr)) {
                start.setMinimalDaysInFirstWeek(1);
                start.setFirstDayOfWeek(1);
                curr = start;
            }
        }
        switch (calRowConf.getScheduleType()) {
            case DAILY: {
                return findNextDailySchedule(curr, prev, execTime, calRowConf.getSkipFrequency());
            }
            case WEEKLY: {
                if (!isSet(calRowConf.getDaysOfWeek())) {
                    throw new IllegalArgumentException("InCorrect Weekly Schedule. 'daysOfWeek' array is EMPTY");
                }
                return findNextWeeklySchedule(curr, prev, execTime, calRowConf.getSkipFrequency(), calRowConf.getDaysOfWeek());
            }
            case MONTHLY: {
                if (isSet(calRowConf.getDates())) {
                    return findNextMonthlySchedule1(curr, prev, execTime, calRowConf.getSkipFrequency(), calRowConf.getDates(), calRowConf.isDatesInReverseOrder());
                }
                if (isSet(calRowConf.getWeeksOfMonth()) && isSet(calRowConf.getDaysOfWeek())) {
                    return findNextMonthlySchedule2(curr, prev, execTime, calRowConf.getSkipFrequency(), calRowConf.getWeeksOfMonth(), calRowConf.getDaysOfWeek());
                }
                throw new IllegalArgumentException("InCorrect Monthly Schedule. Either set {dates} OR{weeksOfMonth,daysOfweek}");
            }
            case YEARLY: {
                if (isSet(calRowConf.getMonths())) {
                    if (isSet(calRowConf.getDates())) {
                        final Calendar currCalBeforeCalculation = calRowConf.createCalendarObj();
                        currCalBeforeCalculation.setMinimalDaysInFirstWeek(1);
                        currCalBeforeCalculation.setFirstDayOfWeek(1);
                        currCalBeforeCalculation.setTimeInMillis(curr.getTime().getTime());
                        try {
                            return findNextYearlySchedule1(curr, prev, execTime, calRowConf.getSkipFrequency(), calRowConf.getMonths(), calRowConf.getDates(), calRowConf.isDatesInReverseOrder());
                        }
                        catch (final Exception e) {
                            throw new IllegalArgumentException("Invalid yearly Calendar Configuration :: " + calRowConf + " previousCalendar :: [" + ((prev == null) ? prev : prev.getTime()) + "] currCalBeforeCalculation :: [" + currCalBeforeCalculation.getTime() + "]", e);
                        }
                    }
                    if (isSet(calRowConf.getWeeksOfMonth()) && isSet(calRowConf.getDaysOfWeek())) {
                        return findNextYearlySchedule2(curr, prev, execTime, calRowConf.getSkipFrequency(), calRowConf.getMonths(), calRowConf.getWeeksOfMonth(), calRowConf.getDaysOfWeek());
                    }
                }
                else if (isSet(calRowConf.getWeeksOfYear()) && isSet(calRowConf.getDaysOfWeek())) {
                    return findNextYearlySchedule3(curr, prev, execTime, calRowConf.getSkipFrequency(), calRowConf.getWeeksOfYear(), calRowConf.getDaysOfWeek());
                }
                throw new IllegalArgumentException("InCorrect Yearly Schedule. Set {months,dates} OR{months,weeksOfMonth,daysOfweek} OR {weeksOfYear, daysOfWeek}");
            }
            default: {
                return null;
            }
        }
    }
    
    private static boolean isSet(final int[] arr) {
        return arr != null && arr.length > 0;
    }
    
    public static Calendar findNextDailySchedule(final Calendar curr, final Calendar prev, final CalendarRowConfig.Time1 execTime, final int skipFrequency) {
        final Calendar next = (Calendar)(ScheduleUtil.usePrevExec ? prev : curr).clone();
        setTime(next, execTime);
        final int freq = skipFrequency + 1;
        if (!ScheduleUtil.usePrevExec && freq > 1 && prev != null) {
            final int mod = (int)(getTimeDiffInDays(next, prev) % freq);
            if (mod > 0) {
                next.add(5, freq - mod);
            }
        }
        if (next.equals(ScheduleUtil.usePrevExec ? prev : curr) || !next.after(ScheduleUtil.usePrevExec ? prev : curr)) {
            next.add(5, freq);
        }
        return next;
    }
    
    public static Calendar findNextWeeklySchedule(final Calendar curr, final Calendar prev, final CalendarRowConfig.Time1 execTime, final int skipFrequency, final int[] daysOfWeek) {
        final Calendar next = (Calendar)(ScheduleUtil.usePrevExec ? prev : curr).clone();
        setTime(next, execTime);
        final int freq = skipFrequency + 1;
        if (!ScheduleUtil.usePrevExec && freq > 1 && prev != null) {
            final int mod = (int)(getTimeDiffInWeeks(next, prev) % freq);
            if (mod > 0) {
                next.add(3, freq - mod);
            }
        }
    Block_8:
        while (true) {
            for (int i = 0; i < daysOfWeek.length; ++i) {
                next.set(7, daysOfWeek[i]);
                if (next.after(ScheduleUtil.usePrevExec ? prev : curr)) {
                    break Block_8;
                }
            }
            next.add(3, freq);
        }
        return next;
    }
    
    public static Calendar findNextMonthlySchedule1(final Calendar curr, final Calendar prev, final CalendarRowConfig.Time1 execTime, final int skipFrequency, final int[] dates, final boolean isDateInReverseOrder) {
        final Calendar next = (Calendar)(ScheduleUtil.usePrevExec ? prev : curr).clone();
        setTime(next, execTime);
        final int freq = skipFrequency + 1;
        if (!ScheduleUtil.usePrevExec && freq > 1 && prev != null) {
            final int mod = (int)(getTimeDiffInMonths(next, prev) % freq);
            if (mod > 0) {
                next.add(2, freq - mod);
            }
        }
        while (true) {
            final int[] adjustedDates = isDateInReverseOrder ? getActualDates(next, dates) : dates;
            final int maxDate = next.getActualMaximum(5);
            for (int i = 0; i < adjustedDates.length; ++i) {
                if (adjustedDates[i] > maxDate) {
                    if (adjustedDates[i] > 31) {
                        throw new IllegalArgumentException("InCorrect Date value [" + adjustedDates[i] + "].");
                    }
                    ScheduleUtil.out.log(Level.WARNING, "findNextMonthlySchedule1 :: dates :: {0}, adjustedDates :: {1}, maxDate for the Calendar :: [{2}] is [{3}]. InCorrect date value :: [{4}] for the month [{5}]. Hence skipping this date.", new Object[] { print(dates), print(adjustedDates), next.getTime(), maxDate, adjustedDates[i], next.get(2) });
                }
                else {
                    next.set(5, adjustedDates[i]);
                    if (next.after(ScheduleUtil.usePrevExec ? prev : curr)) {
                        return next;
                    }
                }
            }
            next.add(2, freq);
        }
    }
    
    public static Calendar findNextMonthlySchedule2(final Calendar curr, final Calendar prev, final CalendarRowConfig.Time1 execTime, final int skipFrequency, final int[] weeksOfMonth, final int[] daysOfWeek) {
        final Calendar next = (Calendar)(ScheduleUtil.usePrevExec ? prev : curr).clone();
        setTime(next, execTime);
        final int freq = skipFrequency + 1;
        if (!ScheduleUtil.usePrevExec && freq > 1 && prev != null) {
            final int mod = (int)(getTimeDiffInMonths(next, prev) % freq);
            if (mod > 0) {
                next.add(2, freq - mod);
            }
        }
        if (daysOfWeek.length != 1) {
            throw new UnsupportedOperationException("Problem with Monthly Schedule. Only one value is supported in [daysOfWeek] array now");
        }
    Block_10:
        while (true) {
            for (int i = 0; i < weeksOfMonth.length; ++i) {
                for (int j = 0; j < daysOfWeek.length; ++j) {
                    setDate1(next, weeksOfMonth[i], daysOfWeek[j]);
                    if (next.after(ScheduleUtil.usePrevExec ? prev : curr)) {
                        break Block_10;
                    }
                }
            }
            next.add(2, freq);
        }
        return next;
    }
    
    public static Calendar findNextYearlySchedule1(final Calendar curr, final Calendar prev, final CalendarRowConfig.Time1 execTime, final int skipFrequency, final int[] months, final int[] dates, final boolean isDatesInReverseOrder) {
        final Calendar next = (Calendar)(ScheduleUtil.usePrevExec ? prev : curr).clone();
        setTime(next, execTime);
        final int freq = skipFrequency + 1;
        if (!ScheduleUtil.usePrevExec && freq > 1 && prev != null) {
            final int mod = getTimeDiffInYears(next, prev) % freq;
            if (mod > 0) {
                next.add(1, freq - mod);
            }
        }
        int yearOfInitialWrongDate = -1;
        while (true) {
            for (int i = 0; i < months.length; ++i) {
                next.set(5, 1);
                next.set(2, months[i]);
                final int[] adjustedDates = isDatesInReverseOrder ? getActualDates(next, dates) : dates;
                final int maxDate = next.getActualMaximum(5);
                for (int j = 0; j < adjustedDates.length; ++j) {
                    if (adjustedDates[j] > maxDate) {
                        ScheduleUtil.out.log(Level.WARNING, "findNextYearlySchedule1 :: months :: {0}, dates :: {1}, adjustedDates :: {2}, maxDate for the Calendar :: [{3}] is [{4}]. InCorrect date value :: [{5}] for the month [{6}]. Hence skipping this date.", new Object[] { print(months), print(dates), print(adjustedDates), next.getTime(), maxDate, adjustedDates[j], months[i] });
                        yearOfInitialWrongDate = ((yearOfInitialWrongDate == -1) ? next.get(1) : yearOfInitialWrongDate);
                        if (next.get(1) >= yearOfInitialWrongDate + 5) {
                            throw new IllegalArgumentException("Invalid Yearly Calendar Configuration");
                        }
                    }
                    else {
                        next.set(5, adjustedDates[j]);
                        if (next.after(ScheduleUtil.usePrevExec ? prev : curr)) {
                            return next;
                        }
                    }
                }
            }
            next.add(1, freq);
        }
    }
    
    private static String print(final int[] i) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int k = 0; k < i.length; ++k) {
            sb.append(i[k]);
            if (k < i.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static Calendar findNextYearlySchedule2(final Calendar curr, final Calendar prev, final CalendarRowConfig.Time1 execTime, final int skipFrequency, final int[] months, final int[] weeksOfMonth, final int[] daysOfWeek) {
        final Calendar next = (Calendar)(ScheduleUtil.usePrevExec ? prev : curr).clone();
        setTime(next, execTime);
        final int freq = skipFrequency + 1;
        if (!ScheduleUtil.usePrevExec && freq > 1 && prev != null) {
            final int mod = getTimeDiffInYears(next, prev) % freq;
            if (mod > 0) {
                next.add(1, freq - mod);
            }
        }
        if (daysOfWeek.length != 1) {
            throw new UnsupportedOperationException("Problem with Yearly Schedule. Only one value is supported in [daysOfWeek] array now");
        }
    Block_11:
        while (true) {
            for (int i = 0; i < months.length; ++i) {
                next.set(5, 1);
                next.set(2, months[i]);
                for (int j = 0; j < weeksOfMonth.length; ++j) {
                    for (int k = 0; k < daysOfWeek.length; ++k) {
                        setDate1(next, weeksOfMonth[j], daysOfWeek[k]);
                        if (next.after(ScheduleUtil.usePrevExec ? prev : curr)) {
                            break Block_11;
                        }
                    }
                }
            }
            next.add(1, freq);
        }
        return next;
    }
    
    public static Calendar findNextYearlySchedule3(final Calendar curr, final Calendar prev, final CalendarRowConfig.Time1 execTime, final int skipFrequency, final int[] weeksOfYear, final int[] daysOfWeek) {
        final Calendar next = (Calendar)(ScheduleUtil.usePrevExec ? prev : curr).clone();
        setTime(next, execTime);
        final int freq = skipFrequency + 1;
        if (!ScheduleUtil.usePrevExec && freq > 1 && prev != null) {
            final int mod = getTimeDiffInYears(next, prev) % freq;
            if (mod > 0) {
                next.add(1, freq - mod);
            }
        }
        if (daysOfWeek.length != 1) {
            throw new UnsupportedOperationException("Problem with Yearly Schedule. Only one value is supported in [daysOfWeek] array now");
        }
    Block_10:
        while (true) {
            for (int i = 0; i < weeksOfYear.length; ++i) {
                for (int j = 0; j < daysOfWeek.length; ++j) {
                    setDate2(next, weeksOfYear[i], daysOfWeek[j]);
                    if (next.after(ScheduleUtil.usePrevExec ? prev : curr)) {
                        break Block_10;
                    }
                }
            }
            next.add(1, freq);
        }
        return next;
    }
    
    private static boolean isSameDay(final Calendar curr, final Calendar prev) {
        return curr.get(1) == prev.get(1) && curr.get(6) == prev.get(6);
    }
    
    private static int getTimeOfDayInSecs(final long value, final String unit) {
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        if ("Hours".equalsIgnoreCase(unit)) {
            hours = (int)value;
        }
        else if ("Minutes".equalsIgnoreCase(unit)) {
            hours = (int)value / 60;
            minutes = (int)value % 60;
        }
        else if ("Seconds".equalsIgnoreCase(unit)) {
            hours = (int)value / 3600;
            final int reminder = (int)value % 3600;
            minutes = reminder / 60;
            seconds = reminder % 60;
        }
        return hours * 3600 + minutes * 60 + seconds;
    }
    
    private static int getTimeOfDayInSecs(final Calendar cal) {
        return cal.get(11) * 3600 + cal.get(12) * 60 + cal.get(13);
    }
    
    private static void setDate1(final Calendar cal, final int weekOfMonth, final int dayOfWeek) {
        final int curr_month = cal.get(2);
        cal.set(4, 1);
        cal.set(7, dayOfWeek);
        if (cal.get(2) != curr_month) {
            cal.add(4, 1);
        }
        if (weekOfMonth == 1) {
            return;
        }
        cal.add(4, weekOfMonth - 1);
        if (cal.get(2) != curr_month) {
            cal.add(4, -1);
        }
    }
    
    private static void setDate2(final Calendar cal, final int weekOfYear, final int dayOfWeek) {
        final int curr_year = cal.get(1);
        cal.set(3, 1);
        cal.set(7, dayOfWeek);
        if (cal.get(1) != curr_year) {
            cal.add(3, 1);
        }
        if (weekOfYear == 1) {
            return;
        }
        cal.add(3, weekOfYear - 1);
        if (cal.get(1) != curr_year) {
            cal.add(3, -1);
        }
    }
    
    private static void setTime(final Calendar cal, final CalendarRowConfig.Time1 time1) {
        cal.set(11, time1.hours());
        cal.set(12, time1.minutes());
        cal.set(13, time1.seconds());
    }
    
    public static int[] getActualDates(final Calendar cal, final int[] reverseDates) {
        final int[] dates = new int[reverseDates.length];
        final int maxDate = cal.getActualMaximum(5);
        for (int i = 0; i < reverseDates.length; ++i) {
            dates[i] = maxDate - reverseDates[i] + 1;
        }
        return dates;
    }
    
    public static long getTimeDiffInDays(final Calendar t1, final Calendar t2) {
        int days = 0;
        int year3;
        for (int year1 = t1.get(1), year2 = year3 = t1.get(1); year3 < year1; ++year3) {
            days += (isLeapYear(year3) ? 366 : 365);
        }
        return days + t1.get(6) - t2.get(6);
    }
    
    private static boolean isLeapYear(final int year) {
        return (year % 100 == 0) ? (year % 400 == 0) : (year % 4 == 0);
    }
    
    public static long getTimeDiffInWeeks(final Calendar t1, final Calendar t2) {
        int year1 = t1.get(1);
        int year2 = t2.get(1);
        final int week1 = t1.get(3);
        final int week2 = t2.get(3);
        if (week1 == 1 && t1.get(2) == 11) {
            ++year1;
        }
        if (week2 == 1 && t2.get(2) == 11) {
            ++year2;
        }
        return (year1 - year2) * 52 + week1 - week2;
    }
    
    public static long getTimeDiffInMonths(final Calendar t1, final Calendar t2) {
        return (t1.get(1) - t2.get(1)) * 12 + t1.get(2) - t2.get(2);
    }
    
    public static int getTimeDiffInYears(final Calendar t1, final Calendar t2) {
        return t1.get(1) - t2.get(1);
    }
    
    public static void main(final String[] args) throws Exception {
        final DataDictionary dd = com.adventnet.persistence.PersistenceUtil.getDataDictionary(new File("/advent/vinod/test/m5_19/AdventNet/MickeyLite/conf/TaskEngine/data-dictionary.xml").toURL());
        MetaDataUtil.addDataDictionaryConfiguration(dd);
    }
    
    static {
        ScheduleUtil.className = ScheduleUtil.class.getName();
        ScheduleUtil.out = Logger.getLogger(ScheduleUtil.className);
        ScheduleUtil.intersectionIter = 0;
        ScheduleUtil.allowedRepFreqs = new ArrayList<String>();
        ScheduleUtil.allowedUnitTime = new ArrayList<String>();
        ScheduleUtil.usePrevExec = "true".equalsIgnoreCase(PersistenceInitializer.getConfigurationValue("CalcNextTimeBasedOnPrevExec"));
        ScheduleUtil.allowedRepFreqs.add("DAILY");
        ScheduleUtil.allowedRepFreqs.add("WEEKLY");
        ScheduleUtil.allowedRepFreqs.add("MONTHLY");
        ScheduleUtil.allowedRepFreqs.add("YEARLY");
        ScheduleUtil.allowedRepFreqs.add("NONE");
        ScheduleUtil.allowedUnitTime.add("SECONDS");
        ScheduleUtil.allowedUnitTime.add("MINUTES");
        ScheduleUtil.allowedUnitTime.add("HOURS");
    }
}
