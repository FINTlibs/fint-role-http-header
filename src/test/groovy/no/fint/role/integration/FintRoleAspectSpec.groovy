package no.fint.role.integration

import no.fint.role.integration.testutils.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication.class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FintRoleAspectSpec extends Specification {

    @Autowired
    private TestRestTemplate restTemplate

    private HttpHeaders headers

    void setup() {
        headers = new HttpHeaders()
    }

    void cleanup() {
        headers.clear()
    }

    def "Sending correct role header and role"() {
        given:
        headers.add("x-role", "FINT_ADMIN_PORTAL_USER")

        when:
        def response = restTemplate.exchange("/role1", HttpMethod.GET, new HttpEntity<>(headers), String)

        then:
        response.statusCode == HttpStatus.OK
    }

    def "Sending custom role header and role"() {
        given:
        headers.add("x-test-role", "FINT_ADMIN_PORTAL_USER")

        when:
        def response = restTemplate.exchange("/role2", HttpMethod.GET, new HttpEntity<>(headers), String)

        then:
        response.statusCode == HttpStatus.OK
    }

    def "Missing role header"() {
        when:
        def response = restTemplate.exchange("/role1", HttpMethod.GET, new HttpEntity<>(headers), String)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "Sending correct role header and wrong role"() {
        given:
        headers.add("x-role", "FINT_ADMIN_PORTAL")

        when:
        def response = restTemplate.exchange("/role1", HttpMethod.GET, new HttpEntity<>(headers), String)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
    }
}
