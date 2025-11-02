public class Simulation {

    Container container = new Container();

    public Simulation() {
        Thread loop = new Thread(this::loop);
        loop.start();
    }

    public void loop() {
        long last = System.nanoTime();

        while (true) {
            long now = System.nanoTime();
            double deltaTime = (now - last) / 1_000_000_000.0; // seconds
            last = now;

            // cap dt to avoid jumps if debugger pauses
            if (deltaTime > 0.05) deltaTime = 0.05;

            container.update(deltaTime);

            // sleep ~1ms so CPU isn't at 100%
            try { Thread.sleep(1); } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
        new Simulation();
    }
}