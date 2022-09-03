package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<?> addRequest(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                        @RequestBody RequestDto requestDto) {
        return requestClient.addRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<?> getRequest(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId) {
        return requestClient.getRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) Long userId,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                            @RequestParam(value = "size", required = false, defaultValue = "1") int size) {
        if (size < 1) {
            return new ResponseEntity<>("Page size should not be less than 1.", HttpStatus.BAD_REQUEST);
        } else if (from < 0) {
            return new ResponseEntity<>("Page from should not be less than 0.", HttpStatus.BAD_REQUEST);
        }
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<?> getRequestById(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) Long userId,
                                            @PathVariable(name = "requestId") int requestId) {
        return requestClient.getRequestById(userId, requestId);
    }
}
