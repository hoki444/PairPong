package com.algy.schedcore.frontend.idl.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import com.algy.schedcore.frontend.idl.IDLValue;
import com.algy.schedcore.frontend.idl.IDLValue.Type;
import com.algy.schedcore.frontend.idl.reflection.templates.AssetSignatureObjTemplate;
import com.algy.schedcore.frontend.idl.reflection.templates.Matrix4ObjTemplate;
import com.algy.schedcore.frontend.idl.reflection.templates.QuaternionObjTemplate;
import com.algy.schedcore.middleend.asset.AssetSig;
import com.algy.schedcore.util.Pair;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;

class BooleanPrimReflector implements IDLPrimitiveReflector {
    @Override
    public void parseAndSet(IDLValue value, Field field, Object obj) {
        try {
            field.setBoolean(obj, value.asBoolean());
        } catch (IllegalArgumentException e) {
            throw new IDLReflectionError(e);
        } catch (IllegalAccessException e) {
            throw new IDLReflectionError(e);
        }
    }
}
class BooleanBoxReflector implements IDLObjectReflector {
    @Override
    public Object parse(IDLValue value, Class<?> type) {
        return value.asBoolean();
    }
}

class IntegerPrimCoercedReflector implements IDLPrimitiveReflector {
    @Override
    public void parseAndSet(IDLValue value, Field field, Object obj) {
        Class<?> type = field.getType();
        long idlValue = value.asInteger();
        try {
            if (type.equals(short.class)) {
                field.setShort(obj, (short)idlValue);
            } else if (type.equals(byte.class)) {
                field.setByte(obj, (byte)idlValue);
            } else if (type.equals(int.class)) {
                field.setInt(obj, (int)idlValue);
            } else if (type.equals(long.class)) {
                field.setLong(obj, (long)idlValue);
            } else if (type.equals(float.class)) {
                field.setFloat(obj, (float)idlValue);
            } else if (type.equals(double.class)) {
                field.setDouble(obj, (double)idlValue);
            } 
        } catch (IllegalArgumentException e) {
            throw new IDLReflectionError(e);
        } catch (IllegalAccessException e) {
            throw new IDLReflectionError(e);
        }
    }
}

class IntegerBoxCoercedReflector implements IDLObjectReflector {
    @Override
    public Object parse(IDLValue value, Class<?> type) {
        long idlValue = value.asInteger();
        if (type.equals(Short.class)) {
            return (short)idlValue;
        } else if (type.equals(Byte.class)) {
            return (byte)idlValue;
        } else if (type.equals(Integer.class)) {
            return (int)idlValue;
        } else if (type.equals(Long.class)) {
            return (long)idlValue;
        } else if (type.equals(Float.class)) {
            return (float)idlValue;
        } else if (type.equals(Double.class)) {
            return (double)idlValue;
        } 
        return null;
    }
}

class FloatPrimCoercedReflector implements IDLPrimitiveReflector {
    @Override
    public void parseAndSet(IDLValue value, Field field, Object obj) {
        Class<?> type = field.getType();
        float idlValue = value.asFloat();
        try {
            if (type.equals(float.class)) {
                field.setFloat(obj, (float)idlValue);
            } else if (type.equals(double.class)) {
                field.setDouble(obj, (double)idlValue);
            } 
        } catch (IllegalArgumentException e) {
            throw new IDLReflectionError(e);
        } catch (IllegalAccessException e) {
            throw new IDLReflectionError(e);
        }
    }
}

class FloatBoxCoercedReflector implements IDLObjectReflector {
    @Override
    public Object parse(IDLValue value, Class<?> type) {
        float idlValue = value.asFloat();
        if (type.equals(Float.class)) {
            return (float)idlValue;
        } else if (type.equals(Double.class)) {
            return (double)idlValue;
        } 
        return null;
    }
}

class StringReflector implements IDLObjectReflector {
    @Override
    public Object parse(IDLValue value, Class<?> cls) {
        return value.asString();
    }
}

class ClassReflector implements IDLObjectReflector {

    @Override
    public Object parse(IDLValue value, Class<?> cls) {
        try {
            Class<?> result = Class.forName(value.asClassName());
            return result;
        } catch (ClassNotFoundException e) {
            throw new IDLReflectionError(e);
        }
    }
}



@SuppressWarnings("rawtypes")
public class IDLReflector {
    private static HashMap<Pair<IDLValue.Type, Class>, IDLPrimitiveReflector> primRefls;
    private static HashMap<Pair<IDLValue.Type, Class>, IDLObjectReflector> defaultRefls;
    
    public synchronized static void registerReflector (IDLObjectReflector refl,
                                                       IDLValue.Type idlType, 
                                                       Class<?> destType) {
        defaultRefls.put(Pair.cons(idlType, (Class)destType), refl);
    }
    public synchronized static void registerTemplate (Class<? extends IDLObjectReflectorTemplate> templateClass,
                                                      Class<?> destType) {
        defaultRefls.put(Pair.cons(Type.Dict, (Class)destType), 
                         IDLObjectReflectorTemplate.makeReflector(templateClass));
    }

    static {
        primRefls = new HashMap<Pair<IDLValue.Type, Class>, IDLPrimitiveReflector>();
        defaultRefls = new HashMap<Pair<IDLValue.Type,Class>, IDLObjectReflector>();
        
        /*
         * Primitive map
         */
        
        primRefls.put(Pair.cons(IDLValue.Type.Boolean, (Class)boolean.class), 
                      new BooleanPrimReflector());
        primRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)byte.class), 
                      new IntegerPrimCoercedReflector());
        primRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)short.class), 
                      new IntegerPrimCoercedReflector());
        primRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)int.class), 
                      new IntegerPrimCoercedReflector());
        primRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)long.class), 
                      new IntegerPrimCoercedReflector());
        primRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)float.class), 
                      new IntegerPrimCoercedReflector());
        primRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)double.class), 
                      new IntegerPrimCoercedReflector());
        primRefls.put(Pair.cons(IDLValue.Type.Float, (Class)float.class), 
                      new FloatPrimCoercedReflector());
        primRefls.put(Pair.cons(IDLValue.Type.Float, (Class)double.class), 
                      new FloatPrimCoercedReflector());;
        
        defaultRefls.put(Pair.cons(IDLValue.Type.Boolean, (Class)Boolean.class), 
                      new BooleanBoxReflector());
        defaultRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)Byte.class), 
                      new IntegerBoxCoercedReflector());
        defaultRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)Short.class), 
                      new IntegerBoxCoercedReflector());
        defaultRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)Integer.class), 
                      new IntegerBoxCoercedReflector());
        defaultRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)Long.class), 
                      new IntegerBoxCoercedReflector());
        defaultRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)Float.class), 
                      new IntegerBoxCoercedReflector());
        defaultRefls.put(Pair.cons(IDLValue.Type.Integer, (Class)Double.class), 
                      new IntegerBoxCoercedReflector());
        defaultRefls.put(Pair.cons(IDLValue.Type.Float, (Class)Float.class), 
                      new FloatBoxCoercedReflector());
        defaultRefls.put(Pair.cons(IDLValue.Type.Float, (Class)Double.class), 
                      new FloatBoxCoercedReflector());
                      
        defaultRefls.put(Pair.cons(IDLValue.Type.String, (Class)String.class),
                         new StringReflector());
        defaultRefls.put(Pair.cons(IDLValue.Type.ClassName, (Class)Class.class),
                         new ClassReflector());

        registerBaseTemplates ();
    }
    
    private static void registerBaseTemplates ( ) {
        // register base templates for gdx's basic classes
        IDLReflector.registerTemplate(QuaternionObjTemplate.class, Quaternion.class);
        IDLReflector.registerTemplate(Matrix4ObjTemplate.class, Matrix4.class);
        IDLReflector.registerTemplate(AssetSignatureObjTemplate.class, AssetSig.class);
    }
    
    // private Object reflectorLock = new Object();
    
    private static Object parseArray (IDLValue value, Class<?> clazz, boolean inCreation) {
        if (!value.isArray()) {
            throw new IDLReflectionError("Not an array value");
        }
        Class<?> elemType = clazz.getComponentType();
        if (elemType == null) {
            throw new IDLReflectionError("Not an array type");
        }
        int len = value.asArray().length;
        Object result = Array.newInstance (elemType, len);
        for (int idx = 0; idx < len; idx++) {
            IDLValue elem = value.asArray()[idx];
            Array.set(result, idx, parse(elem, elemType, inCreation));
        }
        return result;
    }
    public static boolean isPrimitive (Class<?> cls) {
        return byte.class.equals(cls) ||
               short.class.equals(cls) ||
               int.class.equals(cls) ||
               long.class.equals(cls) ||
               float.class.equals(cls) ||
               double.class.equals(cls) ||
               char.class.equals(cls) ||
               boolean.class.equals(cls) ||
               void.class.equals(cls);
    }

    private static void parseField (IDLValue value, Field field, Object obj, boolean inCreation) {
        Class<?> clazz = field.getType();
        Pair elem = Pair.cons(value.getType(), clazz);
        Object fieldValue;
        if (primRefls.containsKey(elem)) {
            primRefls.get(elem).parseAndSet(value, field, obj);
        } else {
            try {
                if (clazz.isArray()) {
                    fieldValue = parseArray(value, clazz, inCreation);
                } else {
                    fieldValue = parse(value, clazz, inCreation);
                }
            } catch (IDLReflectionError e) {
                throw new IDLReflectionError("Error occurred when retrieving value of field '" + 
                                             field.getName() + "' in " + field.getDeclaringClass(), e);
            }
            if (fieldValue == null && isPrimitive(clazz)) {
                throw new IDLReflectionError("Cannot set to null primitive field '" +
                                             field.getName() + "' in " + field.getDeclaringClass());
            }
            try {
                field.set(obj, fieldValue);
            } catch (IllegalArgumentException e) {
                throw new IDLReflectionError(e);
            } catch (IllegalAccessException e) {
                throw new IDLReflectionError(e);
            }
        } 
    }
    
    public static Object parse (IDLValue value, Class<?> clazz, boolean inCreation) {
        Pair elem = Pair.cons(value.getType(), clazz);
        if (defaultRefls.containsKey(elem)) {
            return defaultRefls.get(elem).parse(value, clazz);
        } else if (value.isNull()) {
            return null;
        } else {
            return reflParse(value, clazz, inCreation);
        }
    }

    private static Object reflParse (IDLValue value, Class<?> clazz, boolean inCreation) {
        if (!value.isDict()) {
            throw new IDLReflectionError("Cannot convert " + value + " to " + clazz);
        }
        
        Constructor ctor;
        Object instance;
        try {
            ctor = clazz.getDeclaredConstructor();
        } catch (SecurityException e) {
            throw new IDLReflectionError(e);
        } catch (NoSuchMethodException e) {
            throw new IDLReflectionError("Constructor with no formal argument is required in " + clazz);
        }
        try {
            ctor.setAccessible(true);
            instance = ctor.newInstance();
        } catch (IllegalArgumentException e) {
            throw new IDLReflectionError(e);
        } catch (InstantiationException e) {
            throw new IDLReflectionError(e);
        } catch (IllegalAccessException e) {
            throw new IDLReflectionError(e);
        } catch (InvocationTargetException e) {
            throw new IDLReflectionError(e);
        }
    
        Map<String, IDLValue> dict = value.asDict();
        Pair<Field[], Field[]> filteredFields = filterMetafields(clazz.getDeclaredFields());
        Field[] metaFields = filteredFields.first;
        Field[] commonFields = filteredFields.second;

        HashMap<String, Field> fieldNameMap = new HashMap<String, Field>();
        TreeSet<String> requiredNames = new TreeSet<String>();
        ArrayList<SelectiveGroup> selectiveGroups = new ArrayList<SelectiveGroup>();
        HashMap<String, Object> defvals = null;
        HashSet<String> notModifiables = new HashSet<String>();

        for (Field field : commonFields)
            fieldNameMap.put(field.getName(), field);

        try {
            for (Field field : metaFields) {
                field.setAccessible(true);
                Object metaField = field.get(instance);
                if (metaField instanceof RequiredFields) {
                    RequiredFields requiredFields = (RequiredFields)metaField;
                    for (String requiredName : requiredFields.fieldNames)
                        requiredNames.add(requiredName);
                } else if (metaField instanceof SelectiveGroup) {
                    selectiveGroups.add((SelectiveGroup) metaField);
                } else if (metaField instanceof DefaultValue && inCreation) {
                    if (defvals != null) {
                        throw new IDLReflectionError("Default key-value pairs can be specified at most once");
                    }
                    defvals = ((DefaultValue)metaField).defvals;
                    for (String key : defvals.keySet()) {
                        if (!fieldNameMap.containsKey(key)) {
                            throw new IDLReflectionError("Key '" + key + "' out of default key-value pairs is invalid");
                        }
                    }
                } else if (metaField instanceof NotModifiable && !inCreation) {
                    for (String nm : ((NotModifiable)metaField).fieldNames)
                        notModifiables.add(nm);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IDLReflectionError(e);
        } catch (IllegalAccessException e) {
            throw new IDLReflectionError(e);
        }


        // required key check
        // This only works if it is in creation mode.
        if (inCreation) {
            for (String requiredKey : requiredNames) {
                if (!fieldNameMap.containsKey(requiredKey)) {
                    throw new IDLReflectionError("Invalid required key '" + requiredKey + "'");
                }
                if (!dict.containsKey(requiredKey) && 
                    (defvals == null || !defvals.containsKey(requiredKey))) {
                    throw new IDLReflectionError("Key '" + requiredKey +  "' is required");
                }
            }
        }

        // redundancy check for dict key
        for (String dictKey : dict.keySet()) {
            if (!fieldNameMap.containsKey(dictKey)) {
                throw new IDLReflectionError("Redundant key in dict: " + dictKey + ". Might be caused by incorrect name of key");
            }
        }
        
        // Check group
        for (SelectiveGroup group: selectiveGroups) {
            String selectedName = null;
            for (String nameInGroup : group.fieldNames) {
                if (dict.containsKey(nameInGroup)) {
                    if (selectedName == null) {
                        selectedName = nameInGroup;
                    } else 
                        throw new IDLReflectionError("Fields('" + selectedName + "', '" + nameInGroup + "')" +
                                                     " in the same selective group cannot be specified together");
                } 
            }
            // it only works in creation mode.
            if (selectedName == null && inCreation && !group.unspecifiable) {
                StringBuilder builder = new StringBuilder();
                if (group.fieldNames.length > 0) {
                    builder.append('\'');
                    builder.append(group.fieldNames[0]);
                    builder.append('\'');
                    if (group.fieldNames.length > 1) {
                        for (int idx = 1; idx < group.fieldNames.length; idx++) {
                            builder.append(", ");
                            builder.append('\'');
                            builder.append(group.fieldNames[idx]);
                            builder.append('\'');
                        }
                    }
                }
                throw new IDLReflectionError("No field in Group(" + builder + ") is specified");
            }
            group.selectedField = selectedName;
        }
        
        for (Field field : commonFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (defvals != null && defvals.containsKey(fieldName)) {
                try {
                    field.set(instance, defvals.get(fieldName));
                } catch (IllegalArgumentException e) {
                    throw new IDLReflectionError(e);
                } catch (IllegalAccessException e) {
                    throw new IDLReflectionError(e);
                }
            } else if (dict.containsKey(fieldName)) {
                if (notModifiables.contains(fieldName)) {
                    throw new IDLReflectionError("Field '" + field +"' is not modifiable");
                }
                parseField(dict.get(fieldName), field, instance, inCreation);
            }
        }
        return instance;
    }
    private static Pair<Field[], Field[]> filterMetafields(Field [] fields) {
        ArrayList<Field> meta = new ArrayList<Field>();
        ArrayList<Field> nonmeta = new ArrayList<Field>();
        
        for (Field field : fields) {
            if (MetaReflectionField.class.isAssignableFrom(field.getType())) {
                meta.add(field);
            } else {
                if (!field.getName().startsWith("__") ||
                    !field.getName().endsWith("__")) {
                    nonmeta.add(field);
                }
            }
        }
        return Pair.cons(meta.toArray(new Field[0]), nonmeta.toArray(new Field[0]));
    }
}