package baraholkateam.rest.service;

import baraholkateam.rest.dto.ContactTypeDTO;
import baraholkateam.rest.mapper.ContactTypeMapper;
import baraholkateam.rest.repository.ContactTypeRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с типами контактов пользователя из объявления
 */
@Service
public class ContactTypeService {

    @Autowired
    private ContactTypeRepository contactTypeRepository;

    /**
     * Получает тип контакта по id
     * @param contactTypeId id типа контакта
     * @return тип контакта или null, если типа контакта с таким id не существует
     */
    public ContactTypeDTO getContactType(@NotNull Long contactTypeId) {
        return contactTypeRepository.findById(contactTypeId)
                .map(ContactTypeMapper::getContactTypeDTO)
                .orElse(null);
    }

    /**
     * Получает тип контакта по имени типа
     * @param contactTypeName имя типа контакта
     * @return тип контакта или null, если типа контакта с таким именем не существует
     */
    public ContactTypeDTO getContactTypeByName(@NotNull String contactTypeName) {
       return contactTypeRepository.getContactTypeByContactTypeName(contactTypeName)
               .map(ContactTypeMapper::getContactTypeDTO)
               .orElse(null);
    }

}
