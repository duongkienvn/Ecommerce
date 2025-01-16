package com.project.shopapp.repository.custom.impl;

import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.model.request.ProductRequest;
import com.project.shopapp.repository.custom.ProductRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    private void joinTable(StringBuilder join, ProductRequest productRequest) {
        if (productRequest.getCategoryName() != null) {
            join.append(" join categories c on c.id = p.category_id");
        }
    }

    public void query(StringBuilder where, ProductRequest productRequest) {
        if (productRequest.getName() != null) {
            where.append(" and p.name like '%" + productRequest.getName() + "%' ");
        }

        if (productRequest.getPriceFrom() != null || productRequest.getPriceTo() != null) {
            if (productRequest.getPriceFrom() != null) {
                where.append(" and p.price >= " + productRequest.getPriceFrom());
            }
            if (productRequest.getPriceTo() != null) {
                where.append(" and p.price <= " + productRequest.getPriceTo());
            }
        }

        if (productRequest.getCategoryName() != null) {
            where.append(" and c.name = " + "\"" + productRequest.getCategoryName() + "\"");
        }
    }

    @Override
    public Page<ProductEntity> findProduct(ProductRequest productRequest, Pageable pageable) {
        StringBuilder sql = new StringBuilder("select p.* from products p ");
        StringBuilder join = new StringBuilder();
        StringBuilder where = new StringBuilder(" where 1 = 1 ");
        joinTable(join, productRequest);
        query(where, productRequest);
        where.append(" group by p.id ");
        sql.append(join).append(where);
        Query query = entityManager.createNativeQuery(sql.toString(), ProductEntity.class);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<ProductEntity> productEntities = query.getResultList();

        Query countQuery = entityManager.createNativeQuery("select count(*) from products p" + join + where);
        Long totalElements = ((Number) countQuery.getResultList().size()).longValue();

        return new PageImpl<>(productEntities, pageable, totalElements);
    }
}
