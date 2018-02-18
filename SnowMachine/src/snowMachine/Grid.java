package snowMachine;

public class Grid {
    public int rows = 3;
    public int columns = 3;
    private int total;

    public void update() {
        total = rows * columns;
    }

    public int getTotal() {
        return total;
    }

    Grid() {
        update();
    }
}
