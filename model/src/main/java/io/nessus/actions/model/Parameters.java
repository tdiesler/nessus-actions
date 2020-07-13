package io.nessus.actions.model;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Parameters {
    
    private Map<String, Object> pmap = new LinkedHashMap<>();
    private boolean immutable;
    
    public Parameters() {
    }

    public Parameters(Map<String, Object> values) {
        pmap.putAll(values);
    }

    public Parameters(Parameters params) {
        this(params, false);
    }

    public Parameters(Parameters params, boolean immutable) {
        pmap.putAll(params.toMap());
        if (immutable) {
            this.pmap = Collections.unmodifiableMap(pmap);
            this.immutable = immutable;
        }
    }

    public boolean isEmpty() {
        return pmap.isEmpty();
    }
    
    public List<String> keys() {
        return new ArrayList<>(pmap.keySet());
    }
    
    @SuppressWarnings("unchecked")
    public <T> T put(Class<T> type, T value) {
        return (T) pmap.put(toName(type), value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T put(String name, T value) {
        return (T) pmap.put(name, value);
    }
    
    public Parameters putAll(Parameters params) {
        putAll(params.toMap());
        return this;
    }
    
    public Parameters putAll(Map<String, Object> values) {
        pmap.putAll(values);
        return this;
    }
    
    public Parameters clear() {
        pmap.clear();
        return this;
    }
    
    public boolean isImmutable() {
        return immutable;
    }

    public Parameters makeImmutable() {
        return new Parameters(this, true);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(String name, Class<T> type) {
        Object value = pmap.get(name);
        value = convertValue(value, type); 
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, T defaultValue) {
        AssertArg.notNull(defaultValue, "Null default value");
        T result = get(name, (Class<T>) defaultValue.getClass());
        return result != null ? result : defaultValue;
    } 
    
    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, Class<T> type) {
        
        if (value == null) 
            return null;
        
        if (type.isArray()) {
            Class<?> compType = type.getComponentType();
            if (value.equals("[]")) 
                value = Collections.emptyList();
            if (!(value instanceof List)) {
                String valstr = value.toString();
                if (valstr.startsWith("[") && valstr.endsWith("]")) {
                    valstr = valstr.substring(1, valstr.length() - 1);
                    String[] toks = valstr.split(",");
                    value = Arrays.asList(toks).stream().map(t -> t.trim()).collect(Collectors.toList());
                } else {
                    value = Arrays.asList(value);
                }
            }
            List<?> vallst = (List<?>) value;
            List<?> reslst = vallst.stream().map(el -> convertValue(el, compType)).collect(Collectors.toList());
            Object[] resarr = (Object[]) Array.newInstance(compType, reslst.size());
            return (T) reslst.toArray(resarr);
        }
        
        if (value.equals("")) 
            return null; 
        
        if (type.isAssignableFrom(value.getClass())) 
            return (T) value;
        
        String strval = value.toString().trim();
        
        if (type == Boolean.class) {
            value = Boolean.valueOf(strval);
        } else if (BigDecimal.class == type) {
            if (strval.endsWith("%")) {
                strval = strval.substring(0, strval.length() - 1);
                value = new BigDecimal(strval).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            } else {
                value = new BigDecimal(strval);
            }
        } else if (Double.class == type) {
            value = new Double(strval);
        } else if (Integer.class == type) {
            value = new Integer(strval);
        } else if (Long.class == type) {
            value = new Long(strval);
        } else if (Parameters.class == type) {
            value = Parameters.fromString(strval);
        } else if (String.class == type) {
            value = strval;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type.getSimpleName());
        }
        
        return (T) value;
    }
    
    public <T> T notNull(String name, Class<T> type) {
        T value = get(name, type);
        AssertState.notNull(value, "Cannot obtain parameter '" + name + "' from: " + this);
        return value;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T remove(Class<T> type) {
        return (T) remove(toName(type));
    }

    public <T> T remove(String name, Class<T> type) {
    	T resval = get(name, type);
    	pmap.remove(name);
        return resval;
    }
    
    public Object remove(String name) {
    	Object resval = get(name, Object.class);
    	pmap.remove(name);
        return resval;
    }
    
    private String toName(Class<?> classKey) {
        return classKey.getSimpleName();
    }
    
    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(pmap);
    }
    
    public Map<String, Object> toMap(List<String> names) {
        Map<String, Object> result = pmap.entrySet().stream()
                .filter(e -> names == null || names.contains(e.getKey()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return result;
    }
    
    public static Parameters fromString(String spec) {
        AssertArg.isTrue(spec.startsWith("{") && spec.endsWith("}"), "Invalid params spec: " + spec);
        spec = spec.substring(1, spec.length() - 1);
        Parameters params = new Parameters();
        if (spec.length() > 0) {
            Iterator<String> it = Arrays.asList(spec.split(",")).iterator();
            while (it.hasNext()) {
                String keyval = it.next();
                while (keyval.contains("=[") && !keyval.endsWith("]")) {
                    keyval += "," + it.next();
                }
                String[] toks = keyval.split("=");
                String key = toks[0].trim();
                String val = toks[1].trim();
                if (val.startsWith("[") && val.endsWith("]")) {
                    val = val.substring(1, val.length() - 1);
                    String[] valarr = val.split(",");
                    List<String> vallst = Arrays.asList(valarr).stream()
                            .map(v -> v.trim())
                            .collect(Collectors.toList());
                    params.put(key, vallst);
                } else {
                    params.put(key, val);
                    
                }
            }
        }
        return params;
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Parameters)) return false;
        Parameters other = (Parameters) obj;
        Map<String, Object> m1 = new LinkedHashMap<>();
        Map<String, Object> m2 = new LinkedHashMap<>();
        pmap.keySet().stream().sorted().forEach(k -> m1.put(k, pmap.get(k)));
        other.pmap.keySet().stream().sorted().forEach(k -> m2.put(k, other.pmap.get(k)));
        return m1.toString().equals(m2.toString());
    }

    public String toString() {
        return pmap.toString();
    }
}
