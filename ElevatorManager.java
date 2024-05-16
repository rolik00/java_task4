import java.util.*;

public class ElevatorManager {
    public static List<Request> waiting = new ArrayList<>();
    public ArrayList<Map<Integer, ArrayDeque<Integer>>> floors_queue;
    public Elevator first;
    public Elevator second;
    public int floor_count;
    public static boolean stop_flag = false;

    ElevatorManager(int count)
    {
        floor_count = count;
        first = new Elevator(1, 8);
        second = new Elevator(2, 4);
        floors_queue = new ArrayList<>(floor_count + 1);
        for (int i = 0; i <= floor_count; i++)
        {
            Map<Integer, ArrayDeque<Integer>> floorMap = new HashMap<>();
            floorMap.put(-1, new ArrayDeque<>());
            floorMap.put(1, new ArrayDeque<>());
            floors_queue.add(floorMap);
        }
    }
    public synchronized void floor_passed()
    {
        check_floor(first);
        check_floor(second);
        change_status(first);
        change_status(second);
        Main.create_background();
    }
    public synchronized void add_passenger(Request request)
    {
        //System.out.println("\uD83D\uDCEC Новый запрос: c " + (request.start_floor + 1) + " до " + (request.end_floor + 1) + " этажа");
        Main.start_floor = (request.start_floor + 1 < 10 ? Integer.toString(request.start_floor + 1) + " " : Integer.toString(request.start_floor + 1));
        Main.end_floor = (request.end_floor + 1 < 10 ? Integer.toString(request.end_floor + 1) + " " : Integer.toString(request.end_floor + 1));
        floors_queue.get(request.start_floor).get(request.direction).add(request.end_floor);
        if (first.target_floors.isEmpty() && second.target_floors.isEmpty())
        {
            if (Math.abs(first.current_floor - request.start_floor) <= Math.abs(second.current_floor - request.start_floor))
            {
                first.target_floors.add(request.start_floor);
                first.passenger_status = request.direction;
                //System.out.println("☑ Лифт №1 принял запрос на " + (request.start_floor + 1) + " этаже и едет " + (request.direction == 1 ? "вверх" : "вниз"));
            }
            else
            {
                second.target_floors.add(request.start_floor);
                second.passenger_status = request.direction;
                //System.out.println("☑ Лифт №2 принял запрос на " + (request.start_floor + 1) + " этаже и едет " + (request.direction == 1 ? "вверх" : "вниз"));
            }
        }
        else if (first.target_floors.isEmpty() || (first.passenger_status == request.direction && ((request.direction == 1 && first.current_floor <= request.start_floor) || (request.direction == -1 && first.current_floor >= request.start_floor))))
        {
            first.target_floors.add(request.start_floor);
            first.passenger_status = request.direction;
            //System.out.println("☑ Лифт №1 принял запрос на " + (request.start_floor + 1) + " этаже и едет " + (request.direction == 1 ? "вверх" : "вниз"));
        }
        else if (second.target_floors.isEmpty() || (second.passenger_status == request.direction && ((request.direction == 1 && second.current_floor <= request.start_floor) || (request.direction == -1 && second.current_floor >= request.start_floor))))
        {
            second.target_floors.add(request.start_floor);
            second.passenger_status = request.direction;
            //System.out.println("☑ Лифт №2 принял запрос на " + (request.start_floor + 1) + " этаже и едет " +  (request.direction == 1 ? "вверх" : "вниз"));
        }
        else
        {
            waiting.add(request);
            Main.flag = 1;
            //System.out.println("⌛️ Все лифты заняты, запрос находится в ожидании");
        }
        Main.create_background();
    }
    private void check_floor(Elevator elevator)
    {
        while (elevator.passengers.contains(elevator.current_floor))
        {
            int passenger_count = elevator.passengers.size();
            elevator.passengers.removeAll(List.of(elevator.current_floor));
            elevator.target_floors.removeAll(List.of(elevator.current_floor));
            //System.out.println("❌ На " + (elevator.current_floor + 1) + " этаже из лифта №"+ elevator.ID + " вышло " + (passenger_count - elevator.passengers.size()) + " человек");
            if (elevator.passengers.size() == 0)
            {
                elevator.passenger_status = 0;
            }
        }
        if (elevator.passenger_status != 0)
        {
            ArrayDeque<Integer> floor_queue = floors_queue.get(elevator.current_floor).get(elevator.passenger_status);
            while (!floor_queue.isEmpty() && elevator.passengers.size() < elevator.capacity)
            {
                int current_passenger = floor_queue.poll();
                elevator.target_floors.removeAll(List.of(elevator.current_floor));
                pop_from_waiting(elevator, current_passenger);
                elevator.target_floors.add(current_passenger);
                elevator.passengers.add(current_passenger);
                elevator.status = elevator.passenger_status;
                //System.out.println("✅ Пассажир зашел в лифт №" + elevator.ID + " на " + (elevator.current_floor + 1) + " этаже");
            }
        }
        else
        {
            ArrayDeque<Integer> floor_queue_up = floors_queue.get(elevator.current_floor).get(1);
            ArrayDeque<Integer> floor_queue_down = floors_queue.get(elevator.current_floor).get(-1);
            ArrayDeque<Integer> floor_queue;
            if (floor_queue_up.size() >= floor_queue_down.size())
            {
                floor_queue = floor_queue_up;
                elevator.status = 1;
                elevator.passenger_status = 1;
            }
            else
            {
                floor_queue = floor_queue_down;
                elevator.status = -1;
                elevator.passenger_status = -1;
            }
            while (!floor_queue.isEmpty() && elevator.passengers.size() < elevator.capacity)
            {
                int current_passenger = floor_queue.poll();
                elevator.target_floors.removeAll(List.of(elevator.current_floor));
                pop_from_waiting(elevator, current_passenger);
                elevator.target_floors.add(current_passenger);
                elevator.passengers.add(current_passenger);
                //System.out.println("✅ Пассажир зашел в лифт №" + elevator.ID + " на " + (elevator.current_floor + 1) + " этаже");
            }
        }
    }
    private void pop_from_waiting(Elevator elevator, int current_passenger)
    {
        int i = 0;
        while (i < waiting.size())
        {
            Request request = waiting.get(i);
            if (request.start_floor == elevator.current_floor && request.end_floor == current_passenger)
            {
                waiting.remove(i);
            }
            i++;
        }
    }
    private void change_status(Elevator elevator)
    {
        if (elevator.target_floors.isEmpty())
        {
            if (waiting.isEmpty())
            {
                elevator.status = 0;
                elevator.passenger_status = 0;
            }
            else
            {
                Request request = waiting.get(0);
                waiting.remove(0);
                elevator.target_floors.add(request.start_floor);
                elevator.passenger_status = request.direction;
                elevator.status = elevator.current_floor < request.start_floor ? 1 : -1;
                //System.out.println("☑ Лифт №" + elevator.ID + " принял запрос на " + (request.start_floor + 1) + "этаже и едет " +  (request.direction == 1 ? "вверх" : "вниз"));
            }
        }
        else if (elevator.target_floors.peek() == elevator.current_floor)
        {
            elevator.target_floors.removeAll(List.of(elevator.current_floor));
        }
        else if (elevator.target_floors.peek() < elevator.current_floor)
        {
            elevator.status = -1;
        }
        else
        {
            elevator.status = 1;
        }
        if (elevator.status == 0)
        {
            //System.out.println("☕ Лифт №" + elevator.ID + " находится на " + (elevator.current_floor + 1) + " этаже");
            if (elevator.ID == 1)
            {
                Main.floor_1 = (elevator.current_floor + 1 < 10 ? Integer.toString(elevator.current_floor + 1) + " " : Integer.toString(elevator.current_floor + 1));
                Main.info_1 = "0";
            }
            else
            {
                Main.floor_2 = (elevator.current_floor + 1 < 10 ? Integer.toString(elevator.current_floor + 1) + " " : Integer.toString(elevator.current_floor + 1));
                Main.info_2 = "0";
            }
        }
        else if (elevator.status == -1)
        {
            if (elevator.current_floor > 0)
            {
                elevator.current_floor--;
            }
            else
            {
                elevator.status = 0;
                elevator.passenger_status = 0;
            }
            //System.out.println("\uD83D\uDD3D Лифт №" + elevator.ID + " едет вниз. Текущий номер этажа = " + (elevator.current_floor + 1));
            if (elevator.ID == 1)
            {
                Main.floor_1 = (elevator.current_floor + 1 < 10 ? Integer.toString(elevator.current_floor + 1) + " " : Integer.toString(elevator.current_floor + 1));
                Main.info_1 = Integer.toString(elevator.passengers.size());
            }
            else
            {
                Main.floor_2 = (elevator.current_floor + 1 < 10 ? Integer.toString(elevator.current_floor + 1) + " " : Integer.toString(elevator.current_floor + 1));
                Main.info_2 = Integer.toString(elevator.passengers.size());
            }
        }
        else if (elevator.status == 1)
        {
            if (elevator.current_floor < floor_count)
            {
                elevator.current_floor++;
            }
            else
            {
                elevator.status = 0;
                elevator.passenger_status = 0;
            }
            //System.out.println("\uD83D\uDD3C Лифт №" + elevator.ID + " едет вверх. Текущий номер этажа = " + (elevator.current_floor + 1));
            if (elevator.ID == 1)
            {
                Main.floor_1 = (elevator.current_floor + 1 < 10 ? Integer.toString(elevator.current_floor + 1) + " " : Integer.toString(elevator.current_floor + 1));
                Main.info_1 = Integer.toString(elevator.passengers.size());
            }
            else
            {
                Main.floor_2 = (elevator.current_floor + 1 < 10 ? Integer.toString(elevator.current_floor + 1) + " " : Integer.toString(elevator.current_floor + 1));
                Main.info_2 = Integer.toString(elevator.passengers.size());
            }
        }
    }
}