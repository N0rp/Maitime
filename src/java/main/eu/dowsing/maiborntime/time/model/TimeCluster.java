package eu.dowsing.maiborntime.time.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import eu.dowsing.maiborntime.time.model.SingleTimeUnit.TimeUnit;
import eu.dowsing.maiborntime.xml.model.Work;

public class TimeCluster {

    private ObservableList<TimeList> times = FXCollections.observableArrayList();

    private TimeUnit timeUnit;

    public TimeCluster(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public ObservableList<TimeList> getTimeList() {
        return this.times;
    }

    public void clear() {
        times.clear();
    }

    public void add(Work work) {

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(work.getTimeFrom());
        System.out.println("Date that is inserted is: " + format1.format(c.getTime()));

        for (TimeList list : times) {
            if (list.getFrom() <= work.getTimeFrom() && work.getTimeFrom() < list.getTo()) {
                list.addWork(work);
                return;
            }
        }

        // create new time list since it did not fit anywhere else
        long from;
        long to;

        c.setTimeInMillis(work.getTimeFrom());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int date = c.get(Calendar.DATE);
        int mainTime;
        // String displayDate = year + "-" + month + "-" + date;
        // System.out.println("Date is " + displayDate);

        System.out.println("Date that is inserted is: " + format1.format(c.getTime()));

        Calendar temp = Calendar.getInstance();
        // TODO: there will be miliseconds that are not covered by these rules that have to be fixed
        if (timeUnit == TimeUnit.Year) {
            mainTime = year;
            temp.set(year, 0, 1, 0, 0, 0);
            from = temp.getTimeInMillis();
            temp.set(year, 11, 31, 0, 0);
            to = temp.getTimeInMillis();
        } else if (timeUnit == TimeUnit.Month) {
            mainTime = month;
            temp.set(year, month, 1, 0, 0, 0);
            from = temp.getTimeInMillis();
            temp.getMaximum(Calendar.DATE);
            temp.set(year, month, temp.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0, 0);
            to = temp.getTimeInMillis();
        } else if (timeUnit == TimeUnit.Week) {
            mainTime = week;
            temp.setWeekDate(year, week, Calendar.MONDAY);
            from = temp.getTimeInMillis();
            temp.setWeekDate(year, week, Calendar.SUNDAY);
            to = temp.getTimeInMillis();
        } else { // time unit is date/day
            mainTime = date;
            temp.set(year, month, date, 0, 0, 0);
            from = temp.getTimeInMillis();
            temp.set(year, month, date + 1, 0, 0, 0);
            temp.add(Calendar.SECOND, -1);
            to = temp.getTimeInMillis();
        }

        Calendar cf = Calendar.getInstance();
        cf.setTimeInMillis(from);
        Calendar ct = Calendar.getInstance();
        ct.setTimeInMillis(to);
        System.out.println(timeUnit + ": " + format1.format(cf.getTime()) + " <= " + format1.format(c.getTime())
                + " < " + format1.format(ct.getTime()));

        TimeList timeList = new TimeList(getName(mainTime, timeUnit), from, to);
        timeList.addWork(work);
        times.add(timeList);
    }

    @Override
    public String toString() {
        return "TimeCluster for " + timeUnit + " with [" + times + "]";
    }

    private String getName(int time, TimeUnit unit) {
        if (unit == TimeUnit.Year) {
            return time + "";
        } else if (unit == TimeUnit.Month) {
            return WorkStoreDivider.MONTH_NAMES[time];
        } else if (unit == TimeUnit.Week) {
            return "KW " + (time + 1);
        } else if (unit == TimeUnit.Date) {
            return WorkStoreDivider.DAY_NAMES[time];
        }

        return "null";
    }

}
