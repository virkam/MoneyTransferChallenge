package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferMoneyControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void createAccounts() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
    	      .content("{\"accountId\":\"Id-456\",\"balance\":2000}")).andExpect(status().isCreated());
    
    Account account = accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
    
    Account account2 = accountsService.getAccount("Id-456");
    assertThat(account2.getAccountId()).isEqualTo("Id-456");
    assertThat(account2.getBalance()).isEqualByComparingTo("2000");
  }

  @Test
  public void sendMoney() throws Exception {
	  
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-456\",\"balance\":2000}")).andExpect(status().isCreated());
	  
    this.mockMvc.perform(post("/v1/moneytransfer").contentType(MediaType.APPLICATION_JSON)
      .content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"Id-456\", \"amount\":700}")).andExpect(status().isCreated());

	    Account accountFrom = accountsService.getAccount("Id-123");
	    Account accountTo = accountsService.getAccount("Id-456");
	    
	    assertThat(accountFrom.getBalance()).isEqualByComparingTo("300");
	    assertThat(accountTo.getBalance()).isEqualByComparingTo("2700");
}

  
  @Test
  public void sendMoneyNotEnoughBalance() throws Exception {
	  
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-456\",\"balance\":2000}")).andExpect(status().isCreated());
	  
    this.mockMvc.perform(post("/v1/moneytransfer").contentType(MediaType.APPLICATION_JSON)
      .content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"Id-456\", \"amount\":700}")).andExpect(status().isCreated());

    
    this.mockMvc.perform(post("/v1/moneytransfer").contentType(MediaType.APPLICATION_JSON)
    	      .content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"Id-456\", \"amount\":700}")).andExpect(status().isBadRequest());
    	  
    
	    Account accountFrom = accountsService.getAccount("Id-123");
	    Account accountTo = accountsService.getAccount("Id-456");
	    
	    assertThat(accountFrom.getBalance()).isEqualByComparingTo("300");
	    assertThat(accountTo.getBalance()).isEqualByComparingTo("2700");
}

  
 
  
  @Test
  public void transferMoneyNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/moneytransfer").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }
  
  @Test
  public void sendMoneyNoFromAccountId() throws Exception {
        
    this.mockMvc.perform(post("/v1/moneytransfer").contentType(MediaType.APPLICATION_JSON)
  	      .content("{\"toAccountId\":\"Id-456\", \"amount\":700}")).andExpect(status().isBadRequest());
  }
  
  @Test
  public void sendMoneyNoToAccountId() throws Exception {
        
    this.mockMvc.perform(post("/v1/moneytransfer").contentType(MediaType.APPLICATION_JSON)
  	      .content("{\"fromAccountId\":\"Id-123\", \"amount\":700}")).andExpect(status().isBadRequest());
  
  }
  
  @Test
  public void sendMoneyNegativeAmount() throws Exception {
    
	  this.mockMvc.perform(post("/v1/moneytransfer").contentType(MediaType.APPLICATION_JSON)
    	      .content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"Id-456\", \"amount\":-700}")).andExpect(status().isBadRequest());
	  
  }
  
  @Test
  public void sendMoneyEmptyFromAccountId() throws Exception {
    
	  this.mockMvc.perform(post("/v1/moneytransfer").contentType(MediaType.APPLICATION_JSON)
    	      .content("{\"fromAccountId\":\"\",\"toAccountId\":\"Id-456\", \"amount\":-700}")).andExpect(status().isBadRequest());
	  
  }
  
  @Test
  public void sendMoneyEmptyToAccountId() throws Exception {
    
	  this.mockMvc.perform(post("/v1/moneytransfer").contentType(MediaType.APPLICATION_JSON)
    	      .content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"\", \"amount\":-700}")).andExpect(status().isBadRequest());
	  
  }
  
   
}
