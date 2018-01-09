package example.com.sunshine.util;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import example.com.sunshine.R;

public class SunshineDateUtils {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS / 24;

    /**
     * This method returns the number of days since the epoch (January 01, 1970, 12:00 Midnight UTC)
     * in UTC time from the current date.
     *
     * @param date A date in milliseconds in local time.
     * @return The number of days in UTC time from the epoch.
     */
    public static long getDayNumber(long date) {
        TimeZone timeZone = TimeZone.getDefault();
        long gmtOffset = timeZone.getOffset(date);

        return (date + gmtOffset) / DAY_IN_MILLIS;
    }

    /**
     * Normalizes the date for database insertion.
     *
     * @param date The UTC date to normalize.
     * @return The UTC date at 12 midnight.
     */
    public static long normalizeDate(long date) {
        return date / DAY_IN_MILLIS * DAY_IN_MILLIS;
    }

    /**
     * Converts the given UTC date into local timezone.
     *
     * @param utcDate The UTC datetime to convert to a local datetime, in milliseconds.
     * @return The local date (the UTC datetime - the TimeZone offset) in milliseconds.
     */
    public static long getLocalDateFromUTC(long utcDate) {
        TimeZone timeZone = TimeZone.getDefault();
        long gmtOffset = timeZone.getOffset(utcDate);

        return utcDate - gmtOffset;
    }

    /**
     * Converts the given date into UTC format.
     *
     * @param localDate The local datetime to convert to a UTC datetime, in milliseconds.
     * @return The UTC date (the local datetime + the TimeZone offset) in milliseconds.
     */
    public static long getUTCDateFromLocal(long localDate) {
        TimeZone timeZone = TimeZone.getDefault();
        long gmtOffset = timeZone.getOffset(localDate);

        return localDate + gmtOffset;
    }

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds (UTC)
     * @param showFullDate Used to show a fuller-version of the date, which always contains either
     * the day of the week, today, or tomorrow, in addition to the date.
     * @return A user-friendly representation of the date such as "Today, June 8", "Tomorrow",
     * or "Friday"
     */
    public static String getFriendlyDateString(Context context, long dateInMillis, boolean showFullDate) {
        long localdate = getLocalDateFromUTC(dateInMillis);
        long dayNumber = getDayNumber(localdate);
        long currentDayNumber = getDayNumber(System.currentTimeMillis());

        if (dayNumber == currentDayNumber || showFullDate) {
            String dayName = getDayName(context, localdate);
            String readableDate = getReadableDateString(context, localdate);

            if (dayNumber - currentDayNumber < 2) {
                String localizedDayName = new SimpleDateFormat("EEEE", Locale.US).format(localdate);
                return readableDate.replace(localizedDayName, dayName);
            } else {
                return readableDate;
            }
        } else if (dayNumber < currentDayNumber + 7) {
            return getDayName(context, localdate);
        } else {
            int flags = DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_NO_YEAR |
                        DateUtils.FORMAT_ABBREV_ALL |
                        DateUtils.FORMAT_SHOW_WEEKDAY;
            return DateUtils.formatDateTime(context, localdate, flags);
        }
    }

    /**
     * Returns a date string in the format specified, which shows a date, without a year,
     * abbreviated, showing the full weekday.
     *
     * @param context Used by DateUtils to format the date in the current locale
     * @param timeInMillis Time in milliseconds since the epoch (local time)
     * @return The formatted date string
     */
    private static String getReadableDateString(Context context, long timeInMillis) {
        int flags = DateUtils.FORMAT_SHOW_DATE |
                    DateUtils.FORMAT_NO_YEAR |
                    DateUtils.FORMAT_SHOW_WEEKDAY;
        return DateUtils.formatDateTime(context, timeInMillis, flags);
    }

    /**
     * Given a day, returns just the name to use for that day.
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds (local time)
     * @return the string day of the week
     */
    private static String getDayName(Context context, long dateInMillis) {
        long dayNumber = getDayNumber(dateInMillis);
        long currentDayNumber = getDayNumber(System.currentTimeMillis());

        if (dayNumber == currentDayNumber) {
            return context.getString(R.string.today);
        } else if (dayNumber == currentDayNumber + 1) {
            return context.getString(R.string.tomorrow);
        } else {
            return new SimpleDateFormat("EEEE", Locale.US).format(dateInMillis);
        }
    }
}
