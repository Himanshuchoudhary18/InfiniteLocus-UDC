package utilsApi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import static utilsApi.RefactoredRestAssuredHelper.enableAdditionalFieldsCheckInResponse;

@Getter
@Setter
public class StandardResponse<T> {

    public static <T> T parseJsonResponse(String responseJson, Class<T> responseType) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, enableAdditionalFieldsCheckInResponse);
        return mapper.readValue(responseJson, responseType);
    }

    public static <T> StandardResponse<T> parseJsonResponse(String responseArray, TypeReference<StandardResponse<T>> typeReference) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, enableAdditionalFieldsCheckInResponse);
        return objectMapper.readValue(responseArray, typeReference);
    }
}
