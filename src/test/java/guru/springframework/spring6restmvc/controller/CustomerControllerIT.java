package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerMapper customerMapper;

    @Rollback
    @Transactional
    @Test
    void testListAllEmptyList() {
        customerRepository.deleteAll();
        List<CustomerDTO> dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(0);
    }

    @Test
    void testListAll() {
        List<CustomerDTO> dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(3);
    }

    @Test
    void testGetByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.getCustomerById(UUID.randomUUID());
        });
    }

    @Test
    void testGetById() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO customerDTO = customerController.getCustomerById(customer.getId());
        assertThat(customerDTO).isNotNull();
    }

    // above used as the data needs to be reset once test is done for the other tests to pass
    @Rollback
    @Transactional
    @Test
    void saveNewCustomerTest(){
        // Sent  in post action
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name(" Pipo")
                .build();

        // returned from the controller
        ResponseEntity responseEntity =  customerController.handlePost(customerDTO);

        // do the assertions customer is created
        // that hears has information
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));//created
        assertThat(responseEntity.getHeaders().getLocation().getPath()).isNotNull();

        // now we retrieve the UUID from the responseEntity and to assertion on it
        String[] locationID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedId = UUID.fromString(locationID[4]);
        Customer customer = customerRepository.findById(savedId).get();
        assertThat(customer).isNotNull();

    }

    @Test
    void updateExistingCustomerTest(){
        Customer customer = customerRepository.findAll().getFirst();
        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(customer);
        customerDTO.setId(null);// system generated
        customerDTO.setVersion(null);
        customerDTO.setUpdateDate(null);
        customerDTO.setCreatedDate(null);
        final String customerName = "Pipo";
        customerDTO.setName(customerName);

        ResponseEntity responseEntity =  customerController.updateCustomerByID(customer.getId(),customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204)); //204 = NoContent

        Customer updatedCustomer = customerRepository.findById(customer.getId()).get();
        assertThat(updatedCustomer.getName()).isEqualTo(customerName);

    }

    // here we send a random UUID that will not be found in the
    // so a NotFoundException is thrown by the comntroller
    @Test
    void testUpdateNotFound(){
        assertThrows(NotFoundException.class, () ->{
            customerController.updateCustomerByID(UUID.randomUUID(), CustomerDTO.builder().build());
        });

    }

    @Rollback
    @Transactional
    @Test
    void testDeleteByIdFound(){
        Customer customer = customerRepository.findAll().getFirst();

        ResponseEntity responseEntity = customerController.deleteCustomerById(customer.getId());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));//no content

        assertThat(customerRepository.findById(customer.getId())).isEmpty();
    }

    // return a 404 if not found
    @Test
    void testDeleteByIdNotFound(){
        assertThrows(NotFoundException.class, () -> {
            customerController.deleteCustomerById(UUID.randomUUID());
        });
    }




}










