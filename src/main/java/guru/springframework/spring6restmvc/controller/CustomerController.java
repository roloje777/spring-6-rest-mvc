package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final CustomerService customerService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Customer> listAllCustomers(){
        return customerService.getAllCustomers();
    }

    @RequestMapping(value = "{customerId}", method = RequestMethod.GET)
    public Customer getCustomerById(@PathVariable("customerId") UUID id){
        return customerService.getCustomerById(id);
    }

    /*
         Assignment - Handle HTTP Post for Create new Customer

        Create Controller method to handle post

        Update Request Mapping

        Save to in-memory hash map

        Return 201 status with location of created customer object
     */
    @PostMapping
    public ResponseEntity handlePost(@RequestBody Customer customer){
        // save the customer
        Customer savedCustomer = customerService.saveNewCustomer(customer);

        // Location
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/v1/customer/" + savedCustomer.getId().toString());

        // return location and status code
        return new ResponseEntity(headers, HttpStatus.CREATED);

    }

    @PutMapping("{customerId}")
    public ResponseEntity updateById(@PathVariable("customerId") UUID id, @RequestBody Customer customer){

        customerService.updateByID(id,customer);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity deletById(@PathVariable("customerId") UUID id){
        customerService.deleteById(id);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


}
