package dk.syslab.proxy.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Configuration")
public class Config {

    private String name; // overlay network id
    private String description;
    private List<String> categories;
    private String ipaddress;
    private int port;
    private String number;

    public Config() {
        categories = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElementWrapper(name = "categories")
    @XmlElement(name = "category")
    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> category) {
        this.categories = category;
    }

    public void addCategory(String category) {
        this.categories.add(category);
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
