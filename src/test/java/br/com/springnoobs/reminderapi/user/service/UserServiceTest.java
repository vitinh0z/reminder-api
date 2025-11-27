package br.com.springnoobs.reminderapi.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.springnoobs.reminderapi.user.dto.request.ContactRequestDTO;
import br.com.springnoobs.reminderapi.user.dto.request.CreateUserRequestDTO;
import br.com.springnoobs.reminderapi.user.dto.response.UserResponseDTO;
import br.com.springnoobs.reminderapi.user.entity.Contact;
import br.com.springnoobs.reminderapi.user.entity.User;
import br.com.springnoobs.reminderapi.user.exception.EmailAlreadyExistsException;
import br.com.springnoobs.reminderapi.user.exception.EmailNotFoundException;
import br.com.springnoobs.reminderapi.user.exception.UserNotFoundException;
import br.com.springnoobs.reminderapi.user.repository.ContactRepository;
import br.com.springnoobs.reminderapi.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateUserWhenRequestIsValid() {
        // Arrange
        var contactRequest = new ContactRequestDTO("test@test.com", "123456789");
        var userRequest = new CreateUserRequestDTO("John", "Doe", contactRequest);

        when(contactRepository.existsByEmail(any())).thenReturn(false);
        when(repository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponseDTO response = userService.create(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals("test@test.com", response.contact().email());
        verify(repository).save(any(User.class));
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenCreatingUserWithExistingEmail() {
        // Arrange
        var contactRequest = new ContactRequestDTO("test@test.com", "123456789");
        var userRequest = new CreateUserRequestDTO("John", "Doe", contactRequest);

        when(contactRepository.existsByEmail("test@test.com")).thenReturn(true);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.create(userRequest));
    }

    @Test
    void shouldReturnUserWhenFindingByExistingEmail() {
        // Arrange
        String email = "test@test.com";
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        Contact contact = new Contact();
        contact.setEmail(email);
        contact.setUser(user);
        user.setContact(contact);

        when(contactRepository.findByEmail(email)).thenReturn(Optional.of(contact));

        // Act
        UserResponseDTO response = userService.findByEmail(email);

        // Assert
        assertNotNull(response);
        assertEquals("John", response.firstName());
    }

    @Test
    void shouldThrowEmailNotFoundExceptionWhenFindingByNonExistingEmail() {
        // Arrange
        String email = "test@test.com";
        when(contactRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EmailNotFoundException.class, () -> userService.findByEmail(email));
    }

    @Test
    void shouldReturnAllUsers() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        Contact contact = new Contact();
        contact.setEmail("test@test.com");
        user.setContact(contact);

        when(repository.findAll()).thenReturn(List.of(user));

        // Act
        List<UserResponseDTO> responses = userService.findAll();

        // Assert
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("John", responses.get(0).firstName());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UserResponseDTO> responses = userService.findAll();

        // Assert
        assertTrue(responses.isEmpty());
    }

    @Test
    void shouldDeleteUserWhenIdIsValid() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.delete(userId);

        // Assert
        verify(repository).delete(user);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenDeletingNonExistingUser() {
        // Arrange
        Long userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
    }
}
