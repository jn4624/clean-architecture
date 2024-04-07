package buckpal.account.adapter.out.persistence;

import buckpal.account.application.port.out.LoadAccountPort;
import buckpal.account.application.port.out.UpdateAccountStatePort;
import buckpal.account.domain.Account;
import buckpal.account.domain.Activity;
import buckpal.common.PersistenceAdapter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static buckpal.account.domain.Account.AccountId;

@RequiredArgsConstructor
@PersistenceAdapter
public class AccountPersistenceAdapter implements LoadAccountPort, UpdateAccountStatePort {
    private final SpringDataAccountRepository accountRepository;
    private final ActivityRepository activityRepository;
    private final AccountMapper accountMapper;

    @Override
    public Account loadAccount(AccountId accountId, LocalDateTime baselineDate) {
        AccountJpaEntity account = accountRepository.findById(accountId.getValue())
                .orElseThrow(EntityNotFoundException::new);

        List<ActivityJpaEntity> activities = activityRepository.findByOwnerSince(accountId.getValue(), baselineDate);

        Long withdrawalBalance = orZero(activityRepository.getWithdrawalBalanceUntil(accountId.getValue(), baselineDate));

        Long depositBalance = orZero(activityRepository.getDepositBalanceUntil(accountId.getValue(), baselineDate));

        return accountMapper.mapToDomainEntity(
                account,
                activities,
                withdrawalBalance,
                depositBalance
        );
    }

    @Override
    public void updateActivities(Account account) {
        for (Activity activity : account.getActivityWindow().getActivities()) {
            if (activity.getId() == null) {
                activityRepository.save(accountMapper.mapToJapEntity(activity));
            }
        }
    }

    private Long orZero(Long value) {
        return value == null ? 0L : value;
    }
}
