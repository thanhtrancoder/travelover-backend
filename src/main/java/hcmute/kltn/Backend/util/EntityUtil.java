package hcmute.kltn.Backend.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityUtil {
    public static String getAllValue(Object obj) throws IllegalAccessException {
    	String allValue = "";
    	
        Class<?> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field itemField : fields) {
        	itemField.setAccessible(true);
        	
            Object value = itemField.get(obj);
            if (value == null || value.toString().isEmpty()) {
            	continue;
            }
            boolean isPrimitive = itemField.getType().isPrimitive();
            boolean isString = itemField.getType().isAssignableFrom(String.class);
            boolean isJavaLang = itemField.getType().getName().startsWith("java.lang");
            
            boolean isMyPackage = itemField.getType().getName().startsWith("hcmute.kltn");
            boolean isList = itemField.getType().isAssignableFrom(List.class);
            if (isMyPackage) {
            	allValue += " " + getAllValue(value);
            } else if (isList) {
            	List<Object> objectList = new ArrayList<>();
            	objectList.addAll((List) value);
            	for (Object itemObject : objectList) {
                    try {
                    	allValue += " " + getAllValue(itemObject);
                    } catch (Exception e) {
                    	allValue += " " + value; 
					}
            		
            	}
            } else {
            	allValue += " " + value;
            } 
        }
        return allValue;
    }
}
