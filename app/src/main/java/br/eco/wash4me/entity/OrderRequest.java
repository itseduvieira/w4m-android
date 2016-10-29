package br.eco.wash4me.entity;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class OrderRequest {
    private List<Product> products;
    private GregorianCalendar date;
    private Car car;

    public OrderRequest() {
        products = new ArrayList<>();
        date = new GregorianCalendar(new Locale("pt", "BR"));
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Double calculatePrice() {
        Double total = 0.0;

        for(Product p : products) {
            total += p.getPrice();
        }

        return total;
    }
}
