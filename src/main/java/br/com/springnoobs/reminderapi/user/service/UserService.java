package br.com.springnoobs.reminderapi.user.service;

import br.com.springnoobs.reminderapi.user.dto.request.CreateUserRequestDTO;
import br.com.springnoobs.reminderapi.user.dto.response.UserResponseDTO;
import br.com.springnoobs.reminderapi.user.entity.User;
import br.com.springnoobs.reminderapi.user.entity.contact.Contact;
import br.com.springnoobs.reminderapi.user.exception.EmailAlreadyExistsException;
import br.com.springnoobs.reminderapi.user.exception.EmailNotFoundException;
import br.com.springnoobs.reminderapi.user.exception.IllegalArgumentException;
import br.com.springnoobs.reminderapi.user.exception.UserNotFoundException;
import br.com.springnoobs.reminderapi.user.mapper.UserMapper;
import br.com.springnoobs.reminderapi.user.repository.ContactRepository;
import br.com.springnoobs.reminderapi.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    public UserService(UserRepository userRepository, ContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    @Transactional
    public UserResponseDTO saveUser(CreateUserRequestDTO dto) {
        var contactDTO = dto.contactRequestDTO();
        if (dto.firstName() == null || dto.lastName() == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (contactDTO == null || contactDTO.email() == null || contactDTO.phoneNumber() == null) {
            throw new IllegalArgumentException("Contact cannot be null");
        }
        if (contactRepository.existsByEmail(dto.contactRequestDTO().email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = new User();
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());

        User savedUser = userRepository.save(user);

        Contact contact = new Contact();
        contact.setEmail(dto.contactRequestDTO().email());
        contact.setPhoneNumber(dto.contactRequestDTO().phoneNumber());
        contact.setUser(savedUser);

        contactRepository.save(contact);
        savedUser.setContact(contact);
        return UserMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponseDTO findByEmail(String email) {
        Contact contact = contactRepository
                .findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("E-mail not found" + email));
        User user = contact.getUser();
        return UserMapper.toResponse(user);
    }

    @Transactional
    public List<UserResponseDTO> listAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toResponseList(users);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found" + id));
        if (user.getContact() != null) {
            contactRepository.delete(user.getContact());
        }
        userRepository.delete(user);
    }
}
