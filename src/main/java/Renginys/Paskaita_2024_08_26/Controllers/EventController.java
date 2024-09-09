package Renginys.Paskaita_2024_08_26.Controllers;

import Renginys.Paskaita_2024_08_26.Models.Event;
import Renginys.Paskaita_2024_08_26.Models.Filter;
import Renginys.Paskaita_2024_08_26.Services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;


    @PostMapping("/new")
    public ResponseEntity<Boolean> registerItem(@RequestBody(required = false) Event event, @RequestHeader("Authorization") String authorizationHeader) {

        String response = eventService.registerItem(event,authorizationHeader);
        HttpStatus status = checkHttpStatus(response);

        if(status == HttpStatus.OK) return new ResponseEntity<>(true, status);
        else return new ResponseEntity<>(false, status);
    }

    @PutMapping("/new/{id}")
    public ResponseEntity<Boolean> updateItem(@RequestBody(required = false) Event event, @RequestHeader("Authorization") String authorizationHeader, @PathVariable int id) {

        String response = eventService.updateItem(event,authorizationHeader,id);
        HttpStatus status = checkHttpStatus(response);

        if(status == HttpStatus.OK) return new ResponseEntity<>(true, status);
        else return new ResponseEntity<>(false, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getItemByID(@PathVariable int id) {

        Event event = eventService.getItemByID(id);

        if(event.getId() == 0)  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @PostMapping("/getAll")
    public ResponseEntity<List<Event>> getAllItems(@RequestParam int offset , @RequestParam  int limit, @RequestBody(required = false) Filter filter) {

        List<Event> eventList = eventService.getAllItems(offset,limit,filter);

        if(eventList.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(eventList, HttpStatus.OK);
    }

    private HttpStatus checkHttpStatus(String response){

        switch (response){
            case "Invalid username or password", "No authorization":
                return HttpStatus.UNAUTHORIZED;
            case "Database connection failed":
                return HttpStatus.INTERNAL_SERVER_ERROR;
            case "Invalid data":
                return HttpStatus.BAD_REQUEST;
            case "Event was successfully added","user authorize","Event was successfully updated":
                return HttpStatus.OK;
            default:
                return HttpStatus.NOT_IMPLEMENTED;
        }

    }




}
