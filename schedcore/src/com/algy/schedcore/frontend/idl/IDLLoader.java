package com.algy.schedcore.frontend.idl;

import java.util.ArrayList;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.Item;
import com.algy.schedcore.frontend.idl.IDLParser.CompDescriptor;
import com.algy.schedcore.middleend.GameItem;
import com.algy.schedcore.middleend.Transform;
import com.algy.schedcore.middleend.asset.AssetDirectory;
import com.algy.schedcore.middleend.asset.Eden;
import com.algy.schedcore.util.Pair;
import com.badlogic.gdx.files.FileHandle;

public class IDLLoader {
    private static AssetDirectory<IDLCompLoader> compLoaders = new AssetDirectory<IDLCompLoader>();
    private static AssetDirectory<IDLCompServerLoader> compServerLoaders = new AssetDirectory<IDLCompServerLoader>();
    
    
    static {
        // TODO
    }
    
    static GameItem fromDescList (IDLParser parser, 
            ArrayList<CompDescriptor> creationList) {
        GameItem gameItem = new GameItem();

        for (CompDescriptor desc : creationList) {
            IDLCompLoader loader = IDLLoader.assetGetCompLoader(desc.compName);
            BaseComp comp = loader.load(desc.dict);
            if (comp instanceof Transform) {
                gameItem.remove(Transform.class);
                gameItem.add(comp);
            } else {
                gameItem.add(comp);
            }
        }
        return gameItem;
    }

    public synchronized static boolean hasCompLoader (String assetName) {
        return compLoaders.has(assetName);
    }
    
    public synchronized static boolean hasServerLoader (String assetName) {
        return compServerLoaders.has(assetName);

    }
    
    public synchronized static IDLCompLoader assetGetCompLoader (String assetName) {
        IDLCompLoader result = getCompLoader (assetName);
        if (result == null) {
            throw new IDLNameError(assetName + " is not found");
        }
        return result;
    }

    public synchronized static IDLCompServerLoader assertGetCompServerLoader (String assetName) {
        IDLCompServerLoader result = getCompServerLoader (assetName);
        
        if (result == null) {
            throw new IDLNameError(assetName + " is not found");
        }
        return result;
    }
    
    public synchronized static IDLCompLoader getCompLoader (String assetName) {
        IDLCompLoader result = compLoaders.get(assetName);
        return result;
    }

    public synchronized static IDLCompServerLoader getCompServerLoader (String assetName) {
        IDLCompServerLoader result = compServerLoaders.get(assetName);
        return result;
    }
    
    public synchronized static void registerCompLoader (IDLCompLoader loader) {
        String assetName = loader.assetName;
        if (compLoaders.has(assetName)) {
            throw new IDLNameError("Tried to register " + assetName + " as a IDL component loader, " +
                                   "But already have loader with the same name");
                           
        } else {
            compLoaders.put(assetName, loader, true);
        }
    }
    
    public synchronized static void registerCompServerLoader (IDLCompServerLoader loader) {
        String assetName = loader.assetName;
        if (compServerLoaders.has(assetName)) {
            throw new IDLNameError("Tried to register " + assetName + " as a IDL component server loader, " +
                                   "But already have loader with the same name");
                           
        } else {
            compServerLoaders.put(assetName, loader, true);
        }
    }
    
    
    
    public synchronized static Eden loadItemDef (FileHandle fileHandle) {
        return loadItemDef (fileHandle.readString());
    }
    public synchronized static Eden loadItemDef (String source) {
        IDLParserForDef parser = new IDLParserForDef(source);
        try {
            parser.parseItemLang();
        } catch (Throwable t) {
            throw new IDLLoadError(t);
        }
        return parser.getResult();
    }
    
    public synchronized static
    Pair<ArrayList<GameItem>, Item<BaseCompServer, Object>> 
    loadScene (FileHandle fileHandle, Eden eden, Item<BaseCompServer, ?> serverItem) {
        return loadScene (fileHandle.readString(), eden, serverItem);
    }
   
    @SuppressWarnings("unchecked")
    public synchronized static
    Pair<ArrayList<GameItem>, Item<BaseCompServer, Object>> 
    loadScene (String source, Eden eden, Item<BaseCompServer, ?> serverItem) {
        IDLParserForScene parser;
        if (serverItem == null)
            parser = new IDLParserForScene(source, eden);
        else
            parser = new IDLParserForScene(source, eden, (Item<BaseCompServer, Object>)serverItem);
        try {
            parser.parseSceneLang();
        } catch (Throwable t) {
            throw new IDLLoadError(t);
        }
        return parser.getResult();
    }
}
