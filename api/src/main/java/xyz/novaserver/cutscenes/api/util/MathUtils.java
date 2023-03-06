package xyz.novaserver.cutscenes.api.util;

import xyz.novaserver.cutscenes.api.data.Vector3f;

public class MathUtils {

    // Based on code from Wikipedia and adapted for use here
    // https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles#Source_code_2
    public static Vector3f quaternionToVector(double x, double y, double z, double w) {
        // roll (x-axis rotation)
        double sinr_cosp = -2 * (w * z + x * y);
        double cosr_cosp = 1 - 2 * (z * z + x * x);
        double roll_rad = Math.atan2(sinr_cosp, cosr_cosp);
        double roll_deg = Math.toDegrees(roll_rad);

        // pitch (y-axis rotation)
        double pitch_rad;
        double sinp = -2 * (w * x - y * z);
        if (Math.abs(sinp) >= 1)
            pitch_rad = Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else
            pitch_rad = Math.asin(sinp);
        double pitch_deg = Math.toDegrees(pitch_rad);

        // yaw (z-axis rotation)
        double siny_cosp = 2 * (w * y + z * x);
        double cosy_cosp = 1 - 2 * (x * x + y * y);
        double yaw_rad = Math.atan2(siny_cosp, cosy_cosp);
        if (yaw_rad > 0.0) {
            yaw_rad = Math.toRadians(180) - yaw_rad;
        } else {
            yaw_rad = -Math.toRadians(180) - yaw_rad;
        }
        double yaw_deg = Math.toDegrees(yaw_rad);

        return new Vector3f(pitch_deg, yaw_deg, roll_deg);
    }

}
