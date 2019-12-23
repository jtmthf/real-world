package com.jtmthf.realworld.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonTypeIdResolver(WrapWith.TypeIdResolver.class)
@JacksonAnnotationsInside
public @interface WrapWith {
    String value();

    class TypeIdResolver extends TypeIdResolverBase {

        @Override
        public String idFromValue(Object value) {
            return idFromClass(value.getClass());
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> type) {
            if (value == null) {
                return idFromClass(type);
            }
            return idFromValue(value);
        }

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.CUSTOM;
        }

        private String idFromClass(Class<?> clazz) {
            WrapWith wrapWith = clazz.getAnnotation(WrapWith.class);
            return wrapWith.value();
        }
    }
}
