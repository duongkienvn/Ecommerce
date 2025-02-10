package com.project.shopapp.converter;

import com.project.shopapp.model.request.ProductRequest;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ProductRequestMapper {
    public static Map<String, Object> toMap(ProductRequest productRequest) {
        Map<String, Object> map = new HashMap<>();
        BeanWrapper beanWrapper = new BeanWrapperImpl(productRequest);

        for (var propertyDescriptor : beanWrapper.getPropertyDescriptors()) {
            String propertyName = propertyDescriptor.getName();
            Object value = beanWrapper.getPropertyValue(propertyName);
            if (value != null) {
                map.put(propertyName, value);
            }
        }

        return map;
    }

    public static ProductRequest toProductRequest(Map<String, Object> productMap) {
        ProductRequest productRequest = new ProductRequest();

        productMap.forEach((key, value) -> {
            try {
                if (!key.equals("page") && !key.equals("size")) {
                    Field field = ProductRequest.class.getDeclaredField(key);
                    field.setAccessible(true);

                    Class<?> fieldType = field.getType();
                    if (fieldType == Float.class) {
                        if (value != null && !String.valueOf(value).isEmpty()) {
                            value = Float.parseFloat(value.toString());
                        } else {
                            value = Float.parseFloat("0");
                        }
                    }

                    field.set(productRequest, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        return productRequest;
    }
}
