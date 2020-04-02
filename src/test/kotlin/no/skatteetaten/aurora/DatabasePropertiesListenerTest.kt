package no.skatteetaten.aurora

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [DatabasePropertiesListener::class])
class DatabasePropertiesListenerTest {

    @Value("\${spring.datasource.url}")
    private lateinit var url: String

    @Value("\${spring.datasource.username}")
    private lateinit var username: String

    @Value("\${spring.datasource.password}")
    private lateinit var password: String

    @Test
    fun `Set database properties`() {
        assertThat(url).isEqualTo("http://localhost")
        assertThat(username).isEqualTo("user")
        assertThat(password).isEqualTo("pass")
    }
}
