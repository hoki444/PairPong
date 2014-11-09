package com.algy.schedcore.frontend.idl.reflection;

import java.util.Map;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.frontend.idl.IDLCompCreatorModifier;
import com.algy.schedcore.frontend.idl.IDLGameContext;
import com.algy.schedcore.frontend.idl.IDLValue;

public abstract class IDLCompTemplate extends IDLTemplateMixin {
    protected abstract BaseComp create (IDLGameContext context);
    protected abstract void modify (IDLGameContext context, BaseComp comp);

    public final static IDLCompCreatorModifier 
    makeCreatorModifier (final Class<? extends IDLCompTemplate> templateClass, 
                         final Class<? extends BaseComp> associatedCompClass) {
        class CreatorModifierTemplate implements IDLCompCreatorModifier {
            private IDLCompTemplate getInstance (Map<String, IDLValue> dict, boolean useDefault) {
                IDLCompTemplate self = (IDLCompTemplate)IDLReflector.parse(IDLValue.createDict(dict), 
                                                                           templateClass, useDefault);
                return self;
            }

            @Override
            public Class<? extends BaseComp> getType() {
                return associatedCompClass;
            }

            @Override
            public void modify(IDLGameContext context, BaseComp freshComp, Map<String, IDLValue> dict) {
                IDLCompTemplate instance = getInstance(dict, false);
                instance.modify(context, freshComp);
            }

            @Override
            public BaseComp create(IDLGameContext context, Map<String, IDLValue> dict) {
                IDLCompTemplate instance = getInstance(dict, true);
                return instance.create(context);
            }
        }
        return new CreatorModifierTemplate ();
    }
}