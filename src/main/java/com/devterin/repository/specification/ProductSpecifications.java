package com.devterin.repository.specification;

import com.devterin.entity.Attribute;
import com.devterin.entity.Product;
import com.devterin.entity.Variant;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class ProductSpecifications {
    private ProductSpecifications() {
    }

    public static Specification<Product> catalog(
            String search,
            String category,
            Integer minPrice,
            Integer maxPrice,
            Collection<String> sizes,
            Collection<String> colors,
            String sort
    ) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addSearchPredicate(predicates, root, builder, search);
            addCategoryPredicate(predicates, root, builder, category);
            addPricePredicate(predicates, root, query, builder, minPrice, true);
            addPricePredicate(predicates, root, query, builder, maxPrice, false);
            addAttributePredicate(predicates, root, query, builder, sizes, "size");
            addAttributePredicate(predicates, root, query, builder, colors, "color");

            if (!isCountQuery(query)) {
                applySort(root, query, builder, sort);
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static void addSearchPredicate(
            List<Predicate> predicates,
            Root<Product> root,
            CriteriaBuilder builder,
            String search
    ) {
        if (search == null || search.isBlank()) return;

        String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
        predicates.add(builder.or(
                builder.like(builder.lower(root.get("name")), pattern),
                builder.like(builder.lower(root.get("description")), pattern)
        ));
    }

    private static void addCategoryPredicate(
            List<Predicate> predicates,
            Root<Product> root,
            CriteriaBuilder builder,
            String category
    ) {
        if (category == null || category.isBlank()) return;

        Join<Product, ?> categoryJoin = root.join("category", JoinType.INNER);
        predicates.add(builder.equal(
                builder.lower(categoryJoin.get("name")),
                category.trim().toLowerCase(Locale.ROOT)
        ));
    }

    private static void addPricePredicate(
            List<Predicate> predicates,
            Root<Product> root,
            CriteriaQuery<?> query,
            CriteriaBuilder builder,
            Integer price,
            boolean minimum
    ) {
        if (price == null) return;

        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<Variant> variant = subquery.from(Variant.class);
        subquery.select(builder.min(variant.get("price")));
        subquery.where(builder.equal(variant.get("product"), root));

        predicates.add(minimum
                ? builder.greaterThanOrEqualTo(subquery, price)
                : builder.lessThanOrEqualTo(subquery, price));
    }

    private static void addAttributePredicate(
            List<Predicate> predicates,
            Root<Product> root,
            CriteriaQuery<?> query,
            CriteriaBuilder builder,
            Collection<String> values,
            String attributeType
    ) {
        if (values == null || values.isEmpty()) return;

        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Variant> variant = subquery.from(Variant.class);
        Join<Variant, Attribute> attribute = variant.join("attributes", JoinType.INNER);
        Join<Attribute, ?> type = attribute.join("attributeType", JoinType.INNER);

        Expression<String> typeName = builder.lower(type.get("name"));
        Expression<String> attributeValue = builder.lower(attribute.get("value"));
        List<String> normalizedValues = values.stream()
                .map(value -> value.toLowerCase(Locale.ROOT))
                .toList();

        Predicate typePredicate = attributeType.equals("size")
                ? builder.like(typeName, "%size%")
                : builder.or(
                builder.like(typeName, "%color%")
        );

        subquery.select(builder.literal(1L));
        subquery.where(
                builder.equal(variant.get("product"), root),
                typePredicate,
                attributeValue.in(normalizedValues)
        );
        predicates.add(builder.exists(subquery));
    }

    private static void applySort(
            Root<Product> root,
            CriteriaQuery<?> query,
            CriteriaBuilder builder,
            String sort
    ) {
        List<Order> orders = new ArrayList<>();
        if ("price_asc".equals(sort) || "price_desc".equals(sort)) {
            Subquery<Integer> price = query.subquery(Integer.class);
            Root<Variant> variant = price.from(Variant.class);
            price.select(builder.min(variant.get("price")));
            price.where(builder.equal(variant.get("product"), root));
            orders.add("price_asc".equals(sort) ? builder.asc(price) : builder.desc(price));
        } else if ("sold_desc".equals(sort)) {
            Subquery<Long> sold = query.subquery(Long.class);
            Root<Variant> variant = sold.from(Variant.class);
            sold.select(builder.sumAsLong(variant.get("soldQuantity")));
            sold.where(builder.equal(variant.get("product"), root));
            orders.add(builder.desc(sold));
        }

        orders.add(builder.desc(root.get("id")));
        query.orderBy(orders);
    }

    private static boolean isCountQuery(CriteriaQuery<?> query) {
        Class<?> resultType = query.getResultType();
        return resultType == Long.class || resultType == long.class;
    }
}
