package com.db.awmd.challenge.web;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.TransferMoneyReqDetails;
import com.db.awmd.challenge.exception.BankTransactionException;
import com.db.awmd.challenge.service.TransferMoneyService;

import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/v1/moneytransfer")
@Slf4j
public class TransferMoneyController {


	  private final TransferMoneyService transferMoneyService;
	  
	  
	  
	  

	  @Autowired
	  public TransferMoneyController(TransferMoneyService transferMoneyService) {
	    this.transferMoneyService = transferMoneyService;
	    
	  }

	  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Object> processMoneyTransfer(@RequestBody @Valid TransferMoneyReqDetails transferMoney) {
	    log.info("Creating TransferMoney {}", transferMoney);

	    try {
	    	this.transferMoneyService.sendMoney(transferMoney.getFromAccountId(), transferMoney.getToAccountId(), transferMoney.getAmount());
	    	
	    } catch (BankTransactionException daie ) {
	      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	    catch (InterruptedException daie ) {
		      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
		    }

	    

	    return new ResponseEntity<>(HttpStatus.CREATED);
	  }

}
