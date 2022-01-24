package nextstep.subway.acceptance.step_feature;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.StationResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class StationStepFeature {

    public static final String GANGNAM_STATION_NAME = "강남역";
    public static final String YEOKSAM_STATION_NAME = "역삼역";
    public static final String NONHYEON_STATION_NAME = "논현역";
    public static final String PANGYO_STATION_NAME = "판교역";
    private static final String CREATE_STATION_NAME_PARAM_KEY = "name";
    private static final String STATION_BASE_URI = "stations";

    public static StationResponse callCreateAndFind(String stationName) {
        ExtractableResponse<Response> createResponse = callCreateStation(stationName);
        String uri = createResponse.header("Location");

        ExtractableResponse<Response> response = callFindStationByUri(uri);
        return response.as(StationResponse.class);

    }

    public static ExtractableResponse<Response> callCreateStation(String stationName) {
        Map<String, String> stationParams = createStationParams(stationName);

        return RestAssured.given().log().all()
                .body(stationParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(STATION_BASE_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> callCreateStation(Map<String, String> StationParams) {
        return RestAssured.given().log().all()
                .body(StationParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(STATION_BASE_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> callFindAllStation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(STATION_BASE_URI)
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> callFindStationByUri(String uri) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> callDeleteStation(String uri) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
        return response;
    }

    public static Map<String, String> createGangnamStation() {
        return createStationParams(GANGNAM_STATION_NAME);
    }

    public static Map<String, String> createYeoksamStation() {
        return createStationParams(YEOKSAM_STATION_NAME);
    }

    private static Map<String, String> createStationParams(String name) {
        Map<String, String> result = new HashMap();
        result.put(CREATE_STATION_NAME_PARAM_KEY, name);

        return result;
    }

}
