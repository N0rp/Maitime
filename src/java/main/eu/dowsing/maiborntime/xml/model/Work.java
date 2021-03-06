package eu.dowsing.maiborntime.xml.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "work")
/**
 * Defines work that is done by the user
 * @author richardg
 *
 */
@XmlType(propOrder = { "author", "timeFrom", "timeTo", "unit", "partner", "project", "subproject", "task", "subtask",
        "taskTime" })
public class Work {

    private String author;
    private long timeFrom;
    private long timeTo;

    // @XmlAttribute
    // @XmlIDREF
    private int unit;

    private String partner;
    private String project;
    private String subproject;
    private String task;
    private String subtask;
    private long taskTime;

    // If you like the variable name, e.g. "name", you can easily change this
    // name for your XML-Output:
    @XmlElement(name = "title")
    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getSubtask() {
        return subtask;
    }

    public void setSubtask(String subTask) {
        this.subtask = subTask;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(long time) {
        this.timeFrom = time;
    }

    public long getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(long time) {
        this.timeTo = time;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getSubproject() {
        return subproject;
    }

    public void setSubproject(String subproject) {
        this.subproject = subproject;
    }

    public long getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(long taskTime) {
        this.taskTime = taskTime;
    }

}
