package Renginys.Paskaita_2024_08_26.Services;

import Renginys.Paskaita_2024_08_26.Models.Comment;
import Renginys.Paskaita_2024_08_26.Models.Event;
import Renginys.Paskaita_2024_08_26.Repositories.CommentRepository;
import Renginys.Paskaita_2024_08_26.Repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EventRepository eventRepository;

    public List<Comment> getCommentsByEventID(int eventID){
        return commentRepository.getCommentsByEventID(eventID);
    }

    public String registerComment(Comment comment){
        if(comment == null || comment.getEventID() == 0) return "Invalid data";
        Event event = eventRepository.getItemByID(comment.getEventID());
        LocalDateTime localDateTime = LocalDateTime.now();
        if(event.getDate().isBefore(localDateTime)) return commentRepository.registerComment(comment);
        else return "Comments are not allowed yet!";
    }

    public String rateEvent(int stars, int eventID){

        if(stars > 5 ) stars = 5;
        if(stars < 1 ) stars = 1;

        return commentRepository.rateEvent(stars,eventID);
    }
    public double getRating(int eventID){
        List<Integer> ratingList = commentRepository.getAllRatingByID(eventID);
        return calculateAverage(ratingList);
    }

    private double calculateAverage(List<Integer> ratingList){
        if(ratingList.isEmpty()) return 0.0;

        int sum = 0;
        for (int n : ratingList) {
            sum += n;
        }
        return (double) sum / ratingList.size();

    }




}
