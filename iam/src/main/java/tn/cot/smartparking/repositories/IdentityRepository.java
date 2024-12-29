package tn.cot.smartparking.repositories;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import tn.cot.smartparking.entities.Identity;

import java.util.Optional;

@Repository
public interface IdentityRepository extends CrudRepository<Identity, String> {
    Optional<Identity> findByEmail(String email);
    Optional<Identity> findByUsername(String username);
}
