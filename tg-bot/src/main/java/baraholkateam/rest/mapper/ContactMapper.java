package baraholkateam.rest.mapper;

import baraholkateam.rest.dto.ContactDTO;
import baraholkateam.rest.dto.ContactTypeDTO;
import baraholkateam.rest.model.ContactEntity;
import baraholkateam.rest.model.ContactTypeEntity;
import baraholkateam.util.ContactType;

import java.util.List;

/**
 * Маппер бизнес сущности контактов пользователя из объявления и JPA сущности
 */
public class ContactMapper {

    public static ContactEntity getContactEntity(ContactDTO contactDTO) {
        ContactTypeDTO contactTypeDTO = contactDTO.getContactType();
        ContactTypeEntity contactTypeEntity = ContactTypeEntity.builder()
                .contactTypeId(contactTypeDTO.getContactTypeId())
                .contactTypeName(contactTypeDTO.getContactTypeName().getName())
                .build();
        return ContactEntity.builder()
                .contactId(contactDTO.getContactId())
                .contactType(contactTypeEntity)
                .contactName(contactDTO.getContactName())
                .build();
    }

    public static ContactDTO getContactDTO(ContactEntity contactEntity) {
        ContactTypeEntity contactTypeEntity = contactEntity.getContactType();
        ContactTypeDTO contactTypeDTO = ContactTypeDTO.builder()
                .contactTypeId(contactTypeEntity.getContactTypeId())
                .contactTypeName(ContactType.valueOf(contactTypeEntity.getContactTypeName()))
                .build();
        return ContactDTO.builder()
                .contactId(contactEntity.getContactId())
                .contactType(contactTypeDTO)
                .contactName(contactEntity.getContactName())
                .build();
    }

    public static List<ContactEntity> getContactEntityList(List<ContactDTO> contactDTOList) {
        return contactDTOList.stream()
                .map(ContactMapper::getContactEntity)
                .toList();
    }

    public static List<ContactDTO> getContactDTOList(List<ContactEntity> contactEntityList) {
        return contactEntityList.stream()
                .map(ContactMapper::getContactDTO)
                .toList();
    }

}
