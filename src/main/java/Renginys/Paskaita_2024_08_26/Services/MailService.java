package Renginys.Paskaita_2024_08_26.Services;


import Renginys.Paskaita_2024_08_26.Models.CartListItem;
import Renginys.Paskaita_2024_08_26.Models.MailResponse;
import Renginys.Paskaita_2024_08_26.Models.Order;
import Renginys.Paskaita_2024_08_26.Repositories.MailReposiroty;
import Renginys.Paskaita_2024_08_26.Repositories.StripeRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;


@Service
public class MailService {


    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Autowired
    private MailReposiroty mailReposiroty;

    @Autowired
    private StripeRepository stripeRepository;


    public String registerEmail(String email){
        sendThatYouHaveRegistered(email);
        return mailReposiroty.registerEmail(email);

    }



    public void sendThatYouHaveRegistered(String email){
        String title = "Welcome to Our Exclusive Insider Circle!";
        String content = "Thank you for subscribing to our newsletter and joining our exclusive community! We’re excited to have you on board and can’t wait to keep you updated with the latest news, special offers, and promotions.\n" +
                "\n" +
                "Here’s what you can look forward to:\n" +
                "\n" +
                "Exclusive Offers: Be the first to hear about our limited-time deals and discounts.\n" +
                "New Product Announcements: Get notified of the latest products and launches before anyone else.\n" +
                "Exciting Events: Stay in the loop on upcoming events, sales, and exciting happenings.\n" +
                "We’ll be delivering value straight to your inbox, so keep an eye out for all the great content coming your way!\n" +
                "\n" +
                "If you ever have any questions or feedback, feel free to reply to this email—we’re always happy to hear from our valued subscribers.\n" +
                "\n" +
                "Thank you for joining us on this exciting journey!";

        sendMail(email, title, content);

    };

    public void sendMailOrderNotification(Order order, String customerEmailFromStripe){
        if (order.getId() == 0 ) return;

        String sendTo = order.getCustomerEmail();
        String title = "Best shop order " + order.getId();
        String content = sendMailOrderNotificationContent(order);

        sendMail(sendTo, title, content);
    }

    public String sendMailOrderNotificationContent(Order order){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(order.getCustomerName());
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("Your order is:");

        HashMap<String, CartListItem> itemHashMap = getOrderItemHashMap(order.getOrderCartJsonString());

        for (String key : itemHashMap.keySet()) {
            stringBuilder.append("\n");
            stringBuilder.append(itemHashMap.get(key).toString());
        }

        stringBuilder.append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("Your payment status is: ");
        stringBuilder.append(order.getOrderStatus());

        stringBuilder.append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("Your refund key is: ");
        String refundKey = stripeRepository.getUIIDWithOrderID(order.getId());
        stringBuilder.append(refundKey);

        return stringBuilder.toString();

    }

    private HashMap<String, CartListItem> getOrderItemHashMap(String jsonItemString){

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, CartListItem>>() {}.getType(); // example Something(){} this part creates generic abstract object what we define in {}. getType() is gson method that gets the type/class of that object
        //extra note interesting interaction with LocalDateTime need to allow something thus new class setup.

        HashMap<String, CartListItem> hashMap = gson.fromJson(jsonItemString, type);

        return hashMap;
    }



    public void sendMail(String sendTo, String title, String sendContent) {
        Email from = new Email("sergejus.balciunas@gmail.com");
        Email to = new Email(sendTo);
        Content content = new Content("text/plain", sendContent);
        Mail mail = new Mail(from, title, to, content);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            MailResponse mailResponse = new MailResponse();
            mailResponse.setTo(sendTo);
            mailResponse.setTitle(title);
            mailResponse.setResponseBody(response.getBody());
            mailResponse.setResponseCode(response.getStatusCode() + "");
            mailResponse.setResponseHeader(response.getHeaders().toString());

            mailReposiroty.registerMail(mailResponse);

        } catch (IOException ex) {
            // throw ex;
        }
    }


}
