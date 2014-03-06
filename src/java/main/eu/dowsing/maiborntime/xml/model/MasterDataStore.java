package eu.dowsing.maiborntime.xml.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The store for our master data
 * 
 * @author richardg
 * 
 */
// This statement means that class "MasterDataStore.java" is the root-element of our example
@XmlRootElement(namespace = "eu.dowsing.maiborntime.xml.model")
public class MasterDataStore {

    // XmLElementWrapper generates a wrapper element around XML representation
    @XmlElementWrapper(name = "unitList")
    // XmlElement sets the name of the entities
    @XmlElement(name = "unit")
    private ArrayList<Unit> unitList;
    private String version;
    private long lastUpdateAt;

    public static MasterDataStore generateTemplate() {
        ArrayList<Unit> unitList = new ArrayList<>();

        Unit unit1 = new Unit();
        unit1.setName("PE");
        unitList.add(unit1);

        Unit unit2 = new Unit();
        unit2.setName("SE");
        unitList.add(unit2);

        MasterDataStore masterStore = new MasterDataStore();
        masterStore.setUnitList(unitList);

        return masterStore;
    }

    public static MasterDataStore read(String filePath) throws FileNotFoundException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(MasterDataStore.class);
        Unmarshaller um = context.createUnmarshaller();
        return (MasterDataStore) um.unmarshal(new FileReader(filePath));
    }

    public static void write(String filePath, MasterDataStore workstore) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(MasterDataStore.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        // Write to System.out
        // m.marshal(workstore, System.out);

        // Write to File
        m.marshal(workstore, new File(filePath));
    }

    public void setUnitList(ArrayList<Unit> unitList) {
        this.unitList = unitList;
        setLastUpdateAt();
    }

    public ArrayList<Unit> getUnitsList() {
        return unitList;
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