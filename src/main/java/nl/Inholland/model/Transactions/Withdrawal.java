package nl.Inholland.model.Transactions;


import lombok.Data;
import lombok.NoArgsConstructor;
import nl.Inholland.model.Accounts.CurrentAccount;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@NoArgsConstructor
@Entity
@Data
public class Withdrawal extends Transaction{


  //  private CurrentAccount account;


  //  private BigDecimal amount;
}
