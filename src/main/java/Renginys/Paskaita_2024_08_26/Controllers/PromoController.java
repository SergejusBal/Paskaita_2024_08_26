package Renginys.Paskaita_2024_08_26.Controllers;

import Renginys.Paskaita_2024_08_26.Services.PromoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/promo")
public class PromoController {

    @Autowired
    private PromoService promoService;
    @GetMapping("/check")
    public ResponseEntity<Double> checkPromo(@RequestParam(required = false) String code) {

        if(code == null) return new ResponseEntity<>(1.0, HttpStatus.OK);

        double promoSize = promoService.checkPromo(code);
        return new ResponseEntity<>(promoSize, HttpStatus.OK);

    }
}
