package Renginys.Paskaita_2024_08_26.Repositories;

import Renginys.Paskaita_2024_08_26.Models.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

@Repository
public class PromoRepository {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public double checkPromo(String code){

        double promoSize = 1;

        String sql = "SELECT * FROM promo WHERE promo_code = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,code);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return 1.0;

            int promoAmount = resultSet.getInt("amount");
            if (promoAmount <= 0) return 1.0;

            promoSize = resultSet.getDouble("promo_size");


        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return 1.0;
        }

        return promoSize;
    }


    public int getCount(String code){

        int promoAmount;

        String sql = "SELECT * FROM promo WHERE promo_code = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,code);
            ResultSet resultSet =  preparedStatement.executeQuery();

            if(!resultSet.next()) return 0;

            promoAmount = resultSet.getInt("amount");


        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return 0;
        }

        return promoAmount;

    }

    public void modifyCount(String code, int count){

        String sql = "UPDATE promo SET amount = ? WHERE promo_code = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, count);
            preparedStatement.setString(2, code);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {
            System.out.println(e.getMessage());

        }

    }

    public String deleteOnePromoCode(String Code){

        String sql = "DELETE FROM promo WHERE promo_code = ? limit 1";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,Code);
            preparedStatement.executeUpdate();

        }catch (SQLException e) {

            System.out.println(e.getMessage());
            return "Database connection failed";
        }

        return "One promo element was successfully deleted";
    }



}
