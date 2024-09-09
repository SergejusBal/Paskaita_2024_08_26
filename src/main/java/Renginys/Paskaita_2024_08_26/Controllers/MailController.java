package Renginys.Paskaita_2024_08_26.Controllers;

import Renginys.Paskaita_2024_08_26.Models.Event;
import Renginys.Paskaita_2024_08_26.Services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping("/register/{email}")
    public ResponseEntity<Boolean> registerEmail(@PathVariable String email) {

        String response = mailService.registerEmail(email);
        HttpStatus status = checkHttpStatus(response);

        if(status == HttpStatus.OK) return new ResponseEntity<>(true, status);
        else return new ResponseEntity<>(false, status);
    }
    private HttpStatus checkHttpStatus(String response){

        switch (response){
            case "Invalid username or password", "No authorization":
                return HttpStatus.UNAUTHORIZED;
            case "Database connection failed":
                return HttpStatus.INTERNAL_SERVER_ERROR;
            case "User already exists", "Email already exists":
                return HttpStatus.CONFLICT;
            case "Invalid data":
                return HttpStatus.BAD_REQUEST;
            case "User was successfully added","user authorize", "Email was successfully added":
                return HttpStatus.OK;
            default:
                return HttpStatus.NOT_IMPLEMENTED;
        }

    }


}
