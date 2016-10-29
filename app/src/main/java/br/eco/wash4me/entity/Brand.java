package br.eco.wash4me.entity;

public class Brand {
    private Integer id;
    private String name;
    private String fipeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFipeName() {
        return fipeName;
    }

    public void setFipeName(String fipeName) {
        this.fipeName = fipeName;
    }
}
