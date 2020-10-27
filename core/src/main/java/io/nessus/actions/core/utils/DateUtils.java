package io.nessus.actions.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String format(Date tstamp) {
        return getTimestampFormat().format(tstamp);
    }

    public static String format(Date tstamp, boolean dateOnly) {
        SimpleDateFormat sdf = dateOnly ? getDateFormat() : getTimestampFormat();
        return sdf.format(tstamp);
    }

    public static Date parse(String tstr) {
        try {
            if (tstr.contains(" "))
                return getTimestampFormat().parse(tstr);
            else
                return getDateFormat().parse(tstr);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static long elapsedTime(Date startTime) {
        return elapsedTime(startTime, new Date(), TimeUnit.MILLISECONDS);
    }

    public static long elapsedTime(Date startTime, TimeUnit unit) {
        return elapsedTime(startTime, new Date(), unit);
    }

    public static long elapsedTime(Date startTime, Date endTime) {
        return elapsedTime(startTime, endTime, TimeUnit.MILLISECONDS);
    }

    public static long elapsedTime(Date startTime, Date endTime, TimeUnit unit) {
        long elapsed = endTime.getTime() - startTime.getTime();
        return elapsed / unit.toMillis(1);
    }

    public static String elapsedTimeString(Date startTime) {
        return elapsedTimeString(startTime, new Date());
    }
    
    public static String elapsedTimeString(Date startTime, Date endTime) {
        return elapsedTimeString(elapsedTime(startTime, endTime, TimeUnit.MILLISECONDS));
    }
    
    public static String elapsedTimeString(Long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long hours = seconds / 3600;
        long mins = (seconds - hours * 3600) / 60;
        long secs = (seconds - hours * 3600) % 60;
        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }
    
    // SimpleDateFormat is not thread safe  
    
    private static ThreadLocal<SimpleDateFormat> tstampAssociation = new ThreadLocal<>();
    private static ThreadLocal<SimpleDateFormat> dateAssociation = new ThreadLocal<>();
    
    private static SimpleDateFormat getTimestampFormat() {
        synchronized (tstampAssociation) {
            SimpleDateFormat format = tstampAssociation.get();
            if (format == null) {
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                tstampAssociation.set(format);
            }
            return format;
        }
    }
    
    private static SimpleDateFormat getDateFormat() {
        synchronized (dateAssociation) {
            SimpleDateFormat format = dateAssociation.get();
            if (format == null) {
                format = new SimpleDateFormat("yyyy-MM-dd");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateAssociation.set(format);
            }
            return format;
        }
    }
}
