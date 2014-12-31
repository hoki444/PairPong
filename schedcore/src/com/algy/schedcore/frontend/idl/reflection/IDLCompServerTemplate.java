package com.algy.schedcore.frontend.idl.reflection;

import java.util.Map;

import com.algy.schedcore.BaseCompMgr;
import com.algy.schedcore.frontend.idl.IDLCompServerCreatorModifier;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.IDLValue;

public abstract class IDLCompServerTemplate extends IDLTemplateMixin {
    protected abstract BaseCompMgr create (IDLGameContext conetxt);
    protected abstract void modify (IDLGameContext conetxt, BaseCompMgr server);

    public final static IDLCompServerCreatorModifier
    makeCreatorModifier (final Class<? extends IDLCompServerTemplate> templateClass, 
                         final Class<? extends BaseCompMgr> associatedCompServerClass) {
        class CreatorModifierTemplate implements IDLCompServerCreatorModifier {
            private IDLCompServerTemplate getInstance (Map<String, IDLValue> dict, boolean useDefault) {
                IDLCompServerTemplate self = 
                        (IDLCompServerTemplate)IDLReflector.parse(IDLValue.createDict(dict), 
                                                                  templateClass, useDefault);
                return self;
            }

            @Override
            public Class<? extends BaseCompMgr> getType() {
                return associatedCompServerClass;
            }

            @Override
            public void modify(IDLGameContext context, BaseCompMgr server, Map<String, IDLValue> dict) {
                IDLCompServerTemplate instance = getInstance(dict, false);
                instance.modify(context, server);
            }

            @Override
            public BaseCompMgr create(IDLGameContext context, Map<String, IDLValue> dict) {
                IDLCompServerTemplate instance = getInstance(dict, true);
                return instance.create(context);
            }
        }
        return new CreatorModifierTemplate ();
    }
}
