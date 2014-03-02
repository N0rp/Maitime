package eu.dowsing.maiborntime.xml.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The unit in the company
 * 
 * @author richardg
 * 
 */
@XmlRootElement(name = "unit")
/**
 * Defines work that is done by the user
 * @author richardg
 *
 */
@XmlType(propOrder = { "name" })
public class Unit {

    private String name;

    // If you like the variable name, e.g. "name", you can easily change this
    // name for your XML-Output:
    @XmlElement(name = "title")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
