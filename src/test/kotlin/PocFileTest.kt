import kotlin.test.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals
import kotlin.test.fail

class PocFileTest {

    @Test
    fun poc() {
        // given
        // when
        val a = PocFile.poc()

        // then
        assertEquals(12, a)
        println("POC was ok!")
    }

    @Ignore
    @Test
    fun fail_every_time() {
        fail()
    }
}