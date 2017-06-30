package cc.doctor.search.common.utils;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by doctor on 2017/5/21.
 */
public class ReflectUtils {
    private static final Logger log = LoggerFactory.getLogger(ReflectUtils.class);

    public static Map<String, Class> getMethodParamNameTypes(String method, Class clazz) {
        Map<String, Class> nameTypeMap = new LinkedHashMap<>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals(method)) {
                Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                try {
                    ClassPool pool = ClassPool.getDefault();
                    CtClass cc = pool.get(clazz.getName());
                    CtMethod cm = cc.getDeclaredMethod(method);
                    // 使用javaassist的反射方法获取方法的参数名
                    MethodInfo methodInfo = cm.getMethodInfo();
                    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                    LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                            .getAttribute(LocalVariableAttribute.tag);
                    int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
                    for (int i = 0; i < parameterTypes.length; i++) {
                        String variableName = attr.variableName(i + pos);
                        nameTypeMap.put(variableName, parameterTypes[i]);
                    }

                } catch (NotFoundException e) {
                    log.error("", e);
                }

            }
        }
        return nameTypeMap;
    }

    public static Map<String, Field> getObjectAttrNameFields(Class clazz) {
        Map<String, Field> nameTypeMap = new LinkedHashMap<>();
        Field[] fields = clazz.getFields();
        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> fieldList = new LinkedList<>();
        fieldList.addAll(Arrays.asList(fields));
        fieldList.addAll(Arrays.asList(declaredFields));
        for (Field declaredField : fieldList) {
            String name = declaredField.getName();
            Class<?> type = declaredField.getType();
            if (!Modifier.isStatic(type.getModifiers())) {
                nameTypeMap.put(name, declaredField);
            }
        }
        return nameTypeMap;
    }

    public static void set(String field, Object value, Object instance) {
        String setMethod = "set" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
        try {
            Method method = instance.getClass().getMethod(setMethod, value.getClass());
            method.invoke(instance);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("", e);
        }
    }

    public static Object get(String field, Object instance) {
        String getMethod = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
        try {
            Method method = instance.getClass().getMethod(getMethod);
            return method.invoke(instance);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("", e);
            return null;
        }
    }
}
