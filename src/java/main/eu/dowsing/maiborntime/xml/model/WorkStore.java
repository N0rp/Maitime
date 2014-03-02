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
    @XmlElementWrapper(name = "bookList")
    // XmlElement sets the name of the entities
    @XmlElement(name = "book")
    private ArrayList<Work> bookList;
    private String name;
    private String location;

    public void setBookList(ArrayList<Work> bookList) {
        this.bookList = bookList;
    }

    public ArrayList<Work> getBooksList() {
        return bookList;
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