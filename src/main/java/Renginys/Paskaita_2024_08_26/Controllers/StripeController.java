package Renginys.Paskaita_2024_08_26.Controllers;

import Renginys.Paskaita_2024_08_26.Services.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/stripe")
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/pay")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> data) {
        try {

            Integer orderID = Integer.parseInt(data.get("orderID").toString());

            Session session = stripeService.createCheckoutSession(orderID);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("id", session.getId());
            responseData.put("paymentCode", session.getMetadata().get("uuid"));
            responseData.put("paymentID", session.getMetadata().get("id"));

            return ResponseEntity.ok(responseData);
        } catch (StripeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    @GetMapping("/refund/{id}")
    public ResponseEntity<HashMap<String,String>>refundByID(@PathVariable int id, @RequestHeader("Authorization") String authorizationHeader)  {

        HashMap<String,String> responseHashMap = stripeService.refundByOrderID(id, authorizationHeader);
        HttpStatus status = checkHttpStatus(responseHashMap.get("response"));

        if(status == HttpStatus.OK) return new ResponseEntity<>(responseHashMap, HttpStatus.OK);
        return new ResponseEntity<>(responseHashMap, status);

    }
    @GetMapping("/refund/key/{refundKey}")
    public ResponseEntity<HashMap<String,String>>refundByRefundKey(@PathVariable String refundKey)  {

        HashMap<String,String> responseHashMap = stripeService.refundByRefundKey(refundKey);
        HttpStatus status = checkHttpStatus(responseHashMap.get("response"));

        if(status == HttpStatus.OK) return new ResponseEntity<>(responseHashMap, HttpStatus.OK);
        return new ResponseEntity<>(responseHashMap, status);

    }
    @GetMapping("/refund/check/{refundKey}")
    public ResponseEntity<Long>checkRefundSize(@PathVariable String refundKey)  {

       Long response = stripeService.checkRefundSize(refundKey);

       if(response != null) return new ResponseEntity<>(response, HttpStatus.OK);
       else return new ResponseEntity<>(0L, HttpStatus.NO_CONTENT);
    }


    private HttpStatus checkHttpStatus(String response){

        switch (response){
            case "No authorization":
                return HttpStatus.UNAUTHORIZED;
            case "Database connection failed":
                return HttpStatus.INTERNAL_SERVER_ERROR;
            case "Invalid data":
                return HttpStatus.BAD_REQUEST;
            case "Refund was successful","Status modified","No refund available":
                return HttpStatus.OK;
            default:
                return HttpStatus.NOT_IMPLEMENTED;
        }

    }



    @GetMapping("/{uuid}/{uuidSecret}/{id}")
    public RedirectView redirect(@PathVariable String uuid, @PathVariable String uuidSecret,@PathVariable int id) {
        stripeService.setPaymentStatus(id,uuid,uuidSecret);
        return new RedirectView("http://127.0.0.1:5500/succes.html");
    }
    @GetMapping("/{uuid}/{id}")
    public RedirectView redirect(@PathVariable String uuid,@PathVariable int id) {
       stripeService.setPaymentStatus(id,uuid,null);
        return new RedirectView("http://127.0.0.1:5500/fail.html");
    }

}