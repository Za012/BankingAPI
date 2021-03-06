package nl.Inholland.model.requests;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountRequest {

    private String countryCode;
    private String bank;
    private String bban = null;
    private String user;
    private String name;
    private String balance;
    private String type;
    private String dailyLimit;
    private String interestRate;

    public AccountRequest(String countryCode, String bank, String name, String balance, String dailyLimit, String interestRate) {
        this.countryCode = countryCode;
        this.bank = bank;
        this.name = name;
        this.balance = balance;
        this.dailyLimit = dailyLimit;
        this.interestRate = interestRate;
    }

}
