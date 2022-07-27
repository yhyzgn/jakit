package com.yhy.jakit.starter.dynamic.datasource.mongo.repository;

import com.mongodb.client.result.DeleteResult;
import com.yhy.jakit.starter.dynamic.datasource.mongo.dynamic.DynamicMongoTemplate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.StreamUtils;
import org.springframework.data.util.Streamable;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * 支持动态数据源的 MongoRepository
 * <p>
 * Created on 2021-12-08 16:34
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class DynamicMongoRepository<T, ID> implements MongoRepository<T, ID>, ApplicationContextAware {
    private final Class<T> typeT;
    private final Class<ID> typeId;
    private final MongoEntityInformation<T, ID> metadata;
    private DynamicMongoTemplate dynamicMongoTemplate;

    @SuppressWarnings("unchecked")
    public DynamicMongoRepository() {
        Type clazz = getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) clazz).getActualTypeArguments();
        typeT = (Class<T>) types[0];
        typeId = (Class<ID>) types[1];

        this.metadata = entityInformationFor();
    }

    private MongoEntityInformation<T, ID> entityInformationFor() {
        TypeInformation<T> typeInformation = ClassTypeInformation.from(typeT);
        MongoPersistentEntity<T> mongoPersistentEntity = new BasicMongoPersistentEntity<>(typeInformation);
        return new MappingMongoEntityInformation<>(mongoPersistentEntity, typeId);
    }

    private MongoTemplate currentTemplate() {
        return dynamicMongoTemplate.current();
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.dynamicMongoTemplate = context.getBean(DynamicMongoTemplate.class);
    }

    // -------------------------------------------------------------------------
    // Methods from CrudRepository
    // -------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Object)
     */
    @Override
    public <S extends T> S save(S entity) {

        Assert.notNull(entity, "Entity must not be null!");

        if (metadata.isNew(entity)) {
            return currentTemplate().insert(entity, metadata.getCollectionName());
        }

        return currentTemplate().save(entity, metadata.getCollectionName());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.repository.MongoRepository#saveAll(java.lang.Iterable)
     */
    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {

        Assert.notNull(entities, "The given Iterable of entities not be null!");

        Streamable<S> source = Streamable.of(entities);
        boolean allNew = source.stream().allMatch(metadata::isNew);

        if (allNew) {

            List<S> result = source.stream().collect(Collectors.toList());
            return new ArrayList<>(currentTemplate().insert(result, metadata.getCollectionName()));
        }

        return source.stream().map(this::save).collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findById(java.io.Serializable)
     */
    @Override
    public Optional<T> findById(ID id) {

        Assert.notNull(id, "The given id must not be null!");

        return Optional.ofNullable(
            currentTemplate().findById(id, metadata.getJavaType(), metadata.getCollectionName()));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#existsById(java.lang.Object)
     */
    @Override
    public boolean existsById(ID id) {

        Assert.notNull(id, "The given id must not be null!");

        return currentTemplate().exists(getIdQuery(id), metadata.getJavaType(),
            metadata.getCollectionName());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll()
     */
    @Override
    public List<T> findAll() {
        return findAll(new Query());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAllById(java.lang.Iterable)
     */
    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {

        Assert.notNull(ids, "The given Ids of entities not be null!");

        return findAll(getIdQuery(ids));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#count()
     */
    @Override
    public long count() {
        return currentTemplate().count(new Query(), metadata.getCollectionName());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#deleteById(java.lang.Object)
     */
    @Override
    public void deleteById(ID id) {

        Assert.notNull(id, "The given id must not be null!");

        currentTemplate().remove(getIdQuery(id), metadata.getJavaType(), metadata.getCollectionName());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
     */
    @Override
    public void delete(T entity) {

        Assert.notNull(entity, "The given entity must not be null!");

        DeleteResult deleteResult = currentTemplate().remove(entity, metadata.getCollectionName());

        if (metadata.isVersioned() && deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() == 0) {
            throw new OptimisticLockingFailureException(String.format(
                "The entity with id %s with version %s in %s cannot be deleted! Was it modified or deleted in the meantime?",
                metadata.getId(entity), metadata.getVersion(entity),
                metadata.getCollectionName()));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#deleteAllById(java.lang.Iterable)
     */
    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {

        Assert.notNull(ids, "The given Iterable of ids must not be null!");

        currentTemplate().remove(getIdQuery(ids), metadata.getJavaType(),
            metadata.getCollectionName());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
     */
    @Override
    public void deleteAll(Iterable<? extends T> entities) {

        Assert.notNull(entities, "The given Iterable of entities must not be null!");

        entities.forEach(this::delete);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#deleteAll()
     */
    @Override
    public void deleteAll() {
        currentTemplate().remove(new Query(), metadata.getCollectionName());
    }

    // -------------------------------------------------------------------------
    // Methods from PagingAndSortingRepository
    // -------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<T> findAll(Pageable pageable) {

        Assert.notNull(pageable, "Pageable must not be null!");

        long count = count();
        List<T> list = findAll(new Query().with(pageable));

        return new PageImpl<>(list, pageable, count);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
     */
    @Override
    public List<T> findAll(Sort sort) {

        Assert.notNull(sort, "Sort must not be null!");

        return findAll(new Query().with(sort));
    }

    // -------------------------------------------------------------------------
    // Methods from MongoRepository
    // -------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.repository.MongoRepository#insert(java.lang.Object)
     */
    @Override
    public <S extends T> S insert(S entity) {

        Assert.notNull(entity, "Entity must not be null!");

        return currentTemplate().insert(entity, metadata.getCollectionName());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.repository.MongoRepository#insert(java.lang.Iterable)
     */
    @Override
    public <S extends T> List<S> insert(Iterable<S> entities) {

        Assert.notNull(entities, "The given Iterable of entities not be null!");

        Collection<S> list = toCollection(entities);

        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        return new ArrayList<>(currentTemplate().insertAll(list));
    }

    // -------------------------------------------------------------------------
    // Methods from QueryByExampleExecutor
    // -------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.QueryByExampleExecutor#findOne(org.springframework.data.domain.Example)
     */
    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {

        Assert.notNull(example, "Sample must not be null!");

        Query query = new Query(new Criteria().alike(example)) //
            .collation(metadata.getCollation());

        return Optional
            .ofNullable(currentTemplate().findOne(query, example.getProbeType(), metadata.getCollectionName()));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.repository.MongoRepository#findAllByExample(org.springframework.data.domain.Example)
     */
    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return findAll(example, Sort.unsorted());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.repository.MongoRepository#findAllByExample(org.springframework.data.domain.Example, org.springframework.data.domain.Sort)
     */
    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {

        Assert.notNull(example, "Sample must not be null!");
        Assert.notNull(sort, "Sort must not be null!");

        Query query = new Query(new Criteria().alike(example)) //
            .collation(metadata.getCollation()) //
            .with(sort);

        return currentTemplate().find(query, example.getProbeType(), metadata.getCollectionName());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.repository.MongoRepository#findAllByExample(org.springframework.data.domain.Example, org.springframework.data.domain.Pageable)
     */
    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {

        Assert.notNull(example, "Sample must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");

        Query query = new Query(new Criteria().alike(example)) //
            .collation(metadata.getCollation()).with(pageable); //

        List<S> list = currentTemplate().find(query, example.getProbeType(), metadata.getCollectionName());

        return PageableExecutionUtils.getPage(list, pageable,
            () -> currentTemplate().count(Query.of(query).limit(-1).skip(-1), example.getProbeType(), metadata.getCollectionName()));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.QueryByExampleExecutor#count(org.springframework.data.domain.Example)
     */
    @Override
    public <S extends T> long count(Example<S> example) {

        Assert.notNull(example, "Sample must not be null!");

        Query query = new Query(new Criteria().alike(example)) //
            .collation(metadata.getCollation());

        return currentTemplate().count(query, example.getProbeType(), metadata.getCollectionName());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.QueryByExampleExecutor#exists(org.springframework.data.domain.Example)
     */
    @Override
    public <S extends T> boolean exists(Example<S> example) {

        Assert.notNull(example, "Sample must not be null!");

        Query query = new Query(new Criteria().alike(example)) //
            .collation(metadata.getCollation());

        return currentTemplate().exists(query, example.getProbeType(), metadata.getCollectionName());
    }

    // -------------------------------------------------------------------------
    // Utility methods
    // -------------------------------------------------------------------------

    private Query getIdQuery(Object id) {
        return new Query(getIdCriteria(id));
    }

    private Criteria getIdCriteria(Object id) {
        return where(metadata.getIdAttribute()).is(id);
    }

    private Query getIdQuery(Iterable<? extends ID> ids) {

        return new Query(new Criteria(metadata.getIdAttribute())
            .in(toCollection(ids)));
    }

    private static <E> Collection<E> toCollection(Iterable<E> ids) {
        return ids instanceof Collection ? (Collection<E>) ids
            : StreamUtils.createStreamFromIterator(ids.iterator()).collect(Collectors.toList());
    }

    private List<T> findAll(@Nullable Query query) {

        if (query == null) {
            return Collections.emptyList();
        }

        return currentTemplate().find(query, metadata.getJavaType(), metadata.getCollectionName());
    }

}
