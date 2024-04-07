package buckpal.account.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
@Getter
public class Activity {
    private ActivityId id;
    private final Account.AccountId ownerAccountId;
    private final Account.AccountId sourceAccountId;
    private final Account.AccountId targetAccountId;
    private final LocalDateTime timestamp;
    private final Money money;

    public Activity(Account.AccountId ownerAccountId,
                    Account.AccountId sourceAccountId,
                    Account.AccountId targetAccountId,
                    LocalDateTime timestamp,
                    Money money) {
        this.id = null;
        this.ownerAccountId = ownerAccountId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.timestamp = timestamp;
        this.money = money;
    }

    @Value
    public static class ActivityId {
        private Long value;
    }
}
