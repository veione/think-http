package com.think.http.handler;

import com.think.http.Router;
import com.think.http.annotation.DeleteMapping;
import com.think.http.annotation.GetMapping;
import com.think.http.annotation.PostMapping;
import com.think.http.annotation.PutMapping;
import com.think.http.annotation.WebHandler;
import com.think.http.constant.StatusCode;
import com.think.http.context.HttpContext;
import com.think.http.result.JsonResult;
import com.think.http.result.Result;
import com.think.http.util.ClassScanner;
import com.think.http.util.Config;
import com.think.http.util.Md5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Router management.
 *
 * @author veione
 */
public class RouterHandler {
    private static final Logger logger = LoggerFactory.getLogger(RouterHandler.class);
    private static final Map<String, Router> routerMap = new HashMap<>(128);

    static {
        init();
    }

    /**
     * initial handle
     */
    public static void init() {
        String handlePackage = Config.getString("handle.package");
        if (handlePackage == null || handlePackage.isEmpty()) {
            throw new IllegalStateException("Handle scan package can't be empty.");
        }
        Set<Class<?>> classes = ClassScanner.getClasses(handlePackage, c -> c.isAnnotationPresent(WebHandler.class));
        logger.info("Loaded handle count: {}", classes.size());
        Iterator<Class<?>> iter = classes.iterator();
        while (iter.hasNext()) {
            Class<?> cls = iter.next();
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                Router router = new Router();
                router.setClazz(cls);
                router.setInvoker(method);

                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping get = method.getAnnotation(GetMapping.class);
                    router.setEndPoint(get.value());
                    router.setHttpMethod(get.method());
                }
                if (method.isAnnotationPresent(PostMapping.class)) {
                    PostMapping post = method.getAnnotation(PostMapping.class);
                    router.setEndPoint(post.value());
                    router.setHttpMethod(post.method());
                }
                if (method.isAnnotationPresent(PutMapping.class)) {
                    PutMapping put = method.getAnnotation(PutMapping.class);
                    router.setEndPoint(put.value());
                    router.setHttpMethod(put.method());
                }
                if (method.isAnnotationPresent(DeleteMapping.class)) {
                    DeleteMapping delete = method.getAnnotation(DeleteMapping.class);
                    router.setEndPoint(delete.value());
                    router.setHttpMethod(delete.method());
                }

                if (router.getEndPoint() != null) {
                    routerMap.put(router.getEndPoint() + ":" + router.getHttpMethod().toString(), router);
                }
            }
        }
    }

    private static boolean verifyAuth(HttpContext context) {
        String auth = Config.getString("auth.key");
        if (auth != null && !auth.isEmpty()) {
            Iterator<Map.Entry<String, String>> iter = context.getParameters().entrySet().iterator();
            StringBuilder strBuilder = new StringBuilder();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                if (entry.getKey().equals("sign")) {
                    continue;
                }
                strBuilder.append(entry.getValue());
            }
            strBuilder.append(auth);
            String originalSign = Md5Utils.getMD5(strBuilder.toString());
            String sign = context.getParameter("sign");
            if (originalSign.equals(sign)) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static Result execute(HttpContext context) {
        Result result = new JsonResult();
        boolean verify = verifyAuth(context);

        if (verify) {
            if (context.getEndPoint() == null) {
                result.handle(StatusCode.API_NOT_FOUND);
                return result;
            }
            Router router = routerMap.get(context.getEndPoint() + ":" + context.getHttpMethod().toString());

            if (router == null) {
                result.handle(StatusCode.API_NOT_FOUND);
                return result;
            }

            if (router.getHttpMethod().toString() != context.getHttpMethod().toString()) {
                result.handle(StatusCode.REQUEST_METHOD_NOT_ALLOWED);
                return result;
            }

            try {
                router.setContext(context);
                Object data = router.doHandle();
                result.handle(data);
            } catch (Exception e) {
                logger.error("Execute web handle error", e);
                result.handle(StatusCode.SERVER_INTER_ERROR);
            }
        } else {
            result.handle(StatusCode.REQUEST_NO_AUTH);
        }

        return result;
    }

    public static Map<String, Router> getRouterMap() {
        return routerMap;
    }
}
