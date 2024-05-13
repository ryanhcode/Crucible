package dev.foundry.crucible.fog;

import net.minecraft.util.Mth;
import org.joml.Vector3d;

public class JomlCubicSampler {

    private static final int GAUSSIAN_SAMPLE_RADIUS = 2;
    private static final int GAUSSIAN_SAMPLE_BREADTH = 6;
    private static final double[] GAUSSIAN_SAMPLE_KERNEL = new double[]{0.0, 1.0, 4.0, 6.0, 4.0, 1.0, 0.0};
    private static final Vector3d TEMP = new Vector3d();

    private JomlCubicSampler() {
    }

    public static Vector3d gaussianSampleVec3(Vector3d vec, JomlVec3Fetcher fetcher) {
        int i = Mth.floor(vec.x);
        int j = Mth.floor(vec.y);
        int k = Mth.floor(vec.z);
        double d = vec.x - (double) i;
        double e = vec.y - (double) j;
        double f = vec.z - (double) k;
        double sum = 0.0;
        vec.set(0.0);

        for (int l = 0; l < GAUSSIAN_SAMPLE_BREADTH; ++l) {
            double h = Mth.lerp(d, GAUSSIAN_SAMPLE_KERNEL[l + 1], GAUSSIAN_SAMPLE_KERNEL[l]);
            int m = i - GAUSSIAN_SAMPLE_RADIUS + l;

            for (int n = 0; n < GAUSSIAN_SAMPLE_BREADTH; ++n) {
                double o = Mth.lerp(e, GAUSSIAN_SAMPLE_KERNEL[n + 1], GAUSSIAN_SAMPLE_KERNEL[n]);
                int p = j - GAUSSIAN_SAMPLE_RADIUS + n;

                for (int q = 0; q < GAUSSIAN_SAMPLE_BREADTH; ++q) {
                    double r = Mth.lerp(f, GAUSSIAN_SAMPLE_KERNEL[q + 1], GAUSSIAN_SAMPLE_KERNEL[q]);
                    int s = k - GAUSSIAN_SAMPLE_RADIUS + q;
                    double t = h * o * r;
                    sum += t;
                    fetcher.fetch(m, p, s, TEMP);
                    vec.add(TEMP.x * t, TEMP.y * t, TEMP.z * t);
                }
            }
        }

        return vec.div(sum);
    }

    @FunctionalInterface
    public interface JomlVec3Fetcher {

        void fetch(int x, int y, int z, Vector3d store);
    }
}
