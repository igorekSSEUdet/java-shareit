package gateway.request.service;

import gateway.request.dto.RequestDto;
import org.springframework.http.ResponseEntity;

public interface RequestRemoteCommand {
    ResponseEntity<Object> createRequest(RequestDto requestDto, Long userId);

    ResponseEntity<Object> getAllOwnRequests(Long userId);

    ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size);

    ResponseEntity<Object> getRequestById(Long userId, Long requestId);
}
