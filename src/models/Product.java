package models;

import java.time.LocalDateTime;

public class Product {
    private int produtctID;
    private double price;
    private String description;
    private int fk_client_id;
    private LocalDateTime creationDateTime;

    public Product() {
        this.produtctID = 0;
        this.price = 0;
        this.description = "Sem descrição";
        this.fk_client_id = 0;
        this.creationDateTime = null;
    }
    
    public Product(int produtctID, double price, String description, int fk_client_id, LocalDateTime creationDateTime) {
        this.produtctID = produtctID;
        this.price = price;
        this.description = description;
        this.fk_client_id = fk_client_id;
        this.creationDateTime = creationDateTime;
    }
    
    public int getProdutctID() {
        return produtctID;
    }

    public void setProdutctID(int produtctID) {
        this.produtctID = produtctID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFk_client_id() {
        return fk_client_id;
    }

    public void setFk_client_id(int fk_client_id) {
        this.fk_client_id = fk_client_id;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    @Override
    public String toString() {
        return "Product{" + "produtctID=" + produtctID + ", price=" + price + ", description=" + description + ", fk_client_id=" + fk_client_id + '}';
    }
}
