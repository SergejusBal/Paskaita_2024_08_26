package Renginys.Paskaita_2024_08_26.Repositories;

import Renginys.Paskaita_2024_08_26.Models.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Repository
public class OrderRepository {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public HashMap<String,String> registerOrder(Order order){

        HashMap<String,String> response = new HashMap<>();
        int orderID;

        if(     order.getCustomerName() == null         || order.getCustomerEmail() == null || order.getCustomerAddress() == null ||
                order.getOrderCartJsonString() == null  || order.getPromoCode() == null) {

            response.put("response", "Invalid data");
            return response;
        }

        String sql = "INSERT INTO order_ (customer_name, customer_email, customer_address, order_cart, promo_code, payment_status)\n" +
                "VALUES (?,?,?,?,?,?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1,order.getCustomerName());
            preparedStatement.setString(2,order.getCustomerEmail());
            preparedStatement.setString(3,order.getCustomerAddress());
            preparedStatement.setString(4,order.getOrderCartJsonString());
            preparedStatement.setString(5,order.getPromoCode());
            preparedStatement.setString(6,"pending");

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            orderID = resultSet.getInt(1);

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            response.put("response","Database connection failed");
            return response;

        }

        response.put("response","Order was successfully added");
        response.put("orderID","" + orderID);
        return response;
     }


    public String updateOrder(Order order, int id){

        if(     order.getCustomerName() == null         || order.getCustomerEmail() == null     ||  order.getCustomerAddress() == null ||
                order.getOrderCartJsonString() == null  || order.getPromoCode() == null         ||  order.getOrderStatus() == null) {
            return "Invalid data";
        }

        String sql = "UPDATE order_ SET customer_name = ?, customer_email = ?, customer_address = ?, order_cart = ?, " +
                "promo_code = ?, payment_status = ? WHERE id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1,order.getCustomerName());
            preparedStatement.setString(2,order.getCustomerEmail());
            preparedStatement.setString(3,order.getCustomerAddress());
            preparedStatement.setString(4,order.getOrderCartJsonString());
            preparedStatement.setString(5,order.getPromoCode());
            preparedStatement.setString(6,order.getOrderStatus());
            preparedStatement.setInt(7,id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return "Database connection failed";

        }

        return "Order was successfully updated";
    }


    public List<Order> getAllOrders(int offset , int limit, String paymentStatus){

        if(paymentStatus == null || paymentStatus.isEmpty()) paymentStatus = "%";

        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM order_ WHERE payment_status LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,paymentStatus);
            preparedStatement.setInt(2,limit);
            preparedStatement.setInt(3,offset);
            ResultSet resultSet =  preparedStatement.executeQuery();

            while(resultSet.next()) {

                Order order = new Order();

                order.setId(resultSet.getInt("id"));
                order.setCustomerName(resultSet.getString("customer_name"));
                order.setCustomerEmail(resultSet.getString("customer_email"));
                order.setCustomerAddress(resultSet.getString("customer_address"));
                order.setOrderCartJsonString(resultSet.getString("order_cart"));
                order.setPromoCode(resultSet.getString("promo_code"));
                order.setOrderStatus(resultSet.getString("payment_status"));

                orderList.add(order);
            }

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

        return orderList;

    }

    public Order getOrderByID(int id){

        Order order = new Order();
        String sql = "SELECT * FROM order_ WHERE id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return new Order();

            order.setId(resultSet.getInt("id"));
            order.setCustomerName(resultSet.getString("customer_name"));
            order.setCustomerEmail(resultSet.getString("customer_email"));
            order.setCustomerAddress(resultSet.getString("customer_address"));
            order.setOrderCartJsonString(resultSet.getString("order_cart"));
            order.setPromoCode(resultSet.getString("promo_code"));
            order.setOrderStatus(resultSet.getString("payment_status"));

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return new Order();
        }

        return order;
    }


    public void setPaymentStatus(int orderID, String paymentStatus){
        String sql = "UPDATE order_ SET payment_status = ? WHERE id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, paymentStatus);
            preparedStatement.setInt(2, orderID);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }
    public boolean isOrderPaid(int id){

        boolean isOrderPaid = false;

        String sql = "SELECT payment_status FROM order_ WHERE id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return isOrderPaid;

            isOrderPaid = resultSet.getString(1).equals("Paid");

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return isOrderPaid;
        }

        return isOrderPaid;

    }

    public String deleteOrderByOrderID(int orderID){

        String sql = "DELETE FROM order_ WHERE id = ?;";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,orderID);
            preparedStatement.executeUpdate();

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return "Database connection failed";
        }

        return "Order was successfully deleted";
    }

    public String getProductJsonByOrderID(int id){

        String productsStringJSON = "[[]]";

        String sql = "SELECT order_cart FROM order_ WHERE id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return productsStringJSON;

            productsStringJSON = resultSet.getString(1);

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return productsStringJSON;
        }

        return productsStringJSON;
    }

    public String getUsedPromo(int id){

        String promoCode = "";

        String sql = "SELECT promo_code FROM order_ WHERE id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return promoCode;

            promoCode = resultSet.getString(1);

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return promoCode;
        }

        return promoCode;
    }


}
