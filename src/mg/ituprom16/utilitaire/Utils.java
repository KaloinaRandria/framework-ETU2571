package mg.ituprom16.utilitaire;

import java.lang.reflect.Method;
import java.util.HashMap;

import mg.ituprom16.annotations.Get;

public class Utils {
    public static void scanClass(Class<?> classController , HashMap<String,Mapping> mapping) {
        Method[] methods = classController.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(Get.class)) {
                Get getAnnot = methods[i].getAnnotation(Get.class);
                mapping.put(getAnnot.value(), new Mapping(classController.getName(), methods[i].getName()));
            }
        }
    }
}
