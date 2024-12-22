package baraholkateam.rest.service;

import baraholkateam.rest.dto.ContactDTO;
import baraholkateam.rest.dto.ContactTypeDTO;
import baraholkateam.rest.mapper.ContactMapper;
import baraholkateam.rest.repository.ContactRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с контактами пользователя из объявления
 */
@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    /**
     * Получает контакт по id
     * @param contactId id контакта
     * @return контакт или null, если контакта не существует
     */
    public ContactDTO getContact(@NotNull Long contactId) {
        return contactRepository.findById(contactId)
                .map(ContactMapper::getContactDTO)
                .orElse(null);
    }

    /**
     * Сохраняет контакт
     * @param contactDTO контакт для сохранения
     */
    public void addContact(@NotNull ContactDTO contactDTO) {
        contactRepository.save(ContactMapper.getContactEntity(contactDTO));
    }

    /**
     * Обновляет тип контакта для контакта пользователя
     * @param contactId id контакта
     * @param contactTypeDTO новый тип контакта
     */
    public void updateContactType(@NotNull Long contactId, @NotNull ContactTypeDTO contactTypeDTO) {
        contactRepository.findById(contactId)
                .ifPresent(contactEntity -> {
                    ContactDTO contactDTO = ContactMapper.getContactDTO(contactEntity);
                    contactDTO.setContactType(contactTypeDTO);
                    contactRepository.save(ContactMapper.getContactEntity(contactDTO));
                });
    }

    /**
     * Обновляет контакт пользователя
     * @param contactId id контакта
     * @param contactName новый контакт
     */
    public void updateContactName(@NotNull Long contactId, @NotNull String contactName) {
        contactRepository.findById(contactId)
                .ifPresent(contactEntity -> {
                    ContactDTO contactDTO = ContactMapper.getContactDTO(contactEntity);
                    contactDTO.setContactName(contactName);
                    contactRepository.save(ContactMapper.getContactEntity(contactDTO));
                });
    }

    /**
     * Обновляет тип контакта и контакт пользователя
     * @param contactId id контакта
     * @param contactTypeDTO новый тип контакта
     * @param contactName новый контакт
     */
    public void updateContactTypeAndName(@NotNull Long contactId,
                                         @NotNull ContactTypeDTO contactTypeDTO,
                                         @NotNull String contactName) {
        contactRepository.findById(contactId)
                .ifPresent(contactEntity -> {
                    ContactDTO contactDTO = ContactMapper.getContactDTO(contactEntity);
                    contactDTO.setContactType(contactTypeDTO);
                    contactDTO.setContactName(contactName);
                    contactRepository.save(ContactMapper.getContactEntity(contactDTO));
                });
    }

    /**
     * Удаляет контакт по id
     * @param contactId id контакта
     */
    public void deleteContact(@NotNull Long contactId) {
        contactRepository.deleteById(contactId);
    }

}
