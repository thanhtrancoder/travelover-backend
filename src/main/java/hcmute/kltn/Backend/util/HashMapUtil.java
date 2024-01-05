package hcmute.kltn.Backend.util;

import java.util.HashMap;

public class HashMapUtil {
	private static Object getData(String input) {
		String data = input.trim();
		data = data.replace("\\", "");
		data = data.replace("\"", "");
		data = data.replace("{", "");
		data = data.replace("}", "");
        if (data.equals("null")) {
        	String str = null;
        	return str;
        } else if (data.equals("true")) {
        	return true;
        } else if (data.equals("false")) {
        	return false;
        } else {
        	return data;
        }
	}
	
	public static HashMap<Object, Object> stringToHashMap(String jsonString) {
		HashMap<Object, Object> hashMap = new HashMap<>();

		String inputJson = new String();
		int objectBegin = jsonString.indexOf("{");
		if (objectBegin == 0) {
			inputJson = jsonString.substring(1, jsonString.length());
		}
		int objectEnd = jsonString.lastIndexOf("}");
		if (objectEnd == (jsonString.length() - 1)) {
			inputJson = inputJson.substring(0, inputJson.length() - 1);
		}

		while (true) {
			Object key = new Object();
			Object value = new Object();
			String[] objectItem = inputJson.split(",", 2);
			
			String[] objectKeyValue = objectItem[0].split(":", 2);
			if (objectKeyValue.length == 1) {
				break;
			}

			key = getData(objectKeyValue[0]);
			
			String[] objectSub = objectItem[0].split("\\{");
			if (objectSub.length > 1) {
				int subOpen = 0;
				int subClose = 0;
				
				subOpen = inputJson.indexOf("{");
				subClose = inputJson.indexOf("}");

				String subString = new String();
				subString = inputJson.substring(subOpen, subClose + 1);
				
				String[] subOpenString = subString.split("\\{");
				String[] subCloseString = subString.split("\\}", -1);
				
				String[] subCloseStringClone = inputJson.split("\\}", -1);
				int index = 1;
				while (subOpenString.length != subCloseString.length) {
					
					for (int i = 0; i < (subOpenString.length - subCloseString.length); i++) {
						subClose += subCloseStringClone[index].length() + 1;
						index++;
					}
					
					subString = inputJson.substring(subOpen, subClose + 1);
					subOpenString = subString.split("\\{");
					subCloseString = subString.split("\\}", -1);
				}
				
				String subStringElse = new String();
				subStringElse = inputJson.substring(subClose + 2);

				value = stringToHashMap(subString);
				hashMap.put(key, value);
				
				inputJson = subStringElse;
				
				System.out.println("inputJson = " + inputJson);
				System.out.println("subString = " + subString);
				System.out.println("subStringElse = " + subStringElse);
				System.out.println();
			} else {
				int objectClose = objectKeyValue[1].indexOf("}");
				
				value = getData(objectKeyValue[1]);
				hashMap.put(key, value);
				
				if (objectItem.length == 1 || objectClose != -1) {
					inputJson = "";
					break;
				} else {
					inputJson = objectItem[1];
				}
				
				System.out.println("inputJson = " + inputJson);
				System.out.println("objectItem[0] = " + objectItem[0]);
				System.out.println("objectItem[1] = " + objectItem[1]);
				System.out.println();
			}
			
			

		}

        return hashMap;
	}
}
