package Renginys.Paskaita_2024_08_26.Repositories;

import Renginys.Paskaita_2024_08_26.Models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;

@Repository
public class UserRepository {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;




    public String registerUser(User user){

        if(user.getName() == null || user.getPassword() == null) return "Invalid data";

        String sql = "INSERT INTO user (name,password)\n" +
                "VALUES (?,?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getPassword());

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());

            if (e.getErrorCode() == 1062) return "User already exists";

            return "Database connection failed";
        }
        return "User was successfully added";
    }


    public HashMap<String,String> login(User user){

        HashMap<String,String> response = new HashMap<>();

        if(user.getName() == null || user.getPassword() == null){
            response.put("response","Invalid data");
            return response;
        }

        String sql = "SELECT * FROM user WHERE name = ? AND password = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getPassword());
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) {
                response.put("response","Invalid username or password");
                return response;

            }
            user.setId(resultSet.getInt("id"));

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            response.put("response","Database connection failed");
            return response;
        }

        response.put("response","user authorize");
        response.put("id",user.getId() + "");
        return response;

    }



}
