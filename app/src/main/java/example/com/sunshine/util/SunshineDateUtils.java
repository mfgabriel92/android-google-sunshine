package example.com.sunshine.util;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import example.com.sunshine.R;

public class SunshineDateUtils {

    public static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);

    /**
     * Normalizes the date for database insertion.
     *
     * @param date The UTC date to normalize.
     * @return The UTC date at 12 midnight.
     */
    public static long normalizeDate(long date) {
        return TimeUnit.MILLISECONDS.toDays(date) * DAY_IN_MILLIS;
    }

    /**
     * In order to ensure consistent inserts into WeatherProvider, we check that dates have been
     * normalized before they are inserted. If they are not normalized, we don't want to accept
     * them, and leave it up to the caller to throw an IllegalArgumentException.
     *
     * @param millisSinceEpoch Milliseconds since January 1, 1970 at midnight
     * @return true if the date represents the beginning of a day in Unix time, false otherwise
     */
    public static boolean isDateNormalized(long millisSinceEpoch) {
        return millisSinceEpoch % DAY_IN_MILLIS == 0;
    }

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.
     *
     * @param context Context to use for resource localization
     * @param normalizedUtcMidnight The date in milliseconds (UTC)
     * @param showFullDate Used to show a fuller-version of the date, which always contains either
     * the day of the week, today, or tomorrow, in addition to the date.
     * @return A user-friendly representation of the date such as "Today, June 8", "Tomorrow",
     * or "Friday"
     */
    public static String getFriendlyDateString(Context context, long normalizedUtcMidnight, boolean showFullDate) {
        long localDate = getLocalMidnightFromNormalizedUtcDate(normalizedUtcMidnight);
        long daysFromEpochToProvidedDate = TimeUnit.MILLISECONDS.toDays(localDate);
        long daysFromEpochToToday = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());

        if (daysFromEpochToProvidedDate == daysFromEpochToToday || showFullDate) {
            String dayName = getDayName(context, localDate);
            String readableDate = getReadableDateString(context, localDate);

            if (daysFromEpochToProvidedDate - daysFromEpochToToday < 2) {
                String localizedDayName = new SimpleDateFormat("EEEE", Locale.US).format(localDate);
                return readableDate.replace(localizedDayName, dayName);
            } else {
                return readableDate;
            }
        } else if (daysFromEpochToProvidedDate < daysFromEpochToToday + 7) {
            return getDayName(context, localDate);
        } else {
            int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_NO_YEAR
                | DateUtils.FORMAT_ABBREV_ALL
                | DateUtils.FORMAT_SHOW_WEEKDAY;
            return DateUtils.formatDateTime(context, localDate, flags);
        }
    }

    /**
     * This method returns the number of milliseconds (UTC time) for today's date at midnight in
     * the local time zone
     *
     * @return The number of milliseconds
     */
    public static long getNormalizedUtcDateForToday() {
        long utcNowMillis = System.currentTimeMillis();
        TimeZone currentTimeZone = TimeZone.getDefault();
        long gmtOffsetMillis = currentTimeZone.getOffset(utcNowMillis);
        long timeSinceEpochLocalTimeMillis = utcNowMillis + gmtOffsetMillis;
        long daysSinceEpochLocal = TimeUnit.MILLISECONDS.toDays(timeSinceEpochLocalTimeMillis);

        return TimeUnit.DAYS.toMillis(daysSinceEpochLocal);
    }

    /**
     * This method will return the local time midnight for the provided normalized UTC date.
     *
     * @param normalizedUtcDate UTC time at midnight for a given date. This number comes from the
     *                          database
     * @return The local date corresponding to the given normalized UTC date
     */
    private static long getLocalMidnightFromNormalizedUtcDate(long normalizedUtcDate) {
        TimeZone timeZone = TimeZone.getDefault();
        long gmtOffset = timeZone.getOffset(normalizedUtcDate);

        return normalizedUtcDate - gmtOffset;
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
        long daysFromEpochProvided = TimeUnit.MILLISECONDS.toDays(dateInMillis);
        long daysFromEpochToday = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        int daysAfterToday = (int) (daysFromEpochProvided - daysFromEpochToday);

        switch (daysAfterToday) {
            case 0:
                return context.getString(R.string.today);
            case 1:
                return context.getString(R.string.tomorrow);
            default:
                return new SimpleDateFormat("EEEE", Locale.US).format(dateInMillis);
        }
    }
}
