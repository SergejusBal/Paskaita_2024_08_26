package Renginys.Paskaita_2024_08_26.Controllers;

import Renginys.Paskaita_2024_08_26.Models.Comment;
import Renginys.Paskaita_2024_08_26.Services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/new")
    public ResponseEntity<Boolean> registerItem(@RequestBody Comment comment) {

        String response = commentService.registerComment(comment);
        HttpStatus status = checkHttpStatus(response);

        if(status == HttpStatus.OK) return new ResponseEntity<>(true, status);
        else return new ResponseEntity<>(false, status);
    }

    @GetMapping("/rate/{stars}/{eventID}")
    public ResponseEntity<Boolean> rateEvent(@PathVariable int stars, @PathVariable int eventID) {

        String response = commentService.rateEvent(stars,eventID);
        HttpStatus status = checkHttpStatus(response);

        if(status == HttpStatus.OK) return new ResponseEntity<>(true, status);
        else return new ResponseEntity<>(false, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Comment>> getCommentsByEventID(@PathVariable int id) {

        List<Comment> commentList = commentService.getCommentsByEventID(id);

        if(commentList.isEmpty())  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(commentList, HttpStatus.OK);
    }

    @GetMapping("/getRating/{id}")
    public ResponseEntity<Double> getRating(@PathVariable int id) {

        double rating = commentService.getRating(id);

        if(rating == 0.0)  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(rating, HttpStatus.OK);
    }



    private HttpStatus checkHttpStatus(String response) {

        switch (response) {
            case "Database connection failed":
                return HttpStatus.INTERNAL_SERVER_ERROR;
            case "Invalid data":
                return HttpStatus.BAD_REQUEST;
            case "Comments are not allowed yet!":
                return HttpStatus.FORBIDDEN;
            case "Comment was successfully added", "Rating was successfully added":
                return HttpStatus.OK;
            default:
                return HttpStatus.NOT_IMPLEMENTED;
        }


    }
}
