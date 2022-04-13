package cn.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 工具类
 */
public class BaseHelper {

    private final static Logger log = Logger.getLogger(BaseHelper.class);

    public static final String PARAM_EQUAL = "=";

    public static final String PARAM_AND = "&";

    public static String toJSONString(Object obj) {
        JSONObject json = new JSONObject();
        try {
            Map<String, String> map = bean2Parameters(obj);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 对Object进行List<NameValuePair>转换后按key进行升序排序，以key=value&...形式返回
     * 
     * @param list
     * @return
     */
    public static String sortParam(Object order) {
        Map<String, String> map = bean2Parameters(order);
        return sortParam(map);
    }

    public static String sortParamAll(Object order) {
        Map<String, String> map = bean2Parameters(order);
        return sortCheckParam(map);
    }

    /**
     * 对Object进行List<NameValuePair>转换后按key进行升序排序，以key=value&...形式返回
     * 
     * @param list
     * @return
     */
    public static String sortCheckParam(Map<String, String> order) {
        if (order == null) {
            return null;
        }

        Map<String, String> parameters = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String obj1, String obj2) {
                return obj1.compareToIgnoreCase(obj2);
            }
        });
        parameters.putAll(order);

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (null != entry.getValue() && !"".equals(entry.getValue()) && !entry.getKey().equals("sign")) {
                sb.append(entry.getKey());
                sb.append(PARAM_EQUAL);
                sb.append(entry.getValue());
                sb.append(PARAM_AND);
            }
        }

        String params = sb.toString();
        if (sb.toString().endsWith(PARAM_AND)) {
            params = sb.substring(0, sb.length() - 1);
        }
        log.info("待签名串:" + params);
        return params;
    }

    /**
     * 将bean转换成键值对列表
     * 
     * @param bean
     * @return
     */
    public static Map<String, String> bean2Parameters(Object bean) {
        if (bean == null) {
            return null;
        }

        Map<String, String> parameters = new HashMap<String, String>();

        if (null != parameters) {
            // 取得bean所有public 方法
            Method[] Methods = bean.getClass().getMethods();
            for (Method method : Methods) {
                if (method != null && method.getName().startsWith("get") && !method.getName().startsWith("getClass")) {
                    // 得到属性的类名
                    String value = "";
                    // 得到属性值
                    try {
                        String className = method.getReturnType().getSimpleName();
                        if (className.equalsIgnoreCase("int")) {
                            int val = 0;
                            try {
                                val = (Integer) method.invoke(bean);
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            value = String.valueOf(val);
                        } else if (className.equalsIgnoreCase("String")) {
                            try {
                                value = (String) method.invoke(bean);
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                        if (value != null && value != "") {
                            // 添加参数
                            // 将方法名称转化为id，去除get，将方法首字母改为小写
                            String param = method.getName().replaceFirst("get", "");
                            if (param.length() > 0) {
                                String first = String.valueOf(param.charAt(0)).toLowerCase();
                                param = first + param.substring(1);
                            }
                            parameters.put(param, value);
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return parameters;
    }

    /**
     * 对对Object转换后, 以key=value&...形式返回按key进行升序排序，以key=value&...形式返回
     * 
     * @param order
     * @return
     */
    public static String sortParam(Map<String, String> order) {
        if (null == order) {
            return null;
        }

        Map<String, String> parameters = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String obj1, String obj2) {
                return obj1.compareToIgnoreCase(obj2);
            }
        });
        parameters.putAll(order);

        if (null != parameters) {
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {

                if (null != entry.getValue() && !"".equals(entry.getValue()) && !entry.getKey().equals("id_type")
                        && !entry.getKey().equals("id_no") && !entry.getKey().equals("acct_name")
                        && !entry.getKey().equals("flag_modify") && !entry.getKey().equals("user_id")
                        && !entry.getKey().equals("no_agree") && !entry.getKey().equals("card_no")
                        && !entry.getKey().equals("test_mode")) {
                    sb.append(entry.getKey());
                    sb.append(PARAM_EQUAL);
                    sb.append(entry.getValue());
                    sb.append(PARAM_AND);
                }
            }

            String params = sb.toString();
            if (sb.toString().endsWith(PARAM_AND)) {
                params = sb.substring(0, sb.length() - 1);
            }
            log.info("待签名串:" + params);
            return params;
        }

        return null;
    }
}
