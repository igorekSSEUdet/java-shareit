package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.DetailedItemDto;
import ru.practicum.shareit.item.dto.ItemCreationRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

public interface ItemService {
    static void checkItemExistsById(ItemRepository itemRepository, Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw ItemNotFoundException.getFromItemId(itemId);
        }
    }

    static void checkOwnerOfItemByItemIdAndUserId(ItemRepository itemRepository,
                                                  Long itemId, Long userId) {
        Long ownerId = itemRepository.getReferenceById(itemId).getOwner().getId();
        if (!ownerId.equals(userId)) {
            throw ItemNotFoundException.getFromItemIdAndUserId(itemId, userId);
        }
    }

    ItemDto addItem(ItemCreationRequestDto itemDto, Long ownerId);

    ItemDto updateItem(ItemUpdateRequestDto itemDto, Long itemId, Long ownerId);

    DetailedItemDto getItemByItemId(Long itemId, Long userId);

    List<DetailedItemDto> getItemsByOwnerId(Long ownerId);

    List<ItemDto> searchItemsByNameOrDescription(String text);
}
