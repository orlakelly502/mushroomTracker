import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner ip = new Scanner(System.in);

        try (DBConnect conn = new DBConnect("devuser", "12QWaszxc")) {
            MushApp mApp = new MushApp(ip, conn);

            // Constructor call for Thread creates a Runnable - implementing Runnable single abstract method void run()
            // run has NO throws clause so methods using this thread cannot throw their errors further up to chain and
            // instead need to handle them themselves.
            Thread pollingThread = new Thread(mApp::pollAndPersistReadings);
            Thread menuThread = new Thread(mApp::navigateMenu);

            // .start spins up new threads .run() just runs on current thread
            pollingThread.start();
            menuThread.start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}// class end
