package com.example.spring_homework5.controller;

import com.example.spring_homework5.domain.Account;
import com.example.spring_homework5.domain.Customer;
import com.example.spring_homework5.dto.account.AccountDtoRequest;
import com.example.spring_homework5.dto.customer.CustomerDtoRequest;
import com.example.spring_homework5.dto.customer.CustomerDtoResponse;
import com.example.spring_homework5.exception.AccountNotFoundException;
import com.example.spring_homework5.exception.CustomerNotFoundException;
import com.example.spring_homework5.exception.InsufficientBalanceException;
import com.example.spring_homework5.exception.SameCustomerException;
import com.example.spring_homework5.mapper.account.AccountDtoMapperRequest;
import com.example.spring_homework5.mapper.customer.CustomerDtoMapperRequest;
import com.example.spring_homework5.mapper.customer.CustomerDtoMapperResponse;
import com.example.spring_homework5.service.DefaultAccountService;
import com.example.spring_homework5.service.DefaultCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {
    private final DefaultCustomerService customerService;
    private final DefaultAccountService accountService;
    private final CustomerDtoMapperResponse customerDtoMapperResponse;
    private final CustomerDtoMapperRequest customerDtoMapperRequest;
    private final AccountDtoMapperRequest accountDtoMapperRequest;

    @Operation(summary = "Get customer by id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable Long id) {
        log.info("Fetching customer with ID {}", id);
        try {
            customerService.assignAccountsToCustomers();
            CustomerDtoRequest customerDto = customerDtoMapperRequest.convertToDto(customerService.getOne(id).orElseThrow(() -> new CustomerNotFoundException("Customer not found")));
            log.info("Customer with ID {} fetched successfully", id);
            return ResponseEntity.ok(customerDto);
        } catch (CustomerNotFoundException e) {
            log.error("Customer not found with ID {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with ID " + id + " not found");
        } catch (Exception e) {
            log.error("An unexpected error occurred while fetching customer with ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @Operation(summary = "Get all customers")
    @GetMapping("/all")
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Fetching all customers, page: {}, size: {}", page, size);
        try {
            Page<CustomerDtoResponse> customersPage = customerService.findAll(0, Integer.MAX_VALUE, PageRequest.of(page, size)).map(customerDtoMapperResponse::convertToDto);
            List<CustomerDtoResponse> customers = customersPage.getContent();
            log.info("Fetched {} customers", customers.size());
            return ResponseEntity.ok().body(customers);
        } catch (Exception e) {
            log.error("An unexpected error occurred while retrieving customers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @Operation(summary = "Create a new customer")
    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerDtoRequest customer) {
        log.info("Creating a new customer");
        Customer newCustomer = customerDtoMapperRequest.convertToEntity(customer);
        try {
            customerService.save(newCustomer);
            log.info("Customer created successfully with ID {}", newCustomer.getId());
            return ResponseEntity.ok(customerDtoMapperResponse.convertToDto(newCustomer));
        } catch (SameCustomerException e) {
            log.error("Failed to create customer: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update customer")
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody CustomerDtoRequest customerDto) {
        log.info("Updating customer with ID {}", id);
        try {
            Customer updatedCustomer = customerDtoMapperRequest.convertToEntity(customerDto);
            updatedCustomer.setId(id);
            Customer currentCustomer = customerService.update(id, updatedCustomer);
            if (customerDto.getName() != null) {
                currentCustomer.setName(customerDto.getName());
            }
            if (customerDto.getEmail() != null) {
                currentCustomer.setEmail(customerDto.getEmail());
            }
            if (customerDto.getAge() >= 18) {
                currentCustomer.setAge(customerDto.getAge());
            }
            log.info("Customer with ID {} updated successfully", id);
            return ResponseEntity.ok(currentCustomer);
        } catch (CustomerNotFoundException e) {
            log.error("Customer not found with ID {}", id, e);
            return ResponseEntity.badRequest().body("Customer with ID " + id + " not found");
        } catch (SameCustomerException e) {
            log.error("Failed to update customer: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred while updating the customer with ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the customer");
        }
    }

    @Operation(summary = "Delete a customer by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with ID {}", id);
        try {
            Customer customer = customerService.getOne(id).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
            customerService.deleteCustomerAccounts(customer);
            customerService.deleteById(customer.getId());
            log.info("Customer with ID {} deleted successfully", id);
            return ResponseEntity.ok().build();
        } catch (CustomerNotFoundException e) {
            log.error("Customer not found with ID {}", id, e);
            return ResponseEntity.badRequest().body("Customer with ID " + id + " not found");
        } catch (Exception e) {
            log.error("An error occurred while deleting the customer with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the customer");
        }
    }

    @PostMapping("/{customerId}/accounts")
    public ResponseEntity<?> createAccountForCustomer(@PathVariable Long customerId, @RequestBody AccountDtoRequest accountDto) {
        log.info("Creating account for customer with ID {}", customerId);
        try {
            Customer customer = customerService.getOne(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
            Account account = accountDtoMapperRequest.convertToEntity(accountDto);
            customerService.createAccountForCustomer(customer.getId(), accountDto.getCurrency(), accountDto.getBalance());
            customerService.update(customerId, customer);
            log.info("Account created successfully for customer with ID {}", customerId);
            return ResponseEntity.ok(customer);
        } catch (CustomerNotFoundException e) {
            log.error("Customer not found with ID {}", customerId, e);
            return ResponseEntity.badRequest().body("Customer with ID " + customerId + " not found");
        } catch (AccountNotFoundException e) {
            log.error("Account cannot be null");
            return ResponseEntity.badRequest().body("Account cannot be null");
        } catch (InsufficientBalanceException e) {
            log.error("Insufficient balance for customer with ID {}", customerId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred while adding the account to the customer with id {}", customerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the account to the customer");
        }
    }

    @Operation(summary = "Delete an account from a customer by its ID")
    @DeleteMapping("/{customerId}/accounts/{accountNumber}")
    public ResponseEntity<?> deleteAccountFromCustomer(@PathVariable Long customerId, @PathVariable UUID accountNumber) {
        log.info("Deleting account with number {} from customer with ID {}", accountNumber, customerId);
        try {
            Customer customer = customerService.getOne(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
            Account accountToDelete = null;
            for (Account account : customer.getAccounts()) {
                if (account.getNumber().equals(accountNumber)) {
                    accountToDelete = account;
                    break;
                }
            }
            if (accountToDelete != null) {
                customerService.deleteAccountFromCustomer(customer.getId(), accountToDelete.getNumber());
                customerService.update(customerId, customer);
                log.info("Account with number {} deleted successfully from customer with ID {}", accountNumber, customerId);
                return ResponseEntity.ok("Account successfully deleted");
            } else {
                log.error("Account with number {} not found for customer with ID {}", accountNumber, customerId);
                return ResponseEntity.badRequest().body("Account with number " + accountNumber + " not found for customer with ID " + customerId);
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid account number format: {}", accountNumber, e);
            return ResponseEntity.badRequest().body("Invalid account number format: " + accountNumber);
        } catch (CustomerNotFoundException e) {
            log.error("Customer not found with ID {}", customerId, e);
            return ResponseEntity.badRequest().body("Customer with ID " + customerId + " not found");
        } catch (AccountNotFoundException e) {
            log.error("Account with number {} not found for customer with ID {}", accountNumber, customerId);
            return ResponseEntity.badRequest().body("Account with number " + accountNumber + " not found for customer with ID " + customerId);
        } catch (Exception e) {
            log.error("An error occurred while deleting the account of the customer with id {}", customerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the account of the customer");
        }
    }
}

