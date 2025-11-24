import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Simulation {

    Container container = new Container();

    final int EXPERIMENT_NUMBER = 0; // update this every time
 
    PrintWriter observations;

    long startTime;

    public Simulation() {

        startTime = System.nanoTime();

        try {
            observations = new PrintWriter(new FileWriter("output/experiment_"+EXPERIMENT_NUMBER+".csv"));
        } catch (IOException e) {
            throw new RuntimeException(e); // or handle differently
        }

        observations.printf("experiment,%d\n", EXPERIMENT_NUMBER);
        observations.printf("particleCount,%d\n", container.particleCount);
        observations.printf("particleMass,%f\n", container.particleMass);
        observations.printf("volume,%f\n\n", container.getVolume());

        observations.println("frame,time,pressure,RMSSpeed,vXAvg,vYAvg,vZAvg");

        Thread loop = new Thread(this::loop);
        loop.start();
    }

    final long logInterval = 20;
    final long numLogs = 100;

    public void loop() {
        long last = System.nanoTime();

        for(int frame = 0; frame < logInterval*numLogs; frame++) {
            long now = System.nanoTime();
            double deltaTime = (now - last) * 1e-9; // seconds
            last = now;

            // cap dt to avoid jumps if debugger pauses
            if (deltaTime > 0.05) deltaTime = 0.05;

            container.update(deltaTime);

            // output to CSV
            if(frame % logInterval == 0) {
                Vector avgVel = container.computeAverageVelocity();
                observations.printf("%d,%f,%f,%f,%f,%f,%f,\n", 
                    frame, 
                    ((double) (now - startTime) )*1e-9, 
                    container.getPressure(),
                    container.computeRMSSpeed(),
                    avgVel.getX(),
                    avgVel.getY(),
                    avgVel.getZ()
                );
            }

            // sleep ~1ms so CPU isn't at 100%
            // try { Thread.sleep(1); } catch (Exception ignored) {}
        }

        double histogramWidth = 10.0;
        int[] histogram = container.computeSpeedHistogram(histogramWidth);
        observations.printf("\nhistogramWidth,%f\nhistogram,", histogramWidth);
        // System.out.println(Simulation.join(histogram, ","));
        observations.printf(Simulation.join(histogram, ",") + "\n");

        observations.flush();
        observations.close();
        System.out.println("praise Boltzmann! the experiment is a success!");
        System.exit(0);
    }

    public static void main(String[] args) {
        System.out.println("thus, our experiment commences...");

        new Simulation();
    }

    public static String join(int[] arr, String sep) {
    if (arr.length == 0) return "";
    StringBuilder sb = new StringBuilder();

    sb.append(arr[0]);
    for (int i = 1; i < arr.length; i++) {
        sb.append(sep).append(arr[i]);
    }
    return sb.toString();
}
}