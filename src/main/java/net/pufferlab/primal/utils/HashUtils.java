package net.pufferlab.primal.utils;

public class HashUtils {

    public static final int bx = 26; // 26 bits [-33,554,432 to 33,554,432]
    public static final int by = 12; // 12 bits [-2048 to 2048]
    public static final int bz = 26; // 26 bits [-33,554,432 to 33,554,432]

    public static final long xm = (1L << bx) - 1L;
    public static final long ym = (1L << by) - 1L;
    public static final long zm = (1L << bz) - 1L;

    public static final int bxs = bz + by;
    public static final int bys = bz;
    public static final int bzs = 0;

    public static final long cm = 0xFFFFFFFFL;
    public static final int czs = 32;

    public static long packCoord(int x, int y, int z) {

        long lx = x & xm;
        long ly = y & ym;
        long lz = z & zm;

        return (lx << bxs) | (ly << bys) | (lz << bzs);
    }

    public static long packChunkCoord(int x, int z) {

        return ((long) x & cm) | (((long) z & cm) << czs);
    }

    public static long packChunkCoord(long worldSeed, int x, int z) {
        long packed = packChunkCoord(x, z);

        long seed = hashWorldSeed(worldSeed, packed);

        return seed;
    }

    public static int unpackX(long packed) {

        return (int) (packed << (64 - bx - bxs) >> (64 - bx));
    }

    public static int unpackY(long packed) {

        return (int) (packed << (64 - by - bys) >> (64 - by));
    }

    public static int unpackZ(long packed) {

        return (int) (packed << (64 - bz - bzs) >> (64 - bz));
    }

    // Index Based Hashes
    public static int pack2DCoord(int x, int z) {
        return (z << 4) | x;
    }

    public static int pack3DCoord(int x, int y, int z) {
        return (x & 15) | ((y & 15) << 4) | ((z & 15) << 8);
    }

    public static int unpack3DX(int packed) {
        return packed & 15;
    }

    public static int unpack3DY(int packed) {
        return (packed >>> 4) & 15;
    }

    public static int unpack3DZ(int packed) {
        return (packed >>> 8) & 15;
    }

    public static long hashWorldSeed(long worldSeed, long packed) {
        long seed = worldSeed ^ packed;

        seed ^= seed >>> 33;
        seed *= 0xff51afd7ed558ccdL;
        seed ^= seed >>> 33;
        seed *= 0xc4ceb9fe1a85ec53L;
        seed ^= seed >>> 33;

        return seed;
    }

    public static int hashWorldSeed(long worldSeed, int packed) {
        long seed = worldSeed ^ (packed & 0xFFFFFFFFL);

        seed ^= seed >>> 33;
        seed *= 0xff51afd7ed558ccdL;
        seed ^= seed >>> 33;
        seed *= 0xc4ceb9fe1a85ec53L;
        seed ^= seed >>> 33;

        return Long.hashCode(seed);
    }

    public static int hashString(long worldSeed, String string) {
        int hashCode = string.hashCode();

        int seed = hashWorldSeed(worldSeed, hashCode);

        return seed;
    }

    // Float Hashes
    private static final float totalRange = 4.0f * (float) Math.PI;
    private static final int totalBuckets = 128;

    private static final float floatScale = totalBuckets / totalRange;

    public static int angleHashCode(float value) {
        return (int) (value * floatScale);
    }

    public static int getRGB(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    public static float getR(int color) {
        return ((color >> 16) & 0xFF) / 255f;
    }

    public static float getG(int color) {
        return ((color >> 8) & 0xFF) / 255f;
    }

    public static float getB(int color) {
        return (color & 0xFF) / 255f;
    }

    public static float getA(int color) {
        return ((color >> 24) & 0xFF) / 255f;
    }

    // Block
    public static int pack(short a, short b) {
        return ((a & 0xFFFF) << 16) | (b & 0xFFFF);
    }
}
