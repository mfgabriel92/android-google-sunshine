package example.com.sunshine;

import android.os.Build;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    /**
     * Simple test assuring that the current version of Android is greater than Gingerbread
     */
    @Test
    public void testAndroidVersionGreaterThanGingerbread() {
        int currentVersion = Build.VERSION.SDK_INT;
        int gingerbread = Build.VERSION_CODES.GINGERBREAD;

        assertTrue(currentVersion > gingerbread);
    }
}
