package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RequestDto addRequest(int userId, Request request) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found!"));
        if (request.getDescription() == null) throw new BadRequestException("Description should not be null!");

        request.setCreated(LocalDateTime.now());
        request.setRequestor(userId);

        requestRepository.save(request);
        return RequestMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> getRequest(int userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found!"));

        return requestRepository.findAllByRequestor(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getAllRequests(int userId, int from, int size) {
        return requestRepository.search(userId, PageRequest.of(from, size)).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto getRequestById(int userId, int requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found!"));
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Wrong request id."));
        return RequestMapper.toRequestDto(request);
    }
}
