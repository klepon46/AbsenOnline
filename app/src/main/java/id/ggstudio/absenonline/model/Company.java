package id.ggstudio.absenonline.model;

public class Company {

    private Integer idCompany;

    private String nameCompany;

    private String dept;

    private String abbrCompany;

    public Integer getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(Integer idCompany) {
        this.idCompany = idCompany;
    }

    public String getNameCompany() {
        return nameCompany;
    }

    public void setNameCompany(String nameCompany) {
        this.nameCompany = nameCompany;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getAbbrCompany() {
        return abbrCompany;
    }

    public void setAbbrCompany(String abbrCompany) {
        this.abbrCompany = abbrCompany;
    }

    @Override
    public String toString() {
        return nameCompany;
    }
}
