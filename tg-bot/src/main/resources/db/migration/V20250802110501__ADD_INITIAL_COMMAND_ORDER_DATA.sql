MERGE INTO command_order
USING (
    SELECT 1 id,
           20 current_command_id,
           21 next_command_id
    UNION ALL
    SELECT 2,
           21,
           22
    UNION ALL
    SELECT 3,
           22,
           23
    UNION ALL
    SELECT 4,
           5,
           7
    UNION ALL
    SELECT 5,
           7,
           8
    UNION ALL
    SELECT 6,
           8,
           9
    UNION ALL
    SELECT 7,
           9,
           10
    UNION ALL
    SELECT 8,
           10,
           11
    UNION ALL
    SELECT 9,
           11,
           12
    UNION ALL
    SELECT 10,
           12,
           13
    UNION ALL
    SELECT 11,
           13,
           14
    UNION ALL
    SELECT 12,
           14,
           15
    UNION ALL
    SELECT 13,
           15,
           16
    UNION ALL
    SELECT 14,
           16,
           17
    UNION ALL
    SELECT 15,
           17,
           18
    UNION ALL
    SELECT 16,
           18,
           19
    UNION ALL
    SELECT 17,
           1,
           3
    UNION ALL
    SELECT 18,
           2,
           3
    UNION ALL
    SELECT 19,
           3,
           3
    UNION ALL
    SELECT 20,
           4,
           3
    UNION ALL
    SELECT 21,
           6,
           4
    UNION ALL
    SELECT 22,
           19,
           3
    UNION ALL
    SELECT 23,
           23,
           3
) AS d
ON (command_order_id = d.id)
WHEN MATCHED THEN
    UPDATE SET current_command_id = d.current_command_id,
               next_command_id = d.next_command_id
WHEN NOT MATCHED THEN
    INSERT (command_order_id, current_command_id, next_command_id)
    VALUES (d.id, d.current_command_id, d.next_command_id);
