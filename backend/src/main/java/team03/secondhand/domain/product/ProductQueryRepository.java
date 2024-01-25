package team03.secondhand.domain.product;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team03.secondhand.domain.product.vo.ProductSearchCondition;
import team03.secondhand.domain.watchlist.Watchlist;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static team03.secondhand.domain.category.QCategory.*;
import static team03.secondhand.domain.location.QLocation.*;
import static team03.secondhand.domain.watchlist.QWatchlist.*;
import static team03.secondhand.domain.member.QMember.member;
import static team03.secondhand.domain.product.QProduct.product;

@Repository
public class ProductQueryRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public ProductQueryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        queryFactory = new JPAQueryFactory(entityManager);
    }

    public Optional<Product> getDetailProductBy(Long productId) {
        return Optional.ofNullable(queryFactory
                .select(product)
                .from(product)
                .join(product.member, member).fetchJoin()
                .join(product.location, location).fetchJoin()
                .join(product.category, category).fetchJoin()
                .where(product.productId.eq(productId))
                .fetchOne());
    }

    public List<Product> getMemberProductsBy(Long memberId, ProductSearchCondition condition, Pageable pageable) {
        return queryFactory
                .select(product)
                .from(product)
                .join(product.category, category).fetchJoin()
                .join(product.location, location).fetchJoin()
                .where(locationIdEqual(condition.getLocationId())
                        , categoryIdEqual(condition.getCategoryId())
                        , memberIdEqual(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.updatedAt.desc())
                .fetch();
    }

    public List<Product> getWatchProductsBy(Long memberId, Pageable pageable) {
        List<Watchlist> watchLists = queryFactory.select(watchlist)
                .from(watchlist)
                .where(watchlist.member.memberId.eq(memberId))
                .fetch();

        List<Long> productIds = watchLists.stream()
                .map(watch -> watch.getProduct().getProductId())
                .collect(Collectors.toList());

        return queryFactory
                .select(product)
                .from(product)
                .join(product.category, category).fetchJoin()
                .join(product.location, location).fetchJoin()
                .where(product.productId.in(productIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.updatedAt.desc())
                .fetch();
    }

    public List<Product> getProductsBy(ProductSearchCondition condition, Pageable pageable) {
        return queryFactory
                .select(product)
                .from(product)
                .join(product.category, category).fetchJoin()
                .join(product.location, location).fetchJoin()
                .where(locationIdEqual(condition.getLocationId())
                        ,categoryIdEqual(condition.getCategoryId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.updatedAt.desc())
                .fetch();
    }

    private BooleanExpression memberIdEqual(Long memberId) {
        return memberId != null ? product.member.memberId.eq(memberId) : null;
    }

    private BooleanExpression locationIdEqual(Long locationId) {
        return locationId != null ? product.location.locationId.eq(locationId) : null;
    }

    private BooleanExpression categoryIdEqual(Long categoryId) {
        return categoryId != null ? product.category.categoryId.eq(categoryId) : null;
    }


}
