package br.eco.wash4me.entity;

public class Order {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pedido #" + getId();
    }
}
