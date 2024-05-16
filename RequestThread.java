import java.util.Random;

public class RequestThread implements Runnable {
    private ElevatorManager manager;
    private int request_count;
    RequestThread(ElevatorManager manage, int count)
    {
        manager = manage;
        request_count = count;
    }

    @Override
    public void run()
    {
        Random r = new Random();
        for (int i = 0; i < request_count; i++)
        {
            int start_floor = r.nextInt(manager.floor_count);
            int end_floor = r.nextInt(manager.floor_count);
            while(start_floor == end_floor)
            {
                end_floor = r.nextInt(manager.floor_count);
            }
            int direction = (start_floor < end_floor ? 1 : -1);
            this.manager.add_passenger(new Request(start_floor, end_floor, direction));
            try
            {
                Thread.sleep(1000); //интервал между запросами равен 1 секунде
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            ElevatorManager.stop_flag = true;
        }
    }
}
