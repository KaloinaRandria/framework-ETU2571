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

    public static Object invokedMethod(HashMap<String, Mapping> map, String urlValue , HashMap<String, String> parameters) throws Exception {
        Object toReturn = new Object();
        for (int i = 0; i < map.size(); i++) {
            if (map.get(urlValue) != null) {
                Mapping mapping = map.get(urlValue);
                Class<?> myClass = Class.forName(mapping.getClassName());
                Class<?>[] paramClasses = new Class[parameters.size()];
                Vector<String> parameterKeys = new Vector<>();
                Set<String> keys = parameters.keySet();
                for (String key : keys) {
                    parameterKeys.add(key);
                }

                for (int j = 0; j < paramClasses.length; j++) {
                    paramClasses[j] = parameters.get(parameterKeys.elementAt(j)).getClass();
                }

                Object[] methodAttributs = new Object[parameters.size()];
                Method myMethod = myClass.getDeclaredMethod(mapping.getMethodName(), paramClasses);
                Parameter[] parameters2 = myMethod.getParameters();

                int count = 0;
                for (int j = 0; j < parameters2.length; j++) {
                    if (parameters2[j].isAnnotationPresent(GetParam.class)) {
                        GetParam paramAnnot = parameters2[j].getAnnotation(GetParam.class);
                        methodAttributs[count] = parameters.get(paramAnnot.value()); 
                    } else if(parameters.containsKey(parameters2[j].getName())) {
                        methodAttributs[count] = parameters.get(parameters2[j].getName());
                        count++;
                    }
                }
                Object myObject = myClass.getDeclaredConstructor(new Class<?>[0]).newInstance(new Object[0]);
                toReturn = myMethod.invoke(myObject, methodAttributs);
            }
            else {
                throw new IllegalArgumentException("URL non reconnu");
            } 
        }
        return toReturn;
    }
}
