/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.plugin.tars.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.soul.common.dto.MetaData;
import org.dromara.soul.common.utils.GsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Proxy info util.
 *
 * @author tydhot
 */
public class PrxInfoUtil {

    private static final Map<String, PrimitiveType> PRIMITIVE_TYPE;

    static {
        PRIMITIVE_TYPE = new HashMap();
        PRIMITIVE_TYPE.put("int", new PrimitiveType(int.class, o -> Integer.valueOf(o)));
        PRIMITIVE_TYPE.put("double", new PrimitiveType(double.class, o -> Double.valueOf(o)));
        PRIMITIVE_TYPE.put("long", new PrimitiveType(long.class, o -> Long.valueOf(o)));
        PRIMITIVE_TYPE.put("short", new PrimitiveType(short.class, o -> Short.valueOf(o)));
        PRIMITIVE_TYPE.put("byte", new PrimitiveType(byte.class, o -> Byte.valueOf(o)));
        PRIMITIVE_TYPE.put("boolean", new PrimitiveType(boolean.class, o -> Boolean.valueOf(o)));
        PRIMITIVE_TYPE.put("char", new PrimitiveType(char.class, o -> o.charAt(0)));
        PRIMITIVE_TYPE.put("float", new PrimitiveType(float.class, o -> Float.valueOf(o)));
    }

    /**
     * Get class type by name.
     *
     * @param className className
     * @return the type to invoke
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public static Class getParamClass(final String className) throws ClassNotFoundException {
        if (PRIMITIVE_TYPE.keySet().contains(className)) {
            return PRIMITIVE_TYPE.get(className).getClazz();
        } else {
            return Class.forName(className);
        }
    }

    /**
     * Get proxy class name to get tars proxy.
     *
     * @param metaData metaData
     * @return className
     */
    public static String gerPrxName(final MetaData metaData) {
        return metaData.getPath().replace("/", "") + metaData.getMethodName() + "Prx";
    }

    /**
     * Get methodName to get tars proxy.
     *
     * @param metaData metaData
     * @return methodName
     */
    public static String gerMethodName(final MetaData metaData) {
        return "promise_" + metaData.getMethodName();
    }

    /**
     * Get objectName to get tars proxy.
     *
     * @param metaData metaData
     * @return objectName
     */
    public static String gerObjectName(final MetaData metaData) {
        String[] ipAndPort = metaData.getAppName().split(":");
        return metaData.getServiceName() + "@tcp -h " + ipAndPort[0] + " -p " + ipAndPort[1];
    }

    /**
     * Get param to invoke tars server.
     *
     * @param paramTypes paramTypes
     * @param paramNames paramNames
     * @param body body
     * @return the param to invoke
     */
    public static Object[] getParamArray(final Class[] paramTypes, final String[] paramNames, final String body) {
        Map bodyMap = GsonUtils.getInstance().convertToMap(body);
        Object[] param = new Object[paramNames.length];
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            Class paramType = paramTypes[i];
            if (PRIMITIVE_TYPE.keySet().contains(paramType.getName())) {
                param[i] = PRIMITIVE_TYPE.get(paramType.getName()).getFunc().apply((String) bodyMap.get(paramName));
            } else {
                param[i] = bodyMap.get(paramName);
            }
        }
        return param;
    }

    @AllArgsConstructor
    @Getter
    static class PrimitiveType {
        private Class clazz;

        private Function<String, Object> func;
    }
}
