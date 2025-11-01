public class Vector {
    public final double x;
    public final double y;
    public final double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public static Vector add(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vector scale(double c, Vector a) {
        return new Vector(c*a.x, c*a.y, c*a.z);
    }

    public static Vector subtract(Vector a, Vector b) {
        // a - b
        return Vector.add(a, Vector.scale(-1.0, b));
    }

    public static double magnitude(Vector a) {
        return Math.sqrt(a.x*a.x + a.y*a.y + a.z*a.z);
    }

    public static Vector normalize(Vector a) {
        return a.scale(1.0/a.magnitude());
    }

    public static double dot(Vector a, Vector b) {
        return a.x*b.x + a.y*b.y + a.z*b.z;
    }

    public Vector add(Vector b) {
        return Vector.add(this, b);
    }

    public Vector scale(double c) {
        return Vector.scale(c, this);
    }

    public Vector subtract(Vector b) {
        return Vector.subtract(this, b);
    }

    public double magnitude() {
        return Vector.magnitude(this);
    }

    public Vector normalize() {
        return this.scale(1.0/this.magnitude());
    }

    public double dot(Vector b) {
        return Vector.dot(this, b);
    }
}
