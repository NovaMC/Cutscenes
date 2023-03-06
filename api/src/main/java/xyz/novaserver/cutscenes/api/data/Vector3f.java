package xyz.novaserver.cutscenes.api.data;

public class Vector3f implements Cloneable {
    private double x;
    private double y;
    private double z;

    public Vector3f(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(float x, float y, float z) {
        this(x, y, (double) z);
    }


    public Vector3f add(Vector3f other) {
        return add(other.x, other.y, other.z);
    }

    public Vector3f add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }


    public Vector3f subtract(Vector3f other) {
        return subtract(other.x, other.y, other.z);
    }

    public Vector3f subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }


    public double x() {
        return x;
    }

    public Vector3f x(double x) {
        this.x = x;
        return this;
    }

    public double y() {
        return y;
    }

    public Vector3f y(double y) {
        this.y = y;
        return this;
    }

    public double z() {
        return z;
    }

    public Vector3f z(double z) {
        this.z = z;
        return this;
    }

    @Override
    public Vector3f clone() {
        try {
            return (Vector3f) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    @Override
    public String toString() {
        return "Vector3f{" + x +
                ", " + y +
                ", " + z +
                '}';
    }
}
