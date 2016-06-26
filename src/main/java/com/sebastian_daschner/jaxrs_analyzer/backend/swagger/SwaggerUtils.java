/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;

import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;

/**
 * Contains Swagger JSON type functionality.
 *
 * @author Sebastian Daschner
 */
final class SwaggerUtils {

    private SwaggerUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts the given Java type to the Swagger JSON type.
     *
     * @param type The Java type definition
     * @return The Swagger type
     */
    static SwaggerType toSwaggerType(final String type) {
        if (STRING.equals(type))
            return SwaggerType.STRING;

        if (BOOLEAN.equals(type) || PRIMITIVE_BOOLEAN.equals(type))
            return SwaggerType.BOOLEAN;

        if (INTEGER_TYPES.contains(type))
            return SwaggerType.INTEGER;

        if (DOUBLE_TYPES.contains(type))
            return SwaggerType.NUMBER;

        // TODO
//        if (type.getCtClass().isArray())
//            return SwaggerType.ARRAY;

        try {
            return toSwaggerType(Class.forName(Type.getType(type).getClassName()));
        } catch (ClassNotFoundException e) {
            // TODO: log unexpected behaviour
            return SwaggerType.OBJECT;
        }
    }

    static SwaggerType toSwaggerType(final Class<?> type) {
        if (type != null) {
            if (type.isEnum()) {
                return SwaggerType.STRING;
            }

            try {
                type.getConstructor(String.class);
                return SwaggerType.STRING;
            } catch (NoSuchMethodException e) {
            }

            try {
                if (Modifier.isStatic(type.getDeclaredMethod("valueOf", String.class).getModifiers())) {
                    return SwaggerType.STRING;
                }
            } catch (NoSuchMethodException e) {
            }

            try {
                if (Modifier.isStatic(type.getDeclaredMethod("fromString", String.class).getModifiers())) {
                    return SwaggerType.STRING;
                }
            } catch (NoSuchMethodException e) {
            }
        }

        return SwaggerType.OBJECT;
    }
}