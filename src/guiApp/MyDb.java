package guiApp;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class MyDb

{
    private Connection connection;
    private Statement stmt;
    private ResultSet rs;
    private Date date;
    private String stringDate;
    //Параметры подключения к базе данных:
    private String driverName = "com.mysql.jdbc.Driver";
    private String serverName = "localhost";
    private String mydatabase = "test";
    private String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
    private String username = "root";
    private String password = "root";

    public String getStringDate() {
        return stringDate;
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStmt() {
        return stmt;
    }

    public ResultSet getRs() {
        return rs;
    }

    MyDb() {
        try {
            //Инициализация подключения к БД
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, username, password);
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Чтение из БД последних координат
    public int[] db_read() {
        int[] position = new int[2];
        String readQuery = "select x,y from position order by id desc limit 1 ";
        try {
            rs = stmt.executeQuery(readQuery);
            if (rs.next()) {
                position[0] = rs.getInt("x");
                position[1] = rs.getInt("y");
            }
        } catch (SQLException e) {
            e.printStackTrace();   
        }
        return position;
    }

    //Запись в БД времени и координат
    public void db_write(int x, int y) {
        try {
            String insertQuery = " insert into position (x,y, date) values (?, ?, ?)";
            date = new java.util.Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ");
            stringDate = simpleDateFormat.format(date);
            PreparedStatement preparedStmt = connection.prepareStatement(insertQuery);
            preparedStmt.setInt(1, x);
            preparedStmt.setInt(2, y);
            preparedStmt.setString(3, stringDate);
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // /Чтение из БД истории перемещения квадрата (координаты  и время)
    public Vector getDataFromDB() throws SQLException {
        Vector result = new Vector();
        Vector<String> element;
        String query = "select x,y,date from position order by id desc limit 50 ";
        try {
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                element = new Vector();
                element.add(String.valueOf(rs.getString("date")));
                element.add(String.valueOf(rs.getInt("x")));
                element.add(String.valueOf(rs.getInt("y")));
                result.add(element);
            }
        } catch (SQLException e) {
            e.printStackTrace();   
        }
        return result;
    }
}
