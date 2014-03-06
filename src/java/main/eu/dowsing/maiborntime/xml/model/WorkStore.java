package eu.dowsing.maiborntime.xml.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The store for our work objects
 * 
 * @author richardg
 * 
 */
// This statement means that class "WorkStore.java" is the root-element of our example
@XmlRootElement(namespace = "eu.dowsing.maiborntime.xml.model")
public class WorkStore {

    // XmLElementWrapper generates a wrapper element around XML representation
    @XmlElementWrapper(name = "workList")
    // XmlElement sets the name of the entities
    @XmlElement(name = "work")
    private ArrayList<Work> workList;

    private String version;
    private long lastUpdateAt;

    /**
     * Generate a template store for a work store.
     * 
     * @return
     */
    public static WorkStore generateTemplate() {
        ArrayList<Work> workList = new ArrayList<>();

        Calendar c = Calendar.getInstance();

        {
            Work work = new Work();
            work.setAuthor("Wilhelm Tell");
            work.setPartner("Best Company");
            c.set(Calendar.YEAR, 2014);
            c.set(Calendar.MONTH, 1);
            work.setTimeFrom(c.getTimeInMillis());
            c.set(Calendar.MONTH, 3);
            work.setTimeTo(c.getTimeInMillis());
            workList.add(work);
        }

        {
            Work work = new Work();
            work.setAuthor("Johann Strauss");
            work.setPartner("Crap Company");
            c.set(Calendar.YEAR, 2013);
            c.set(Calendar.MONTH, 0);
            work.setTimeFrom(c.getTimeInMillis());
            c.set(Calendar.MONTH, 0);
            work.setTimeTo(c.getTimeInMillis());
            workList.add(work);
        }

        {
            Work work = new Work();
            work.setAuthor("Neil Strauss");
            work.setPartner("CoolCompany");
            c.set(Calendar.YEAR, 2012);
            work.setTimeFrom(c.getTimeInMillis());
            c.set(Calendar.YEAR, 2013);
            work.setTimeTo(c.getTimeInMillis());
            workList.add(work);
        }

        // create bookstore, assigning book
        WorkStore workstore = new WorkStore();
        workstore.setWorkList(workList);
        workstore.setVersion("1");

        return workstore;
    }

    public static WorkStore read(String filePath) throws FileNotFoundException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(WorkStore.class);
        Unmarshaller um = context.createUnmarshaller();
        return (WorkStore) um.unmarshal(new FileReader(filePath));
    }

    public static void write(String filePath, WorkStore workstore) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(WorkStore.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        // Write to System.out
        m.marshal(workstore, System.out);

        // Write to File
        m.marshal(workstore, new File(filePath));
    }

    public void setWorkList(ArrayList<Work> bookList) {
        this.workList = bookList;
        setLastUpdateAt();
    }

    public ArrayList<Work> getWorksList() {
        return workList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    /**
     * Sets the last update time to the current system time
     */
    private void setLastUpdateAt() {
        this.lastUpdateAt = System.currentTimeMillis();
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }
}