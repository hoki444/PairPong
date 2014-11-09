package com.algy.schedcore.frontend.idl;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.BaseCompServer;
import com.algy.schedcore.frontend.idl.reflection.IDLCompServerTemplate;
import com.algy.schedcore.frontend.idl.reflection.IDLCompTemplate;
import com.algy.schedcore.frontend.idl.reflection.templates.BtDetectorTemplate;
import com.algy.schedcore.frontend.idl.reflection.templates.BtPhysicsWorldTemplate;
import com.algy.schedcore.frontend.idl.reflection.templates.BtRigidBodyTemplate;
import com.algy.schedcore.frontend.idl.reflection.templates.CameraServerTemplate;
import com.algy.schedcore.frontend.idl.reflection.templates.LightCompTemplate;
import com.algy.schedcore.frontend.idl.reflection.templates.SimpleModelCompTemplate;
import com.algy.schedcore.frontend.idl.reflection.templates.TransformTemplate;
import com.algy.schedcore.middleend.CameraServer;
import com.algy.schedcore.middleend.LightComp;
import com.algy.schedcore.middleend.ModelFactoryComp;
import com.algy.schedcore.middleend.Transform;
import com.algy.schedcore.middleend.bullet.BtDetectorComp;
import com.algy.schedcore.middleend.bullet.BtPhysicsWorld;
import com.algy.schedcore.middleend.bullet.BtRigidBodyComp;
import com.algy.schedcore.util.ObjectDirectory;

public class IDLLoader {
    private static ObjectDirectory<IDLCompLoader> compLoaders = new ObjectDirectory<IDLCompLoader>();
    private static ObjectDirectory<IDLCompServerLoader> compServerLoaders = new ObjectDirectory<IDLCompServerLoader>();
    
    
    static {
        registerCompTemplate("transform", TransformTemplate.class, Transform.class);
        registerCompTemplate("light", LightCompTemplate.class, LightComp.class);
        registerCompTemplate("bullet/detector", BtDetectorTemplate.class, BtDetectorComp.class);
        registerCompTemplate("bullet/rigidBody", BtRigidBodyTemplate.class, BtRigidBodyComp.class);
        registerCompTemplate("simpleModel", SimpleModelCompTemplate.class, ModelFactoryComp.class);
        registerCompServerTemplate("camera", CameraServerTemplate.class, CameraServer.class);
        registerCompServerTemplate("bullet/world", 
                                   BtPhysicsWorldTemplate.class, 
                                   BtPhysicsWorld.class);
    }
    

    public synchronized static 
    boolean hasCompLoader (String assetName) {
        return compLoaders.has(assetName);
    }
    
    public synchronized static 
    boolean hasServerLoader (String assetName) {
        return compServerLoaders.has(assetName);

    }
    
    public synchronized static 
    IDLCompLoader assetGetCompLoader (String assetName) {
        IDLCompLoader result = getCompLoader (assetName);
        if (result == null) {
            throw new IDLNameError(assetName + " is not found");
        }
        return result;
    }

    public synchronized static 
    IDLCompServerLoader assertGetCompServerLoader (String assetName) {
        IDLCompServerLoader result = getCompServerLoader (assetName);
        
        if (result == null) {
            throw new IDLNameError(assetName + " is not found");
        }
        return result;
    }
    
    public synchronized static 
    IDLCompLoader getCompLoader (String assetName) {
        IDLCompLoader result = compLoaders.get(assetName);
        return result;
    }

    public synchronized static 
    IDLCompServerLoader getCompServerLoader (String assetName) {
        IDLCompServerLoader result = compServerLoaders.get(assetName);
        return result;
    }
    
    public synchronized static void 
    registerCompTemplate (String name, 
                          Class<? extends IDLCompTemplate> templateClass,
                          Class<? extends BaseComp> associatedType) {
        IDLCompCreatorModifier cm = 
                IDLCompTemplate.makeCreatorModifier(templateClass, associatedType);
        registerCompLoader(new IDLCompLoader(name, cm, cm));
    }

    public synchronized static void 
    registerCompServerTemplate (String name, 
                                Class<? extends IDLCompServerTemplate> templateClass,
                                Class<? extends BaseCompServer> associatedType) {
        IDLCompServerCreatorModifier cm = 
                IDLCompServerTemplate.makeCreatorModifier(templateClass, associatedType);
        registerCompServerLoader(new IDLCompServerLoader(name, cm, cm));
    }
    
    
    public synchronized static 
    void registerCompLoader (IDLCompLoader loader) {
        String assetName = loader.assetName;
        if (compLoaders.has(assetName)) {
            throw new IDLNameError("Tried to register " + assetName + " as a IDL component loader, " +
                                   "But already have loader with the same name");
                           
        } else {
            compLoaders.put(assetName, loader, true);
        }
    }
    
    public synchronized static 
    void registerCompServerLoader (IDLCompServerLoader loader) {
        String compServerName = loader.compServerName;
        if (compServerLoaders.has(compServerName)) {
            throw new IDLNameError("Tried to register " + compServerName + " as a IDL component server loader, " +
                                   "But already have loader with the same name");
                           
        } else {
            compServerLoaders.put(compServerName, loader, true);
        }
    }
    
    
    public synchronized static 
    IDLResult loadItemDef (String source, IDLGameContext context) {
        IDLParserForDef parser = new IDLParserForDef(source, context);
        try {
            parser.parseItemLang();
        } catch (Throwable t) {
            throw new IDLLoadError(t);
        }
        return parser.getResult();
    }
    
   
    public synchronized static IDLResult
    loadScene (String source, IDLGameContext context) {
        IDLParserForScene parser;
        parser = new IDLParserForScene(source, context);
        try {
            parser.parseSceneLang();
        } catch (Throwable t) {
            throw new IDLLoadError(t);
        }
        return parser.getResult();
    }
    
    public synchronized static IDLResult
    modifyScene (String source, IDLGameContext context) {
        IDLParserForMod parser;
        parser = new IDLParserForMod(source, context);
        try {
            parser.parseModLang();
        } catch (Throwable t) {
            throw new IDLLoadError(t);
        }
        return parser.getResult();
    }
}