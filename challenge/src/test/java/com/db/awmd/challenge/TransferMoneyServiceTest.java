package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferMoneyReqDetails;
import com.db.awmd.challenge.exception.BankTransactionException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransferMoneyService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransferMoneyServiceTest {

  @Autowired
  private TransferMoneyService transferMoneyServiceTest;
  
  @Autowired
  private AccountsService accountsService;

  
  @Test
  public void sendMoneyTransfer() throws Exception {
	  Account account = new Account("Id-123");
	    account.setBalance(new BigDecimal(1000));
	    this.accountsService.createAccount(account);
	    Account account2 = new Account("Id-456");
	    account2.setBalance(new BigDecimal(2000));
	    this.accountsService.createAccount(account2);
	    
	    
	  TransferMoneyReqDetails transferMoneyReqDetails  = new TransferMoneyReqDetails();
	  transferMoneyReqDetails.setFromAccountId("Id-123");
	  transferMoneyReqDetails.setToAccountId("Id-456");
	  transferMoneyReqDetails.setAmount(700D);
    
    transferMoneyServiceTest.sendMoney(transferMoneyReqDetails.getFromAccountId(), transferMoneyReqDetails.getToAccountId(), transferMoneyReqDetails.getAmount());

    
    assertThat(this.accountsService.getAccount("Id-123").getBalance()).isEqualByComparingTo("300");
    
    assertThat(this.accountsService.getAccount("Id-456").getBalance()).isEqualByComparingTo("2700");
  }
  

  @Test
  public void sendMoney_failsOnEnounghBalInFromAcc() throws Exception {

  
	    
	  TransferMoneyReqDetails transferMoneyReqDetails  = new TransferMoneyReqDetails();
	  transferMoneyReqDetails.setFromAccountId("Id-123");
	  transferMoneyReqDetails.setToAccountId("Id-456");
	  transferMoneyReqDetails.setAmount(700D);
	  Account account = this.accountsService.getAccount(transferMoneyReqDetails.getFromAccountId());
  try
  {
	  transferMoneyServiceTest.sendMoney(transferMoneyReqDetails.getFromAccountId(), transferMoneyReqDetails.getToAccountId(), transferMoneyReqDetails.getAmount());
	  fail("Should have failed when balance in not available in from account Id");
  }catch(BankTransactionException ex)
  {
	  assertThat(ex.getMessage()).isEqualTo("The money in the account '" + transferMoneyReqDetails.getFromAccountId() + "' is not enough (" + account.getBalance() + ")");
              
  }
 

  }

}
