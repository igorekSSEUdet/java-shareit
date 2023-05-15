package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, HashMap<Integer, Item>> items = new HashMap<>();
    private final UserStorage userStorage;
    private int id = 0;
    private final DateTimeFormatter logTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Autowired
    public InMemoryItemStorage(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Optional<ItemDto> createItem(Item item, int userId) {
        if (!userStorage.checkHasUser(userId)) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        }
        if (!items.containsKey(userId)) {
            items.put(userId, new HashMap<>());
        }
        item.setOwner(UserDtoMapper.toUser(userStorage.getUserById(userId).get()));
        item.setId(++id);
        items.get(userId).put(item.getId(), item);
        log.info(LocalDateTime.now().format(logTimeFormat) + " : Пользователь с id = " + userId + " добавил вещь с id = " + item.getId());
        return Optional.of(ItemDtoMapper.toItemDto(item));
    }

    @Override
    public Optional<ItemDto> getItemById(int itemId, int userId) {
        if (userStorage.checkHasUser(userId)) {
            log.info(LocalDateTime.now().format(logTimeFormat) + " : Получена вещь с id = " + itemId);
            return Optional.of(ItemDtoMapper.toItemDto(items.values().stream()
                    .flatMap(map -> map.values().stream())
                    .filter(item -> item.getId() == itemId)
                    .findFirst().orElseThrow()));
        } else {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        }
    }

    @Override
    public List<Item> getAllItemsByUserId(int userId) {
        if (checkHasUserAndItem(userId)) {
            log.info(LocalDateTime.now().format(logTimeFormat) + " : Пользователь с id = " + userId + " получил список всех вещей");
            return new ArrayList<>(items.get(userId).values());
        } else throw new UserNotFoundException("User with id = " + userId + " not found");
    }

    @Override
    public Optional<ItemDto> updateItem(Item updateItem, int id, int userId) {
        if (checkHasUserAndItem(userId)) {
            Item item = getItem(id, userId);
            Item itemForUpdate = getItemForUpdate(item, updateItem);
            items.get(userId).replace(id, itemForUpdate);
            log.info(LocalDateTime.now().format(logTimeFormat) + " : Обновлена вещь с id = " + id);
            return Optional.of(ItemDtoMapper.toItemDto(itemForUpdate));
        } else {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        }
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            log.info(LocalDateTime.now().format(logTimeFormat) + " : Поиск по тексту: " + text);
            return items.values().stream()
                    .flatMap(map -> map.values().stream())
                    .filter(item -> item.getName().toLowerCase().trim().contains(text.toLowerCase().trim()) ||
                            item.getDescription().toLowerCase().trim().contains(text.toLowerCase().trim()) &&
                                    item.getAvailable())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteItem(int id, int userId) {
        if (checkHasUserAndItem(userId)) {
            log.info(LocalDateTime.now().format(logTimeFormat) + " : Удалена вещь с id = " + id);
            items.get(userId).remove(id);
        } else throw new UserNotFoundException("User with id = " + userId + " not found");
    }

    private Item getItem(int itemId, int userId) {
        try {
            return items.get(userId).get(itemId);
        } catch (NullPointerException ex) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        }
    }

    private boolean checkHasUserAndItem(int userId) {
        return userStorage.checkHasUser(userId) && items.containsKey(userId);
    }

    private Item getItemForUpdate(Item item, Item update) {
        if (update.getDescription() != null) {
            item.setDescription(update.getDescription());
        }
        if (update.getName() != null) {
            item.setName(update.getName());
        }
        if (update.getId() != null) {
            item.setId(update.getId());
        }
        if (update.getAvailable() != null) {
            item.setAvailable(update.getAvailable());
        }
        return item;

    }
}
