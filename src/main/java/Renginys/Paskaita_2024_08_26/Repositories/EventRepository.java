package Renginys.Paskaita_2024_08_26.Repositories;


import Renginys.Paskaita_2024_08_26.Models.Event;
import Renginys.Paskaita_2024_08_26.Models.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventRepository {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public String registerItem(Event event){

        if( event.getName() == null || event.getDescription() == null || event.getPrice() == null ||
            event.getCategory() == null || event.getDate() == null || event.getImageUrl() == null )
            return "Invalid data";

        String sql = "INSERT INTO event (name,description,price,category,date,imageUrl)\n" +
                "VALUES (?,?,?,?,?,?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,event.getName());
            preparedStatement.setString(2,event.getDescription());
            preparedStatement.setDouble(3,event.getPrice().doubleValue());
            preparedStatement.setString(4,event.getCategory());
            preparedStatement.setString(5,event.getDate().toString());
            preparedStatement.setString(6,event.getImageUrl());

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());

            return "Database connection failed";
        }

        return "Event was successfully added";

    }

    public String updateItem(Event event, int id){

        if( event.getName() == null || event.getDescription() == null || event.getPrice() == null ||
            event.getCategory() == null || event.getDate() == null || event.getImageUrl() == null )
            return "Invalid data";

        String sql = "UPDATE event SET name = ?, description = ?, price = ?, category = ?, imageUrl = ?, date = ? WHERE id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1,event.getName());
            preparedStatement.setString(2,event.getDescription());
            preparedStatement.setDouble(3,event.getPrice().doubleValue());
            preparedStatement.setString(4,event.getCategory());
            preparedStatement.setString(5,event.getImageUrl());
            preparedStatement.setString(6,event.getDate().toString());
            preparedStatement.setInt(7,id);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return "Database connection failed";

        }

        return "Event was successfully updated";
    }
    public Event getItemByID(int id){

        Event event = new Event();
        String sql = "SELECT * FROM event WHERE id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return new Event();

            event.setId(resultSet.getInt("id"));
            event.setName(resultSet.getString("name"));
            event.setDescription(resultSet.getString("description"));
            event.setPrice(BigDecimal.valueOf(resultSet.getDouble("price")));
            event.setCategory(resultSet.getString("category"));

            LocalDateTime date = formatDateTime(resultSet.getString("date"));
            event.setDate(date);

            event.setImageUrl(resultSet.getString("imageUrl"));

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return new Event();
        }

        return event;
    }

    public List<Event> getAllItems(int offset , int limit, Filter filter){

        String category = "%";
        if(filter != null && filter.getCategory() != null) category = "%" + filter.getCategory() + "%";

        List<Event> eventList = new ArrayList<>();
        String sql = "SELECT * FROM event WHERE category LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,category);
            preparedStatement.setInt(2,limit);
            preparedStatement.setInt(3,offset);
            ResultSet resultSet =  preparedStatement.executeQuery();

            while(resultSet.next()) {

                Event event = new Event();

                event.setId(resultSet.getInt("id"));
                event.setName(resultSet.getString("name"));
                event.setDescription(resultSet.getString("description"));
                event.setPrice(BigDecimal.valueOf(resultSet.getDouble("price")));
                event.setCategory(resultSet.getString("category"));

                LocalDateTime date = formatDateTime(resultSet.getString("date"));
                event.setDate(date);

                event.setImageUrl(resultSet.getString("imageUrl"));

                eventList.add(event);
            }

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

        return eventList;

    }

    public BigDecimal getEventPriceByID(int id){

        BigDecimal price = BigDecimal.ZERO;

        String sql = "SELECT price FROM event WHERE id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return price;

            price = BigDecimal.valueOf(resultSet.getDouble("price"));


        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return price;
        }

        return price;
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
