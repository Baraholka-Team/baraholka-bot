MERGE INTO contact_type
USING (
    SELECT 1 id,
           'phone' name
    UNION ALL
    SELECT 2,
           'email'
    UNION ALL
    SELECT 3,
           'social'
    UNION ALL
    SELECT 4,
           'unknown'
) AS d
ON (contact_type_id = d.id)
WHEN MATCHED THEN
    UPDATE SET name = d.name
WHEN NOT MATCHED THEN
    INSERT (contact_type_id, name)
    VALUES (d.id, d.name);
