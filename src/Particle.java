public class Particle {
    private Vector position;
    private Vector velocity;
    final double mass = 1.0;
    final double reciprocalMass = 1.0/mass;
    final double radius = 1.0;

    public Particle(Vector initialPosition, Vector initialVelocity) {
        this.position = initialPosition;
        this.velocity = initialVelocity;
    }

    public Vector getPosition() {
        return position;
    }
    public void setPosition(Vector newPosition) {
        this.position = newPosition;
    }

    public Vector getVelocity() {
        return velocity;
    }

    

    public Vector getMomentum() {
        return velocity.scale(mass);
    }

    public void applyImpulse(Vector impulse) {
        velocity = velocity.add(impulse.scale(reciprocalMass));
    }

    public void integrate(double timestep) {
        position = position.add(velocity);
    }

    public static boolean isColliding(Particle p1, Particle p2) {
        return p1.position.subtract(p2.position).magnitude() <= (p1.radius + p2.radius);
    }
}
