package uzumtech.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uzumtech.notification.entity.Merchant;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
