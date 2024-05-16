public class ElevatorManagerThread implements Runnable {
    private ElevatorManager manager;

    ElevatorManagerThread(ElevatorManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void run()
    {
        while (!ElevatorManager.stop_flag || !(manager.first.target_floors.isEmpty() && manager.second.target_floors.isEmpty())) {
            manager.floor_passed();
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}