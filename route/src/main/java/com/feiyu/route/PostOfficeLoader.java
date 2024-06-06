package com.feiyu.route;

import java.lang.reflect.TypeVariable;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 通过Java SPI机制加载 PostOffice
 * @author zhufeifei 2024/6/6
 **/

public class PostOfficeLoader {
    @SuppressWarnings("unchecked")
    public static <MSG, UID, N>  PostOffice<MSG, UID, N> load(Class<MSG> msgClass, Class<UID> uidClass, Class<N> nClass) {
        ServiceLoader<PostOffice> load = ServiceLoader.load(PostOffice.class);
        Iterator<PostOffice> iterator = load.iterator();
        PostOffice<MSG, UID, N> postOffice = null;
        while (iterator.hasNext()) {
            PostOffice p = iterator.next();
            // TODO 验证范型是否合法
            postOffice = p;
        }
        return postOffice;
    }
}
