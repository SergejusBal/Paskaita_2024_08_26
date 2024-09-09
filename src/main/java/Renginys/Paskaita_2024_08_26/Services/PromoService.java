package Renginys.Paskaita_2024_08_26.Services;

import Renginys.Paskaita_2024_08_26.Repositories.PromoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromoService {

    @Autowired
    private PromoRepository promoRepository;

    public double checkPromo(String code){
        return promoRepository.checkPromo(code);
    }
    public void modifyCount(String code){
        int count = getCount(code) - 1;
        promoRepository.modifyCount(code, count);
    }

    public int getCount(String code){
        return promoRepository.getCount(code);
    }




}
