package br.eco.wash4me.entity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ScheduleDay {
    private GregorianCalendar now;

    public ScheduleDay() {
        now = new GregorianCalendar(new Locale("pt", "BR"));
    }

    public void setDate(Integer day, Integer month, Integer year) {
        now.set(year, month, day);
    }

    public void setTime(Integer hour, Integer minutes) {
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), hour, minutes, 0);
    }

    public Integer getDay() {
        return now.get(Calendar.DAY_OF_MONTH);
    }

    public void setDay(Integer day) {
        now.set(Calendar.DAY_OF_MONTH, day);
    }

    public String getWeekDay() {
        return now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR")).split("-")[0];
    }

    public String getMonthTitle() {
        return now.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("pt", "BR"));
    }

    public Integer getMonth() {
        return now.get(Calendar.MONTH);
    }

    public void setMonth(Integer month) {
        now.set(Calendar.MONTH, month);
    }

    public Integer getYear() {
        return now.get(Calendar.YEAR);
    }

    public void setYear(Integer year) {
        now.set(Calendar.YEAR, year);
    }
}
