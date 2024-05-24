package buckpal.account;

import buckpal.account.application.port.out.LoadAccountPort;
import buckpal.account.domain.Account;
import buckpal.account.domain.Account.AccountId;
import buckpal.account.domain.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SendMoneySystemTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private LoadAccountPort loadAccountPort;

    @Test
    @Sql("classpath:SendMoneySystemTest.sql")
    void sendMoney() {
        Money initialSourceBalance = sourceAccount().calculateBalance();
        Money initialTargetBalance = targetAccount().calculateBalance();

        ResponseEntity response = whenSendMoney(
                sourceAccountId(),
                targetAccountId(),
                transferredAmount());

        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(sourceAccount().calculateBalance())
                .isEqualTo(initialSourceBalance.minus(transferredAmount()));
        then(targetAccount().calculateBalance())
                .isEqualTo(initialTargetBalance.plus(transferredAmount()));
    }

    private Account sourceAccount() {
        return loadAccount(sourceAccountId());
    }

    private Account targetAccount() {
        return loadAccount(targetAccountId());
    }

    private Account loadAccount(AccountId accountId) {
        return loadAccountPort.loadAccount(
                accountId,
                LocalDateTime.now());
    }

    private ResponseEntity whenSendMoney(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money amount) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);

        return testRestTemplate.exchange(
                "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                HttpMethod.POST,
                httpEntity,
                Object.class,
                sourceAccountId.getValue(),
                targetAccountId.getValue(),
                amount.getAmount());
    }

    private Money transferredAmount() {
        return Money.of(500L);
    }

    private Money balanceOf(AccountId accountId) {
        Account account = loadAccountPort.loadAccount(accountId, LocalDateTime.now());
        return account.calculateBalance();
    }

    private AccountId sourceAccountId() {
        return new AccountId(1L);
    }

    private AccountId targetAccountId() {
        return new AccountId(2L);
    }
}
