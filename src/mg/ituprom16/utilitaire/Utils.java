package mg.ituprom16.utilitaire;

import java.lang.reflect.Method;
import java.util.HashMap;

import mg.ituprom16.annotations.Get;

public class Utils {
    public static String modifierClassPath(String classpath) {
        classpath = classpath.substring(1);
        classpath = classpath.replace("%20", " ");
        return classpath;
    }

    public static void scanClass(Class<?> annotClass, HashMap<String, Mapping> map) {
        Method[] methods = annotClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(Get.class)) {
                Get getAnnot = methods[i].getAnnotation(Get.class);
                map.put(getAnnot.value(), new Mapping(annotClass.getName(), methods[i].getName()));
            }
        }
    }
}
