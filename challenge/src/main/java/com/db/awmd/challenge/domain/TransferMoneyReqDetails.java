package com.db.awmd.challenge.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TransferMoneyReqDetails {
	@NotNull
	private String fromAccountId;
	@NotNull
    private String toAccountId;
    @NotNull
    @Min(value = 0, message = "The amount to transfer should always be a positive number.")
    private Double amount;
 
    public TransferMoneyReqDetails() {
 
    }
 
 
 
    @JsonCreator
    public TransferMoneyReqDetails(@JsonProperty("fromAccountId") String fromAccountId
    		,@JsonProperty("toAccountId") String toAccountId,
      @JsonProperty("amount") Double amount) {
      this.fromAccountId = fromAccountId;
      this.toAccountId = toAccountId;
      this.amount = amount;
    }
    
}
