package io.plantya.management.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class UserSequenceRepository {

    private final EntityManager em;

    public UserSequenceRepository(EntityManager em) {
        this.em = em;
    }

    public long nextIndex() {
        return ((Number) em
                .createNativeQuery("SELECT nextval('user_code_seq')")
                .getSingleResult()
        ).longValue();
    }
}
