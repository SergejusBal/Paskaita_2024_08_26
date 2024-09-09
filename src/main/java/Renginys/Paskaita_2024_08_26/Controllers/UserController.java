package Renginys.Paskaita_2024_08_26.Controllers;

import Renginys.Paskaita_2024_08_26.Models.User;
import Renginys.Paskaita_2024_08_26.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Boolean> registerUser(@RequestBody User user) {

        String response = userService.registerUser(user);
        HttpStatus status = checkHttpStatus(response);

        if(status == HttpStatus.OK) return new ResponseEntity<>(true, status);
        else return new ResponseEntity<>(false, status);
    }




    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {

        HashMap<String,String> responseMap = userService.login(user);
        HttpStatus status = checkHttpStatus(responseMap.get("response"));

        if(status == HttpStatus.OK) return new ResponseEntity<>(responseMap.get("JWT"), status);
        else return new ResponseEntity<>(responseMap.get("response"), status);
    }

    @GetMapping("/autoLogin")
    public ResponseEntity<Boolean> userAutoLogIn(@RequestHeader("Authorization") String authorizationHeader) {

        boolean response = userService.userAutoLogIn(authorizationHeader);

        if(response) return new ResponseEntity<>(true, HttpStatus.OK);
        else return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
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
