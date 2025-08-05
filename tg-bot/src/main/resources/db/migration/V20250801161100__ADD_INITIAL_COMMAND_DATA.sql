MERGE INTO command
USING (
    SELECT 1 id,
           'start' name,
           'Старт' description
    UNION ALL
    SELECT 2,
           'help',
           'Справочная информация по боту'
    UNION ALL
    SELECT 3,
           'menu',
           'Главное меню'
    UNION ALL
    SELECT 4,
           'my_advertisements',
           'Созданные пользователем актуальные объявления'
    UNION ALL
    SELECT 5,
           'new_advertisement',
           'Создание нового объявления'
    UNION ALL
    SELECT 6,
           'delete_advertisement',
           'Удаление созданного объявления'
    UNION ALL
    SELECT 7,
           'add_photos',
           'Добавить фотографии'
    UNION ALL
    SELECT 8,
           'confirm_photo',
           'Подтвердить фотографии'
    UNION ALL
    SELECT 9,
           'add_description',
           'Добавить описание'
    UNION ALL
    SELECT 10,
           'add_city',
           'Добавить город'
    UNION ALL
    SELECT 11,
           'add_advertisement_types',
           'Выбрать типы объявления'
    UNION ALL
    SELECT 12,
           'add_categories',
           'Выбрать категории'
    UNION ALL
    SELECT 13,
           'add_price',
           'Добавить стоимость'
    UNION ALL
    SELECT 14,
           'confirm_price',
           'Подтвердить стоимость'
    UNION ALL
    SELECT 15,
           'add_contacts',
           'Добавить контакты'
    UNION ALL
    SELECT 16,
           'add_phone',
           'Добавить номер телефона'
    UNION ALL
    SELECT 17,
           'confirm_phone',
           'Подтвердить номер телефона'
    UNION ALL
    SELECT 18,
           'add_social',
           'Добавить ссылку'
    UNION ALL
    SELECT 19,
           'confirm_ad',
           'Подтвердить'
    UNION ALL
    SELECT 20,
           'search_advertisement',
           'Поиск объявлений по хэштегам'
    UNION ALL
    SELECT 21,
           'search_advertisement_types',
           'Выбор типов объявления для поиска'
    UNION ALL
    SELECT 22,
           'search_product_categories',
           'Выбор категорий товаров для поиска'
    UNION ALL
    SELECT 23,
           'show_found_advertisements',
           'Вывод найденных объявлений'
) AS d
ON (command_id = d.id)
WHEN MATCHED THEN
    UPDATE SET name = d.name,
               description = d.description
WHEN NOT MATCHED THEN
    INSERT (command_id, name, description)
    VALUES (d.id, d.name, d.description);
