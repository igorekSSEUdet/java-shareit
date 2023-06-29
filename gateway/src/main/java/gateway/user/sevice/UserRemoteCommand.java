package gateway.user.sevice;

import gateway.item.dto.UserCreationRequestDto;
import gateway.item.dto.UserUpdateRequestDto;
import org.springframework.http.ResponseEntity;

public interface UserRemoteCommand {

    ResponseEntity<Object> addUser(UserCreationRequestDto userDto);

    ResponseEntity<Object> updateUser(Long userId, UserUpdateRequestDto userDto);

    ResponseEntity<Object> getUserById(Long userId);

    ResponseEntity<Object> getAllUsers();

    ResponseEntity<Object> deleteUserById(Long userId);
}
