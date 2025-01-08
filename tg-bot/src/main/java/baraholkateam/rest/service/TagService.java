package baraholkateam.rest.service;

import baraholkateam.rest.dto.TagDTO;
import baraholkateam.rest.dto.TagTypeDTO;
import baraholkateam.rest.mapper.TagMapper;
import baraholkateam.rest.mapper.TagTypeMapper;
import baraholkateam.rest.repository.TagRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с тегами в объявлении
 */
@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    /**
     * Получает тег по id
     * @param tagId id тега
     * @return тег или null, если тег не существует
     */
    public TagDTO getTagById(@NotNull Long tagId) {
        return tagRepository.findById(tagId)
                .map(TagMapper::getTagDTO)
                .orElse(null);
    }

    /**
     * Получает тег по названию
     * @param name название тега
     * @return тег или null, если тег не существует
     */
    public TagDTO getTagByName(@NotNull String name) {
        return tagRepository.findByName(name)
                .map(TagMapper::getTagDTO)
                .orElse(null);
    }

    /**
     * Получает список тегов по типу тега
     * @param tagTypeDTO тип тега для поиска
     * @return список тегов выбранного типа
     */
    public List<TagDTO> getAllTagsByTagType(@NotNull TagTypeDTO tagTypeDTO) {
        return tagRepository.findAllByTagType(TagTypeMapper.getTagTypeEntity(tagTypeDTO)).stream()
                .map(TagMapper::getTagDTO)
                .toList();
    }

}
