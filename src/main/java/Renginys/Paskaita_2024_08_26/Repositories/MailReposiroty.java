package Renginys.Paskaita_2024_08_26.Repositories;

import Renginys.Paskaita_2024_08_26.Models.Event;
import Renginys.Paskaita_2024_08_26.Models.MailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class MailReposiroty {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public String registerEmail(String email){

        if(email == null) return "Invalid data";

        String sql = "INSERT INTO email (email) VALUES (?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,email);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());

            if (e.getErrorCode() == 1062) return "Email already exists";

            return "Database connection failed";
        }

        return "Email was successfully added";

    }


    public String registerMail(MailResponse mailResponse){

        String sql = "INSERT INTO mail_info (send_to,title,response_code,response_body,response_header)\n" +
                "VALUES (?,?,?,?,?);";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,mailResponse.getTo());
            preparedStatement.setString(2,mailResponse.getTitle());
            preparedStatement.setString(3,mailResponse.getResponseCode());
            preparedStatement.setString(4,mailResponse.getResponseBody());
            preparedStatement.setString(5,mailResponse.getResponseHeader());

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());

            return "Database connection failed";
        }

        return "Mail log was successfully added";

    }

}
