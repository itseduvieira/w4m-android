package br.eco.wash4me.entity;

public class Product {
    private Integer id;
    private String name;
    private String description;
    private Boolean featured;
    private Double priceSmall;
    private Double priceMedium;
    private Double priceLarge;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Double getPrice(Car car) {
        if(car.getSize().equals("S")) {
            return getPriceSmall();
        } else if(car.getSize().equals("M")) {
            return getPriceMedium();
        } else {
            return getPriceLarge();
        }
    }

    public Double getPriceSmall() {
        return priceSmall;
    }

    public void setPriceSmall(Double priceSmall) {
        this.priceSmall = priceSmall;
    }

    public Double getPriceMedium() {
        return priceMedium;
    }

    public void setPriceMedium(Double priceMedium) {
        this.priceMedium = priceMedium;
    }

    public Double getPriceLarge() {
        return priceLarge;
    }

    public void setPriceLarge(Double priceLarge) {
        this.priceLarge = priceLarge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return id.equals(product.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
