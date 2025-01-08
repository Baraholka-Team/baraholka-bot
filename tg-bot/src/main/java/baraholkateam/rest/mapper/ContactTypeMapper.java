package baraholkateam.rest.mapper;

import baraholkateam.rest.dto.ContactTypeDTO;
import baraholkateam.rest.model.ContactTypeEntity;
import baraholkateam.util.ContactType;

/**
 * Маппер бизнес сущности типов контактов пользователя из объявления и JPA сущности
 */
public class ContactTypeMapper {

    public static ContactTypeEntity getContactTypeEntity(ContactTypeDTO contactTypeDTO) {
        return ContactTypeEntity.builder()
                .contactTypeId(contactTypeDTO.getContactTypeId())
                .contactTypeName(contactTypeDTO.getContactTypeName().getName())
                .build();
    }

    public static ContactTypeDTO getContactTypeDTO(ContactTypeEntity contactTypeEntity) {
        return ContactTypeDTO.builder()
                .contactTypeId(contactTypeEntity.getContactTypeId())
                .contactTypeName(ContactType.valueOf(contactTypeEntity.getContactTypeName()))
                .build();
    }

}
