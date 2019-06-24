package dk.orda.demo;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Hashtable;
import java.util.Map;

@XmlRootElement(name = "map")
public class DerMap {

    private Map<String, DerNode> entries;

    public DerMap() {
        entries = new Hashtable<>();
    }

    public void put(String name, DerNode node) {
        entries.put(name, node);
    }

    public Map<String, DerNode> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, DerNode> ders) {
        this.entries = ders;
    }
}
