import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static String floor_1 = "1 ", floor_2 = "1 ", came_1 = "0", came_2 = "0", left_1 = "0", left_2 = "0", info_1 = "0", info_2 = "0", start_floor = "  ", end_floor = "  ";
    public static int flag = 0;
    public static void clear_concole()
    {
        try
        {
            Thread.sleep(1000);
            if (System.getProperty("os.name").contains("Windows"))
            {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }

        } catch (IOException | InterruptedException ex) {}
    }

    public static void create_background()
    {
        String request =    "                Новый запрос:     С " + start_floor + " до " + end_floor + "           \n";
        String background = "--------------------------------------------------------------------------------------\n" +
                "|         ---------------------------            ---------------------------         |\n" +
                "|         |       ---------         |            |       ---------         |         |\n" +
                "|         |       |   " + floor_1 + "  |         |            |       |   " + floor_2 +"  |         |         |\n" +
                "|         |       ---------         |            |       ---------         |         |\n" +
                "|         |                         |            |                         |         |\n" +
                "|         |                         |            |                         |         |\n" +
                "|         | Всего сейчас в лифте    |            |Всего сейчас в лифте     |         |\n" +
                "|         | " + info_1 + " человек               |            | " + info_2 + " человек               |         |\n" +
                "|         |                         |            |                         |         |\n" +
                "|         |                         |            |                         |         |\n" +
                "|         |                         |            |                         |         |\n" +
                "|         |         Лифт №1         |            |         Лифт №2         |         |\n" +
                "|         ---------------------------            ---------------------------         |\n" +
                "--------------------------------------------------------------------------------------\n";

        clear_concole();
        System.out.println(request + background);
        if (flag == 1)
        {
            System.out.println("Все лифты заняты, запрос находится в ожидании");
            flag = 0;
        }
    }
    private static void threads_call(int floorCount, int requestCount)
    {
        ElevatorManager manager = new ElevatorManager(floorCount);
        Thread requests = new Thread(new RequestThread(manager, requestCount));
        Thread elevators = new Thread(new ElevatorManagerThread(manager));
        requests.start();
        elevators.start();
        try
        {
            requests.join();
            elevators.join();
        }
        catch (InterruptedException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите количество этажей в доме:");
        int floor_count = scanner.nextInt();
        System.out.println("Введите количество запросов вызова лифта: ");
        int request_count = scanner.nextInt();
        if (floor_count != 0 && request_count != 0) threads_call(floor_count, request_count);
        else
        {
            System.out.println("Введено число 0!");
            System.exit(0);
        }
    }
}