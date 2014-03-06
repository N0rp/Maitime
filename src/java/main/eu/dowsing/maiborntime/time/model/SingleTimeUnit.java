package eu.dowsing.maiborntime.time.model;

import java.util.List;

import eu.dowsing.maiborntime.xml.model.Work;

/**
 * Only one single time unit, one day, one week etc.
 * 
 * @author richardg
 * 
 */
public class SingleTimeUnit {

    public enum TimeUnit {
        Year, Month, Week, Date
    }

    private String name;
    private TimeUnit timeUnit;
    private List<Work> workList;

    /**
     * Create a new time unit model.
     * 
     * @param workList
     *            the work items for this time unit
     * @param timeUnit
     *            the type of the time unit
     */
    public SingleTimeUnit(String name, TimeUnit timeUnit, List<Work> workList) {
        this.name = name;
        this.timeUnit = timeUnit;
        this.workList = workList;
    }

    public String getName() {
        return name;
    }

    public void addWork(Work work) {
        this.workList.add(work);
    }

    public List<Work> getWorkList() {
        return this.workList;
    }

}
