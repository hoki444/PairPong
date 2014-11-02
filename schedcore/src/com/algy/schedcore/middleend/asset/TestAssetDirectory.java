package com.algy.schedcore.middleend.asset;

import java.util.Map.Entry;

public class TestAssetDirectory {
    public static void main(String [] args) {
        AssetDirectory<String> assetDirectory = new AssetDirectory<String>();
        
        assetDirectory.put("holly/xxxk", "NO SWEARING...", true);
        if(!assetDirectory.put("holly/land/billy", "Hi!", true))
            System.out.println("FFFF");
        if(!assetDirectory.put("holly/land/evan", "Hello!", true))
            System.out.println("GGG");
            
        
        for (Entry<String, String> entry : assetDirectory.entries()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        
        for (String obj : assetDirectory) {
            System.out.println(obj);
        }
    }
}
