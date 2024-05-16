import java.util.*;
public class Elevator {
    public int ID;
    public int current_floor = 0;
    public ArrayDeque<Integer> target_floors = new ArrayDeque<Integer>();
    public int status = 0; // 1 - лифт движется вверх, -1 - лифт едет вниз, 0 - лифт стоит на месте
    public int capacity; // максимальное количество пассажиров, которое может вмещать лифт: для грузового лифта - 8, для пассажирского - 4
    public List<Integer> passengers = new ArrayList<Integer>();
    public int passenger_status = 0; // 1 - пассажирам нужно наверх, 0 - нет пассажиров, -1 - пассажирам нужно вниз
    Elevator(int id, int cap)
        {
        ID = id;
        capacity = cap;
    }
}
