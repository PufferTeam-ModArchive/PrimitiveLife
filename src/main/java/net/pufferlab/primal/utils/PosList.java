package net.pufferlab.primal.utils;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

public class PosList {

    TLongList coords = new TLongArrayList();

    public void add(int x, int y, int z) {
        coords.add(HashUtils.packCoord(x, y, z));
    }

    public boolean contains(int x, int y, int z) {
        return coords.contains(HashUtils.packCoord(x, y, z));
    }

    public long get(int i) {
        return coords.get(i);
    }

    public int getX(int i) {
        return HashUtils.unpackX(get(i));
    }

    public int getY(int i) {
        return HashUtils.unpackY(get(i));
    }

    public int getZ(int i) {
        return HashUtils.unpackZ(get(i));
    }

    public int size() {
        return coords.size();
    }

    public TLongList values() {
        return coords;
    }
}
