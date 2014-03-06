package eu.dowsing.maiborntime.time.model;

import java.util.LinkedList;
import java.util.List;

import javafx.collections.ObservableList;
import eu.dowsing.maiborntime.time.model.SingleTimeUnit.TimeUnit;
import eu.dowsing.maiborntime.xml.model.Work;
import eu.dowsing.maiborntime.xml.model.WorkStore;

public class WorkStoreDivider {

    public static final String[] DAY_NAMES = new String[] { "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag",
            "Samstag", "Sonntag" };

    public static final String[] MONTH_NAMES = new String[] { "Januar", "Februar", "März", "April", "Mai", "Juni",
            "Juli", "August", "September", "Oktober", "November", "Dezember" };

    private TimeCluster years = new TimeCluster(TimeUnit.Year);
    private TimeCluster months = new TimeCluster(TimeUnit.Month);
    private TimeCluster weeks = new TimeCluster(TimeUnit.Week);
    private TimeCluster days = new TimeCluster(TimeUnit.Date);

    /**
     * Get the work items that fit the given time unit
     * 
     * @param timeUnit
     * @param workStore
     * @return
     */
    public ObservableList<TimeList> getTimeList(TimeUnit timeUnit) {
        if (timeUnit == TimeUnit.Year) {
            return years.getTimeList();
        } else if (timeUnit == TimeUnit.Month) {
            return months.getTimeList();
        }
        if (timeUnit == TimeUnit.Week) {
            return weeks.getTimeList();
        } else {
            return days.getTimeList();
        }
    }

    /**
     * Update work lists
     * 
     * @param workStore
     */
    public void updateListData(WorkStore workStore) {
        System.out.println("Updating list data");
        years.clear();
        months.clear();
        weeks.clear();
        days.clear();

        List<Work> workList = getSortedList(workStore.getWorksList());
        for (Work work : workList) {
            addData(work);
        }
    }

    /**
     * Get a time sorted work list
     * 
     * @param unsorted
     * @return
     */
    private List<Work> getSortedList(List<Work> unsorted) {
        List<Work> sorted = new LinkedList<>();
        for (Work work : unsorted) {
            sorted = insert(sorted, work);
        }
        return sorted;
    }

    private List<Work> insert(List<Work> list, Work work) {
        for (int i = 0; i < list.size(); i++) {
            if (work.getTimeFrom() < list.get(i).getTimeFrom()) {
                list.add(i, work);
                return list;
            }
        }

        // add at the end if it hasn't been by now
        list.add(work);
        return list;
    }

    /**
     * Add a single new work object to data.
     * 
     * @param work
     */
    public void addData(Work work) {
        years.add(work);
        months.add(work);
        weeks.add(work);
        days.add(work);
    }

    @Override
    public String toString() {
        return years + "\n" + months + "\n" + weeks + "\n" + days;
    }

}
