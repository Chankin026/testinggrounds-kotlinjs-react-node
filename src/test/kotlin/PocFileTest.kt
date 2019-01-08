import kotlin.test.Test
import kotlin.test.assertEquals

class PocFileTest {

    @Test
    fun poc() {
        // given
        // when
        val a = PocFile.poc()

        // then
        assertEquals(12, a)
    }
}