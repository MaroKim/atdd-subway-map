package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("tableTruncator")
public class StationAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseTruncator databaseTruncator;

    @BeforeEach
    public void setup() throws Exception {
        RestAssured.port = port;
        databaseTruncator.afterPropertiesSet();
        databaseTruncator.cleanTable();
    }

    private static List<Map<String, Object>> stations;
    static RestUtil restUtil;
    static SubwayTestHelper testHelper;

    @BeforeAll
    static void init() {
        restUtil = new RestUtil();
        testHelper = new SubwayTestHelper();
        stations = testHelper.makeStationList("마곡역", "디지털미디어시티역", "마곡나루역");
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames =
                RestAssured.given().log().all()
                        .when().get("/stations")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    // TODO: 지하철역 목록 조회 인수 테스트 메서드 생성
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        //given
        testHelper.makeGivenCondition("/stations", HttpStatus.CREATED.value(), stations.get(0), stations.get(1));

        //when
        List<String> names = restUtil.getResponseJsonListDataByKey("/stations", "name", String.class);

        //then
        assertThat(names).containsAnyOf(stations.get(0).get("name").toString(), stations.get(1).get("name").toString());
    }


    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    // TODO: 지하철역 제거 인수 테스트 메서드 생성
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        //given
        Long id = restUtil.createEntityData("/stations", HttpStatus.CREATED.value(), stations.get(2));

        //when
        ExtractableResponse<Response> response = restUtil.deleteEntityDataById("/stations/{id}", id);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //then
        List<String> names = restUtil.getResponseJsonListDataByKey("/stations", "name", String.class);
        assertThat(names).doesNotContain(stations.get(2).get("name").toString());


    }
}