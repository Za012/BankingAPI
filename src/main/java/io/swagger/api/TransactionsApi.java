/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.8).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.Body;
import io.swagger.model.SavingsAccount;
import io.swagger.model.Transaction;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-05-19T16:39:42.654Z[GMT]")
@Api(value = "Transactions", description = "the Transactions API")
public interface TransactionsApi {

    @ApiOperation(value = "Create a transaction", nickname = "createTransaction", notes = "inserts a transaction to the system, default status -> waiting", authorizations = {
        @Authorization(value = "bearerAuth")    }, tags={ "Transaction", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful transaction"),
        @ApiResponse(code = 401, message = "Failed transaction"),
        @ApiResponse(code = 403, message = "Account is not permitted to create a transaction") })
    @RequestMapping(value = "/Transactions",
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<Void> createTransaction(@ApiParam(value = "Saving accounts whose interest gonna update" ,required=true )  @Valid @RequestBody Transaction transaction);


    @ApiOperation(value = "Get all transactions that belongs to the Account", nickname = "getAllTransactions", notes = "By using this, you will receive all transactions made in the saved history of the Account.", response = Transaction.class, responseContainer = "List", authorizations = {
        @Authorization(value = "bearerAuth")    }, tags={ "Transaction", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Transaction data, including amount, sender, receiver and date time.", response = Transaction.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Missing parameter info"),
        @ApiResponse(code = 403, message = "Forbidden") })
    @RequestMapping(value = "/Transactions",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    Iterable<Transaction> getAllTransactions(@NotNull @ApiParam(value = "Selects all transactions that belong to user", required = true) @Valid @RequestParam(value = "me", required = true) Boolean me,@ApiParam(value = "The ID of a specific Transaction") @Valid @RequestParam(value = "id", required = false) Integer id,@ApiParam(value = "Date from") @Valid @RequestParam(value = "dateFrom", required = false) String dateFrom,@ApiParam(value = "Date to") @Valid @RequestParam(value = "dateTo", required = false) String dateTo,@ApiParam(value = "Maximum number of entries returned") @Valid @RequestParam(value = "entries", required = false) Integer entries,@ApiParam(value = "Business category", allowableValues = "Living, Entertainment, Food, Transport, Saving, Other") @Valid @RequestParam(value = "category", required = false) String category,@ApiParam(value = "Sorts on amount low to high -> true high to low -> false") @Valid @RequestParam(value = "sort", required = false) Boolean sort,@ApiParam(value = "Filters on selected currency") @Valid @RequestParam(value = "currency", required = false) String currency,@ApiParam(value = "Filters on selected status", allowableValues = "pending, failed, processed") @Valid @RequestParam(value = "status", required = false) String status);


    @ApiOperation(value = "Update the status of the transaction", nickname = "updateTransactionStatus", notes = "", authorizations = {
        @Authorization(value = "bearerAuth")    }, tags={ "Transaction", })
    @ApiResponses(value = { 
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Transaction not found"),
        @ApiResponse(code = 405, message = "Validation exception") })
    @RequestMapping(value = "/Transactions",
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    ResponseEntity<Void> updateTransactionStatus(@ApiParam(value = "" ,required=true )  @Valid @RequestBody Body body,@ApiParam(value = "the Transaction id",required=true) @PathVariable("id") Integer id,@ApiParam(value = "callback Url",required=true) @PathVariable("callBackUrl") String callBackUrl);

}
