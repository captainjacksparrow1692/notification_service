package uzumtech.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uzumtech.notification.entity.Merchant;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByLogin(String login);

    Optional<Merchant> findByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    boolean existsByTaxNumber(String taxNumber);
}
