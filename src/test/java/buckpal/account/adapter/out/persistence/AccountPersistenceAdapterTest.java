package buckpal.account.adapter.out.persistence;

import buckpal.account.domain.Account;
import buckpal.account.domain.Account.AccountId;
import buckpal.account.domain.ActivityWindow;
import buckpal.account.domain.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static buckpal.account.common.AccountTestData.defaultAccount;
import static buckpal.account.common.ActivityTestData.defaultActivity;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({AccountPersistenceAdapter.class, AccountMapper.class})
class AccountPersistenceAdapterTest {
    @Autowired
    private AccountPersistenceAdapter accountPersistenceAdapter;

    @Autowired
    private ActivityRepository activityRepository;

    @Test
    @Sql(value = "classpath:AccountPersistenceAdapterTest.sql")
    void loadsAccount() {
        Account account = accountPersistenceAdapter.loadAccount(
                new AccountId(1L),
                LocalDateTime.of(2018, 8, 10, 0, 0));

        assertThat(account.getActivityWindow().getActivities()).hasSize(2);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(500));
    }

    @Test
    void updatesActivities() {
        Account account = defaultAccount()
                .withBaselineBalance(Money.of(555L))
                .withActivityWindow(new ActivityWindow(
                        defaultActivity()
                                .withId(null)
                                .withMoney(Money.of(1L))
                                .build()))
                .build();

        accountPersistenceAdapter.updateActivities(account);
        assertThat(activityRepository.count()).isEqualTo(1);

        ActivityJpaEntity savedActivity = activityRepository.findAll().get(0);
        assertThat(savedActivity.getAmount()).isEqualTo(1L);
    }
}