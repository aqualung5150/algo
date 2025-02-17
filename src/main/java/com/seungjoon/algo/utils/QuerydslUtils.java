package com.seungjoon.algo.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Pageable;

public abstract class QuerydslUtils {

    public static <T> OrderSpecifier<?>[] getSort(Pageable pageable, EntityPathBase<T> qClass) {

        PathBuilder<T> pathBuilder = new PathBuilder<>(qClass.getType(), qClass.getMetadata());

        return pageable.getSort().stream().map(order -> new OrderSpecifier<>(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.getString(order.getProperty())
        )).toArray(OrderSpecifier[]::new);
    }
}
