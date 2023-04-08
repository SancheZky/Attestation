package com.example.market.services;

import com.example.market.Models.Person;
import com.example.market.repositories.PersonRepository;
import com.example.market.security.PersonDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // получаем пользователя из таблицы по логину с формы авторизаци
        Optional<Person> person = personRepository.findByLogin(username);

        // Если пользователь не найден
        if(person.isEmpty()){
            // исключение что не найден
            // исключение будет поймано спрингсеком и сообщение будет выведено на страницу
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return new PersonDetails(person.get());
    }
}
