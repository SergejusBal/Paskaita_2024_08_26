package Renginys.Paskaita_2024_08_26.Models;

public class Order {

    private int id;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private String orderCartJsonString;
    private String promoCode;
    private String orderStatus;

    public Order() {
    }

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getOrderCartJsonString() {
        return orderCartJsonString;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setOrderCartJsonString(String orderCartJsonString) {
        this.orderCartJsonString = orderCartJsonString;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
