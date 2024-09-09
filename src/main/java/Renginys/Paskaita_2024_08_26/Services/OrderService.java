package Renginys.Paskaita_2024_08_26.Services;

import Renginys.Paskaita_2024_08_26.Models.Order;
import Renginys.Paskaita_2024_08_26.Repositories.OrderRepository;
import Renginys.Paskaita_2024_08_26.Repositories.StripeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private StripeRepository stripeRepository;


    public HashMap<String,String> registerOrder(Order order){
        return orderRepository.registerOrder(order);
    }


    public Order getOrderByID(String authorizationHeader, int id){
        if(userService.userAutoLogIn(authorizationHeader)) return orderRepository.getOrderByID(id);
        else return new Order();
    }

    public Order getOrderByIDUnauthorized(int id){
        return orderRepository.getOrderByID(id);
    }



    public List<Order> getAllOrders(int offset, int limit, String paymentStatus, String authorizationHeader){
        if(userService.userAutoLogIn(authorizationHeader)) return orderRepository.getAllOrders(offset, limit, paymentStatus);
        else return new ArrayList<>();
    }

    public String updateOrder(Order order, String authorizationHeader,  int id ){
        if(order == null) return "Invalid data";
        if(userService.userAutoLogIn(authorizationHeader))  return orderRepository.updateOrder(order,id);
        else return "No authorization";
    }

    public String modifyStatus(String authorizationHeader, int orderID, String paymentStatus){
        if(userService.userAutoLogIn(authorizationHeader)) {
            orderRepository.setPaymentStatus(orderID,paymentStatus);
            return "Status modified";
        }
        else return "No authorization";
    }

    public String deleteOrderByOrderID(int orderID){
        stripeRepository.deletePaymentByOrderID(orderID);
        return orderRepository.deleteOrderByOrderID(orderID);
    }

    public String getProductJsonByOrderID(int id){
        return orderRepository.getProductJsonByOrderID(id);
    }

    public void setPaymentStatus(int orderID, String paymentStatus){
        orderRepository.setPaymentStatus(orderID,paymentStatus);
    }

    public boolean isOrderPaid(int orderID){
        return orderRepository.isOrderPaid(orderID);
    }

    public String getUsedPromo(int orderID){
        return orderRepository.getUsedPromo(orderID);
    }


}
