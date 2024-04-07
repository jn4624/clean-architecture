package buckpal.account.adapter.out.persistence;

import buckpal.account.domain.Account;
import buckpal.account.domain.Account.AccountId;
import buckpal.account.domain.Activity;
import buckpal.account.domain.ActivityWindow;
import buckpal.account.domain.Money;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static buckpal.account.domain.Activity.*;

@Component
public class AccountMapper {
    public Account mapToDomainEntity (
            AccountJpaEntity account,
            List<ActivityJpaEntity> activities,
            Long withdrawalBalance,
            Long depositBalance
    ) {
        Money baselineBalance = Money.subtract(Money.of(depositBalance), Money.of(withdrawalBalance));

        return Account.withId(
                new AccountId(account.getId()),
                baselineBalance,
                mapToActivityWindow(activities)
        );
    }

    public ActivityWindow mapToActivityWindow(List<ActivityJpaEntity> activities) {
        List<Activity> mappedActivities = new ArrayList<>();

        for (ActivityJpaEntity activity : activities) {
            mappedActivities.add(new Activity(
                    new ActivityId(activity.getId()),
                    new AccountId(activity.getOwnerAccountId()),
                    new AccountId(activity.getSourceAccountId()),
                    new AccountId(activity.getTargetAccountId()),
                    activity.getTimestamp(),
                    Money.of(activity.getAmount())
            ));
        }

        return new ActivityWindow(mappedActivities);
    }

    public ActivityJpaEntity mapToJapEntity(Activity activity) {
        return new ActivityJpaEntity(
                activity.getId() == null ? null : activity.getId().getValue(),
                activity.getTimestamp(),
                activity.getOwnerAccountId().getValue(),
                activity.getSourceAccountId().getValue(),
                activity.getTargetAccountId().getValue(),
                activity.getMoney().getAmount().longValue()
        );
    }
}
