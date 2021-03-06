package com.gmail.gogobebe2.thedayahead.timetable;

import android.content.Context;
import android.os.AsyncTask;

import com.gmail.gogobebe2.thedayahead.MainActivity;
import com.gmail.gogobebe2.thedayahead.diary.DiaryEntry;

import java.util.Calendar;
import java.util.List;

public class TimetableHighlighter extends AsyncTask<Void, Void, Void> {
    private Period currentPeriod;
    private Context context;

    TimetableHighlighter(Timetable timetable, Context context) {
        this.context = context;
        MainActivity.timetable = timetable;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Calendar currentCalendar = Calendar.getInstance();
        int currentMonthOfYear = currentCalendar.get(Calendar.MONTH);
        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int currentHourOfDay = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinuteOfHour = currentCalendar.get(Calendar.MINUTE);

        // Highlights current period:
        Period newPeriod = MainActivity.timetable.getPeriod(currentCalendar.get(Calendar.DAY_OF_WEEK),
                currentHourOfDay, currentMinuteOfHour);

        if (newPeriod != null) {
            if (currentPeriod != null) {
                if (!currentPeriod.equals(newPeriod)) return null;
                else {
                    if (currentPeriod.isHighlightedAsCurrent()) currentPeriod.unHighlightAsCurrent();
                    if (currentPeriod.isHighlightedAsImportant()) currentPeriod.unHighlightAsImportant();
                }
            }
            currentPeriod = newPeriod;
            currentPeriod.highlightAsCurrentSession();
        }

        // Highlights as important:
        List<DiaryEntry> diaries = DiaryEntry.loadDiaries(context);
        for (int d = 0; d < diaries.size(); d++) {
            DiaryEntry diaryEntry = diaries.get(d);

            Calendar entryCalendar = Calendar.getInstance();
            entryCalendar.set(entryCalendar.get(Calendar.YEAR), diaryEntry.getMonthOfYear(),
                    diaryEntry.getDayOfMonth());
            int dayOfWeekInt = entryCalendar.get(Calendar.DAY_OF_WEEK);

            Period period = MainActivity.timetable.getPeriod(dayOfWeekInt,
                    diaryEntry.getHourOfDay(), diaryEntry.getMinuteOfHour());

            if (diaryEntry.getMinuteOfHour() < currentMinuteOfHour
                    && diaryEntry.getHourOfDay() <= currentHourOfDay
                    && diaryEntry.getDayOfMonth() <= currentDayOfMonth
                    && diaryEntry.getMonthOfYear() <= currentMonthOfYear) {
                if (period.isHighlightedAsImportant()) period.unHighlightAsImportant();
                diaries.remove(d);
            } else period.highlightImportant();
        }
        //
        return null;
    }

    // TODO find way to keep this running, not just when timetable is opened.
}
