package buckpal.account.application.port.in;

import buckpal.account.domain.Money;

import static buckpal.account.domain.Account.AccountId;

public interface GetAccountBalanceQuery {
    Money getAccountBalance(AccountId accountId);
}
