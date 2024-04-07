package buckpal.account.application.port.out;

import buckpal.account.domain.Account;
import buckpal.account.domain.Account.AccountId;

import java.time.LocalDateTime;

public interface LoadAccountPort {
    Account loadAccount(AccountId accountId, LocalDateTime baselineDate);
}
