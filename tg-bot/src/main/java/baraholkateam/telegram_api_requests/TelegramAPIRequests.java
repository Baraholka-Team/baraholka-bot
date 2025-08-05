package baraholkateam.telegram_api_requests;

import baraholkateam.configuration.BaraholkaBotConfiguration;
import baraholkateam.util.TelegramUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Component
@AllArgsConstructor
public class TelegramAPIRequests {

    private BaraholkaBotConfiguration baraholkaBotConfiguration;
    private final HttpClient client = HttpClient.newHttpClient();
    private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

    private static final String SCHEME = "https";
    private static final String HOST = "api.telegram.org";
    private static final String FORWARD_MESSAGE = "bot%s/forwardMessage";
    private static final String GET_FILE_PATH = "bot%s/getFile";
    private static final String GET_CHAT_MEMBER = "bot%s/getChatMember";
    private static final String CHAT_ID_PARAMETER = "chat_id";
    private static final String FROM_CHAT_ID_PARAMETER = "from_chat_id";
    private static final String MESSAGE_ID_PARAMETER = "message_id";
    private static final String FILE_ID_PARAMETER = "file_id";
    private static final String USER_ID_PARAMETER = "user_id";
    private static final String NOT_SUCCESS_TEXT = """
                                Not 200 code of the response.
                                Request URI: {}
                                Request headers: {}
                                Response: {}""";
    private static final String NO_FIELD_TEXT = """
                                Response doesn't contain '{}' field.
                                Request URI: {}
                                Request headers: {}
                                Response: {}""";
    private static final String RESULT_FIELD = "result";
    private static final String MESSAGE_ID_FIELD = "message_id";
    private static final String FILE_PATH_FIELD = "file_path";
    private static final String STATUS_FIELD = "status";
    private static final String USER_FIELD = "user";
    private static final String ID_FIELD = "id";
    private static final String FIRST_NAME_FIELD = "first_name";
    private static final String LAST_NAME_FIELD = "last_name";
    private static final String USERNAME_FIELD = "username";

    public Long forwardMessage(String fromChatId, String toChatId, Integer messageId) {
        try {
            URI uri = uriBuilder
                    .scheme(SCHEME)
                    .host(HOST)
                    .path(String.format(FORWARD_MESSAGE, baraholkaBotConfiguration.getToken()))
                    .queryParam(CHAT_ID_PARAMETER, toChatId)
                    .queryParam(FROM_CHAT_ID_PARAMETER, fromChatId)
                    .queryParam(MESSAGE_ID_PARAMETER, messageId)
                    .build()
                    .toUri();

            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(uri)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logErrorNotSuccessCode(request, response);
                return null;
            }

            JSONObject object = new JSONObject(response.body());

            if (!object.has(RESULT_FIELD)) {
                logErrorNoField(request, response, RESULT_FIELD);
                return null;
            }

            JSONObject result = object.getJSONObject(RESULT_FIELD);

            if (!result.has(MESSAGE_ID_FIELD)) {
                logErrorNoField(request, response, MESSAGE_ID_FIELD);
                return null;
            }

            return result.getLong(MESSAGE_ID_FIELD);
        } catch (IOException | InterruptedException e) {
            log.error("Cannot forward message: {}", e.getMessage());
            return null;
        }
    }

    public String getFilePath(String fileId) {
        try {
            URI uri = uriBuilder
                    .scheme(SCHEME)
                    .host(HOST)
                    .path(String.format(GET_FILE_PATH, baraholkaBotConfiguration.getToken()))
                    .queryParam(FILE_ID_PARAMETER, fileId)
                    .build()
                    .toUri();
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response;

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logErrorNotSuccessCode(request, response);
                return null;
            }

            JSONObject object = new JSONObject(response.body());

            if (!object.has(RESULT_FIELD)) {
                logErrorNoField(request, response, RESULT_FIELD);
                return null;
            }

            JSONObject result = object.getJSONObject(RESULT_FIELD);

            if (!result.has(FILE_PATH_FIELD)) {
                logErrorNoField(request, response, FILE_PATH_FIELD);
            }

            return result.getString(FILE_PATH_FIELD);
        } catch (IOException | InterruptedException e) {
            log.error("Cannot get file path: {}", e.getMessage());
            return null;
        }
    }

    public String getUserRole(Long chatId, Long userId) {
        try {
            URI uri = uriBuilder
                    .scheme(SCHEME)
                    .host(HOST)
                    .path(String.format(GET_CHAT_MEMBER, baraholkaBotConfiguration.getToken()))
                    .queryParam(CHAT_ID_PARAMETER, chatId)
                    .queryParam(USER_ID_PARAMETER, userId)
                    .build()
                    .toUri();

            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(uri)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logErrorNotSuccessCode(request, response);
                return null;
            }

            JSONObject object = new JSONObject(response.body());

            if (!object.has(RESULT_FIELD)) {
                logErrorNoField(request, response, RESULT_FIELD);
                return null;

            }

            JSONObject result = object.getJSONObject(RESULT_FIELD);

            if (!result.has(STATUS_FIELD)) {
                logErrorNoField(request, response, STATUS_FIELD);
                return null;
            }

            return result.getString(STATUS_FIELD);
        } catch (IOException | InterruptedException e) {
            log.error("Cannot get user role: {}", e.getMessage());
            return null;
        }
    }

    public TelegramUser getUser(Long chatId, Long userId) {
        try {
            URI uri = uriBuilder
                    .scheme(SCHEME)
                    .host(HOST)
                    .path(String.format(GET_CHAT_MEMBER, baraholkaBotConfiguration.getToken()))
                    .queryParam(CHAT_ID_PARAMETER, chatId)
                    .queryParam(USER_ID_PARAMETER, userId)
                    .build()
                    .toUri();

            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(uri)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logErrorNotSuccessCode(request, response);
                return null;
            }

            JSONObject object = new JSONObject(response.body());

            if (!object.has(RESULT_FIELD)) {
                logErrorNoField(request, response, RESULT_FIELD);
                return null;

            }

            JSONObject result = object.getJSONObject(RESULT_FIELD);

            if (!result.has(USER_FIELD)) {
                logErrorNoField(request, response, USER_FIELD);
                return null;
            }

            JSONObject user = result.getJSONObject(USER_FIELD);

            if (!user.has(ID_FIELD)) {
                logErrorNoField(request, response, ID_FIELD);
                return null;
            }

            if (!user.has(FIRST_NAME_FIELD)) {
                logErrorNoField(request, response, FIRST_NAME_FIELD);
                return null;
            }

            String firstName = user.getString(FIRST_NAME_FIELD);

            if (!user.has(LAST_NAME_FIELD)) {
                logErrorNoField(request, response, LAST_NAME_FIELD);
                return null;
            }

            String lastName = user.getString(LAST_NAME_FIELD);

            if (!user.has(USERNAME_FIELD)) {
                logErrorNoField(request, response, USERNAME_FIELD);
                return null;
            }

            Integer uid = user.getInt(ID_FIELD);
            String username = user.getString(USERNAME_FIELD);

            return new TelegramUser(uid, firstName, lastName, username);
        } catch (IOException | InterruptedException e) {
            log.error("Cannot get user: {}", e.getMessage());
            return null;
        }
    }

    private void logErrorNotSuccessCode(HttpRequest request, HttpResponse<String> response) {
        log.error(NOT_SUCCESS_TEXT, request.uri(), request.headers().toString(), response.body());
    }

    private void logErrorNoField(HttpRequest request, HttpResponse<String> response, String field) {
        log.error(NO_FIELD_TEXT, field, request.uri(), request.headers().toString(), response.body());
    }

}
