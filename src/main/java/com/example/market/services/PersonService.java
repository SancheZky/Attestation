package com.example.market.services;

import com.example.market.repositories.PersonRepository;
import com.example.market.Models.Person;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
        private final PersonRepository personRepository;
        private final PasswordEncoder passwordEncoder;

    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Person findByLogin(Person person){
        Optional<Person> person_bd = personRepository.findByLogin(person.getLogin());
        return person_bd.orElse(null);
    }

    @Transactional
    public void registry(Person person){
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        personRepository.save(person);
    }

    public List<Person> getAllPersons(){
        return personRepository.findAll();
    }

    //Установить пользователю роль простого пользователя
    @Transactional
    public void setRoleUSER(int id){
        Optional<Person> personOptional = personRepository.findById(id);
        if(personOptional.isPresent()){
            Person person = personOptional.get();
            person.setRole("ROLE_USER");
            personRepository.save(person);
        }

    }

    // установить пользоватлею роль администратора
    @Transactional
    public void setRoleADMIN(int id){
        Optional<Person> personOptional = personRepository.findById(id);
        if(personOptional.isPresent()){
            Person person = personOptional.get();
            person.setRole("ROLE_ADMIN");
            personRepository.save(person);
        }

    }

}
