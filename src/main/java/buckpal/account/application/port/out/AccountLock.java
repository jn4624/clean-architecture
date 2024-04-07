package buckpal.account.application.port.out;

import buckpal.account.domain.Account.AccountId;

public interface AccountLock {
    void lockAccount(AccountId accountId);

    void releaseAccount(AccountId accountId);
}
