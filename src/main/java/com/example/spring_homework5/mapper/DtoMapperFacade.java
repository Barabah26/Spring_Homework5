package com.example.spring_homework5.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.record.RecordModule;

public class DtoMapperFacade<E, D> {
    private final Class<E> entityClass;
    private final Class<D> dtoClass;

    private final ModelMapper modelMapper = new ModelMapper();

    public DtoMapperFacade(final Class<E> eClass, final Class<D> dClass) {
        entityClass = eClass;
        dtoClass = dClass;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.registerModule(new RecordModule());
    }

    public E convertToEntity(final D dto) {
        final E entity = modelMapper.map(dto, entityClass);

        decorateEntity(entity, dto);

        return entity;
    }

    public D convertToDto(final E entity) {
        final D dto = modelMapper.map(entity, dtoClass);

        decorateDto(dto, entity);

        return dto;
    }

    protected void decorateEntity(final E entity, final D dto) {

    }

    protected void decorateDto(final D dto, final E entity) {

    }
}
