package pl.lodz.p;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        try {
            GameOfLifeApp.main(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

