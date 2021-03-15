package railrouteplanner;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class AppTest {
    @Test
    public void testApp_shouldBeAbleToInstantiate() {
        try {
            @SuppressWarnings("InstantiationOfUtilityClass") App app = new App();
            assertNotNull(app);
        } catch (Exception e) {
            fail("Exception encountered: " + e.getMessage());
        }
    }
}
