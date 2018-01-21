package snowMachine;

public class Grid {
    public int rows;
    public int columns;
    private int total;

    public void update() {
        total = rows * columns;
    }

    public int getTotal() {
        return total;
    }
}
