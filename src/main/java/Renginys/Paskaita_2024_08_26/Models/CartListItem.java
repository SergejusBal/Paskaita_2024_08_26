package Renginys.Paskaita_2024_08_26.Models;

public class CartListItem {

    private int id;
    private String name;
    private int quantity;
    private double price;

    public CartListItem() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ID: " + id + " name: " + name + " quantity: " + quantity + " price " + price;
    }
}
