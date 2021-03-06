package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JsonSerializer.None;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class DefaultSerializerProvider extends SerializerProvider implements Serializable {
    protected transient ArrayList<ObjectIdGenerator<?>> _objectIdGenerators;
    protected transient Map<Object, WritableObjectId> _seenObjectIds;

    public static final class Impl extends DefaultSerializerProvider {
        protected Impl(SerializerProvider src, SerializationConfig config, SerializerFactory f) {
            super(src, config, f);
        }

        public Impl createInstance(SerializationConfig config, SerializerFactory jsf) {
            return new Impl(this, config, jsf);
        }
    }

    public abstract DefaultSerializerProvider createInstance(SerializationConfig serializationConfig, SerializerFactory serializerFactory);

    protected DefaultSerializerProvider() {
    }

    protected DefaultSerializerProvider(SerializerProvider src, SerializationConfig config, SerializerFactory f) {
        super(src, config, f);
    }

    public void serializeValue(JsonGenerator jgen, Object value) throws IOException {
        if (value == null) {
            _serializeNull(jgen);
            return;
        }
        boolean wrap;
        JsonSerializer<Object> ser = findTypedValueSerializer((Class) value.getClass(), true, null);
        String rootName = this._config.getRootName();
        if (rootName == null) {
            wrap = this._config.isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
            if (wrap) {
                PropertyName pname = this._rootNames.findRootName(value.getClass(), this._config);
                jgen.writeStartObject();
                jgen.writeFieldName(pname.simpleAsEncoded(this._config));
            }
        } else if (rootName.length() == 0) {
            wrap = false;
        } else {
            wrap = true;
            jgen.writeStartObject();
            jgen.writeFieldName(rootName);
        }
        try {
            ser.serialize(value, jgen, this);
            if (wrap) {
                jgen.writeEndObject();
            }
        } catch (IOException ioe) {
            throw ioe;
        } catch (Throwable e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = "[no message for " + e.getClass().getName() + "]";
            }
            throw new JsonMappingException(msg, e);
        }
    }

    protected void _serializeNull(JsonGenerator jgen) throws IOException {
        try {
            getDefaultNullValueSerializer().serialize(null, jgen, this);
        } catch (IOException ioe) {
            throw ioe;
        } catch (Throwable e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = "[no message for " + e.getClass().getName() + "]";
            }
            throw new JsonMappingException(msg, e);
        }
    }

    public WritableObjectId findObjectId(Object forPojo, ObjectIdGenerator<?> generatorType) {
        WritableObjectId oid;
        if (this._seenObjectIds == null) {
            this._seenObjectIds = _createObjectIdMap();
        } else {
            oid = (WritableObjectId) this._seenObjectIds.get(forPojo);
            if (oid != null) {
                return oid;
            }
        }
        ObjectIdGenerator<?> generator = null;
        if (this._objectIdGenerators == null) {
            this._objectIdGenerators = new ArrayList(8);
        } else {
            int len = this._objectIdGenerators.size();
            for (int i = 0; i < len; i++) {
                ObjectIdGenerator<?> gen = (ObjectIdGenerator) this._objectIdGenerators.get(i);
                if (gen.canUseFor(generatorType)) {
                    generator = gen;
                    break;
                }
            }
        }
        if (generator == null) {
            generator = generatorType.newForSerialization(this);
            this._objectIdGenerators.add(generator);
        }
        oid = new WritableObjectId(generator);
        this._seenObjectIds.put(forPojo, oid);
        return oid;
    }

    protected Map<Object, WritableObjectId> _createObjectIdMap() {
        if (isEnabled(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID)) {
            return new HashMap();
        }
        return new IdentityHashMap();
    }

    public JsonSerializer<Object> serializerInstance(Annotated annotated, Object serDef) throws JsonMappingException {
        if (serDef == null) {
            return null;
        }
        JsonSerializer<?> ser;
        if (serDef instanceof JsonSerializer) {
            ser = (JsonSerializer) serDef;
        } else if (serDef instanceof Class) {
            Class<?> serClass = (Class) serDef;
            if (serClass == None.class || ClassUtil.isBogusClass(serClass)) {
                return null;
            }
            if (JsonSerializer.class.isAssignableFrom(serClass)) {
                HandlerInstantiator hi = this._config.getHandlerInstantiator();
                ser = hi == null ? null : hi.serializerInstance(this._config, annotated, serClass);
                if (ser == null) {
                    ser = (JsonSerializer) ClassUtil.createInstance(serClass, this._config.canOverrideAccessModifiers());
                }
            } else {
                throw new IllegalStateException("AnnotationIntrospector returned Class " + serClass.getName() + "; expected Class<JsonSerializer>");
            }
        } else {
            throw new IllegalStateException("AnnotationIntrospector returned serializer definition of type " + serDef.getClass().getName() + "; expected type JsonSerializer or Class<JsonSerializer> instead");
        }
        return _handleResolvable(ser);
    }
}
