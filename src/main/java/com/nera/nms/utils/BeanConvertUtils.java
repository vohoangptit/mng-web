package com.nera.nms.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BeanConvertUtils {

    private BeanConvertUtils() {
    }

    /**
     * <h1>Copy a class to an another class(same properties)</h1><br/>
     * @param source
     * @param target
     * @return target
     * <p><b>Ex:</b></p>
     * <p> Class A{name = "abc"; address = "bcd"} </p>
     * <p> Class B{name = ""; phone = ""} </p>
     * <p>BeanConvertUtils.copy(A, B); => B{name ="abc", phone=""}</p>
     * @exception @{@link IllegalAccessException} - {@link InvocationTargetException}
     */
    public static <U, V> V copy(U source, V target) {
        
        try {
            BeanUtils.copyProperties(target, source);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return target;
    }

    /**
     * <h1>Copy a class to an another class (same properties)</h1><br/>
     * @param source
     * @param clazz
     * @return target
     * <p><b>Ex:</b></p>
     * <p> Class A{name = "abc"; address = "bcd"} </p>
     * <p> Class B{name = ""; phone = ""} </p>
     * <p>B = BeanConvertUtils.createAndCopy(A, B.class); => B{name ="abc", phone=""}</p>
     * @exception @{@link InstantiationException} - {@link IllegalAccessException}
     */
    public static <U, V> V createAndCopy(U source, Class<V> clazz) {
            
            try {
                return copy(source, clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            return null;
    }

    /**
     * <h1>Copy a class to an another class (same properties)</h1><br/>
     * @param sources
     * @param clazz
     * @return target
     * <p><b>Ex:</b></p>
     * <p> ArrayList<A>(){name = "abc"; address = "bcd"} </p>
     * <p> ArrayList<B>{name = ""; phone = ""} </p>
     * <p>B = BeanConvertUtils.copyList(A, B.class); => ArrayList<B>{name ="abc", phone=""}</p>
     * @exception @{@link IllegalAccessException} - {@link InvocationTargetException}
     */
    public static <U, V> List<V> copyList(List<U> sources, Class<V> clazz) {
        List<V> targetList = new ArrayList<>();
        sources.forEach(source -> targetList.add(createAndCopy(source, clazz)));

        return targetList;
    }
}
