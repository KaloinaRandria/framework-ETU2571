package mg.ituprom16.utilitaire;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import mg.ituprom16.annotations.Get;
import mg.ituprom16.annotations.GetParam;
import mg.ituprom16.annotations.ObjectParam;

public class Utils {
    public static String modifierClassPath(String classpath) {
        classpath = classpath.substring(1);
        classpath = classpath.replace("%20", " ");
        return classpath;
    }

    public static void scanClass(Class<?> annotClass, HashMap<String, Mapping> map) throws Exception {
        Method[] methods = annotClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(Get.class)) {
                Get getAnnot = methods[i].getAnnotation(Get.class);
                if (map.containsKey(getAnnot.value())) {
                    throw new Exception("Plusieurs URL portent le meme nom !!!");
                } else {
                    map.put(getAnnot.value(), new Mapping(annotClass.getName(), methods[i].getName()));
                }
            }
        }
    }

    public static void scanListClasses(Vector<Class<?>> classes, HashMap<String, Mapping> map) throws Exception {
        for (int i = 0; i < classes.size(); i++) {
            scanClass(classes.get(i), map);
        }
    }

    public static Vector<Class<?>> getAllClassAnnoted(String path, Class<? extends Annotation> annotation)
            throws Exception {
        Vector<Class<?>> classAnnotedList = new Vector<Class<?>>();
        try { 
            File classPathDirectory = new File(path);
            for (File file : classPathDirectory.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    Class<?> class1 = Thread.currentThread().getContextClassLoader()
                            .loadClass(path.split("classes/")[1].replace("/", ".") + className);
                    if (class1.isAnnotationPresent(annotation)) {
                        classAnnotedList.add(class1);
                    }
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return classAnnotedList;
    }

    public static Method getMyMethod(String path , Method[] methods) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(Get.class)) {
                Get getAnnot = method.getAnnotation(Get.class);
                if (getAnnot.value().equals(path)) {
                    return method;
                }
            }
        }
        return null;
    }

    public static Object invokedMethod(HashMap<String, Mapping> map, String urlValue , HashMap<String, String> parameters) throws Exception {
        Object toReturn = new Object();
        for (int i = 0; i < map.size(); i++) {
            if (map.get(urlValue) != null) {
                    Mapping mapping = map.get(urlValue);
                    Class<?> myClass = Class.forName(mapping.getClassName());
                    Method[] methods = myClass.getMethods();
                    Method myMethod = Utils.getMyMethod(urlValue, methods);
                    Parameter[] myParameters = myMethod.getParameters();
                    Object[] methodAttributes = getObjectsAsParameter(myMethod, parameters);
                    int count = 0;
                    for (int j = 0; j < myParameters.length; j++) {
                        if (myParameters[j].isAnnotationPresent(GetParam.class)) {
                            GetParam paramAnnot = myParameters[j].getAnnotation(GetParam.class);
                            methodAttributes[count] = parameters.get(paramAnnot.value());
                            count ++;
                        } else if (parameters.containsKey(myParameters[j].getName())) {
                            methodAttributes[count] = parameters.get(myParameters[i].getName());
                            System.out.println(methodAttributes[count] + "\n");
                            count++;
                        }
                    }
                    Object myObject = myClass.getDeclaredConstructor(new Class<?>[0]).newInstance(new Object[0]);
                    toReturn = myMethod.invoke(myObject, methodAttributes);
            } else {
                throw new IllegalArgumentException("URL non reconnu");
            } 
        }
        return toReturn;
    }

    public static Vector<String> getKeys(HashMap<String , String> map) {
        Vector<String> toReturn = new Vector<>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            toReturn.add(key);            
        }
        return toReturn;
    }
     
    public static HashMap<String, Vector<String>> triObject(HashMap<String , String > map) {
        HashMap<String , Vector<String>> toReturn = new HashMap<String , Vector<String>>();
        Vector<String> getKeys = Utils.getKeys(map);
        for (String key : getKeys) {
            String tempKey = (key.split(":"))[0];
            Vector<String> tempValues = new Vector<>();

            if (!toReturn.containsKey(tempKey)) {
                try {
                    tempValues.add((key.split(":"))[1]);
                    for (String cle : getKeys) {
                        if ((cle.split(":"))[0].equals(tempKey)) {
                            tempValues.add((cle.split(":"))[1]);
                        }
                    }
                } catch (Exception e) {
                    tempValues.add("simple");
                }
                toReturn.put(tempKey, tempValues);
            }
        }
        return toReturn;
    }
    
    public static String getFieldName(Class<?> clazz , String fieldName) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAccessible()) {
                if (field.isAnnotationPresent(ObjectParam.class)) {
                    ObjectParam objectAnnot = field.getAnnotation(ObjectParam.class);
                    if (objectAnnot.value().equals(fieldName)) {
                        return field.getName();
                    }
                }
            } else {
                field.setAccessible(true);
                if (field.isAnnotationPresent(ObjectParam.class)) {
                    ObjectParam objectAnnot = field.getAnnotation(ObjectParam.class);
                    if (objectAnnot.value().equals(fieldName)) {
                        return field.getName();
                    }
                }
                field.setAccessible(false);
            }
        }
        return fieldName;
    }
    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void setter(Class<?> clazz , Field field , String value , Object object) {
        Class<?>[] paramClasses = new Class<?>[1];
        paramClasses[0] = field.getType();
        try {
            Object[] objects = new Object[1];
            String methodName = String.format("set%s", capitalizeFirstLetter(field.getName()));
            Method method;
            if (field.getType().equals(Date.class)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                objects[0] = dateFormat.parse(value);
            } else if (field.getType().equals(int.class)) {
                objects[0] = Integer.parseInt(value);
            } else if (field.getType().equals(double.class)) {
                objects[0] = Double.parseDouble(value);
            } else {
                throw new IllegalArgumentException("Type non supporte : " + field.getType().getSimpleName());
            }
            method = clazz.getDeclaredMethod(methodName, paramClasses);
            method.invoke(object, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }
    public static Object buildObjectByName(Parameter parameter ,String paramName ,Vector<String> inputObject, HashMap<String, String> map)throws Exception {
        Class<?> type = parameter.getType();
        Constructor<?> constructor = type.getConstructor(new Class<?>[0]);
        Object toReturn = constructor.newInstance(new Object[0]);
        for (String input : inputObject) {
            Field field = type.getDeclaredField(Utils.getFieldName(type, input));
            if (field.isAccessible()) {
                String tempValue = map.get(paramName+":"+input);
                Utils.setter(type, field, tempValue, inputObject);
            } else {
                field.setAccessible(true);
                String tempValue = map.get(Utils.getFieldName(type, input));
                Utils.setter(type, field, tempValue, inputObject);
                field.setAccessible(false);
            }
        }
        return toReturn;
    }

    public static Object buildObjectByAnnotation(Parameter parameter, Vector<String> inputObject, HashMap<String ,String> map) throws Exception {
        Class<?> clazz = parameter.getType();
        Constructor<?> constructor = clazz.getConstructor(new Class<?>[0]);
        Object toReturn = constructor.newInstance(new Object[0]);
        for (String input : inputObject) {
            Field field = clazz.getDeclaredField(Utils.getFieldName(clazz, input));
            if (field.isAccessible()) {
                String tempValue = map.get(parameter.getAnnotation(GetParam.class).value()+":"+input);
                Utils.setter(clazz, field, tempValue, inputObject);
            } else {
                field.setAccessible(true);
                String tempValue = map.get(parameter.getAnnotation(GetParam.class).value()+":"+input);
                Utils.setter(clazz, field, tempValue, inputObject);
                field.setAccessible(false);
            }
        }
        return toReturn;
    }
    public static Object[] getObjectsAsParameter(Method method, HashMap<String, String> map) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] toReturn = new Object[parameters.length];
        HashMap<String, Vector<String>> inputObject = Utils.triObject(map);
        int count = 0;
    
        for (Parameter parameter : parameters) {
            String paramName = null;
            Vector<String> listAttributeForClasse = null;
    
            if (parameter.isAnnotationPresent(GetParam.class)) {
                GetParam getAnnot = parameter.getAnnotation(GetParam.class);
                paramName = getAnnot.value();
                listAttributeForClasse = inputObject.get(paramName);
            } else if (inputObject.containsKey(parameter.getName())) {
                paramName = parameter.getName();
                listAttributeForClasse = inputObject.get(paramName);
            }
    
            if (listAttributeForClasse == null) {
                throw new NullPointerException("No value found in inputObject for key: " + paramName);
            }
    
            if (listAttributeForClasse.elementAt(0).equals("simple") == false) {
                if (parameter.isAnnotationPresent(GetParam.class)) {
                    toReturn[count] = Utils.buildObjectByAnnotation(parameter, listAttributeForClasse, map);
                } else {
                    toReturn[count] = buildObjectByName(parameter, paramName, listAttributeForClasse, map);
                }
                count++;
            } else {
                String value = map.get(paramName);
                if (value == null) {
                    throw new NullPointerException("No value found in map for key: " + paramName);
                }
                if (parameter.getType().getSimpleName().equals("int")) {
                    toReturn[count] = Integer.parseInt(value);
                } else if (parameter.getType().getSimpleName().equals("double")) {
                    toReturn[count] = Double.parseDouble(value);
                } else if (parameter.getType().getSimpleName().equals("Date")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    toReturn[count] = dateFormat.parse(value);
                } else {
                    toReturn[count] = value;
                }
                count++;
            }
        }
        return toReturn;
    }
    
    // public static Object[] getObjectsAsParameter(Method method , HashMap<String, String> map) throws Exception 
    // {
    //     Parameter[] parameters = method.getParameters();
    //     Object[] toReturn = new Object[parameters.length];
    //     HashMap<String, Vector<String>> inputObject = Utils.triObject(map);
    //     int count = 0;        
    //     for (Parameter parameter : parameters) {
    //         if (parameter.isAnnotationPresent(GetParam.class)) {
    //             GetParam getAnnot = parameter.getAnnotation(GetParam.class);
    //             if (inputObject.get(getAnnot.value()).elementAt(0).equals("simple") == false) {
    //                 Vector<String> listAttributeForClasse = inputObject.get(getAnnot.value());
    //                 toReturn[count] = Utils.buildObjectByAnnotation(parameter, listAttributeForClasse, map);
    //                 count++;
    //             } else {
    //                 String value = map.get(parameter.getAnnotation(GetParam.class).value());
    //                 if (parameter.getType().getSimpleName().equals("int")) {
    //                     toReturn[count] = Integer.parseInt(value); 
    //                 } else if (parameter.getType().getSimpleName().equals("double")) {
    //                     toReturn[count] = Double.parseDouble(value);
    //                 } else if (parameter.getType().getSimpleName().equals("Date")) {
    //                     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //                     toReturn[count] = dateFormat.parse(value);
    //                 } else {
    //                     toReturn[count] = value;
    //                 }
    //                 count++;
    //             }
                
    //         } else if (inputObject.containsKey(parameter.getName())) {
    //             if (inputObject.get(parameter.getName()).elementAt(0).equals("simple") == false) {
    //                 Vector<String> listAttributeForClasse = inputObject.get(parameter.getName());
    //                 toReturn[count] = buildObjectByName(parameter, null, listAttributeForClasse, map);
    //                 count++;
    //             } else {
    //                 String value = map.get(parameter.getName());
    //                 if (parameter.getType().getSimpleName().equals("int")) {
    //                     toReturn[count] = Integer.parseInt(value); 
    //                 } else if (parameter.getType().getSimpleName().equals("double")) {
    //                     toReturn[count] = Double.parseDouble(value);
    //                 } else if (parameter.getType().getSimpleName().equals("Date")) {
    //                     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //                     toReturn[count] = dateFormat.parse(value);
    //                 } else {
    //                     toReturn[count] = value;
    //                 }
    //                 count++;
    //             }
    //         }
    //     }
    //     return toReturn;

    // }
}

