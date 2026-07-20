import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner ip = new Scanner(System.in);


        try (DBConnect conn = new DBConnect("devuser", "12QWaszxc")) {
            MushApp mApp = new MushApp(ip, conn);
            mApp.run();
        } catch (SQLException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}// class end
