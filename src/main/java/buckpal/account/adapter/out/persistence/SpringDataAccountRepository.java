package buckpal.account.adapter.out.persistence;

import buckpal.account.adapter.out.persistence.AccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, Long> {
}
