package com.seungjoon.algo.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Pageable;

//TODO
public abstract class QuerydslUtils {

    public static <T> OrderSpecifier<?>[] getSort(Pageable pageable, EntityPathBase<T> qClass) {
        return pageable.getSort().stream()
                .map(sort -> {
                    Order direction = Order.valueOf(sort.getDirection().name());
                    return new OrderSpecifier(
                            direction,
                            Expressions.path(Object.class, qClass, sort.getProperty())
                    );
                }).toList()
                .toArray(new OrderSpecifier[0]);
    }
}
