MERGE INTO tag_type
USING (
    SELECT 1 id,
           'city' name
    UNION ALL
    SELECT 2,
           'actuality'
    UNION ALL
    SELECT 3,
           'advertisement_type'
    UNION ALL
    SELECT 4,
           'product_categories'
    UNION ALL
    SELECT 5,
           'unknown'
) AS d
ON (tag_type_id = d.id)
WHEN MATCHED THEN
    UPDATE SET name = d.name
WHEN NOT MATCHED THEN
    INSERT (tag_type_id, name)
    VALUES (d.id, d.name);
