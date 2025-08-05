MERGE INTO tag
USING (
    SELECT 1 id,
           '#Москва' name,
           1 tag_type_id
    UNION ALL
    SELECT 2,
           '#СПб',
           1
    UNION ALL
    SELECT 3,
           '#Екатеринбург',
           1
    UNION ALL
    SELECT 4,
           '#Челябинск',
           1
    UNION ALL
    SELECT 5,
           '#Ульяновск',
           1
    UNION ALL
    SELECT 6,
           '#Омск',
           1
    UNION ALL
    SELECT 7,
           '#Белгород',
           1
    UNION ALL
    SELECT 8,
           '#Петропавловск',
           1
    UNION ALL
    SELECT 9,
           '#Пермь',
           1
    UNION ALL
    SELECT 10,
           '#Волгоград',
           1
    UNION ALL
    SELECT 11,
           '#Киров',
           1
    UNION ALL
    SELECT 12,
           '#Хабаровск',
           1
    UNION ALL
    SELECT 13,
           '#другой_город',
           1
    UNION ALL
    SELECT 14,
           '#актуально',
           2
    UNION ALL
    SELECT 15,
           '#продажа',
           3
    UNION ALL
    SELECT 16,
           '#обмен',
           3
    UNION ALL
    SELECT 17,
           '#дар',
           3
    UNION ALL
    SELECT 18,
           '#торг_уместен',
           3
    UNION ALL
    SELECT 19,
           '#срочно',
           3
    UNION ALL
    SELECT 20,
           '#одежда',
           4
    UNION ALL
    SELECT 21,
           '#обувь',
           4
    UNION ALL
    SELECT 22,
           '#детские_товары',
           4
    UNION ALL
    SELECT 23,
           '#красота_и_здоровье',
           4
    UNION ALL
    SELECT 24,
           '#книги',
           4
    UNION ALL
    SELECT 25,
           '#хобби',
           4
    UNION ALL
    SELECT 26,
           '#домашняя_техника',
           4
    UNION ALL
    SELECT 27,
           '#электроника',
           4
    UNION ALL
    SELECT 28,
           '#спорт',
           4
    UNION ALL
    SELECT 29,
           '#другое',
           4
    UNION ALL
    SELECT 30,
           '#мужское',
           4
    UNION ALL
    SELECT 31,
           '#женское',
           4
    UNION ALL
    SELECT 32,
           '#unknown',
           5
) AS d
ON (tag_id = d.id)
WHEN MATCHED THEN
    UPDATE SET name = d.name,
               tag_type_id = d.tag_type_id
WHEN NOT MATCHED THEN
    INSERT (tag_id, name, tag_type_id)
    VALUES (d.id, d.name, d.tag_type_id);
