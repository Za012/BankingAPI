package io.swagger.service;

import io.swagger.QueryBuilder.Specifications.AccountSpecification;
import io.swagger.QueryBuilder.SpecSearchCriteria;
import io.swagger.model.*;
import io.swagger.repository.AccountRepository;
import io.swagger.repository.IbanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AccountService extends AbstractService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private IbanRepository ibanRepository;

    private BalanceBehaviour balanceBehaviour;

    private ExecutorService service = Executors.newCachedThreadPool();

    @Autowired
    private VaultService vault;

    //new Transaction(new BigDecimal("60.10"),"EUR", "NL02INGB0154356789", CategoryEnum.ENTERTAINMENT, "NL02INGB0154356789", "NL02INGB0153457789", "12-05-2019 22:24:10", StatusEnum.PROCESSED)

    public AccountService(AccountRepository accountRepository, IbanRepository ibanRepository) {
        this.accountRepository = accountRepository;
        this.ibanRepository = ibanRepository;
    }

    public List<Account> getAccounts(String search) {
        Specification<Account> spec = getBuilder(search).build(searchCriteria -> new AccountSpecification((SpecSearchCriteria) searchCriteria));
        return accountRepository.findAll(spec);
    }

    //region might not be needed cause of getAccounts
    public Iterable<Account> getSavings() {
        List<Account> accounts = new ArrayList<>();
        for (Account acc : accountRepository.findAll()) {
            if (acc instanceof SavingsAccount) {
                accounts.add(acc);
            }
        }
        return accounts;
    }


    public Iterable<Account> getCurrents() {
        List<Account> accounts = new ArrayList<>();
        for (Account acc : accountRepository.findAll()) {
            if (acc instanceof CurrentAccount) {
                accounts.add(acc);
            }
        }
        return accounts;
    }

    public void registerAccount(Account account) {
        do{
            account.getIban().buildIban();
        }while(ibanRepository.existsByIbanCode(account.getIban().getIbanCode()));

        account.setStatus(Account.AccountStatusEnum.OPEN);

        vault.addBalance(account.getBalance());
        accountRepository.save(account);
    }

    public void registerSavingsAccount(Account account){
        accountRepository.save(account);
    }

    public void deleteAccount(long id) {
        accountRepository.delete(accountRepository.getOne(id));
    }

    public void updateAccountStatus(Long id) {
        Account account = accountRepository.getOne(id);
        if (account.getStatus() == Account.AccountStatusEnum.OPEN){
            account.setStatus(Account.AccountStatusEnum.ClOSED);
        }else{
            account.setStatus(Account.AccountStatusEnum.OPEN);
        }
        accountRepository.save(account);
    }


    public Account getAccount(long id) {
        Account account = accountRepository.getOne(id);
        if (account != null) {
            return account;
        } else {
            throw new NoSuchElementException();
        }
    }

    public Account getAccountByIban(String iban) {
        Account account = accountRepository.getAccountByIban(iban);
        if(account != null){
            return account;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void balanceUpdate(Transaction transaction){
        Account senderAccount = getAccountByIban(transaction.getSender().getIbanCode());
        Account receiverAccount = getAccountByIban(transaction.getReceiver().getIbanCode());

        this.balanceBehaviour = new BalanceDecrease();
        senderAccount.setBalance(this.balanceBehaviour.updateBalance(senderAccount, transaction.getAmount()));
        senderAccount.addTransactionsItem(transaction);
        accountRepository.save(senderAccount);

        this.balanceBehaviour = new BalanceIncrease();
        receiverAccount.setBalance(this.balanceBehaviour.updateBalance(senderAccount, transaction.getAmount()));
        receiverAccount.addTransactionsItem(transaction);
        accountRepository.save(receiverAccount);
    }

    public boolean bothAccountsActive(Transaction transaction){

        Account senderAccount = getAccountByIban(transaction.getSender().getIbanCode());
        Account receiverAccount = getAccountByIban(transaction.getReceiver().getIbanCode());

        if(senderAccount.getStatus() == Account.AccountStatusEnum.ClOSED || receiverAccount.getStatus() == Account.AccountStatusEnum.ClOSED) return false;

        return true;

    }

    //endregion

//    public void registerAccount(Account account) {
//        do {
//            account.getIban().setBban(null);
//            account.getIban().buildIban();
//        } while (ibanRepository.existsByIbanCode(account.getIban().getIbanCode()));
//
//        accountRepository.save(account);
//    }

}
