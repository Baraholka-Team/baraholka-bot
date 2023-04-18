package baraholkateam.secure_constants;

public final class SecureConstants {
    public static final String CREATE_TABLES = """
            CREATE TABLE IF NOT EXISTS "Advertisement"
            (
                chat_id bigint NOT NULL,
                message_id bigint NOT NULL,
                tags character(100),
                creation_time bigint,
                next_update_time bigint,
                PRIMARY KEY (chat_id, message_id)
            );""";
    public static final String INSERT_NEW_ADVERTISEMENT = """
            INSERT INTO "Advertisement" VALUES (?, ?, ?, ?, ?);""";
    public static final String TAGS_SEARCH = """
            SELECT message_id
            FROM "Advertisement"
            WHERE ? <@ regexp_split_to_array(tags, ' ')
            ORDER BY creation_time DESC
            LIMIT ?;""";
    public static final String REMOVE_ALL_DATA = """
            DELETE FROM "Advertisement";""";
}
