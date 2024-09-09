package Renginys.Paskaita_2024_08_26.Services;

import Renginys.Paskaita_2024_08_26.Models.Event;
import Renginys.Paskaita_2024_08_26.Models.Filter;
import Renginys.Paskaita_2024_08_26.Repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserService userService;

    public String registerItem(Event event, String authorizationHeader){
        if(event == null) return "Invalid data";
        if(userService.userAutoLogIn(authorizationHeader)) return eventRepository.registerItem(event);
        else return "No authorization";
    }

    public String updateItem(Event event, String authorizationHeader, int id ){
        if(event == null) return "Invalid data";
        if(userService.userAutoLogIn(authorizationHeader))  return eventRepository.updateItem(event,id);
        else return "No authorization";
    }

    public Event getItemByID(int id){
        return eventRepository.getItemByID(id);
    }

    public List<Event> getAllItems(int offset , int limit, Filter filter){
        return eventRepository.getAllItems(offset,limit,filter);
    }

    public BigDecimal getEventPriceByID(int id){
        return eventRepository.getEventPriceByID(id);
    }
}