import java.util.ArrayList;

public class Container {

    int particleCount = 100;
    ArrayList<Particle> particles = new ArrayList<Particle>(particleCount);

    double width = 800;
    double height = 800;
    double depth = 800;

    double meanInitialSpeed = 100;

    int iterationsPerFrame = 3;

    public Container() {
        // spawn particles
        for(int i = 0; i < particleCount; i++) {
            Vector pPos = new Vector(Math.random() * width, Math.random() * height, Math.random() * depth);
            Vector pVel = new Vector(Math.random()*2.0*meanInitialSpeed, 0.0, 0.0);
            Particle p = new Particle(pPos, pVel);
            particles.add(p);
        }
    }

    public void update(double deltaTime) {
        // repeat a bunch of times for good measure?
        double dt = deltaTime / iterationsPerFrame;
        for(int i = 0; i < iterationsPerFrame; i++) {
            // detect collisions, calculate and apply impulses
            handleCollisions();
            // integrate positions
            for(int p = 0; p < particleCount; p++) {
                Particle particle = particles.get(p);
                particle.integrate(dt);
            }
        }
    }

    private void handleCollisions() {
        // particle-particle collisions
        for(int i = 0; i < particleCount - 1; i++) {
            for(int j = i + 1; j < particleCount; j++) {
                Particle p1 = particles.get(i);
                Particle p2 = particles.get(j);
                if(Particle.isColliding(p1, p2)) {
                    // elastic collisions, hence momentum along collision normal gets swapped
                    Vector normal = p1.getPosition().subtract(p2.getPosition()).normalize();
                    double impulseMagnitude = p2.getMomentum().subtract(p1.getMomentum()).dot(normal);
                    Vector impulse = normal.scale(impulseMagnitude);

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

            if(pPos.getX() >= width) {
                p.setPosition(new Vector(width, pPos.getY(), pPos.getZ()));
                if(pP.getX() > 0.0) {
                    p.applyImpulse(new Vector(-2.0 * pP.getX(), pP.getY(), pP.getZ()));
                }
            }
            if(pPos.getX() <= 0.0) {
                p.setPosition(new Vector(0.0, pPos.getY(), pPos.getZ()));
                if(pP.getX() < 0.0) {
                    p.applyImpulse(new Vector(-2.0 * pP.getX(), pP.getY(), pP.getZ()));
                }
            }

            if(pPos.getY() >= height) {
                p.setPosition(new Vector(pPos.getX(), height, pPos.getZ()));
                if(pP.getY() > 0.0) {
                    p.applyImpulse(new Vector(pP.getX(), -2.0 * pP.getY(), pP.getZ()));
                }
            }
            if(pPos.getY() <= 0.0) {
                p.setPosition(new Vector(pPos.getX(), 0.0, pPos.getZ()));
                if(pP.getY() < 0.0) {
                    p.applyImpulse(new Vector(pP.getX(), -2.0 * pP.getY(), pP.getZ()));
                }
            }

            if(pPos.getZ() >= depth) {
                p.setPosition(new Vector(pPos.getX(), pPos.getY(), depth));
                if(pP.getZ() > 0.0) {
                    p.applyImpulse(new Vector(pP.getX(), pP.getY(), -2.0 * pP.getZ()));
                }
            }
            if(pPos.getZ() <= 0.0) {
                p.setPosition(new Vector(pPos.getX(), pPos.getY(), 0.0));
                if(pP.getX() < 0.0) {
                    p.applyImpulse(new Vector(pP.getX(), pP.getY(), -2.0 * pP.getZ()));
                }
            }
        }
    }
}
