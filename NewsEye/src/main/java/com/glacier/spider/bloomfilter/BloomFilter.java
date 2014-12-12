package com.glacier.spider.bloomfilter;

import java.util.BitSet;

/**
 * Created by glacier on 14-12-12.
 */
public class BloomFilter {
    private BitSet bitSet;
    private int bitSetSize, elementNumber;
    private int[] hashSeed = {7,11,13,19,31,37};
    private int addCount = 0;

    public BloomFilter(int elementNumber) {
        this.elementNumber = elementNumber;
        this.bitSetSize = elementNumber * 100;
        bitSet = new BitSet();

    }

    public boolean add(String value) {
        try {
            for (int i = 0; i < hashSeed.length; i++) {
                long key = hashKey(value, i);

                long key1 = (key >> 32) & 0xfffffff;
                long key2 = key & 0xfffffff;

                setBit(key1 % bitSetSize);
                setBit(key2 % bitSetSize);
            }
            addCount ++;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean contains(String value) {
        for ( int i = 0; i < hashSeed.length; i ++ ) {
            long key = hashKey(value, i);

            long key1 = (key >> 32) & 0xfffffff;
            long key2 = key & 0xfffffff;

            if ( getBit(key1) == false )    return false;
            if ( getBit(key2) == false )    return false;
        }
        return true;
    }

    public int getAddCount() {  return addCount;    }

    private long hashKey(String value, int index) {
        long key = 0;
        for (int i = 0; i < value.length(); i ++) {
            key = key * hashSeed[index] + value.charAt(i);
        }
        return key;
    }

    private void setBit(long key) {
        bitSet.set(new Integer(String.valueOf(key)), true);
    }

    private boolean getBit(long key) {
        return bitSet.get(new Integer(String.valueOf(key)));
    }

}
