package com.example.bank_rest.config;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.Role;
import com.example.bank_rest.entity.User;
import com.example.bank_rest.entity.enums.StatusCard;
import com.example.bank_rest.entity.enums.UserRole;
import com.example.bank_rest.entity.enums.UserStatus;
import com.example.bank_rest.repository.CardRepository;
import com.example.bank_rest.repository.RoleRepository;
import com.example.bank_rest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@DependsOn("liquibase")
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    public DataLoader(RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, CardRepository cardRepository) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Role> allRoles = roleRepository.findAll();
        Role role = new Role();
        Role role2 = new Role();
        if (allRoles.isEmpty()) {

            role.setRole(UserRole.ROLE_USER);
            roleRepository.save(role);

            role2.setRole(UserRole.ROLE_ADMIN);
            roleRepository.save(role2);
        }
        List<User> allUsers = userRepository.findAll();
        if (allUsers.isEmpty()) {
            User user = new User("admin", "admin", "admin", "1111111111111", UserStatus.ACTIVE, passwordEncoder.encode("adminchik"), List.of(role2, role));

            userRepository.save(user);

            User user2 = new User("string", "string", "string", "2222222222222", UserStatus.ACTIVE, passwordEncoder.encode("stringst"), List.of(role));
            Card card = new Card("string", "2222222222222222", user2, LocalDate.of(2025, 10, 4), StatusCard.ACTIVE, BigDecimal.TEN);
            Card card1 = new Card("string", "3333333333333333", user2, LocalDate.of(2025, 11, 4), StatusCard.ACTIVE, BigDecimal.TEN);

            userRepository.save(user2);
            cardRepository.saveAll(List.of(card, card1));

            User user3 = new User("aaaaa", "aaaaa", "aaaaa", "3333333333333", UserStatus.ACTIVE, passwordEncoder.encode("aaaaaaaa"), List.of(role));
            Card card2 = new Card("aaaaa", "4444444444444444", user3, LocalDate.of(2025, 10, 4), StatusCard.ACTIVE, BigDecimal.ZERO);

            userRepository.save(user3);
            cardRepository.save(card2);

            User user4 = new User("bbbbb", "bbbbb", "bbbbb", "4444444444444", UserStatus.BLOCKED, passwordEncoder.encode("bbbbbbbb"), List.of(role));
            Card card3 = new Card("bbbbb", "1212121212121212", user4, LocalDate.of(2025, 12, 4), StatusCard.ACTIVE, BigDecimal.TEN);

            userRepository.save(user4);
            cardRepository.save(card3);

        }
    }
}
