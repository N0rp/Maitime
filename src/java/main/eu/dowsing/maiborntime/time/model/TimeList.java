package eu.dowsing.maiborntime.time.model;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import eu.dowsing.maiborntime.xml.model.Work;

/**
 * A list of time units, days, weeks, months etc.
 * 
 * @author richardg
 * 
 */
public class TimeList {

    private List<Work> workList = new LinkedList<>();

    private final String name;

    private long from;
    private long to;

    public TimeList(String name, long from, long to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    public List<Work> getWork() {
        return this.workList;
    }

    @Override
    public String toString() {
        return "TimeList " + getName();
    }

    public String getName() {
        return this.name;
    }

    public long getFrom() {
        return this.from;
    }

    public long getTo() {
        return this.to;
    }

    public void addWork(Work work) {
        workList.add(work);
    }

    public static TimeList getYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(year, 0, 0);
        long from = c.getTimeInMillis();
        c.set(year + 1, 0, 0);
        long to = c.getTimeInMillis();
        return new TimeList("" + year, from, to);
    }

    // public static TimeList getMonth(int year, int month) {
    // Calendar c = Calendar.getInstance();
    // c.set(year, month, 0);
    //
    // }

    public static TimeList getWeek(int year, int week) {
        Calendar c = Calendar.getInstance();
        c.setWeekDate(year, week, 0);
        long from = c.getTimeInMillis();
        c.setWeekDate(year, week, 6);
        long to = c.getTimeInMillis();
        return new TimeList("Woche " + week, from, to);
    }

    // public static TimeList getDate(int year, int month, int date) {
    // Calendar c = Calendar.getInstance();
    // c.set(year, month, date);
    //
    // }
}
