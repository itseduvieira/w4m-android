package br.eco.wash4me.entity;

import java.util.ArrayList;
import java.util.List;

public class OrderRequest {
    private List<Product> products;

    public OrderRequest() {
        products = new ArrayList<>();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Double calculatePrice() {
        Double total = 0.0;

        for(Product p : products) {
            total += p.getPrice();
        }

        return total;
    }
}
