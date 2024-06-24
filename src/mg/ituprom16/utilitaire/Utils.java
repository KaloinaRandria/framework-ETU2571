package mg.ituprom16.utilitaire;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import mg.ituprom16.annotations.Get;
import mg.ituprom16.annotations.GetParam;

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
                    Object[] methodAttributes = new Object[myParameters.length];
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
    
}

