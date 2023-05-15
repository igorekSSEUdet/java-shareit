package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    Optional<ItemDto> createItem(Item item, int userId);

    Optional<ItemDto> getItemById(int id, int userId);

    List<Item> getAllItemsByUserId(int userId);

    Optional<ItemDto> updateItem(Item item, int id, int userId);

    List<Item> searchItem(String text);

    void deleteItem(int id, int userId);
}
