package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;


public interface RequestService {
    RequestDto addRequest(int userId, Request request);

    List<RequestDto> getRequest(int userId);

    List<RequestDto> getAllRequests(int userId, int from, int size);

    RequestDto getRequestById(int userId, int requestId);
}
