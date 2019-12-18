package com.db.awmd.challenge.service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.BankTransactionException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class TransferMoneyService {
	

  @Getter
  private final AccountsRepository accountsRepository;
  
  private final NotificationService notificationService;

  //
  private Lock bankLock = new ReentrantLock(); 
  
  @Autowired
  public TransferMoneyService(AccountsRepository accountsRepository,NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  

  

  // 
  
  public void addAmount(String id, Double amount) throws BankTransactionException {
      Account account = this.getAccount(id);
      if (account == null) {
          throw new BankTransactionException("Account not found " + id);
      }
      Double newBalance = Double.valueOf(account.getBalance()+"" )+ amount;
      if (newBalance < 0) {
          throw new BankTransactionException(
                  "The money in the account '" + id + "' is not enough (" + account.getBalance() + ")");
      }
      account.setBalance(new BigDecimal(newBalance));
  }


  public void sendMoney(String fromAccountId, String toAccountId, Double amount) throws BankTransactionException, InterruptedException 
  {
	  Account fromAccountBkp = null ;
	  Account toAccountBkp = null;
	  try 
	  {
		  boolean flag = bankLock.tryLock(100, TimeUnit.MILLISECONDS); // wait for lock for some time if other request acquires locked  
		  
		  if(flag)
		  {
			  		fromAccountBkp = this.getAccount(fromAccountId);
			  		fromAccountBkp = fromAccountBkp != null ? new Account(fromAccountBkp.getAccountId(), fromAccountBkp.getBalance())
							: null;
			  		toAccountBkp = this.getAccount(toAccountId);
			  		toAccountBkp = toAccountBkp != null ? new Account(toAccountBkp.getAccountId(), toAccountBkp.getBalance()) : null;
	
					addAmount(toAccountId, amount);
					addAmount(fromAccountId, -amount);
					Account account = this.getAccount(fromAccountId);
					notificationService.notifyAboutTransfer(account,
							"Transfred Money " + account.getBalance() + " on Acoount " + toAccountId);
					account = this.getAccount(toAccountId);
					notificationService.notifyAboutTransfer(account,
							"Received Money " + account.getBalance() + " from Acoount " + fromAccountId);
		  }
			  
		  }catch(BankTransactionException e)
		  {
			  //This is used to rollback the transaction if any account is not found or any technical issue raised.
			  rollBackTransactionInMemory(fromAccountBkp); 
			  rollBackTransactionInMemory(toAccountBkp);
			  throw e;
		  }
		  catch (InterruptedException e) 
		  {
			  throw new BankTransactionException("Please try again !");
			  //throw e;
		}
		finally 
		{
			  bankLock.unlock();
		}
  }
  	//To rollback the transaction  
	public void rollBackTransactionInMemory(Account account)
	{
		if(account != null )
		{
			Account accInMemoryObj = this.getAccount(account.getAccountId());
			accInMemoryObj.setBalance(account.getBalance());
		}
	}


}
