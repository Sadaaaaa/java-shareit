package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<?> addRequest(@RequestHeader (value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                        @RequestBody Request request) {
        return ResponseEntity.ok(requestService.addRequest(userId, request));
    }

    @GetMapping
    public ResponseEntity<?> getRequest(@RequestHeader (value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId) {
        return ResponseEntity.ok(requestService.getRequest(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRequests(@RequestHeader (value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                            @RequestParam(value = "size", required = false, defaultValue = "1") int size) {
        if (size < 1) {
            return new ResponseEntity<>("Page size should not be less than 1.", HttpStatus.BAD_REQUEST);
        } else if (from < 0) {
            return new ResponseEntity<>("Page from should not be less than 0.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(requestService.getAllRequests(userId, from, size));
    }

    @GetMapping("{requestId}")
    public ResponseEntity<?> getRequestById(@RequestHeader (value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                            @PathVariable(name = "requestId") int requestId) {
        return ResponseEntity.ok(requestService.getRequestById(userId, requestId));
    }
}
