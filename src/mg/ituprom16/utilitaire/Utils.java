package mg.ituprom16.utilitaire;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import mg.ituprom16.annotations.Get;

public class Utils {
    public static String modifierClassPath(String classpath) {
        classpath = classpath.substring(1);
        classpath = classpath.replace("%20", " ");
        return classpath;
    } 
    public static void scanClass(Class<?> classController , HashMap<String,Mapping> mapping) {
        Method[] methods = classController.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(Get.class)) {
                Get getAnnot = methods[i].getAnnotation(Get.class);
                mapping.put(getAnnot.value(), new Mapping(classController.getName(), methods[i].getName()));
            }
        }
    }

    public static void scanAllClasses(List<Class<?>> controllerList , HashMap<String,Mapping> mapping) {
        for (int i = 0; i < controllerList.size(); i++) {
            scanClass(controllerList.get(i), mapping);
        }
    }    
}
