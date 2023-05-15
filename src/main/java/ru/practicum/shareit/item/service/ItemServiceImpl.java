package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.Optional;

@Component
@Scope("singleton")
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;

    @Autowired
    public ItemServiceImpl(@Qualifier("inMemoryItemStorage") ItemStorage storage) {
        this.storage = storage;
    }

    @Override
    public Optional<ItemDto> createItem(Item item, int userId) {
        return storage.createItem(item,userId);
    }

    @Override
    public Optional<ItemDto> getItemById(int itemId, int userId) {
        return storage.getItemById(itemId,userId);
    }

    @Override
    public List<Item> getAllItemsByUserId(int userId) {
        return storage.getAllItemsByUserId(userId);
    }

    @Override
    public Optional<ItemDto> updateItem(Item item, int id, int userId) {
        return storage.updateItem(item, id,userId);
    }

    @Override
    public List<Item> searchItem(String text) {
        return storage.searchItem(text);
    }

    @Override
    public void deleteItem(int id, int userId) {
         storage.deleteItem(id,userId);
    }
}
