package Renginys.Paskaita_2024_08_26.Repositories;

import Renginys.Paskaita_2024_08_26.Models.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentRepository {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    public String registerComment(Comment comment){

        if( comment.getName() == null || comment.getComment() == null || comment.getEventID() == 0)
            return "Invalid data";

        String sql = "INSERT INTO comments (event_id,comment,name)\n" +
                "VALUES (?,?,?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,comment.getEventID());
            preparedStatement.setString(2,comment.getComment());
            preparedStatement.setString(3,comment.getName());

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());

            return "Database connection failed";
        }

        return "Comment was successfully added";
    }

    public List<Comment> getCommentsByEventID(int eventID){

        List<Comment> commentList = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE event_id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,eventID);

            ResultSet resultSet =  preparedStatement.executeQuery();

            while(resultSet.next()) {

                Comment comment = new Comment();

                comment.setId(resultSet.getInt("id"));
                comment.setEventID(resultSet.getInt("event_id"));
                comment.setComment(resultSet.getString("comment"));
                comment.setName(resultSet.getString("name"));

                LocalDateTime date = formatDateTime(resultSet.getString("comment_datetime"));
                comment.setDate(date);

                commentList.add(comment);
            }

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

        return commentList;

    }

    public String rateEvent(int stars, int eventID){

        if(stars == 0 || eventID == 0)
            return "Invalid data";

        String sql = "INSERT INTO rating (event_id,stars)\n" +
                "VALUES (?,?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,eventID);
            preparedStatement.setInt(2,stars);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());

            return "Database connection failed";
        }

        return "Rating was successfully added";
    }

    public List<Integer> getAllRatingByID(int eventID){

        List<Integer> commentList = new ArrayList<>();
        String sql = "SELECT * FROM rating WHERE event_id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,eventID);

            ResultSet resultSet =  preparedStatement.executeQuery();

            while(resultSet.next()) {

                Integer integer = resultSet.getInt("stars");

                commentList.add(integer);
            }

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

        return commentList;

    }

    private LocalDateTime formatDateTime(String dateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime rentalDate = null;
        try {
            rentalDate = LocalDateTime.parse(dateTime, dateTimeFormatter);
        }catch(DateTimeParseException | NullPointerException e) {
            rentalDate = LocalDateTime.parse("1900-01-01 00:00:00", dateTimeFormatter);
        }
        return rentalDate;
    }

}
