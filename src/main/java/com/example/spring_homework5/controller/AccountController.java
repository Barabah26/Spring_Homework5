package com.example.spring_homework5.controller;

import com.example.spring_homework5.domain.Account;
import com.example.spring_homework5.dto.account.AccountDtoResponse;
import com.example.spring_homework5.exception.AccountNotFoundException;
import com.example.spring_homework5.exception.InsufficientBalanceException;
import com.example.spring_homework5.exception.InvalidTransferAmountException;
import com.example.spring_homework5.exception.SameAccountException;
import com.example.spring_homework5.mapper.account.AccountDtoMapperResponse;
import com.example.spring_homework5.service.DefaultAccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RequiredArgsConstructor
public class AccountController {
    private final DefaultAccountService accountService;
    private final AccountDtoMapperResponse accountDtoMapperResponse;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "Get all accounts")
    @GetMapping("/all")
    public List<AccountDtoResponse> getAllAccounts() {
        log.info("Received request to get all accounts");
        List<AccountDtoResponse> accounts = accountService.findAll().stream().map(accountDtoMapperResponse::convertToDto).toList();
        log.info("Returning {} accounts", accounts.size());
        return accounts;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccountById(@PathVariable Long id) {
        log.info("Received request to delete account with id {}", id);
        accountService.deleteById(id);
        log.info("Account with id {} deleted", id);
    }

    @Operation(summary = "Get account by id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        log.info("Received request to get account by id {}", id);
        try {
            Account account = accountService.getOne(id).orElseThrow(() -> new AccountNotFoundException("Account not found"));
            AccountDtoResponse accountDtoResponse = accountDtoMapperResponse.convertToDto(account);
            log.info("Returning account: {}", accountDtoResponse);
            return ResponseEntity.ok(accountDtoResponse);
        } catch (AccountNotFoundException e) {
            log.error("Account with id {} not found", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with id " + id + " not found");
        } catch (RuntimeException e) {
            log.error("Unexpected error fetching account with id {}", id, e);
            return ResponseEntity.badRequest().body("Account with id " + id + " not found");
        }
    }

    @Operation(summary = "Deposit an amount to an account")
    @PostMapping("/deposit/{number}")
    public ResponseEntity<?> depositToAccount(@PathVariable UUID number, @RequestBody Double amount) {
        log.info("Received request to deposit amount {} to account {}", amount, number);
        try {
            if (amount <= 0) {
                log.error("Amount for deposit must be greater than 0");
                return ResponseEntity.badRequest().body("Amount for deposit must be greater than 0");
            }
            Account updatedAccount = accountService.depositToAccount(number, amount);
            AccountDtoResponse accountDtoResponse = accountDtoMapperResponse.convertToDto(updatedAccount);
            notifyBalanceChange(number);
            log.info("Deposit successful, updated account: {}", accountDtoResponse);
            return ResponseEntity.ok(accountDtoResponse);
        } catch (IllegalArgumentException e) {
            log.error("Invalid account number format: {}", number, e);
            return ResponseEntity.badRequest().body("Invalid account number format: " + number);
        } catch (AccountNotFoundException e) {
            log.error("Account with number {} not found", number, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with number " + number + " not found");
        } catch (Exception e) {
            log.error("Unexpected error during deposit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @Operation(summary = "Withdraw an amount from an account")
    @PutMapping("/withdraw/{accountNumber}")
    public ResponseEntity<?> withdrawFromAccount(@PathVariable UUID accountNumber, @RequestBody Double amount) {
        log.info("Received request to withdraw amount {} from account {}", amount, accountNumber);
        try {
            boolean withdrawalSuccessful = accountService.withdrawFromAccount(accountNumber, amount);
            if (withdrawalSuccessful) {
                log.info("Withdrawal successful from account {}", accountNumber);
                notifyBalanceChange(accountNumber);
                return ResponseEntity.ok("Withdrawal successful");
            } else {
                log.warn("Insufficient balance for account {}", accountNumber);
                return ResponseEntity.badRequest().body("Insufficient balance");
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid account number format: {}", accountNumber, e);
            return ResponseEntity.badRequest().body("Invalid account number format: " + accountNumber);
        } catch (AccountNotFoundException e) {
            log.error("Account with number {} not found", accountNumber, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with number " + accountNumber + " not found");
        } catch (InsufficientBalanceException e) {
            log.warn("Insufficient balance for account {}: {}", accountNumber, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during withdrawal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @Operation(summary = "Transfer an amount from one account to another")
    @PutMapping("/transfer/{fromAccountNumber}/{toAccountNumber}")
    public ResponseEntity<?> transferMoney(@PathVariable UUID fromAccountNumber, @PathVariable UUID toAccountNumber, @RequestBody double amount) {
        log.info("Received request to transfer amount {} from account {} to account {}", amount, fromAccountNumber, toAccountNumber);
        try {
            accountService.transferMoney(fromAccountNumber, toAccountNumber, amount);
            log.info("Transfer successful from account {} to account {}", fromAccountNumber, toAccountNumber);
            notifyBalanceChange(fromAccountNumber);
            notifyBalanceChange(toAccountNumber);
            return ResponseEntity.ok("Transfer successful");
        } catch (IllegalArgumentException e) {
            log.error("Invalid account number format", e);
            return ResponseEntity.badRequest().body("Invalid account number format");
        } catch (AccountNotFoundException e) {
            log.error("One of the accounts not found: fromAccountNumber={}, toAccountNumber={}", fromAccountNumber, toAccountNumber, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SameAccountException | InvalidTransferAmountException | InsufficientBalanceException e) {
            log.warn("Transfer failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error transferring amount: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    private void notifyBalanceChange(UUID accountNumber) {
        messagingTemplate.convertAndSend("/topic/accountBalance/" + accountNumber, "Balance updated");
    }
}
