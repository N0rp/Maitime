package eu.dowsing.maiborntime.xml.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The store for our work objects
 * 
 * @author richardg
 * 
 */
// This statement means that class "Bookstore.java" is the root-element of our example
@XmlRootElement(namespace = "eu.dowsing.maiborntime.xml.model")
public class WorkStore {

    // XmLElementWrapper generates a wrapper element around XML representation
    @XmlElementWrapper(name = "workList")
    // XmlElement sets the name of the entities
    @XmlElement(name = "work")
    private ArrayList<Work> workList;
    // XmLElementWrapper generates a wrapper element around XML representation
    @XmlElementWrapper(name = "unitList")
    // XmlElement sets the name of the entities
    @XmlElement(name = "unit")
    private ArrayList<Unit> unitList;

    private String name;
    private String location;

    public void setWorkList(ArrayList<Work> bookList) {
        this.workList = bookList;
    }

    public ArrayList<Work> getWorksList() {
        return workList;
    }

    public void setUnitList(ArrayList<Unit> unitList) {
        this.unitList = unitList;
    }

    public ArrayList<Unit> getUnitsList() {
        return unitList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}