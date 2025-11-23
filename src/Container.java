import java.util.ArrayList;

public class Container {

    int particleCount = 2000;
    ArrayList<Particle> particles = new ArrayList<Particle>(particleCount);

    double width = 100;
    double height = 100;
    double depth = 100;

    double meanInitialSpeed = 100;
    double particleMass = 1.0;

    int iterationsPerFrame = 3;

    double netMomentumImpartedSoFar = 0.0;
    long timeSinceLastPressureComputation = System.nanoTime();

    private double pressure = 0.0;
    private double volume = width * height * depth;

    public Container() {
        // spawn particles
        for(int i = 0; i < particleCount; i++) {
            Vector pPos = new Vector(Math.random() * width, Math.random() * height, Math.random() * depth);
            Vector pVel = new Vector(Math.random()*2.0*meanInitialSpeed, 0.0, 0.0);
            Particle p = new Particle(pPos, pVel, particleMass);
            particles.add(p);
        }
    }

    public void update(double deltaTime) {
        // repeat a bunch of times for good measure?
        double dt = deltaTime / iterationsPerFrame;
        for(int i = 0; i < iterationsPerFrame; i++) {

            // integrate positions
            for(int p = 0; p < particleCount; p++) {
                Particle particle = particles.get(p);
                particle.integrate(dt);
            }

            // detect collisions, calculate and apply impulses
            handleCollisions();
        }

        updatePressure();

        // log KE and check if its constant
        // System.out.println("KE: " + computeKineticEnergy());

        // System.out.println("AvgVel: " + computeAverageVelocity().toString(2));
        // System.out.println("RMS: " + computeRMSSpeed());
        // System.out.println("Pressure: " + getPressure());

        // Code tracking a specific particle
        // Particle p1 = particles.get(0);
        // System.out.println(p1.getPosition().toString() + " " + p1.getVelocity().toString());
    }

    private void handleCollisions() {
        // NOTE: ASSUMED ALL MASSES ARE EQUAL

        // particle-particle collisions
        for(int i = 0; i < particleCount - 1; i++) {
            for(int j = i + 1; j < particleCount; j++) {
                Particle p1 = particles.get(i);
                Particle p2 = particles.get(j);
                if(Particle.isColliding(p1, p2)) {
                    Vector normal = p1.getPosition().subtract(p2.getPosition()).normalize();

                    // separate particles
                    double distance = Particle.distance(p1, p2);
                    double epsilon = 1e-9;
                    double offset = epsilon + (p1.radius + p2.radius - distance)/2.0;
                    p1.setPosition(p1.getPosition().add(normal.scale(offset)));
                    p2.setPosition(p2.getPosition().add(normal.scale(-offset)));

                    // ChatGPT says I should recompute the normal otherwise some jitter stuff and KE increases or something
                    normal = p1.getPosition().subtract(p2.getPosition()).normalize();

                    Vector momentumDiff = p2.getMomentum().subtract(p1.getMomentum());

                    // speed of p1 in p2 frame . normal from p2 to p1 < 0, they must NOT be separating
                    // skip if n . relative velocity >= 0
                    // momentumDiff = -m * relative velocity
                    if(Vector.dot(normal, momentumDiff) <= 0) continue;

                    // elastic collisions, hence momentum along collision normal gets swapped
                    Vector impulse = normal.scale(momentumDiff.dot(normal));

                    p1.applyImpulse(impulse);
                    p2.applyImpulse(impulse.scale(-1.0));
                }
            }
        }

        // elastic collisions with walls
        

        for(int i = 0; i < particleCount; i++) {
            Particle p = particles.get(i);
            Vector pPos = p.getPosition();
            Vector pP = p.getMomentum();

            if(pPos.getX() > width) {
                p.setPosition(new Vector(width, pPos.getY(), pPos.getZ()));
                pPos = p.getPosition();
                if(pP.getX() > 0.0) {
                    p.applyImpulse(new Vector(-2.0 * pP.getX(), 0.0, 0.0));
                    netMomentumImpartedSoFar += 2.0 * pP.getX();
                }
            }
            if(pPos.getX() < 0.0) {
                p.setPosition(new Vector(0.0, pPos.getY(), pPos.getZ()));
                pPos = p.getPosition();
                if(pP.getX() < 0.0) {
                    p.applyImpulse(new Vector(-2.0 * pP.getX(), 0.0, 0.0));
                    netMomentumImpartedSoFar += 2.0 * pP.getX();
                }
            }

            if(pPos.getY() > height) {
                p.setPosition(new Vector(pPos.getX(), height, pPos.getZ()));
                pPos = p.getPosition();
                if(pP.getY() > 0.0) {
                    p.applyImpulse(new Vector(0.0, -2.0 * pP.getY(), 0.0));
                    netMomentumImpartedSoFar += 2.0 * pP.getY();
                }
            }
            if(pPos.getY() < 0.0) {
                p.setPosition(new Vector(pPos.getX(), 0.0, pPos.getZ()));
                pPos = p.getPosition();
                if(pP.getY() < 0.0) {
                    p.applyImpulse(new Vector(0.0, -2.0 * pP.getY(), 0.0));
                    netMomentumImpartedSoFar += 2.0 * pP.getY();
                }
            }

            if(pPos.getZ() > depth) {
                p.setPosition(new Vector(pPos.getX(), pPos.getY(), depth));
                pPos = p.getPosition();
                if(pP.getZ() > 0.0) {
                    p.applyImpulse(new Vector(0.0, 0.0, -2.0 * pP.getZ()));
                    netMomentumImpartedSoFar += 2.0 * pP.getZ();
                }
            }
            if(pPos.getZ() < 0.0) {
                p.setPosition(new Vector(pPos.getX(), pPos.getY(), 0.0));
                pPos = p.getPosition();
                if(pP.getZ() < 0.0) {
                    p.applyImpulse(new Vector(0.0, 0.0, -2.0 * pP.getZ()));
                    netMomentumImpartedSoFar += 2.0 * pP.getZ();
                }
            }
        }
    }

    public void updatePressure() {
        double area = 2.0*(width*height + height*depth + depth*width);
        long now = System.nanoTime();
        double dt = ((double) (now - timeSinceLastPressureComputation)) * 1e-9;
        timeSinceLastPressureComputation = now;
        pressure = netMomentumImpartedSoFar / (dt * area);
    }

    public double getVolume() { return volume; }
    
    public double getPressure() { return pressure; }

    public double computeTotalSquaredVelocity() {
        double v2sum = 0.0;
        for(int i = 0; i < particleCount; i++) {
            Particle p = particles.get(i);
            Vector vel = p.getVelocity();

            // magnitude of velocity squared
            double v2 = vel.getX()*vel.getX() + vel.getY()*vel.getY() + vel.getZ()*vel.getZ();

            v2sum += v2;
        }
        return v2sum;
    }

    public double computeKineticEnergy() {
        return computeTotalSquaredVelocity() * particleMass * 0.5;
    }

    public double computeRMSSpeed() {
        return Math.sqrt(computeTotalSquaredVelocity() / ((double) particleCount));
    }

    public Vector computeAverageVelocity() {
        Vector sum = new Vector(0.0, 0.0, 0.0);
        for(int i = 0; i < particleCount; i++) {
            Particle p = particles.get(i);
            Vector v = p.getVelocity();
            sum = sum.add(v);
        }
        sum = sum.scale(1.0/((double) particleCount));
        return sum;
    }
}
