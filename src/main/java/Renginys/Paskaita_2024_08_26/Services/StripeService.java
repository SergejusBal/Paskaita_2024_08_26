package Renginys.Paskaita_2024_08_26.Services;


import Renginys.Paskaita_2024_08_26.Models.CartListItem;
import Renginys.Paskaita_2024_08_26.Models.Event;
import Renginys.Paskaita_2024_08_26.Models.Order;
import Renginys.Paskaita_2024_08_26.Repositories.StripeRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;

import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;
    @Autowired
    private StripeRepository stripeRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private EventService eventService;
    @Autowired
    private PromoService promoService;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;

    private final String REFUND_SIZE = "0.5";
    private final int REFUND_DAYS_APPLIED = 7;


    private final String URL = "localhost";

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentIntent createPaymentIntent(Long amount, String currency) throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .build();

        return PaymentIntent.create(params);
    }

    public HashMap<String,String> refundByOrderID(int orderID, String authorizationHeader ){

        HashMap<String,String> responseHashMap = new HashMap<>();

        if(!userService.userAutoLogIn(authorizationHeader)) {
            responseHashMap.put("response","No authorization");
            return responseHashMap;
        }

        responseHashMap.putAll(refund(orderID));

    return responseHashMap;
    }

    public Long checkRefundSize(String refundKey){

        int orderID = stripeRepository.getOrderIDWithUUID(refundKey);;

        return calculateRefundTotalPrice(orderID);
    }

    public HashMap<String,String> refundByRefundKey(String refundKey){

        int orderID = stripeRepository.getOrderIDWithUUID(refundKey);

        return refund(orderID);
    }

    public HashMap<String,String> refund(int orderID){

        HashMap<String,String> responseHashMap = new HashMap<>();


        String paymentIntentID = stripeRepository.getPaymentIntentID(orderID);
        String chargeID = getPaymentChargeID(paymentIntentID);
        long amountPayed =  getPaymentAmount(paymentIntentID);
        long calculatedRefundAmount = calculateRefundTotalPrice(orderID);

        long refundAmount = Math.min(amountPayed, calculatedRefundAmount);

        RefundCreateParams params =  RefundCreateParams.builder().setCharge(chargeID).setAmount(refundAmount).build();
        System.out.println("Refund details");
        try {
            Refund refund = Refund.create(params);
            responseHashMap.put("Status",refund.getStatus());
            responseHashMap.put("Amount",refund.getAmount() + "");
            responseHashMap.put("response","Refund was successful");
            orderService.setPaymentStatus(orderID,"Refunded");

        } catch (StripeException e) {
            System.out.println("No refund available");
            responseHashMap.put("response","No refund available");
        }

        return responseHashMap;

    }

    public Session createCheckoutSession(Integer orderID) throws StripeException {

        UUID uuid = generateUID();
        UUID uuidSecret = generateUID();
        int payment_id = createPayment(uuid,uuidSecret,orderID);

        Long orderPrice = calculateOrderPrice(orderID);
        String OrderCurrency = "EUR";

        String successUrl = "http://"+URL+":8080/stripe/" + uuid + "/" + uuidSecret + "/" + payment_id;
        String cancelUrl = "http://"+URL+":8080/stripe/" + uuid + "/" + payment_id;

        Map<String, String> metadata = new HashMap<>();
        metadata.put("uuid", uuid.toString());
        metadata.put("id", payment_id + "");
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(OrderCurrency)
                                .setUnitAmount(orderPrice)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Moketi:")
                                        .build())
                                .build())
                        .build())
                .putAllMetadata(metadata)
                .build();

        Session session = Session.create(params);
        stripeRepository.setPaymentIntentID(payment_id, session.getPaymentIntent());
        return session;
    }

    public void setPaymentStatus(int id, String uuid, String uuidSecret){
        int orderID = stripeRepository.getOrderID(id,uuid);

        if(orderID == 0) return;
        if(orderService.isOrderPaid(orderID)) return;

        if (uuidSecret != null && stripeRepository.checkIfPaymentValid(id,uuid,uuidSecret)) {
            orderService.setPaymentStatus(orderID,"Paid");
            Order order = orderService.getOrderByIDUnauthorized(orderID);
            promoService.modifyCount(order.getPromoCode());

            String customerEmailFromStripe = getUserPaymentEmail(orderID);
            mailService.sendMailOrderNotification(order,customerEmailFromStripe);
        }
        else orderService.setPaymentStatus(orderID,"Cancelled");

    }

    private String getUserPaymentEmail(int orderID){
        String paymentIntentID = stripeRepository.getPaymentIntentID(orderID);

        PaymentIntent paymentIntent = getPaymentIntentByPaymentIntentID(paymentIntentID);
        String customerID = paymentIntent.getCustomer();

        return getCustomerEmailByCustomerID(customerID);

    }

    private String getCustomerEmailByCustomerID(String customerId){
        Customer customer;
        String customerEmail = "";
        try {
            customer = Customer.retrieve(customerId);
            customerEmail = customer.getEmail();
        } catch (StripeException e) {
            System.out.println(e.getMessage());
        }
        return customerEmail;
    }

    private PaymentIntent getPaymentIntentByPaymentIntentID(String paymentIntentId){

        PaymentIntent paymentIntent = null;
        try {
           paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            System.out.println(e.getMessage());
        }
        return paymentIntent;

    }

    private String getPaymentChargeID(String paymentIntentId){

        String chargeID = "";
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            chargeID = paymentIntent.getCharges().getData().get(0).getId();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        return chargeID;
    }

    private long getPaymentAmount(String paymentIntentId){

        long amount;
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            amount = paymentIntent.getAmount();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        return amount;
    }



    private int createPayment(UUID uuid,UUID uuidSecret, Integer orderID){
        return stripeRepository.createPayment(uuid, uuidSecret, orderID);
    }


    private UUID generateUID(){
        return UUID.randomUUID();
    }

    private HashMap<String, CartListItem> getOrderItemArrayByOrderID(int orderID){

        String jsonItemString = orderService.getProductJsonByOrderID(orderID);

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, CartListItem>>() {}.getType(); // example Something(){} this part creates generic abstract object what we define in {}. getType() is gson method that gets the type/class of that object
        //extra note interesting interaction with LocalDateTime need to allow something thus new class setup.

        HashMap<String, CartListItem> hashMap = gson.fromJson(jsonItemString, type);

        return hashMap;
    }



    private Long calculateOrderPrice(int orderID){

        HashMap<String, CartListItem> cartList = getOrderItemArrayByOrderID(orderID);
        long totalPrice = 0;

        for (String key : cartList.keySet()) {
            int quantity = cartList.get(key).getQuantity();
            BigDecimal price = eventService.getEventPriceByID(cartList.get(key).getId());
            price = price.multiply(new BigDecimal(100));
            price = price.multiply(new BigDecimal(quantity));
            totalPrice += price.longValue();
        }

        String promoCode = orderService.getUsedPromo(orderID);
        double promoSize = promoService.checkPromo(promoCode);

        return (long) (totalPrice * promoSize);
    }

    private Long calculateRefundTotalPrice(int orderID){

        HashMap<String, CartListItem> cartList = getOrderItemArrayByOrderID(orderID);
        long totalPrice = 0;
        LocalDate today = LocalDate.now();

        for (String key : cartList.keySet()) {
            int quantity = cartList.get(key).getQuantity();
            Event event = eventService.getItemByID(cartList.get(key).getId());
            BigDecimal price = event.getPrice();
            LocalDate eventDate = event.getDate().toLocalDate();
            price = price.multiply(new BigDecimal(100));
            price = price.multiply(new BigDecimal(quantity));

            if(today.isAfter(eventDate.minusDays(REFUND_DAYS_APPLIED))) price = price.multiply(new BigDecimal(REFUND_SIZE));
            if(today.isAfter(eventDate)) price = price.multiply(BigDecimal.ZERO);

            totalPrice += price.longValue();
        }

        String promoCode = orderService.getUsedPromo(orderID);
        double promoSize = promoService.checkPromo(promoCode);

        return (long) (totalPrice * promoSize);
    }

}