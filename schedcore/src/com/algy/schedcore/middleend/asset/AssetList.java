package com.algy.schedcore.middleend.asset;

import java.util.Iterator;
import java.util.TreeMap;

public class AssetList implements Iterable<AssetSig> {
    private TreeMap<String, AssetSig> sigMap = new TreeMap<String, AssetSig>();
    
    public AssetList () {
    }
    
    public void clear() {
        this.sigMap.clear();
    }

    public boolean has(String assetName) {
        return this.sigMap.containsKey(assetName);
    }

    public AssetSig get(String assetName) {
        return this.sigMap.get(assetName);
    }

    public AssetSig add(AssetSig value) {
        return this.sigMap.put(value.assetName, value);
    }

    public AssetSig add(String fileName, Class<?> type) {
        AssetSig sig = new AssetSig(fileName, type);
        return this.add(sig);
    }
    

    public void update(AssetList other) {
        for (AssetSig otherSig : other) {
            this.add(otherSig);
        }
    }

    public AssetSig remove(Object key) {
        return this.sigMap.remove(key);
    }

    @Override
    public Iterator<AssetSig> iterator() {
        return sigMap.values().iterator();
    }
    
    public Iterable<String> assetNames () {
        return this.sigMap.keySet();
    }
    
    /* operations as set  */
    public AssetList difference(AssetList other) {
        AssetList result = new AssetList();
        for (AssetSig thisSig : this) {
            if (!other.has(thisSig.assetName)) {
                result.add(thisSig);
            }
        }
        return result;
    }
    
    
    public AssetList intersection(AssetList other) {
        AssetList result = new AssetList();
        for (AssetSig thisSig : this) {
            if (other.has(thisSig.assetName)) {
                result.add(thisSig);
            }
        }
        return result;
    }
    
    public AssetList union(AssetList other) {
        AssetList result = new AssetList();
        for (AssetSig thisSig : this) {
            result.add(thisSig);
        }
        for (AssetSig otherSig : other) {
            result.add(otherSig);
        }
        return result;
    }
    
    
    public AssetList symmetricDifference(AssetList other) {
        AssetList result = new AssetList();
        for (AssetSig thisSig : this) {
            if (!other.has(thisSig.assetName))
                result.add(thisSig);
        }
        for (AssetSig otherSig : other) {
            if (!this.has(otherSig.assetName))
                result.add(otherSig);
        }
        return result;
    }
}